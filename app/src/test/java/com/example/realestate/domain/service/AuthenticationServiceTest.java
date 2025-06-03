package com.example.realestate.domain.service;

import static org.junit.Assert.*;

import org.junit.Test;

public class AuthenticationServiceTest {

    @Test
    public void validateEmail_validEmail_returnsTrue() {
        // Arrange
        String validEmail = "test@example.com";

        // Act
        boolean result = AuthenticationService.validateEmail(validEmail);

        // Assert
        assertTrue(result);
    }

    @Test
    public void validateEmail_invalidEmail_noAt_returnsFalse() {
        // Arrange
        String invalidEmail = "testexample.com";

        // Act
        boolean result = AuthenticationService.validateEmail(invalidEmail);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validateEmail_invalidEmail_noDot_returnsFalse() {
        // Arrange
        String invalidEmail = "test@examplecom";

        // Act
        boolean result = AuthenticationService.validateEmail(invalidEmail);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validateEmail_nullEmail_returnsFalse() {
        // Act
        boolean result = AuthenticationService.validateEmail(null);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validatePassword_validPassword_returnsTrue() {
        // Arrange
        String validPassword = "Test1@password";

        // Act
        boolean result = AuthenticationService.validatePassword(validPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    public void validatePassword_tooShort_returnsFalse() {
        // Arrange
        String shortPassword = "Te1@";

        // Act
        boolean result = AuthenticationService.validatePassword(shortPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validatePassword_noUppercase_returnsFalse() {
        // Arrange
        String noUppercase = "test1@password";

        // Act
        boolean result = AuthenticationService.validatePassword(noUppercase);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validatePassword_noLowercase_returnsFalse() {
        // Arrange
        String noLowercase = "TEST1@PASSWORD";

        // Act
        boolean result = AuthenticationService.validatePassword(noLowercase);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validatePassword_noDigit_returnsFalse() {
        // Arrange
        String noDigit = "Test@password";

        // Act
        boolean result = AuthenticationService.validatePassword(noDigit);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validatePassword_noSpecialChar_returnsFalse() {
        // Arrange
        String noSpecialChar = "Test1password";

        // Act
        boolean result = AuthenticationService.validatePassword(noSpecialChar);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validatePassword_nullPassword_returnsFalse() {
        // Act
        boolean result = AuthenticationService.validatePassword(null);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validateName_validName_returnsTrue() {
        // Arrange
        String validName = "John";

        // Act
        boolean result = AuthenticationService.validateName(validName);

        // Assert
        assertTrue(result);
    }

    @Test
    public void validateName_tooShort_returnsFalse() {
        // Arrange
        String shortName = "Jo";

        // Act
        boolean result = AuthenticationService.validateName(shortName);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validateName_emptyString_returnsFalse() {
        // Arrange
        String emptyName = "";

        // Act
        boolean result = AuthenticationService.validateName(emptyName);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validateName_onlyWhitespace_returnsFalse() {
        // Arrange
        String whitespace = "   ";

        // Act
        boolean result = AuthenticationService.validateName(whitespace);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validateName_nullName_returnsFalse() {
        // Act
        boolean result = AuthenticationService.validateName(null);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validatePhone_validPhone_returnsTrue() {
        // Arrange
        String validPhone = "1234567890";

        // Act
        boolean result = AuthenticationService.validatePhone(validPhone);

        // Assert
        assertTrue(result);
    }

    @Test
    public void validatePhone_tooShort_returnsFalse() {
        // Arrange
        String shortPhone = "123456789";

        // Act
        boolean result = AuthenticationService.validatePhone(shortPhone);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validatePhone_tooLong_returnsFalse() {
        // Arrange
        String longPhone = "12345678901";

        // Act
        boolean result = AuthenticationService.validatePhone(longPhone);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validatePhone_containsLetters_returnsFalse() {
        // Arrange
        String invalidPhone = "123456789a";

        // Act
        boolean result = AuthenticationService.validatePhone(invalidPhone);

        // Assert
        assertFalse(result);
    }

    @Test
    public void validatePhone_nullPhone_returnsFalse() {
        // Act
        boolean result = AuthenticationService.validatePhone(null);

        // Assert
        assertFalse(result);
    }
}
