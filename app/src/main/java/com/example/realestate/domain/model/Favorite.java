package com.example.realestate.domain.model;

import java.util.Date;

public class Favorite {
    private String email;
    private int propertyId;
    private Date addedDate;

    // Optional reference to the associated property
    private Property property;

    public Favorite() {
        // Default constructor
    }

    // Constructor with required fields
    public Favorite(String email, int propertyId) {
        this.email = email;
        this.propertyId = propertyId;
        this.addedDate = new Date(); // Default to current date when created
    }

    // Constructor with all fields
    public Favorite(String email, int propertyId, Date added) {
        this.email = email;
        this.propertyId = propertyId;
        this.addedDate = added;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    // Composite key equals and hashCode for proper comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Favorite favorite = (Favorite) o;

        if (propertyId != favorite.propertyId) return false;
        return email != null ? email.equals(favorite.email) : favorite.email == null;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + propertyId;
        return result;
    }
}
