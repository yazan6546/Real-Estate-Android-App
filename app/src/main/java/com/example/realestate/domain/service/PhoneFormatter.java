package com.example.realestate.domain.service;

public class PhoneFormatter {

    public static String formatMobile(String number, String country) {
        if (number == null) {
            throw new IllegalArgumentException("Phone number cannot be null.");
        }

        // Remove all non-digit characters
        String digits = number.replaceAll("\\D", "");

        // Remove leading zero if present
        if (digits.startsWith("0")) {
            digits = digits.substring(1);
        }

        return formatMobileNumber(digits, CountryService.countryCodeMap.get(country));

    }

    private static String formatMobileNumber(String digits, String countryCode) {
        if (digits.length() != 9 || !digits.startsWith("5") && !digits.startsWith("7")) {
            throw new IllegalArgumentException("Invalid mobile number format: " + digits);
        }

        return String.format("+%s-%s-%s-%s",
                countryCode,
                digits.substring(0, 2),   // e.g., 59, 79, 50
                digits.substring(2, 5),   // e.g., 321
                digits.substring(5));     // e.g., 2321
    }

}
