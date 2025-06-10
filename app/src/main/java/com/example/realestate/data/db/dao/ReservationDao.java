package com.example.realestate.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.MapInfo;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.realestate.data.db.entity.ReservationEntity;
import com.example.realestate.data.db.entity.ReservationWithPropertyEntity;
import com.example.realestate.data.db.entity.UserWithReservationsAndProperties;
import com.example.realestate.data.db.result.CountryCount;
import com.example.realestate.data.db.entity.UserEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Dao
public interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReservation(ReservationEntity reservation);

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

    @Query("SELECT * FROM reservations WHERE email = :userEmail")
    LiveData<List<ReservationEntity>> getReservationsByUserId(String userEmail);

    /**
     * Query to get reservations by date range
     */
    @Query("SELECT * FROM reservations WHERE start_date >= :startDate AND end_date <= :endDate")
    LiveData<List<ReservationEntity>> getReservationsByDateRange(Date startDate, Date endDate);

    /**
     * Transaction query to get reservation with property details by reservation ID
     */

    @Transaction
    @Query("SELECT * FROM reservations WHERE reservation_id = :reservationId")
    LiveData<ReservationWithPropertyEntity> getReservationWithPropertyById(int reservationId);

    /**
     * Query to get reservation by property ID
     */

    @Query("SELECT * FROM reservations WHERE property_id = :propertyId")
    LiveData<List<ReservationEntity>> getReservationsByPropertyId(int propertyId);

    /**
     * Transaction query to get reservations with property details for a specific
     * user
     */
    @Transaction
    @Query("SELECT * FROM reservations WHERE email = :userEmail")
    LiveData<List<ReservationWithPropertyEntity>> getReservationsWithPropertyByUserId(String userEmail);

    /**
     * Transaction query to get reservations with property details for a specific
     * user filtered by status
     */
    @Transaction
    @Query("SELECT * FROM reservations WHERE email = :userEmail AND status = :status")
    LiveData<List<ReservationWithPropertyEntity>> getReservationsWithPropertyByUserIdAndStatus(String userEmail,
            String status);

    /**
     * Transaction query to get all reservations with property details
     */
    @Transaction
    @Query("SELECT * FROM reservations")
    LiveData<List<ReservationWithPropertyEntity>> getAllReservationsWithProperty();

    /**
     * Transaction query to get all reservations with property details filtered by
     * status
     */
    @Transaction
    @Query("SELECT * FROM reservations WHERE status = :status")
    LiveData<List<ReservationWithPropertyEntity>> getAllReservationsWithPropertyByStatus(String status);

    @Query("SELECT COUNT(*) FROM reservations")
    LiveData<Integer> getReservationCount();

    // Count the reservations in all customer countries
    @Query("SELECT country, COUNT(country) as count" +
            " FROM reservations r " +
            " JOIN users u ON r.email = u.email " +
            "GROUP BY u.country" +
            " ORDER BY COUNT(*) DESC")
    LiveData<List<CountryCount>> getReservationCountByCountry();

    /**
     * Get reservations by user email (synchronous method for repository)
     */
    @Query("SELECT * FROM reservations WHERE email = :userEmail")
    List<ReservationEntity> getReservationsByUserEmail(String userEmail);

    /**
     * Delete reservation by ID
     */
    @Query("DELETE FROM reservations WHERE reservation_id = :reservationId")
    void deleteReservationById(int reservationId);

    /**
     * Multimap query that directly returns a map of users to their reservations
     * This is more efficient than manually building the map
     */

    @Transaction
    @Query("SELECT * FROM users WHERE is_admin = 0")
    LiveData<List<UserWithReservationsAndProperties>> getUsersWithReservationsAndPropertiesInternal();


    /**
     * Multimap query that directly returns a map of users to their reservations filtered by status
     */
    @Transaction
    @Query("SELECT * FROM users WHERE is_admin = 0")
    LiveData<UserWithReservationsAndProperties> getUsersWithReservationsByStatus(String status);
}
