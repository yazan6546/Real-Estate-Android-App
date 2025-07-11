package com.example.realestate.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.realestate.data.api.ApiService;
import com.example.realestate.data.api.JsonResponse;
import com.example.realestate.data.api.dto.PropertyDTO;
import com.example.realestate.data.db.dao.PropertyDao;
import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.data.db.entity.PropertyUpdate;
import com.example.realestate.domain.mapper.PropertyMapper;
import com.example.realestate.domain.model.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of PropertyRepository that handles both API and database
 * operations
 */
public class PropertyRepositoryImpl implements PropertyRepository {

    private final ApiService apiService;
    private final PropertyDao propertyDao;

    public PropertyRepositoryImpl(ApiService apiService, PropertyDao propertyDao) {
        this.apiService = apiService;
        this.propertyDao = propertyDao;
    }

    @Override
    public void refreshProperties(RepositoryCallback<Property> callback) {
        apiService.getJson().enqueue(new Callback<>() {

            @Override
            public void onResponse(@NonNull Call<JsonResponse> call,
                    @NonNull Response<JsonResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<PropertyEntity> entities = new ArrayList<>();
                    for (PropertyDTO dto : response.body().properties) {
                        entities.add(PropertyMapper.toEntity(dto));
                    }
                    savePropertiesToDatabase(entities, callback);
                } else {
                    callback.onError(new Throwable("API request unsuccessful"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call,
                    @NonNull Throwable t) {

                callback.onError(t);
            }
        });
    }

    private void savePropertiesToDatabase(List<PropertyEntity> properties, RepositoryCallback<Property> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                propertyDao.insertNewProperties(properties);

                List<PropertyUpdate> updateEntities = PropertyMapper.toPropertyUpdateList(properties);

                propertyDao.updatePropertiesPartially(updateEntities);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public LiveData<List<Property>> getAllProperties() {
        return Transformations.map(propertyDao.getAllProperties(), PropertyMapper::toDomainList);
    }

    @Override
    public LiveData<Property> getPropertyById(int propertyId) {
        return Transformations.map(propertyDao.getPropertyById(propertyId), PropertyMapper::toDomain);
    }

    @Override
    public LiveData<Integer> getPropertyCount() {
        return propertyDao.getPropertyCount();
    }

    @Override
    public void updateProperty(Property property, RepositoryCallback<Property> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                PropertyEntity entity = PropertyMapper.fromDomain(property);
                propertyDao.updateExistingProperties(java.util.Collections.singletonList(entity));
                callback.onSuccess(property);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public List<Property> getFavoriteProperties() {
        // For now, return an empty list. In a real app, this would fetch from favorites
        // table
        return new ArrayList<>();
    }

    @Override
    public void addToFavorites(String propertyId) {
        // For now, do nothing. In a real app, this would add to favorites table
    }

    @Override
    public void removeFromFavorites(String propertyId) {
        // For now, do nothing. In a real app, this would remove from favorites table
    }
}
