package com.example.authservice.user;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.authservice.exception.InvalidDateException;
import com.example.authservice.exception.InvalidPasswordException;
import com.example.authservice.exception.UserAlreadyExistsException;
import com.example.authservice.exception.UserNotFoundException;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	SHA256Encryption sha256 = new SHA256Encryption();

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
		if (null != existingUser && null != existingUser.getUsername() && !existingUser.getUsername().equals(" ")) {
			return existingUser;
		}
		return null;
	}

	@Override
	public ResponseUser findOneResponse(String username) {

		User user = userRepository.findByUsername(username);
		ResponseUser response = new ResponseUser();
		if (user != null) {
			response = userWithOutPassword(user);
			return response;
		} else {
			throw new UserNotFoundException(UserAuthenticationConstants.USER_DOES_NOT_EXIST + username);
		}

	}

	@Override
	public void saveValues(User user) throws Exception {
		User existingRecord = findOne(user.getUsername());
		String encodedPassword = null;
		if (null == existingRecord) {
			if (null == user.getDob()) {
				throw new InvalidDateException(UserAuthenticationConstants.INVALID_DATE);
			}
			if (isValidPassword(user.getPassword())) {
				encodedPassword = sha256.encrypt(user.getPassword());
				user.setPassword(encodedPassword);
				userRepository.save(user);
			} else {
				throw new InvalidPasswordException(UserAuthenticationConstants.INVALID_PASSWORD);
			}
		} else {
			throw new UserAlreadyExistsException(UserAuthenticationConstants.USER_ALREADY_EXISTS);
		}

	}

	@Override
	public ResponseEntity<String> validateUser(User user) throws Exception {
		User existingRecord = findOne(user.getUsername());
		String encodePassword = sha256.encrypt(user.getPassword());
		if (existingRecord == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserAuthenticationConstants.INVALID_USERNAME);
		} else if (existingRecord.isUsernameExists() && existingRecord.validatePassword(encodePassword)) {
			return ResponseEntity.status(HttpStatus.OK).body(UserAuthenticationConstants.VALID_USER);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(UserAuthenticationConstants.INVALID_PASSWORD_USERNAME);
		}

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

	@Override
	public boolean isValidPassword(String password) {
		return password != null && password.length() >= UserAuthenticationConstants.PASSWORD_MIN_LENGTH
				&& password.matches(UserAuthenticationConstants.PASSWORD_REGEX_UPPERCASE)
				&& password.matches(UserAuthenticationConstants.PASSWORD_REGEX_LOWERCASE)
				&& password.matches(UserAuthenticationConstants.PASSWORD_REGEX_DIGIT)
				&& password.matches(UserAuthenticationConstants.PASSWORD_REGEX_SPECIAL_CHAR);
	}

	@Override
	public ResponseEntity<String> updatePassword(RequestUser requestUser) {

		User existingRecord = findOne(requestUser.getUsername());
		if (null != existingRecord) {
			String encodedPassword = null;
			encodedPassword = sha256.encrypt(requestUser.getOldPassword());
			if (existingRecord.isUsernameExists() && existingRecord.validatePassword(encodedPassword)) {
				String newPassword = requestUser.getUpdatedPassword();
				if ((isValidPassword(newPassword))) {
					String encodedNewPassword = sha256.encrypt(newPassword);
					existingRecord.setPassword(encodedNewPassword);
					userRepository.save(existingRecord);
					return ResponseEntity.status(HttpStatus.OK).body(UserAuthenticationConstants.PASSWORD_UPDATED);
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(UserAuthenticationConstants.INVALID_PASSWORD);
				}
			}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserAuthenticationConstants.INCORRECT_PASSWORD);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(UserAuthenticationConstants.INVALID_USERNAME);
		}

	}


	@Override
	public ResponseEntity<String> deleteUser(@Valid User user) {
		User existingRecord = findOne(user.getUsername());
		if (null != existingRecord) {
			String encodedPassword = null;
			encodedPassword = sha256.encrypt(user.getPassword());
			if (existingRecord.isUsernameExists() && existingRecord.validatePassword(encodedPassword)) {
				userRepository.delete(existingRecord);
				return ResponseEntity.status(HttpStatus.OK)
						.body(UserAuthenticationConstants.USER_DELETED_SUCCESSFULLY + user.getUsername());
			}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserAuthenticationConstants.INCORRECT_PASSWORD);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserAuthenticationConstants.INVALID_USERNAME);
		}
	}
}
