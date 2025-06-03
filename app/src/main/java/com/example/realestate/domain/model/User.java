package com.example.realestate.domain.model;

public class User {

    public static enum Gender {
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

    public User(String firstName, String lastName, String email, String password, String phone, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.country = country;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
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
        } catch (IllegalArgumentException e) {
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
}
