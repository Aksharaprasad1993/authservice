package com.example.authservice.user;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class UserController {

	@Autowired
	private UserService service;

	@GetMapping(UserAuthenticationConstants.GET_USERS)
	public List<ResponseUser> retrieveAllUsers() {
		List<ResponseUser> allUsers = service.retrieveAllUsersWithoutPassword();
		return allUsers;

	}

	@GetMapping(UserAuthenticationConstants.LOOK_UP_USER)
	public ResponseUser retrieveUser(@RequestParam("username") String username) {
		ResponseUser retrievedUser = new ResponseUser();
		retrievedUser = service.findOneResponse(username);
		return retrievedUser;

	}

	@PostMapping(UserAuthenticationConstants.CREATE_USER)
	public ResponseEntity<String> createUser(@Valid @RequestBody User user) throws Exception {

		service.saveValues(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(UserAuthenticationConstants.USER_REGISTERED);

	}

	@PostMapping(UserAuthenticationConstants.VALIDATE_USER)
	public ResponseEntity<String> validateUser(@Valid @RequestBody User user) throws Exception {

		ResponseEntity<String> status = service.validateUser(user);

		return status;

	}

	@PostMapping(UserAuthenticationConstants.UPDATE_PASSWORD)
	public ResponseEntity<String> updatePassword(@RequestBody RequestUser requestUser) throws Exception {

		ResponseEntity<String> status = service.updatePassword(requestUser);

		return status;

	}

	@DeleteMapping(UserAuthenticationConstants.DELETE_USER)
	public ResponseEntity<String> deleteUser(@RequestParam("username") String username) {
		ResponseEntity<String> status = service.deleteUser(username);
		return status;
	}

}
