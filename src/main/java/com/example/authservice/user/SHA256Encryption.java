package com.example.authservice.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class SHA256Encryption {

	public String encrypt(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] hash = digest.digest(input.getBytes());

			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.getMessage();
			return null;
		}
	}

}

