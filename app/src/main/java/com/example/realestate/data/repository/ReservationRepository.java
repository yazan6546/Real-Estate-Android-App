package com.example.realestate.data.repository;

import com.example.realestate.data.db.dao.ReservationDao;
import com.example.realestate.domain.model.Reservation;

public class ReservationRepository {

    private final ReservationDao reservationDao;

    public interface ReservationCallback {
        void onSuccess();
        void onFailure();
    }

    public ReservationRepository(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    public void addReservation(Reservation reservation, ReservationCallback callback) {
        // Logic to add a reservation
        // Call callback.onSuccess() or callback.onFailure() based on the result
    }
}
