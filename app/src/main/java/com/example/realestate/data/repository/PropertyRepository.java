package com.example.realestate.data.repository;

import androidx.lifecycle.LiveData;

import com.example.realestate.data.db.entity.PropertyEntity;

import java.util.List;

import retrofit2.Callback;

/**
 * Repository to handle property data operations, abstracting the origin of the data
 * (network or local database)
 */
public interface PropertyRepository {
    /**
     * Fetch properties from remote source and store them locally
     * @param callback Callback to notify when operation completes
     */
    void refreshProperties(PropertyOperationCallback callback);


    /**
     * Get all properties from local database
     * @return LiveData list of properties
     */
    LiveData<List<PropertyEntity>> getAllProperties();

    /**
     * Get a property by ID from local database
     * @param propertyId the ID of the property
     * @return LiveData containing the property
     */
    LiveData<PropertyEntity> getPropertyById(int propertyId);

    /**
     * Callback interface for property operations
     */
    interface PropertyOperationCallback {
        void onSuccess();
        void onError(Throwable throwable);
    }
}
