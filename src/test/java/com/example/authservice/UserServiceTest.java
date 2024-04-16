package com.example.authservice;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.authservice.exception.UserAlreadyExistsException;
import com.example.authservice.exception.UserNotFoundException;
import com.example.authservice.user.ResponseUser;
import com.example.authservice.user.SHA256Encryption;
import com.example.authservice.user.User;
import com.example.authservice.user.UserRepository;
import com.example.authservice.user.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	UserRepository userMockRepository;
	
    @Mock
    private SHA256Encryption sha256; 

	@InjectMocks
	UserServiceImpl userService;



	// getUsers

	@Test
	public void retrieveAllUsers_Success() throws Exception {

		User users = new User();
		List<User> expected = Arrays.asList(users);

		when(userMockRepository.findAll()).thenReturn(expected);

		List<ResponseUser> actual = userService.retrieveAllUsersWithoutPassword();

		assertAll(() -> assertNotNull(actual), () -> assertEquals(expected.size(), actual.size()));

	}

	// lookUpUser

	@Test
	public void findOneResponse_Success() {

		String username = "testuser1";

		User expected = new User("TestUser1", "Test", username, "pwdtestuser1", "1998, 8, 12");

		when(userMockRepository.findByUsername(username)).thenReturn(expected);

		ResponseUser actual = userService.findOneResponse(username);
		assertAll(() -> assertNotNull(actual), () -> assertEquals(expected.getDob(), actual.getDob()),
				() -> assertEquals(expected.getLastname(), actual.getLastname())

		);

	}

	@Test
	public void findOneResponse_UserNotExists() {

		String username = "testUser";
		doThrow(new UserNotFoundException("User not found")).when(userMockRepository).findByUsername(username);

		assertThrows(UserNotFoundException.class, () -> {
			userService.findOneResponse(username);
		});

	}

	// createUser

	@Test
	public void createUser_Success() throws Exception {
		String username = "User";
		String rawPassword = "password123";
		String encodedPassword = "encodedPassword123";
		User userRequest = new User(username, "L", "user", rawPassword, "2001, 8, 12");

		User newUser = new User(username, "L", "user", encodedPassword, "2001, 8, 12");
		
		 when(sha256.encrypt(rawPassword)).thenReturn(encodedPassword);

		 doAnswer(invocation -> {
	            User savedUser = invocation.getArgument(0);
	            assertEquals(newUser.getUsername(), savedUser.getUsername());
	            assertEquals(newUser.getPassword(), savedUser.getPassword());
	            return null; // or do nothing since the method returns void
	        }).when(userMockRepository).save(any(User.class));

		 userService.saveValues(userRequest);
		
		 verify(sha256, times(1)).encrypt(rawPassword); 
		 verify(userMockRepository, times(1)).save(any(User.class));
	}

	@Test
	public void createUser_ThrowErrorIfUsernameAlreadyExists() {
		User userRequest = new User();
		userRequest.setUsername("kenny");

		doThrow(UserAlreadyExistsException.class).when(userMockRepository).findByUsername(userRequest.getUsername());

		assertThrows(UserAlreadyExistsException.class, () -> {
			userService.findByUsername(userRequest.getUsername());
		});
		verify(userMockRepository, never()).save(any(User.class));

	}

	// validateUser

	@Test
	public void validateUser_ValidUser() throws Exception {
		String username = "testUser";
		String password = "password123";
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);

		when(userMockRepository.findByUsername(username)).thenReturn(user);

		User response = userService.findOne(username);
		assertEquals(user, response);

	}

	@Test
	public void validateUser_InvalidUser() {
		String username = "testUser";
		String password = "invalidPassword";
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);

		doThrow(UserNotFoundException.class).when(userMockRepository).findByUsername(user.getUsername());

		assertThrows(UserNotFoundException.class, () -> {
			userService.findByUsername(user.getUsername());
		});
	}

}
