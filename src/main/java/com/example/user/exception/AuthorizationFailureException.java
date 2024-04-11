package com.example.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationFailureException extends RuntimeException {
	public AuthorizationFailureException(String message) {
		super(message);
	}
}
