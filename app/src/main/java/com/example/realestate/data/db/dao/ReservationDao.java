package com.example.realestate.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.realestate.data.db.entity.*;

import java.util.List;

@Dao
public interface ReservationDao {

    @Insert
    void insertReservation(ReservationEntity reservation);

    @Query("SELECT * FROM reservations WHERE reservation_id = :reservationId")
    ReservationEntity getReservationById(int reservationId);

    @Update
    void updateReservation(ReservationEntity reservation);

    @Delete
    void deleteReservation(ReservationEntity reservation);

    @Query("SELECT * FROM reservations WHERE email = :email")
    List<ReservationEntity> getReservationsByUserId(String email);
}
