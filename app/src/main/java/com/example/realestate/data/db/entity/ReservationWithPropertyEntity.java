package com.example.realestate.data.db.entity;

/**
 * Data class to hold reservation with its associated property details
 * Used for JOIN queries to get reservation and property information together
 */
public class ReservationWithPropertyEntity {
    public ReservationEntity reservation;
    public PropertyEntity property;

    public ReservationWithPropertyEntity() {
        // Default constructor
    }

    public ReservationWithPropertyEntity(ReservationEntity reservation, PropertyEntity property) {
        this.reservation = reservation;
        this.property = property;
    }
}
