package com.example.authservice.user;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	@PostMapping(UserAuthenticationConstants.LOOK_UP_USER)
	public ResponseUser retrieveUser(@Valid @RequestBody User user) {
		ResponseUser retrievedUser = new ResponseUser();
		retrievedUser = service.findOneResponse(user.getUsername());
		return retrievedUser;

	}

	
	@PostMapping(UserAuthenticationConstants.CREATE_USER)
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user) throws Exception {

		service.saveValues(user);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getUsername())
				.toUri();

		return ResponseEntity.created(location).build();

	}

	@PostMapping(UserAuthenticationConstants.VALIDATE_USER)
	public HttpStatus validateUser(@Valid @RequestBody User user) throws Exception {

		HttpStatus status = service.validateUser(user);

		return status;

	}
	


}
