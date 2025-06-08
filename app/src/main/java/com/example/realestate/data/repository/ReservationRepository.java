package com.example.realestate.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.example.realestate.data.db.dao.PropertyDao;
import com.example.realestate.data.db.dao.ReservationDao;
import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.data.db.entity.ReservationEntity;
import com.example.realestate.data.db.result.CountryCount;
import com.example.realestate.domain.mapper.PropertyMapper;
import com.example.realestate.domain.mapper.ReservationMapper;
import com.example.realestate.domain.model.Property;
import com.example.realestate.domain.model.Reservation;
import com.example.realestate.domain.service.CallbackUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class ReservationRepository {

    private final ReservationDao reservationDao;
    private final PropertyDao propertyDao;

    public ReservationRepository(ReservationDao reservationDao, PropertyDao propertyDao) {
        this.reservationDao = reservationDao;
        this.propertyDao = propertyDao;
    }

    // Get reservation by ID with transformed domain model
    public LiveData<Reservation> getReservationById(int reservationId) {
        return Transformations.map(reservationDao.getReservationById(reservationId),
                ReservationMapper::toDomain);
    } // Get reservations by user with transformed domain models

    public LiveData<List<Reservation>> getReservationsByUserId(String email) {
        // Property details could be loaded here if needed
        return Transformations.map(reservationDao.getReservationsByUserId(email),
                ReservationMapper::toDomainList);
    } // Get reservations by user with property details included

    public LiveData<List<Reservation>> getReservationsWithPropertyByUserId(String email) {
        return Transformations.switchMap(
                reservationDao.getReservationsByUserId(email),
                reservationEntities -> {
                    MediatorLiveData<List<Reservation>> result = new MediatorLiveData<>();

                    if (reservationEntities == null || reservationEntities.isEmpty()) {
                        result.setValue(ReservationMapper.toDomainList(reservationEntities));
                        return result;
                    }

                    // Use a background thread to fetch property details
                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            List<Reservation> reservationsWithProperties = new ArrayList<>();

                            for (ReservationEntity reservationEntity : reservationEntities) {
                                // Get property synchronously (this runs in background thread)
                                PropertyEntity propertyEntity = propertyDao
                                        .getPropertyByIdSync(reservationEntity.property_id);

                                // Convert to domain model with property details
                                Reservation reservation = ReservationMapper.toDomainWithProperty(reservationEntity,
                                        propertyEntity);
                                reservationsWithProperties.add(reservation);
                            }

                            // Post result to main thread
                            result.postValue(reservationsWithProperties);
                        } catch (Exception e) {
                            // Fallback to reservations without property details
                            result.postValue(ReservationMapper.toDomainList(reservationEntities));
                        }
                    });

                    return result;
                });
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
    }

    // Get reservation distribution by country
    public LiveData<List<CountryCount>> getReservationCountByCountry() {
        return reservationDao.getReservationCountByCountry();
    }
}
