package com.example.realestate.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.realestate.data.db.dao.PropertyDao;
import com.example.realestate.data.db.dao.ReservationDao;
import com.example.realestate.data.db.entity.ReservationEntity;
import com.example.realestate.data.db.result.CountryCount;
import com.example.realestate.domain.mapper.ReservationMapper;
import com.example.realestate.domain.model.Reservation;
import com.example.realestate.domain.service.CallbackUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class ReservationRepository {

    private final ReservationDao reservationDao;

    public ReservationRepository(ReservationDao reservationDao, PropertyDao propertyDao) {
        this.reservationDao = reservationDao;
    }

    public LiveData<List<Reservation>> getReservationsWithPropertyByUserId(String email) {
        return Transformations.map(reservationDao.getReservationsWithPropertyByUserId(email),
                ReservationMapper::toDomainWithPropertyList);
    }

    public LiveData<List<Reservation>> getReservationWithPropertyByUserIdAndStatus(String email, String status) {
        return Transformations.map(reservationDao.getReservationsWithPropertyByUserIdAndStatus(email,
                status), ReservationMapper::toDomainWithPropertyList);
    }

    public LiveData<List<Reservation>> getAllReservationsWithProperty() {
        return Transformations.map(reservationDao.getAllReservationsWithProperty(),
                ReservationMapper::toDomainWithPropertyList);
    }

    // Get reservations by property ID
    public LiveData<List<Reservation>> getReservationsByPropertyId(int propertyId) {
        return Transformations.map(reservationDao.getReservationsByPropertyId(propertyId),
                ReservationMapper::toDomainList);
    }

    // Get reservations by status
    public LiveData<List<Reservation>> getReservationsByStatus(String status) {
        return Transformations.map(reservationDao.getReservationsByStatus(status),
                ReservationMapper::toDomainList);
    }

    // Get reservations by date range
    public LiveData<List<Reservation>> getReservationsByDateRange(Date startDate, Date endDate) {
        return Transformations.map(reservationDao.getReservationsByDateRange(startDate, endDate),
                ReservationMapper::toDomainList);
    }

    // Add a new reservation
    public void addReservation(Reservation reservation, RepositoryCallback<Reservation> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ReservationEntity entity = ReservationMapper.fromDomain(reservation);
                reservationDao.insertReservation(entity);
                callback.onSuccess(reservation);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void addReservation(Reservation reservation) {
        addReservation(reservation, CallbackUtils.emptyCallback());
    }

    public void insertAll(List<Reservation> reservations, RepositoryCallback<Void> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<ReservationEntity> entities = ReservationMapper.fromDomainList(reservations);
                reservationDao.insertAll(entities);
                callback.onSuccess(null);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void insertAll(List<Reservation> reservations) {
        insertAll(reservations, CallbackUtils.emptyCallback());
    }

    // Update an existing reservation
    public void updateReservation(Reservation reservation, RepositoryCallback<Reservation> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ReservationEntity entity = ReservationMapper.fromDomain(reservation);
                reservationDao.updateReservation(entity);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    // Delete a reservation
    public void deleteReservation(Reservation reservation, RepositoryCallback<Reservation> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ReservationEntity entity = ReservationMapper.fromDomain(reservation);
                reservationDao.deleteReservation(entity);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    // Get the total count of reservations
    public LiveData<Integer> getReservationCount() {
        return reservationDao.getReservationCount();
    } // Get reservation distribution by country

    public LiveData<List<CountryCount>> getReservationCountByCountry() {
        return reservationDao.getReservationCountByCountry();
    }

    // Simplified methods for basic reservation operations
    public boolean submitReservation(Reservation reservation) {
        try {
            ReservationEntity entity = ReservationMapper.fromDomain(reservation);
            reservationDao.insertReservation(entity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Reservation> getUserReservations(String userEmail) {
        try {
            // This would normally return LiveData, but for simplicity return a list
            return ReservationMapper.toDomainList(reservationDao.getReservationsByUserEmail(userEmail));
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    public boolean cancelReservation(String reservationId) {
        try {
            reservationDao.deleteReservationById(Integer.parseInt(reservationId));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
