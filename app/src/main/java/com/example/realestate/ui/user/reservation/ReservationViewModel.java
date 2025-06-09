package com.example.realestate.ui.user.reservation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.realestate.domain.model.Reservation;
import com.example.realestate.data.repository.ReservationRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservationViewModel extends ViewModel {

    private final ReservationRepository repository;
    private final ExecutorService executor;

    private final MutableLiveData<Boolean> reservationResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Reservation>> userReservations = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public ReservationViewModel(ReservationRepository repository) {
        this.repository = repository;
        executor = Executors.newFixedThreadPool(2);
        isLoading.setValue(false);
    }

    public LiveData<Boolean> getReservationResult() {
        return reservationResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Reservation>> getUserReservations() {
        return userReservations;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void submitReservation(Reservation reservation) {
        isLoading.setValue(true);

        executor.execute(() -> {
            try {
                boolean success = repository.submitReservation(reservation);

                reservationResult.postValue(success);
                if (!success) {
                    errorMessage.postValue("Failed to submit reservation. Please try again.");
                }
            } catch (Exception e) {
                reservationResult.postValue(false);
                errorMessage.postValue("An error occurred: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    public void loadUserReservations(String userEmail) {
        isLoading.setValue(true);

        executor.execute(() -> {
            try {
                List<Reservation> reservations = repository.getUserReservations(userEmail);
                userReservations.postValue(reservations);
            } catch (Exception e) {
                errorMessage.postValue("Failed to load reservations: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    public void cancelReservation(String reservationId) {
        isLoading.setValue(true);

        executor.execute(() -> {
            try {
                boolean success = repository.cancelReservation(reservationId);
                if (success) {
                    // Reload reservations to reflect the change
                    // Note: In a real app, you might want to pass the user email here
                    loadUserReservations("");
                } else {
                    errorMessage.postValue("Failed to cancel reservation.");
                }
            } catch (Exception e) {
                errorMessage.postValue("Failed to cancel reservation: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
