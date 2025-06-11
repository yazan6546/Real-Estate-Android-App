package com.example.realestate.data.repository;

import androidx.lifecycle.LiveData;

import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.domain.model.Property;

import java.util.List;

/**
 * Repository to handle property data operations, abstracting the origin of the
 * data
 * (network or local database)
 */
public interface PropertyRepository {
    /**
     * Fetch properties from remote source and store them locally
     *
     * @param callback Callback to notify when operation completes
     */
    void refreshProperties(RepositoryCallback<Property> callback);

    /**
     * Get all properties from local database
     *
     * @return LiveData list of properties
     */
    LiveData<List<Property>> getAllProperties();

    /**
     * Get a property by ID from local database
     *
     * @param propertyId the ID of the property
     * @return LiveData containing the property
     */
    LiveData<Property> getPropertyById(int propertyId);

    /**
     * Get the number of properties
     *
     * @return number of properties as a livedata integer
     */

    LiveData<Integer> getPropertyCount();

    /**
     * Update a property in the database
     *
     * @param property the property to update
     * @param callback callback to notify when operation completes
     */
    void updateProperty(Property property, RepositoryCallback<Property> callback);

    /**
     * Get favorite properties for current user
     *
     * @return List of favorite properties
     */
    List<Property> getFavoriteProperties();

    /**
     * Add property to favorites
     *
     * @param propertyId the property ID to add
     */
    void addToFavorites(String propertyId);

    /**
     * Remove property from favorites
     *
     * @param propertyId the property ID to remove
     */
    void removeFromFavorites(String propertyId);

}


