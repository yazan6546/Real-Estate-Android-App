package com.example.realestate.ui.connection;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.realestate.data.db.dao.PropertyDao;
import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.data.repository.ApiRepository;

import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnectApiViewModel extends ViewModel {

    private final ApiRepository apiRepository;
    private final PropertyDao propertyDao;

    public enum ConnectionState { LOADING, CONNECTED, FAILED }

    private final MutableLiveData<ConnectionState> _connectionState = new MutableLiveData<>();
    public LiveData<ConnectionState> connectionState = _connectionState;

    public ConnectApiViewModel(ApiRepository repository, PropertyDao propertyDao) {
        this.apiRepository = repository;
        this.propertyDao = propertyDao;
    }

    public void connect() {
        _connectionState.setValue(ConnectionState.LOADING);

        apiRepository.fetchProperties(new Callback<>() {
            @Override
            public void onResponse(Call<List<PropertyEntity>> call, Response<List<PropertyEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    savePropertiesToDatabase(response.body());
                } else {
                    _connectionState.postValue(ConnectionState.FAILED);
                }
            }

            @Override
            public void onFailure(Call<List<PropertyEntity>> call, Throwable t) {
                _connectionState.postValue(ConnectionState.FAILED);
            }
        });
    }

    private void savePropertiesToDatabase(List<PropertyEntity> properties) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                propertyDao.insertAll(properties);
                _connectionState.postValue(ConnectionState.CONNECTED);
            } catch (Exception e) {
                _connectionState.postValue(ConnectionState.FAILED);
            }
        });
    }
}