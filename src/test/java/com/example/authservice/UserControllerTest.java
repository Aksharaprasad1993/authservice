package com.example.authservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.authservice.user.User;
import com.example.authservice.user.UserController;
import com.example.authservice.user.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
	
	
	@Mock
	UserService mockservice;  //mock the dependencies of the controller class
	
	
	
	@InjectMocks
	UserController controllertest;   //inject mock objects into the test class
	
	
	@Test
    void createUser_shouldCreateSuccessfully(){
		
		
	}
	

}
