package com.example.realestate.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import com.example.realestate.data.db.entity.*;
import com.example.realestate.data.db.result.CountryCount;

import java.util.Date;
import java.util.List;

@Dao
public interface ReservationDao {

    @Insert
    void insertReservation(ReservationEntity reservation);

    @Insert
    void insertAll(List<ReservationEntity> reservations);

    @Query("SELECT * FROM reservations WHERE reservation_id = :reservationId")
    LiveData<ReservationEntity> getReservationById(int reservationId);

    @Update
    void updateReservation(ReservationEntity reservation);

    @Delete
    void deleteReservation(ReservationEntity reservation);

    @Query("SELECT * FROM reservations WHERE email = :email")
    LiveData<List<ReservationEntity>> getReservationsByUserId(String email);

    @Query("SELECT COUNT(*) FROM reservations")
    LiveData<Integer> getReservationCount();

    @Query("SELECT * FROM reservations WHERE property_id = :propertyId")
    LiveData<List<ReservationEntity>> getReservationsByPropertyId(int propertyId);

    @Query("SELECT * FROM reservations WHERE status = :status")
    LiveData<List<ReservationEntity>> getReservationsByStatus(String status);

    @Query("SELECT * FROM reservations WHERE start_date >= :startDate AND end_date <= :endDate")
    LiveData<List<ReservationEntity>> getReservationsByDateRange(Date startDate, Date endDate);

    // Count the reservations in all customer countries
    @Query("SELECT country, COUNT(country) as count" +
            " FROM reservations r " +
            " JOIN users u ON r.email = u.email " +
            "GROUP BY u.country" +
            " ORDER BY COUNT(*) DESC")
    LiveData<List<CountryCount>> getReservationCountByCountry();
}
