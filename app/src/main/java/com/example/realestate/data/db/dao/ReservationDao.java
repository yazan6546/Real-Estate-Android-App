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
     * Deletes duplicate reservations keeping only the record with the lowest reservation_id
     * when all other fields are identical.
     *
     * @return The number of deleted duplicate records
     */
    @Query("DELETE FROM reservations WHERE reservation_id IN " +
           "(SELECT r1.reservation_id FROM reservations r1 " +
           "JOIN reservations r2 ON " +
           "r1.email = r2.email AND " +
           "r1.property_id = r2.property_id AND " +
           "r1.start_date = r2.start_date AND " +
           "r1.end_date = r2.end_date AND " +
           "r1.status = r2.status " +
           "WHERE r1.reservation_id > r2.reservation_id)")
    int deleteDuplicateReservations();

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



    @Transaction
    @Query("SELECT * FROM users WHERE is_admin = 0 ORDER BY first_name, last_name")
    LiveData<List<UserWithReservationsAndProperties>> getUsersWithReservationsAndPropertiesInternal();



    @Transaction
    @Query("SELECT DISTINCT u.* FROM users u " +
            "INNER JOIN reservations r ON u.email = r.email " +
            "WHERE u.is_admin = 0 AND r.status = :status " +
            "ORDER BY u.first_name, u.last_name")
    LiveData<List<UserWithReservationsAndProperties>> getUsersWithReservationsByStatus(String status);
}
