package com.example.realestate.ui.connection;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.db.DatabaseSeeder;
import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.data.repository.FavoriteRepository;
import com.example.realestate.data.repository.PropertyRepository;
import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.data.repository.ReservationRepository;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.Property;

import java.util.List;

public class ConnectApiViewModel extends ViewModel {

    private final PropertyRepository propertyRepository;
    private final ReservationRepository reservationRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;


    public enum ConnectionState { LOADING, CONNECTED, FAILED }

    private final MutableLiveData<ConnectionState> _connectionState = new MutableLiveData<>();
    public LiveData<ConnectionState> connectionState = _connectionState;

    public ConnectApiViewModel(PropertyRepository repository, ReservationRepository reservationRepository,
                               FavoriteRepository favoriteRepository, UserRepository userRepository) {
        this.propertyRepository = repository;
        this.reservationRepository = reservationRepository;
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }

    public void connect() {
        _connectionState.setValue(ConnectionState.LOADING);

        propertyRepository.refreshProperties(new RepositoryCallback<>() {
            @Override
            public void onSuccess() {
                _connectionState.postValue(ConnectionState.CONNECTED);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("ConnectApiViewModel", "Connection failed", throwable);
                _connectionState.postValue(ConnectionState.FAILED);
            }
        });
    }

    public void load_database() {
        DatabaseSeeder seeder = new DatabaseSeeder(userRepository, reservationRepository, favoriteRepository);
        seeder.seedDatabase();
    }

    /**
     * Factory for creating a ConnectApiViewModel with dependency injection
     */
    public static class Factory implements ViewModelProvider.Factory {
        private final PropertyRepository propertyRepository;
        private final ReservationRepository reservationRepository;
        private final FavoriteRepository favoriteRepository;
        private final UserRepository userRepository;

        public Factory(PropertyRepository propertyRepository,
                       ReservationRepository reservationRepository,
                       FavoriteRepository favoriteRepository, UserRepository userRepository) {
            this.propertyRepository = propertyRepository;
            this.favoriteRepository = favoriteRepository;
            this.reservationRepository = reservationRepository;
            this.userRepository = userRepository;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ConnectApiViewModel.class)) {
                return (T) new ConnectApiViewModel(propertyRepository, reservationRepository, favoriteRepository, userRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
