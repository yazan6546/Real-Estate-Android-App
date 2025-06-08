package com.example.realestate.ui.user.reservations;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.ReservationRepository;
import com.example.realestate.domain.model.Reservation;

import java.util.List;

public class UserReservationsViewModel extends ViewModel {
    private final ReservationRepository reservationRepository;
    private final MediatorLiveData<List<Reservation>> reservations = new MediatorLiveData<>();
    private LiveData<List<Reservation>> currentSource;
    private LiveData<List<Reservation>> currentFilteredSource;

    public UserReservationsViewModel(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public LiveData<List<Reservation>> getReservations() {
        return reservations;
    }

    public void loadUserReservations(String userEmail) {
        // Clear all previous sources to avoid duplicates
        clearAllSources();

        // Get all reservations for the user with property details
        currentSource = reservationRepository.getReservationsWithPropertyByUserId(userEmail);

        // Transform the LiveData to update our local MutableLiveData
        reservations.addSource(currentSource, reservations::setValue);
    }

    public void loadUserReservationsByStatus(String userEmail, String status) {
        // Clear all previous sources to avoid duplicates
        clearAllSources();

        // First get all user reservations with property details, then filter by status
        currentSource = reservationRepository.getReservationsWithPropertyByUserId(userEmail);

        // Transform to filter by status
        currentFilteredSource = Transformations.map(
                currentSource,
                reservationList -> {
                    if (reservationList == null)
                        return null;
                    return reservationList.stream()
                            .filter(reservation -> status.equalsIgnoreCase(reservation.getStatus()))
                            .collect(java.util.stream.Collectors.toList());
                });

        reservations.addSource(currentFilteredSource, reservationList -> {
            reservations.setValue(reservationList);
        });
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
            if (modelClass.isAssignableFrom(UserReservationsViewModel.class)) {
                return (T) new UserReservationsViewModel(reservationRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
