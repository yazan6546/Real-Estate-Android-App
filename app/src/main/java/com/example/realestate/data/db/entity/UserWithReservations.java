package com.example.realestate.data.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/**
 * Entity class for representing a User and their Reservations with property details
 * This is used for multimap relationships in Room
 */
public class UserWithReservations {
    @Embedded
    public UserEntity user;

    @Relation(
            parentColumn = "email",
            entityColumn = "email",
            entity = ReservationEntity.class
    )
    public List<ReservationWithPropertyEntity> reservations;

    public UserWithReservations(UserEntity user, List<ReservationWithPropertyEntity> reservations) {
        this.user = user;
        this.reservations = reservations;
    }

    public UserEntity getUser() {
        return user;
    }

    public List<ReservationWithPropertyEntity> getReservations() {
        return reservations;
    }
}
