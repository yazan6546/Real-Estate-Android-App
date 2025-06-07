package com.example.realestate.domain.service;

public class PhoneFormatter {

    public enum Country {
        Palestine, Jordan, UAE
    }

    public static String formatMobile(String number, Country country) {
        if (number == null) {
            throw new IllegalArgumentException("Phone number cannot be null.");
        }

        // Remove all non-digit characters
        String digits = number.replaceAll("\\D", "");

        // Remove leading zero if present
        if (digits.startsWith("0")) {
            digits = digits.substring(1);
        }

        switch (country) {
            case Palestine:
                return formatMobileNumber(digits, "+970");
            case Jordan:
                return formatMobileNumber(digits, "+962");
            case UAE:
                return formatMobileNumber(digits, "+971");
            default:
                throw new UnsupportedOperationException("Country not supported");
        }
    }

    private static String formatMobileNumber(String digits, String countryCode) {
        if (digits.length() != 9 || !digits.startsWith("5") && !digits.startsWith("7")) {
            throw new IllegalArgumentException("Invalid mobile number format: " + digits);
        }

        return String.format("%s-%s-%s-%s",
                countryCode,
                digits.substring(0, 2),   // e.g., 59, 79, 50
                digits.substring(2, 5),   // e.g., 321
                digits.substring(5));     // e.g., 2321
    }
}
