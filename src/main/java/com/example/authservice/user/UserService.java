package com.example.authservice.user;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public interface UserService {

    public List<User> retrieveAllUsers();

    public User findOne(String username);

    public User saveValues(@Valid User user) throws Exception;

    public HttpStatus validateUser(@Valid User user) throws Exception;

    public ResponseUser findOneResponse(String username);

    public String encodePassword(String password);

    public ResponseUser userWithOutPassword(User user);

    public List<ResponseUser> retrieveAllUsersWithoutPassword();

	public User findByUsername(String userName);

}

