package com.example.realestate.ui.admin.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.db.result.CountryCount;
import com.example.realestate.data.db.result.GenderCount;
import com.example.realestate.data.repository.PropertyRepository;
import com.example.realestate.data.repository.ReservationRepository;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.Property;
import com.example.realestate.domain.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PropertyRepository propertyRepository;

    private final LiveData<Integer> userCount;
    private final LiveData<Integer> reservationCount;
    private final LiveData<Integer> propertyCount;
    private final LiveData<GenderDistribution> genderDistribution;
    private final LiveData<List<CountryStatItem>> topCountries;

    public AdminDashboardViewModel(UserRepository userRepository,
                                  ReservationRepository reservationRepository,
                                  PropertyRepository propertyRepository) {

        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.propertyRepository = propertyRepository;

        // Transform repository data into view model representation

        // Count of users from user repository - use Transformations.map instead of observeForever
        this.userCount = userRepository.getUserCount();

        // Reservation count directly from reservation repository
        this.reservationCount = reservationRepository.getReservationCount();

        // Count of properties from property repository - use Transformations.map instead of observeForever
        this.propertyCount = Transformations.map(
            propertyRepository.getAllProperties(),
            properties -> properties != null ? properties.size() : 0
        );

        // Transform gender count into percentage distribution
        this.genderDistribution = Transformations.map(
                userRepository.getGenderDistribution(),
                this::calculateGenderDistribution);

        // Transform country reservation data into UI items
        this.topCountries = Transformations.map(
                reservationRepository.getReservationCountByCountry(),
                this::createCountryStatItems);
    }

    private GenderDistribution calculateGenderDistribution(GenderCount genderCount) {
        if (genderCount == null) {
            return new GenderDistribution(0, 0);
        }

        int total = genderCount.maleCount + genderCount.femaleCount;
        if (total == 0) {
            return new GenderDistribution(0, 0);
        }

        int malePercentage = (int) ((genderCount.maleCount / (double) total) * 100);
        int femalePercentage = 100 - malePercentage;

        return new GenderDistribution(malePercentage, femalePercentage);
    }

    private List<CountryStatItem> createCountryStatItems(List<CountryCount> countryCounts) {
        if (countryCounts == null) {
            return new ArrayList<>();
        }

        return countryCounts.stream()
                .map(cc -> new CountryStatItem(cc.country, cc.count))
                .collect(Collectors.toList());
    }

    // Getters for LiveData objects
    public LiveData<Integer> getUserCount() {
        return userCount;
    }

    public LiveData<Integer> getReservationCount() {
        return reservationCount;
    }

    public LiveData<Integer> getPropertyCount() {
        return propertyCount;
    }

    public LiveData<GenderDistribution> getGenderDistribution() {
        return genderDistribution;
    }

    public LiveData<List<CountryStatItem>> getTopCountries() {
        return topCountries;
    }

    // Factory for creating ViewModels with dependencies
    public static class Factory implements ViewModelProvider.Factory {
        private final UserRepository userRepository;
        private final ReservationRepository reservationRepository;
        private final PropertyRepository propertyRepository;

        public Factory(UserRepository userRepository,
                      ReservationRepository reservationRepository,
                      PropertyRepository propertyRepository) {
            this.userRepository = userRepository;
            this.reservationRepository = reservationRepository;
            this.propertyRepository = propertyRepository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AdminDashboardViewModel.class)) {
                return (T) new AdminDashboardViewModel(
                        userRepository, reservationRepository, propertyRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
