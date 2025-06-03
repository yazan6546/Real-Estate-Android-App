package com.example.realestate.domain.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Hashing {

    private static final int SALT_LENGTH = 16; // 128 bits
    private static final int ITERATIONS = 20;
    private static final int KEY_LENGTH = 256; // bits

    /**
     * Generates a random salt for password hashing
     * @return byte array containing the salt
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Hashes a password with PBKDF2WithHmacSHA256
     * @param password The plain text password
     * @param salt The salt bytes
     * @return The hashed password as a Base64 string
     */
    public static String hashPassword(String password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Creates a complete password hash with salt (for storage)
     * @param password The plain text password
     * @return String containing salt and hash, separated by ":"
     */
    public static String createPasswordHash(String password) {
        byte[] salt = generateSalt();
        String hash = hashPassword(password, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + hash;
    }

    /**
     * Verifies a password against a stored hash
     * @param password The plain text password to verify
     * @param storedHash The stored password hash (salt:hash)
     * @return true if the password matches
     */
    public static boolean verifyPassword(String password, String storedHash) {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) return false;

        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String computedHash = hashPassword(password, salt);
            return computedHash.equals(parts[1]);
        } catch (Exception e) {
            return false;
        }
    }
}