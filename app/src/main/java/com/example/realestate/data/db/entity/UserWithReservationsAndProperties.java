package com.example.realestate.data.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class UserWithReservationsAndProperties {
    @Embedded
    public UserEntity user;

    @Relation(
            entity = ReservationEntity.class,
            parentColumn = "email",
            entityColumn = "email"
    )
    public List<ReservationWithPropertyEntity> reservations;
}