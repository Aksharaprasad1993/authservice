package com.example.authservice.user;

public  final class UserAuthenticationConstants {
	
	public static final String GET_USERS = "/users";
	public static final String LOOK_UP_USER = "/lookUpUser";
	public static final String CREATE_USER = "/createUser";
	public static final String VALIDATE_USER = "/validateUser";
	public static final String UPDATE_PASSWORD = "/updatePassword";
	public static final String USER_ALREADY_EXISTS = "Username already exists";
	public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
	public static final String USER_DOES_NOT_EXIST = "User does not exist with Username ";
	public static final String VALID_USER = "User is valid";
	public static final String INVALID_USERNAME = "Username does not exist";
	public static final String INVALID_PASSWORD_USERNAME = "Invalid Username or Password";
	public static final String PASSWORD_UPDATED = "Password Updated";
	public static final String INCORRECT_PASSWORD = "Password entered does not match existing password";
	public static final String INVALID_DATE = "Invalid date format for date of birth";
	public static final String INVALID_PASSWORD = "Invalid Password.Password should contain 1 upper case, 1 lower case, 1 number ,1 special character and have minimum 8 characters";
	public static final String PASSWORD_REGEX_UPPERCASE = ".*[A-Z].*";
    public static final String PASSWORD_REGEX_LOWERCASE = ".*[a-z].*";
    public static final String PASSWORD_REGEX_DIGIT = ".*\\d.*";
    public static final String PASSWORD_REGEX_SPECIAL_CHAR = ".*[!@#$%^&*()-+=].*";
    public static final int PASSWORD_MIN_LENGTH = 8;
	
	
	

}
