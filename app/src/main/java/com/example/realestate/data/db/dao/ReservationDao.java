package com.example.realestate.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.realestate.data.db.entity.ReservationEntity;
import com.example.realestate.data.db.entity.ReservationWithPropertyEntity;

import java.util.List;

@Dao
public interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertReservation(ReservationEntity reservation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<ReservationEntity> reservations);

    @Update
    void updateReservation(ReservationEntity reservation);

    @Delete
    void deleteReservation(ReservationEntity reservation);

    @Query("SELECT * FROM reservations WHERE reservation_id = :id")
    LiveData<ReservationEntity> getReservationById(int id);

    @Query("SELECT * FROM reservations")
    LiveData<List<ReservationEntity>> getAllReservations();

    @Query("SELECT * FROM reservations WHERE status = :status")
    LiveData<List<ReservationEntity>> getReservationsByStatus(String status);

    /**
     * Transaction query to get reservations with property details for a specific user
     */
    @Transaction
    @Query("SELECT * FROM reservations WHERE email = :userEmail")
    LiveData<List<ReservationWithPropertyEntity>> getReservationsWithPropertyByUserId(String userEmail);

    /**
     * Transaction query to get reservations with property details for a specific user filtered by status
     */
    @Transaction
    @Query("SELECT * FROM reservations WHERE email = :userEmail AND status = :status")
    LiveData<List<ReservationWithPropertyEntity>> getReservationsWithPropertyByUserIdAndStatus(String userEmail, String status);

    /**
     * Transaction query to get all reservations with property details
     */
    @Transaction
    @Query("SELECT * FROM reservations")
    LiveData<List<ReservationWithPropertyEntity>> getAllReservationsWithProperty();

    /**
     * Transaction query to get all reservations with property details filtered by status
     */
    @Transaction
    @Query("SELECT * FROM reservations WHERE status = :status")
    LiveData<List<ReservationWithPropertyEntity>> getAllReservationsWithPropertyByStatus(String status);
}
