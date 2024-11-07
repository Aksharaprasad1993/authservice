package com.example.project;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserDetailsController {

	@Autowired
	private UserDetailsService service;

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
	public ResponseEntity<String> createUser(@Valid @RequestBody UserDetails user) throws Exception {

		service.saveValues(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(UserAuthenticationConstants.USER_REGISTERED);

	}

	@PostMapping(UserAuthenticationConstants.VALIDATE_USER)
	public ResponseEntity<String> validateUser(@Valid @RequestBody UserDetails user) throws Exception {

		ResponseEntity<String> status = service.validateUser(user);

		return status;

	}

	@PostMapping(UserAuthenticationConstants.UPDATE_PASSWORD)
	public ResponseEntity<String> updatePassword(@RequestBody RequestUser requestUser) throws Exception {

		ResponseEntity<String> status = service.updatePassword(requestUser);

		return status;

	}

	
	@DeleteMapping(UserAuthenticationConstants.DELETE_USER)
	public ResponseEntity<String> deleteUser(@Valid @RequestBody UserDetails user) throws Exception {
		ResponseEntity<String> status = service.deleteUser(user);
		return status;
	}

}
