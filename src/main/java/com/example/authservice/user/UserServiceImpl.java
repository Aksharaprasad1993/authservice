package com.example.authservice.user;



import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.user.exception.AuthorizationFailureException;
import com.example.user.exception.UserNotFoundException;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	/*
	 * @Autowired private PasswordEncoder passwordEncoder;
	 */
	
	@Autowired
	private SHA256Encryption sha256;

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
		if (null == existingRecord) {
			encodedPassword = sha256.encrypt(user.getPassword());
			//encodedPassword = passwordEncoder.encode(user.getPassword());
			//System.out.println(encodedPassword);
			//encodedPassword = encodePassword(user.getPassword());
			user.setPassword(encodedPassword);
			userRepository.save(user);
			return user;
		} else {
			throw new Exception(UserAuthenticationConstants.USER_ALREADY_EXISTS);
		}

	}

	@Override
	public HttpStatus validateUser(User user) throws Exception {
		User existingRecord = findOne(user.getUsername());
		//String encodePassword = encodePassword(user.getPassword());
		String encodePassword = sha256.encrypt(user.getPassword());
		//String encodedPassword = passwordEncoder.encode(user.getPassword());
		if (null != existingRecord && null != existingRecord.getUsername() && null != existingRecord.getPassword()
				&& encodePassword.equalsIgnoreCase(existingRecord.getPassword())) {
			return HttpStatus.OK;
		} else {
			return HttpStatus.UNAUTHORIZED;
			//throw new AuthorizationFailureException(UserAuthenticationConstants.UNAUTHORIZED_ACCESS);
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

	
	/*
	 * public void convertPasswordsToBcrypt() { List<User> users =
	 * userRepository.findAll(); // Fetch all users from the database for (User user
	 * : users) { String encryptedpassword = user.getPassword(); // Get the old
	 * password from the user object String oldPassword =
	 * Base64.getDecoder().decode(encryptedpassword).toString(); String
	 * newHashedPassword = passwordEncoder.encode(oldPassword); // Encode the old
	 * password with bcrypt user.setPassword(newHashedPassword); // Update the user
	 * object with the new bcrypt-encoded password } userRepository.saveAll(users);
	 * // Save the updated user objects back to the database }
	 */
}
