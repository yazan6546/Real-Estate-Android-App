package com.example.realestate.domain.service;

public class AuthenticationService {
    public static boolean validateEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public static boolean validatePassword(String password) {
        return password != null &&
                password.length() >= 6 &&
                password.matches(".*[A-Z].*") && // At least one uppercase letter
                password.matches(".*[a-z].*") && // At least one lowercase letter
                password.matches(".*[0-9].*") && // At least one digit
                password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"); // At least one special character

    }

    public static boolean validatePhone(String phone) {
        return phone != null && phone.matches("[0-9]{9}");
    }

    public static boolean validateName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 3;
    }
}
