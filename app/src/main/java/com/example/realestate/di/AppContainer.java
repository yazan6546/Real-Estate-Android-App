package com.example.realestate.di;

import android.content.Context;

import com.example.realestate.RealEstate;
import com.example.realestate.data.api.ApiClient;
import com.example.realestate.data.api.ApiService;
import com.example.realestate.data.db.AppDatabase;
import com.example.realestate.data.repository.FavoriteRepository;
import com.example.realestate.data.repository.PropertyRepository;
import com.example.realestate.data.repository.PropertyRepositoryImpl;
import com.example.realestate.data.repository.ReservationRepository;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.User;

/**
 * Container of objects shared across the whole app
 * This class follows the manual dependency injection pattern as described in:
 * https://developer.android.com/training/dependency-injection/manual
 */
public class AppContainer {
    // Database instance
    private final AppDatabase database;

    // API service instance
    private final ApiService apiService;

    // Repository instances
    private final PropertyRepository propertyRepository;

    private final UserRepository userRepository;

    private final FavoriteRepository favoriteRepository;

    private final ReservationRepository reservationRepository;

    public AppContainer(Context context) {
        // Initialize database
        database = AppDatabase.getDatabase(context);

        // Initialize API service
        apiService = ApiClient.getApiService();

        // Initialize repositories
        propertyRepository = new PropertyRepositoryImpl(apiService, database.propertyDao());

        userRepository = new UserRepository(database.userDao());
        favoriteRepository = new FavoriteRepository(database.favoriteDao());

        reservationRepository = new ReservationRepository(database.reservationDao(), database.propertyDao());

        // Hardcoded admin user for initial setup
        User adminUser = new User("admin@admin.com", "Admin123!", true);

        userRepository.insertUser(adminUser);

        User user = new User("yazan", "abualoun", "yazanaboeloun@gmail.com", "Admin123!", "594049488", "Jordan",
                "Amman", User.Gender.MALE, false);
        userRepository.insertUser(user);

//        reservationRepository.deleteDuplicateReservations();

    }

    // Getters for dependencies
    public AppDatabase getDatabase() {
        return database;
    }

    public ApiService getApiService() {
        return apiService;
    }

    public PropertyRepository getPropertyRepository() {
        return propertyRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public FavoriteRepository getFavoriteRepository() {
        return favoriteRepository;
    }

    public ReservationRepository getReservationRepository() {
        return reservationRepository;
    }
}
