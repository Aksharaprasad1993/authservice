package com.example.authservice.user;



import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user.exception.AuthorizationFailureException;
import com.example.user.exception.UserNotFoundException;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
//	@Autowired
//	private PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public List<User> retrieveAllUsers() {
		List<User> users = userRepository.findAll();
		return users;
	}

	@Override
	public List<ResponseUser> retrieveAllUsersWithoutPassword() {
		List<User> allUsers = retrieveAllUsers();
		List<ResponseUser> usersWithOutPassword = allUsers.stream().map(user -> userWithOutPassword(user)).toList();
		return usersWithOutPassword;

	}

	@Override
	public User findOne(String username) {
		User existingUser = userRepository.findByUsername(username);
		if(null != existingUser && null != existingUser.getUsername() && !existingUser.getUsername().equals(" ")) {
			return existingUser;
		}
		return null;
	}

	@Override
	public ResponseUser findOneResponse(String username) {

		User user = findOne(username);
		ResponseUser response = new ResponseUser();
		if (user != null) {
			response = userWithOutPassword(user);
			return response;
		} else {
			throw new UserNotFoundException("username " + username + " does not exist");
		}
		

	}

	@Override
	public User saveValues(User user) throws Exception {
		User existingRecord = findOne(user.getUsername());
		String encodedPassword = null;
		//String encodedPassword1 = null;
		if (null == existingRecord) {
			//encodedPassword1 = passwordEncoder.encode(user.getPassword());
			//System.out.println(encodedPassword1);
			System.out.println("saving");
			encodedPassword = encodePassword(user.getPassword());
			user.setPassword(encodedPassword);
			System.out.println(user.getUsername());
			userRepository.save(user);
			return user;
		} else {
			throw new Exception(UserAuthenticationConstants.USER_ALREADY_EXISTS);
		}

	}

	@Override
	public HttpStatus validateUser(User user) throws Exception {
		User existingRecord = findOne(user.getUsername());
		String encodePassword = encodePassword(user.getPassword());
		
		//String encodedPassword1 = passwordEncoder.encode(user.getPassword());
		if (null != existingRecord && null != existingRecord.getUsername() && null != existingRecord.getPassword()
				&& encodePassword.equalsIgnoreCase(existingRecord.getPassword())) {
			return HttpStatus.OK;
		} else {
			throw new AuthorizationFailureException(UserAuthenticationConstants.UNAUTHORIZED_ACCESS);
		}
	}

	@Override
	public String encodePassword(String password) {

		String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());

		return encodedPassword;
	}

	@Override
	public ResponseUser userWithOutPassword(User user) {
		ResponseUser userWithOutPassword = new ResponseUser();
		userWithOutPassword.setUsername(user.getUsername());
		userWithOutPassword.setName(user.getName());
		userWithOutPassword.setLastname(user.getLastname());
		userWithOutPassword.setDob(user.getDob());
		return userWithOutPassword;

	}

	@Override
	public User findByUsername(String userName) {
		User existingUser = userRepository.findByUsername(userName);
		return existingUser;
	}

}
