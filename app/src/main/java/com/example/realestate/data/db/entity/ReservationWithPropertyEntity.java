package com.example.realestate.data.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * Data class to hold reservation with its associated property details
 * Used for JOIN queries to get reservation and property information together
 */
public class ReservationWithPropertyEntity {

    @Embedded
    public ReservationEntity reservation;


    @Relation(
            parentColumn = "property_id",
            entityColumn = "property_id",
            entity = PropertyEntity.class
    )
    public PropertyEntity property;


    public ReservationWithPropertyEntity(ReservationEntity reservation, PropertyEntity property) {
        this.reservation = reservation;
        this.property = property;
    }
}
