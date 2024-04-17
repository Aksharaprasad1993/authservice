package com.example.authservice.user;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public interface UserService {

    List<User> retrieveAllUsers();

    User findOne(String username);

    void saveValues(@Valid User user) throws Exception;

    ResponseEntity<String> validateUser(@Valid User user) throws Exception;

    ResponseUser findOneResponse(String username);

    ResponseUser userWithOutPassword(User user);

    List<ResponseUser> retrieveAllUsersWithoutPassword();

	User findByUsername(String userName);
	
	boolean isValidPassword(String password);

	ResponseEntity<String> updatePassword(RequestUser requestUser);

	//ResponseEntity<String> deleteUser(String username);

	ResponseEntity<String> deleteUser(@Valid User user);

}

