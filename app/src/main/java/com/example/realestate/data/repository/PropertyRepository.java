package com.example.realestate.data.repository;

import androidx.lifecycle.LiveData;

import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.domain.model.Property;

import java.util.List;

/**
 * Repository to handle property data operations, abstracting the origin of the data
 * (network or local database)
 */
public interface PropertyRepository {
    /**
     * Fetch properties from remote source and store them locally
     * @param callback Callback to notify when operation completes
     */
    void refreshProperties(RepositoryCallback<Property> callback);


    /**
     * Get all properties from local database
     * @return LiveData list of properties
     */
    LiveData<List<Property>> getAllProperties();

    /**
     * Get a property by ID from local database
     * @param propertyId the ID of the property
     * @return LiveData containing the property
     */
    LiveData<Property> getPropertyById(int propertyId);


    /**
     * Get the number of properties
     * @return number of properties as a livedata integer
     */

    LiveData<Integer> getPropertyCount();
}
