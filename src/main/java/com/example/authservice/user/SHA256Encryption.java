package com.example.authservice.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Encryption {

    public String encrypt(String input) {
        try {
            // Create SHA-256 MessageDigest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Apply SHA-256 hashing to the input string
            byte[] hash = digest.digest(input.getBytes());

            // Convert byte array to hexadecimal string
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
            // Handle NoSuchAlgorithmException
            e.printStackTrace();
            return null;
        }
    }

}

