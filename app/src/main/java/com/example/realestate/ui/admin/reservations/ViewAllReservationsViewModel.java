package com.example.realestate.ui.admin.reservations;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.data.repository.ReservationRepository;
import com.example.realestate.domain.model.Reservation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ViewAllReservationsViewModel extends ViewModel {
    private final ReservationRepository reservationRepository;
    private final MutableLiveData<List<Reservation>> reservations = new MutableLiveData<>(new ArrayList<>());

    public ViewAllReservationsViewModel(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse("2025-07-01");
            endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse("2025-07-04");
        } catch (ParseException e) {
            Log.i("ParseException", Objects.requireNonNull(e.getMessage()));
        }
        Reservation reservation = new Reservation(1, "yazanaboeloun@gmail.com",101, startDate, endDate, "EXPIRED");
        reservationRepository.addReservation(reservation, new RepositoryCallback<>() {

            @Override
            public void onSuccess(Reservation result) {
                Log.d("Reservation", "Added successfully: " + result);
                loadAllReservations(); // Reload after adding
            }

            @Override
            public void onError(Throwable errorMessage) {
                Log.e("Reservation", "Error adding reservation: " + errorMessage.getMessage());
            }
        });
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
