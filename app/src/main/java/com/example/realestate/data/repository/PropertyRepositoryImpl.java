package com.example.realestate.data.repository;

import androidx.lifecycle.LiveData;

import com.example.realestate.data.api.ApiService;
import com.example.realestate.data.api.dto.PropertyDTO;
import com.example.realestate.data.db.dao.PropertyDao;
import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.domain.mapper.PropertyMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of PropertyRepository that handles both API and database operations
 */
public class PropertyRepositoryImpl implements PropertyRepository {

    private final ApiService apiService;
    private final PropertyDao propertyDao;

    public PropertyRepositoryImpl(ApiService apiService, PropertyDao propertyDao) {
        this.apiService = apiService;
        this.propertyDao = propertyDao;
    }

    @Override
    public void refreshProperties(PropertyOperationCallback callback) {
        apiService.getProperties().enqueue(new Callback<List<PropertyDTO>>() {
            @Override
            public void onResponse(Call<List<PropertyDTO>> call, Response<List<PropertyDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PropertyEntity> entities = new ArrayList<>();
                    for (PropertyDTO dto : response.body()) {
                        entities.add(PropertyMapper.toEntity(dto));
                    }
                    savePropertiesToDatabase(entities, callback);
                } else {
                    callback.onError(new Throwable("API request unsuccessful"));
                }
            }

            @Override
            public void onFailure(Call<List<PropertyDTO>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    private void savePropertiesToDatabase(List<PropertyEntity> properties, PropertyOperationCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                propertyDao.insertAll(properties);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public LiveData<List<PropertyEntity>> getAllProperties() {
        return propertyDao.getAllProperties();
    }

    @Override
    public LiveData<PropertyEntity> getPropertyById(int propertyId) {
        return propertyDao.getPropertyById(propertyId);
    }
}
