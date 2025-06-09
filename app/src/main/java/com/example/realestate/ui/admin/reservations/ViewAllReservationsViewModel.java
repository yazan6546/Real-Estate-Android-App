package com.example.realestate.ui.admin.reservations;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.ReservationRepository;
import com.example.realestate.domain.model.Reservation;
import com.example.realestate.domain.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewAllReservationsViewModel extends ViewModel {
    private final ReservationRepository reservationRepository;
    private final MediatorLiveData<Map<User, List<Reservation>>> userReservationsMap = new MediatorLiveData<>();
    private LiveData<Map<User, List<Reservation>>> currentSource;

    // Current filter status
    private String currentStatus = null;

    public ViewAllReservationsViewModel(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public LiveData<Map<User, List<Reservation>>> getUserReservationsMap() {
        return userReservationsMap;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * Load reservations with optional status filter
     * @param status The status to filter by, or null for all reservations
     */
    public void loadReservations(String status) {
        // Save current status
        currentStatus = status;

        // Clear any existing sources
        if (currentSource != null) {
            userReservationsMap.removeSource(currentSource);
        }

        // Get reservations based on filter
        if (status == null) {
            // Get all reservations
            currentSource = reservationRepository.getAllUserReservationsWithProperty();
        } else {
            // Get filtered reservations
            currentSource = reservationRepository.getAllUserReservationsWithPropertyByStatus(status);
        }

        // Connect to our mediator live data
        userReservationsMap.addSource(currentSource, userReservationsMap::setValue);
    }

    // Factory for creating ViewModel with dependencies
    public static class Factory implements ViewModelProvider.Factory {
        private final ReservationRepository reservationRepository;

        public Factory(ReservationRepository reservationRepository) {
            this.reservationRepository = reservationRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ViewAllReservationsViewModel.class)) {
                return (T) new ViewAllReservationsViewModel(reservationRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
