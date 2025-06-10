package com.example.realestate.domain.model;

import com.example.realestate.domain.exception.ValidationException;
import com.example.realestate.domain.service.AuthenticationService;
import com.example.realestate.domain.service.Hashing;

import java.util.List;

public class User {

    public enum Gender {
        MALE,
        FEMALE
    }

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String country;
    private String city;
    private Gender gender;
    private boolean admin;
    private String profileImage;

    private List<Reservation> reservations;

    public User(String firstName, String lastName, String email, String password, String phone,
                String country, String city, String gender,  boolean admin) {

        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        setPhone(phone);
        setCountry(country);
        setCity(city);
        setGender(gender);
        setAdmin(admin);
    }

    public User(String firstName, String lastName, String email, String password, String phone,
                String country, String city, Gender gender,  boolean admin) {

        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setPassword(password);
        setPhone(phone);
        setCountry(country);
        setCity(city);
        setGender(gender);
        setAdmin(admin);
    }

    public User(String email, String password, boolean admin) {
        setEmail(email);
        setPassword(password);
        setAdmin(admin);
        setGender(Gender.MALE);
        setPhone("594049488");
        setFirstName("Default");
        setLastName("User");
        setCountry("Palestine");
        setCity("Tulkarem");
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (!AuthenticationService.validateName(firstName))
            throw new ValidationException("First name must be at least 3 characters long.");

        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (!AuthenticationService.validateName(lastName))
            throw new ValidationException("Last name must be at least 3 characters long.");

        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!AuthenticationService.validateEmail(email))
            throw new ValidationException("Invalid email format.");

        this.email = email.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (!AuthenticationService.validatePassword(password))
            throw new ValidationException("Password must be at least 6 characters long, " +
                    "contain uppercase and lowercase letters, a digit, and a special character.");

        this.password = password;
    }

    /**
     * hashes, and sets the password
     */
    public void hashAndSetPassword() {
        // Then hash it and store
        this.password = Hashing.createPasswordHash(password);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (!AuthenticationService.validatePhone(phone))
            throw new ValidationException("Phone number must be 9 digits long (without the leading 0)");

        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw new ValidationException("Country cannot be empty.");
        }
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new ValidationException("City cannot be empty.");
        }
        this.city = city;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    // String-based setter for convenience
    public void setGender(String genderStr) {
        try {
            this.gender = Gender.valueOf(genderStr);
        } catch (Exception e) {
            // Default to MALE if invalid string
            this.gender = Gender.MALE;
        }
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
