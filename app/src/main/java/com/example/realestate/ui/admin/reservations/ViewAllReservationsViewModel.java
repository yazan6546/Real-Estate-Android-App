package com.example.realestate.ui.admin.reservations;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.ReservationRepository;
import com.example.realestate.domain.model.Reservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewAllReservationsViewModel extends ViewModel {
    private final ReservationRepository reservationRepository;
    private final MediatorLiveData<List<Reservation>> reservations = new MediatorLiveData<>();
    private LiveData<List<Reservation>> currentSource;
    private LiveData<List<Reservation>> currentFilteredSource;

    public ViewAllReservationsViewModel(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public LiveData<List<Reservation>> getReservations() {
        return reservations;
    }

    public void loadAllReservations() {
        // Clear all previous sources to avoid duplicates
        clearAllSources();

        // Get all reservations with property details
//        currentSource = reservationRepository.getAllReservationsWithProperty();

        // Transform the LiveData to update our local MutableLiveData
        reservations.addSource(currentSource, reservations::setValue);
    }

    public void loadReservationsByStatus(String status) {
        // Clear all previous sources to avoid duplicates
        clearAllSources();

        // First get all reservations with property details, then filter by status
//        currentSource = reservationRepository.getAllReservationsWithProperty();

        // Transform to filter by status
        currentFilteredSource = Transformations.map(
                currentSource,
                reservationList -> {
                    if (reservationList == null)
                        return null;
                    return reservationList.stream()
                            .filter(reservation -> status.equalsIgnoreCase(reservation.getStatus()))
                            .collect(Collectors.toList());
                });

        reservations.addSource(currentFilteredSource, reservations::setValue);
    }

    /**
     * Group reservations by user email
     * @param reservations List of reservations to group
     * @return Map of user emails to their reservations
     */
    public Map<String, List<Reservation>> getReservationsByUser(List<Reservation> reservations) {
        if (reservations == null) {
            return new HashMap<>();
        }

        Map<String, List<Reservation>> userReservations = new HashMap<>();
        for (Reservation reservation : reservations) {
            String userEmail = reservation.getEmail();
            if (!userReservations.containsKey(userEmail)) {
                userReservations.put(userEmail, new ArrayList<>());
            }
            userReservations.get(userEmail).add(reservation);
        }

        return userReservations;
    }

    private void clearAllSources() {
        if (currentSource != null) {
            reservations.removeSource(currentSource);
            currentSource = null;
        }
        if (currentFilteredSource != null) {
            reservations.removeSource(currentFilteredSource);
            currentFilteredSource = null;
        }
    }

    // Factory for creating ViewModel with dependencies
    public static class Factory implements ViewModelProvider.Factory {
        private final ReservationRepository reservationRepository;

        public Factory(ReservationRepository reservationRepository) {
            this.reservationRepository = reservationRepository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ViewAllReservationsViewModel.class)) {
                return (T) new ViewAllReservationsViewModel(reservationRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
