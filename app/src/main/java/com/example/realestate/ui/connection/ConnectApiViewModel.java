package com.example.realestate.ui.connection;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.data.repository.PropertyRepository;

import java.util.List;

public class ConnectApiViewModel extends ViewModel {

    private final PropertyRepository propertyRepository;

    public enum ConnectionState { LOADING, CONNECTED, FAILED }

    private final MutableLiveData<ConnectionState> _connectionState = new MutableLiveData<>();
    public LiveData<ConnectionState> connectionState = _connectionState;

    public ConnectApiViewModel(PropertyRepository repository) {
        this.propertyRepository = repository;
    }

    public void connect() {
        _connectionState.setValue(ConnectionState.LOADING);

        propertyRepository.refreshProperties(new PropertyRepository.PropertyOperationCallback() {
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

    /**
     * Factory for creating a ConnectApiViewModel with dependency injection
     */
    public static class Factory implements ViewModelProvider.Factory {
        private final PropertyRepository propertyRepository;

        public Factory(PropertyRepository propertyRepository) {
            this.propertyRepository = propertyRepository;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ConnectApiViewModel.class)) {
                return (T) new ConnectApiViewModel(propertyRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
