package com.example.realestate.ui.admin.reservations;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.ReservationRepository;
import com.example.realestate.domain.model.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ViewAllReservationsViewModel extends ViewModel {
    private final ReservationRepository reservationRepository;
    private final MutableLiveData<List<Reservation>> reservations = new MutableLiveData<>(new ArrayList<>());

    public ViewAllReservationsViewModel(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public LiveData<List<Reservation>> getReservations() {
        return reservations;
    }

    public void loadAllReservations() {
        // Temporary implementation that sets empty list
        // In a real app, this would fetch from repository
        reservations.setValue(new ArrayList<>());
    }

    public void loadReservationsByStatus(String status) {
        // Temporary implementation that sets empty list
        // In a real app, this would filter by status
        reservations.setValue(new ArrayList<>());
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
