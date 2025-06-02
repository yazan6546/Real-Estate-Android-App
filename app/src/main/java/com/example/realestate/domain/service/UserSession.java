package com.example.realestate.domain.service;

public class UserSession {
    private String email;
    private String password;
    private boolean isAdmin;
    private String firstName;
    private String lastName;

    public UserSession(String email, String password, String firstName, String lastName,
                       boolean isAdmin) {

        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public boolean isAdmin() {
        return isAdmin;
    }

}