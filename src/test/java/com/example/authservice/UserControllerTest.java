package com.example.authservice;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.authservice.user.ResponseUser;
import com.example.authservice.user.User;
import com.example.authservice.user.UserAuthenticationConstants;
import com.example.authservice.user.UserController;
import com.example.authservice.user.UserService;
import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	@Mock
	UserService mockservice; 

	@InjectMocks
	UserController controllertest; 

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(controllertest).build();
	}

	@Test
	void getUsers_Success() throws Exception {

		// given
		ResponseUser users = new ResponseUser();
		List<ResponseUser> expected = Arrays.asList(users);

		when(mockservice.retrieveAllUsersWithoutPassword()).thenReturn(expected);

		mockMvc.perform(get(UserAuthenticationConstants.GET_USERS)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn().equals(expected);


	}


	@Test
	void createUser_CreatedSuccessfully() throws Exception {
		/*
		 * 
		 * // given User user = new User("User", "L", "user", "pwduser",
		 * LocalDate.of(2001, 8, 12));
		 * 
		 * // mockservice.saveValues(user);
		 * 
		 * // when // User request = new User(); ResponseEntity<Object> response =
		 * controllertest.createUser(user);
		 * 
		 * // then assertAll(() -> assertNotNull(response), () ->
		 * assertEquals(HttpStatus.CREATED, response.getStatusCode())
		 * 
		 * );
		 * 
		 */}

	@Test
	void createUser_UsernameExists() throws Exception {
		/*
		 * 
		 * User userRequest = new User("Sam", "T", "sam", "pwdsam", LocalDate.of(2001,
		 * 8, 12));
		 * 
		 * when(mockservice.findByUsername(userRequest.getUsername())).thenThrow(
		 * UserAlreadyExistsException.class);
		 * 
		 * mockMvc.perform(post(UserAuthenticationConstants.CREATE_USER)
		 * .content(userRequest)) .andExpect(status().isConflict());
		 * 
		 * 
		 */}


	@Test
	void createUser_passwordCheck() {

	}

	@Test
	void lookUp_Success() throws Exception {

		String username = "henry";
		User existingUser = new User();
		existingUser.setUsername(username);

		when(mockservice.findByUsername(username)).thenReturn(existingUser);

		String responseJson = mockMvc.perform(get(UserAuthenticationConstants.LOOK_UP_USER).param("username", username))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

        Map<String, Object> jsonResponse = JsonPath.read(responseJson, "$");
        assert jsonResponse.get("username").equals(username);

	}

	@Test
	void lookUp_UserNotExists() throws Exception {
		
	       String username = "nonExistingUser";

	        when(mockservice.findByUsername(username)).thenReturn(null);

	        mockMvc.perform(get(UserAuthenticationConstants.LOOK_UP_USER)
	                .param("username", username))
	                .andExpect(status().isNotFound());
	    }
		

	

}
