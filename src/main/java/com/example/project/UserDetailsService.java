package com.example.project;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;

@Component
public interface UserDetailsService {

    List<UserDetails> retrieveAllUsers();

    UserDetails findOne(String username);

    void saveValues(@Valid UserDetails user) throws Exception;

    ResponseEntity<String> validateUser(@Valid UserDetails user) throws Exception;

    ResponseUser findOneResponse(String username);

    ResponseUser userWithOutPassword(UserDetails user);

    List<ResponseUser> retrieveAllUsersWithoutPassword();

	UserDetails findByUsername(String userName);
	
	boolean isValidPassword(String password);

	ResponseEntity<String> updatePassword(RequestUser requestUser);

	//ResponseEntity<String> deleteUser(String username);

	ResponseEntity<String> deleteUser(@Valid UserDetails user);

}

