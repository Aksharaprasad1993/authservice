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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.authservice.exception.AuthorizationFailureException;
import com.example.authservice.exception.InvalidPasswordException;
import com.example.authservice.exception.UserAlreadyExistsException;
import com.example.authservice.exception.UserNotFoundException;
import com.example.authservice.user.RequestUser;
import com.example.authservice.user.ResponseUser;
import com.example.authservice.user.SHA256Encryption;
import com.example.authservice.user.User;
import com.example.authservice.user.UserAuthenticationConstants;
import com.example.authservice.user.UserRepository;
import com.example.authservice.user.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	UserRepository userMockRepository;

	@InjectMocks
	UserServiceImpl userService;

	SHA256Encryption sha256 = new SHA256Encryption();

	// getUsers

	@Test
	public void testGetUsers_Success() throws Exception {

		List<User> expected = Arrays.asList(new User("username1", "lastname1", "user1", "RawPassword@1", "2000-06-22"),
				new User("username2", "lastname2", "user2", "RawPassword@2", "2000-07-12"));

		when(userMockRepository.findAll()).thenReturn(expected);

		List<ResponseUser> actual = userService.retrieveAllUsersWithoutPassword();

		assertAll(() -> assertNotNull(actual), () -> assertEquals(expected.size(), actual.size()));

	}

	// lookUpUser

	@Test
	public void testLookUp_Success() {

		String username = "testuser1";

		User expected = new User("TestUser1", "Test", username, "Pwdtestuser@1", "1998-08-12");

		when(userMockRepository.findByUsername(username)).thenReturn(expected);

		ResponseUser actual = userService.findOneResponse(username);
		assertAll(() -> assertNotNull(actual), () -> assertEquals(expected.getDob(), actual.getDob()),
				() -> assertEquals(expected.getLastname(), actual.getLastname())

		);

	}

	@Test
	public void testLookUp_UserNotNotFound() {

		String username = "testUser";
		doThrow(new UserNotFoundException("User not found")).when(userMockRepository).findByUsername(username);

		assertThrows(UserNotFoundException.class, () -> {
			userService.findOneResponse(username);
		});

	}

	// createUser

	@Test
	public void testCreateUser_Success() throws Exception {
		String username = "User";
		String rawPassword = "Password@123";
		User userRequest = new User(username, "L", "user", rawPassword, "2001-08-12");

		when(userMockRepository.save(any(User.class))).thenReturn(userRequest);

		userService.saveValues(userRequest);

		verify(userMockRepository).save(any(User.class));

	}

	@Test
	public void testcreateUser_usernameAlreadyExists() {
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
	public void testvalidateUser_Success() throws Exception {
		String username = "testUser";
		String password = "password123";
		String encodedPassword = sha256.encrypt(password);
		User user = new User();
		user.setUsername(username);
		user.setPassword(encodedPassword);

		when(userMockRepository.findByUsername(username)).thenReturn(user);

		User response = userService.findOne(username);
		assertEquals(user.getUsername(), response.getUsername());
		assertEquals(user.getPassword(), response.getPassword());

	}

	@Test
	public void testvalidateUser_UserNotFound() {
		String username = "invalidUser";
		String password = "invalidPassword";
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);

		doThrow(UserNotFoundException.class).when(userMockRepository).findByUsername(user.getUsername());

		assertThrows(UserNotFoundException.class, () -> {
			userService.findByUsername(user.getUsername());
		});
	}

	@Test
	public void testvalidateUser_InvalidUsernameOrPassword() {
		String username = "invalidUser1";
		String password = "invalidPassword1";
		String encodedPassword = sha256.encrypt(password);
		User user = new User();
		user.setUsername(username);
		user.setPassword(encodedPassword);

		doThrow(InvalidPasswordException.class).when(userMockRepository).findByUsername(user.getUsername());
		assertThrows(InvalidPasswordException.class, () -> {
			userService.findByUsername(user.getUsername());
		});

	}

	// update Password

	@Test
	public void testUpdatePassword_Success() {
		RequestUser requestUser = new RequestUser("testUser", "oldPassword@123", "newPassword@123");
		String encodedPassword = sha256.encrypt("oldPassword@123");

		User user = new User("testUser", "testlastname", "testusername", encodedPassword, "2010-03-08");
		when(userMockRepository.findByUsername(requestUser.getUsername())).thenReturn(user);

		ResponseEntity<String> result = userService.updatePassword(requestUser);

		assertEquals(ResponseEntity.status(HttpStatus.OK).body(UserAuthenticationConstants.PASSWORD_UPDATED), result);
		verify(userMockRepository, times(1)).save(any(User.class));
	}

	@Test
	public void testUpdatePassword_PasswordNotMatch() {
		RequestUser requestUser = new RequestUser("testUser", "incorrectPassword@123", "newPassword@123");
		String encodedOldPassword = sha256.encrypt("oldPassword@123");
		User user = new User("testUser", "testlastname", "testusername", encodedOldPassword, "2010-03-08");

		when(userMockRepository.findByUsername(requestUser.getUsername())).thenReturn(user);
		ResponseEntity<String> result = userService.updatePassword(requestUser);

		assertEquals(
				ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserAuthenticationConstants.INCORRECT_PASSWORD),
				result);
		verify(userMockRepository, never()).save(any(User.class));

	}

	@Test
	public void testUpdatePassword_UserNotFound() {
		RequestUser requestUser = new RequestUser("nonExistentUser", "oldPassword", "newPassword");

		when(userMockRepository.findByUsername(requestUser.getUsername())).thenReturn(null);

		ResponseEntity<String> result = userService.updatePassword(requestUser);

		assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).body(UserAuthenticationConstants.INVALID_USERNAME),
				result);
		verify(userMockRepository, never()).save(any(User.class));

	}

	// Delete User

	@Test
	public void testDeleteUser_Success() {
		
		String username = "existingUser";
		String password = "Password";
		String encodedPassword = sha256.encrypt(password);
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);

		User responseuser = new User("testusername", "testlastname", username, encodedPassword, "2010-03-08");
		when(userMockRepository.findByUsername(username)).thenReturn(responseuser);

		ResponseEntity<String> result = userService.deleteUser(user);
		assertEquals(ResponseEntity.status(HttpStatus.OK)
				.body(UserAuthenticationConstants.USER_DELETED_SUCCESSFULLY + username), result);
		verify(userMockRepository, times(1)).delete(responseuser);
	}

	@Test
	public void testDeleteUser_UserNotFound() {
		String username = "nonExistentUser";
		String password = "Password";
		String encodedPassword = sha256.encrypt(password);
		User user = new User();
		user.setUsername(username);
		user.setPassword(encodedPassword);
		when(userMockRepository.findByUsername("nonExistentUser")).thenReturn(null);

		ResponseEntity<String> result = userService.deleteUser(user);
		assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserAuthenticationConstants.INVALID_USERNAME),
				result);
		verify(userMockRepository, never()).delete(any(User.class));

	}
}
