package com.example.authservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.authservice.exception.AuthorizationFailureException;
import com.example.authservice.exception.UserAlreadyExistsException;
import com.example.authservice.exception.UserNotFoundException;
import com.example.authservice.user.RequestUser;
import com.example.authservice.user.ResponseUser;
import com.example.authservice.user.SHA256Encryption;
import com.example.authservice.user.User;
import com.example.authservice.user.UserAuthenticationConstants;
import com.example.authservice.user.UserController;
import com.example.authservice.user.UserService;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	@Mock
	UserService mockservice;

	@InjectMocks
	UserController controllertest;

	private MockMvc mockMvc;

	SHA256Encryption sha256 = new SHA256Encryption();

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(controllertest).build();
	}

	@Test
	public void getUsers_Success() throws Exception {

		// given
		ResponseUser users = new ResponseUser();
		List<ResponseUser> expected = Arrays.asList(users);

		when(mockservice.retrieveAllUsersWithoutPassword()).thenReturn(expected);

		mockMvc.perform(get(UserAuthenticationConstants.GET_USERS)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn().equals(expected);

	}
	
	
	   @Test
	    public void getUsers_EmptyList() throws Exception {
		   List<ResponseUser> result = new ArrayList<ResponseUser>();
				   when(mockservice.retrieveAllUsersWithoutPassword()).thenReturn(result);
				
	        mockMvc.perform(get(UserAuthenticationConstants.GET_USERS))
	               .andExpect(status().isOk())
	               .andReturn().equals(result);
	    }

	// create user

	@Test
	public void testCreateUser_Success() throws Exception {

		String username = "User";
		String rawPassword = "Password@123";
		String encodedPassword = sha256.encrypt(rawPassword);
		User userRequest = new User(username, "L", "user", rawPassword, "2001-08-12");
		userRequest.setPassword(encodedPassword);
		doNothing().when(mockservice).saveValues(any(User.class)); 


		 ResponseEntity<String> responseEntity = controllertest.createUser(userRequest);

		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		assertEquals(UserAuthenticationConstants.USER_REGISTERED, responseEntity.getBody());
		verify(mockservice).saveValues(any(User.class));

	}

	@Test
	public void testCreateUser_UserExists() throws Exception { 
		String username = "existingUser";
		String password = "password";
		String encryptedPassword = sha256.encrypt(password);
		User userRequest = new User(username, "L", "user", encryptedPassword, "2001-08-12");

		doThrow(new UserAlreadyExistsException()).when(mockservice).saveValues(any(User.class));

		assertThrows(UserAlreadyExistsException.class, () -> {
			controllertest.createUser(userRequest);
		});
	}

	// lookUp User

	@Test
	public void testLookUp_Success() throws Exception {
		String username = "testusername";
		ResponseUser responseUser = new ResponseUser(username, "lastname", "testUser", LocalDate.of(1994, 05, 04));
		when(mockservice.findOneResponse(username)).thenReturn(responseUser);

		mockMvc.perform(get(UserAuthenticationConstants.LOOK_UP_USER).param("username", username))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value(username));
	}

	@Test
	public void testLookUp_UserNotFound() throws Exception {

		String username = "nonExistingUser1";

		when(mockservice.findOneResponse(username)).thenThrow(UserNotFoundException.class);

		mockMvc.perform(get(UserAuthenticationConstants.LOOK_UP_USER).param("username", username))
				.andExpect(status().isNotFound());

	}

	// validate User
	@Test
	public void testValidateUser_Success() throws Exception {
		String username = "validUser";
		String password = "validPassword";

		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		 when(mockservice.validateUser(any(User.class))).thenReturn(ResponseEntity.ok(UserAuthenticationConstants.VALID_USER));
		
		ResponseEntity<String> responseEntity = controllertest.validateUser(user);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(UserAuthenticationConstants.VALID_USER, responseEntity.getBody());
    }
	

	@Test
	public void testValidateUser_InvalidPassword() throws Exception {
		String username = "validUser";
		String password = "invalidPassword";
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		doThrow(AuthorizationFailureException.class).when(mockservice).validateUser(user);

		assertThrows(AuthorizationFailureException.class, () -> {
			controllertest.validateUser(user);
		});

	}

	@Test
	public void testValidateUser_UserNotFound() throws Exception {

		String username = "NonExistentUser";
		String password = "Password";
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);

		when(mockservice.validateUser(any(User.class))).thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserAuthenticationConstants.VALID_USER));
		
		ResponseEntity<String> response = controllertest.validateUser(user);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertEquals(UserAuthenticationConstants.VALID_USER, response.getBody());
	}

	// update password

	@Test
	public void testUpdatePassword_Success() throws Exception {

		RequestUser requestuser = new RequestUser("validUser", "oldPassword", "newPassword");
		ResponseEntity<String> responseEntity = ResponseEntity.ok(UserAuthenticationConstants.PASSWORD_UPDATED);
		when(mockservice.updatePassword(requestuser)).thenReturn(responseEntity);

		ResponseEntity<String> response = controllertest.updatePassword(requestuser);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(UserAuthenticationConstants.PASSWORD_UPDATED, response.getBody());
	}

	@Test
	public void testUpdatePassword_PasswordNotMatch() throws Exception {

		RequestUser requestuser = new RequestUser("validUser", "oldPassword", "newPassword");
		ResponseEntity<String> responseEntity = ResponseEntity.badRequest()
				.body(UserAuthenticationConstants.INVALID_PASSWORD);
		when(mockservice.updatePassword(requestuser)).thenReturn(responseEntity);

		ResponseEntity<String> response = controllertest.updatePassword(requestuser);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(UserAuthenticationConstants.INVALID_PASSWORD, response.getBody());
	}

	@Test
	public void testUpdatePassword_UserNotFound() throws Exception {

		RequestUser requestuser = new RequestUser("nonExistentUser", "oldPassword", "newPassword");
		ResponseEntity<String> responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(UserAuthenticationConstants.INVALID_USERNAME);
		when(mockservice.updatePassword(requestuser)).thenReturn(responseEntity);

		ResponseEntity<String> response = controllertest.updatePassword(requestuser);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(UserAuthenticationConstants.INVALID_USERNAME, response.getBody());
	}

	// delete user

	@Test
	public void testDeleteUser_UserExists() throws Exception {
		String username = "existingUser";
		String password = "Password";
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		ResponseEntity<String> responseEntity = ResponseEntity
				.ok(UserAuthenticationConstants.USER_DELETED_SUCCESSFULLY + username);
		when(mockservice.deleteUser(user)).thenReturn(responseEntity);

		ResponseEntity<String> response = controllertest.deleteUser(user);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(UserAuthenticationConstants.USER_DELETED_SUCCESSFULLY + username, response.getBody());
	}

	@Test
	public void testDeleteUser_UserNotFound() throws Exception {
		String username = "nonExistentUser";
		String password = "Password";
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		
		ResponseEntity<String> responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(UserAuthenticationConstants.INVALID_USERNAME);
		when(mockservice.deleteUser(user)).thenReturn(responseEntity);

		ResponseEntity<String> response = controllertest.deleteUser(user);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertEquals(UserAuthenticationConstants.INVALID_USERNAME, response.getBody());
	}

}
