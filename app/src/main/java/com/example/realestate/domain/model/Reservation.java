package com.example.realestate.domain.model;

import java.util.Date;

public class Reservation implements java.io.Serializable {
    private int reservationId;
    private String email;
    private Date startDate;
    private Date endDate;
    private String status;

    // Optional reference to related property
    private Property property;
    private User user;

    public Reservation() {
        // Default constructor
    }

    public Reservation(int reservationId, String email, Date start,
            Date end, String status) {
        this.reservationId = reservationId;
        this.email = email;
        this.startDate = start;
        this.endDate = end;
        this.status = status;
    }

    // Getters and setters
    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPropertyId() {
        return property != null ? property.getPropertyId() : 0;
    }


}
