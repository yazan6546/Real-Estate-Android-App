package com.example.realestate.ui.user.reservation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.ReservationRepository;
import com.example.realestate.domain.model.Property;
import com.example.realestate.domain.model.Reservation;
import com.example.realestate.domain.service.SharedPrefManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReservationViewModel extends ViewModel {

    private final ReservationRepository reservationRepository;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> hasConflict = new MutableLiveData<>(false);
    private final MutableLiveData<String> conflictMessage = new MutableLiveData<>();

    // Date/time selections
    private final MutableLiveData<Date> startDateTime = new MutableLiveData<>();
    private final MutableLiveData<Date> endDateTime = new MutableLiveData<>();

    public ReservationViewModel(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Getters for LiveData
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getHasConflict() {
        return hasConflict;
    }

    public LiveData<String> getConflictMessage() {
        return conflictMessage;
    }

    public LiveData<Date> getStartDateTime() {
        return startDateTime;
    }

    public LiveData<Date> getEndDateTime() {
        return endDateTime;
    }

    /**
     * Set the start date and time
     */
    public void setStartDateTime(Date dateTime) {
        startDateTime.setValue(dateTime);
        checkForConflicts();
    }

    /**
     * Set the end date and time
     */
    public void setEndDateTime(Date dateTime) {
        endDateTime.setValue(dateTime);
        checkForConflicts();
    }

    /**
     * Check for date/time conflicts with existing reservations
     */
    private void checkForConflicts() {
        Date start = startDateTime.getValue();
        Date end = endDateTime.getValue();

        if (start == null || end == null) {
            hasConflict.setValue(false);
            return;
        }

        // Validate that start is before end
        if (start.compareTo(end) >= 0) {
            hasConflict.setValue(true);
            conflictMessage.setValue("Start date/time must be before end date/time");
            return;
        }

        // Validate that start is not in the past
        Calendar now = Calendar.getInstance();
        if (start.before(now.getTime())) {
            hasConflict.setValue(true);
            conflictMessage.setValue("Start date/time cannot be in the past");
            return;
        }

        // Reset conflict state - in a real implementation, you would check against
        // existing reservations
        hasConflict.setValue(false);
        conflictMessage.setValue("");
    }

    /**
     * Check for conflicts with existing reservations for a specific property
     */
    public void checkPropertyReservationConflicts(int propertyId) {
        Date start = startDateTime.getValue();
        Date end = endDateTime.getValue();

        if (start == null || end == null || propertyId <= 0) {
            return;
        }

        // Use the repository's conflict checking method
        reservationRepository.checkReservationConflicts(propertyId, start, end,
                new com.example.realestate.data.repository.ReservationRepository.ConflictCheckCallback() {
                    @Override
                    public void onResult(boolean hasConflictResult, String message,
                            List<Reservation> conflictingReservations) {
                        hasConflict.postValue(hasConflictResult);
                        conflictMessage.postValue(message);
                    }

                    @Override
                    public void onError(Exception error) {
                        // If error checking conflicts, assume no conflict for now
                        hasConflict.postValue(false);
                        conflictMessage.postValue("");
                    }
                });
    }

    /**
     * Create and submit a new reservation
     */
    public void createReservation(Property property, String userEmail) {
        Date start = startDateTime.getValue();
        Date end = endDateTime.getValue();

        if (start == null || end == null) {
            errorMessage.setValue("Please select both start and end date/time");
            return;
        }

        if (hasConflict.getValue() == Boolean.TRUE) {
            errorMessage.setValue("Cannot create reservation due to conflicts. Please select different dates/times.");
            return;
        }

        if (property == null || userEmail == null || userEmail.isEmpty()) {
            errorMessage.setValue("Invalid property or user information");
            return;
        }

        isLoading.setValue(true);

        // Create reservation object
        Reservation reservation = new Reservation();
        reservation.setEmail(userEmail);
        reservation.setStartDate(start);
        reservation.setEndDate(end);
        reservation.setStatus("Pending"); // Default status
        reservation.setProperty(property);
        // Submit reservation
        reservationRepository.addReservation(reservation,
                new com.example.realestate.data.repository.RepositoryCallback<Reservation>() {
                    @Override
                    public void onSuccess(Reservation result) {
                        isLoading.postValue(false);
                        successMessage.postValue("Reservation created successfully!");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        isLoading.postValue(false);
                        errorMessage.postValue("Failed to create reservation: " + throwable.getMessage());
                    }
                });
    }

    /**
     * Clear all error and success messages
     */
    public void clearMessages() {
        errorMessage.setValue(null);
        successMessage.setValue(null);
    }

    /**
     * Reset the date/time selections
     */
    public void resetDateTime() {
        startDateTime.setValue(null);
        endDateTime.setValue(null);
        hasConflict.setValue(false);
        conflictMessage.setValue("");
    }

    /**
     * Validate if reservation can be submitted
     */
    public boolean canSubmitReservation() {
        return startDateTime.getValue() != null &&
                endDateTime.getValue() != null &&
                hasConflict.getValue() != Boolean.TRUE &&
                !Boolean.TRUE.equals(isLoading.getValue());
    }

    /**
     * Factory class for creating ReservationViewModel instances
     */
    public static class Factory implements ViewModelProvider.Factory {
        private final ReservationRepository reservationRepository;

        public Factory(ReservationRepository reservationRepository) {
            this.reservationRepository = reservationRepository;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ReservationViewModel.class)) {
                return (T) new ReservationViewModel(reservationRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
