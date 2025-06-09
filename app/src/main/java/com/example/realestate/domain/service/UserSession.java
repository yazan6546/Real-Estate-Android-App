package com.example.realestate.domain.service;

public class UserSession {
    private String email;
    private final boolean isAdmin;
    private String firstName;
    private String lastName;

    private boolean rememberMe;
    private String profileImage;

    public UserSession(String email, String firstName, String lastName,
                       String profileImage,
                       boolean isAdmin) {

        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
        this.profileImage = profileImage;
        this.rememberMe = false; // Default value
    }

    public UserSession(String email, String firstName, String lastName,
                       boolean isAdmin) {
        this(email, firstName, lastName, null, isAdmin);
    }

    public String getEmail() {
        return email;
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

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}