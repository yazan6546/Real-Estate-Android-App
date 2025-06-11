package com.example.realestate.ui.user.properties;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.FavoriteRepository;
import com.example.realestate.data.repository.PropertyRepository;
import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.domain.model.Favorite;
import com.example.realestate.domain.model.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PropertiesViewModel extends ViewModel {

    private final PropertyRepository propertyRepository;
    private final FavoriteRepository favoriteRepository;

    // Original data sources
    private final LiveData<List<Property>> allProperties;

    // Filtered and processed data
    private final MediatorLiveData<List<Property>> filteredProperties = new MediatorLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<String> selectedPropertyType = new MutableLiveData<>("All");
    private final MutableLiveData<String> selectedLocation = new MutableLiveData<>("All");
    private final MutableLiveData<Double> minPrice = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> maxPrice = new MutableLiveData<>(Double.MAX_VALUE); // UI state
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    // Filter options
    private final MutableLiveData<List<String>> propertyTypes = new MutableLiveData<>();
    private final MutableLiveData<List<String>> locations = new MutableLiveData<>();

    public PropertiesViewModel(PropertyRepository propertyRepository, FavoriteRepository favoriteRepository) {
        this.propertyRepository = propertyRepository;
        this.favoriteRepository = favoriteRepository;

        // Get all properties from repository
        this.allProperties = propertyRepository.getAllProperties();

        // Set up filtered properties mediator
        setupFilteredProperties();

        // Extract filter options when properties change
        extractFilterOptions();
    }

    private void setupFilteredProperties() {
        filteredProperties.addSource(allProperties, properties -> filterProperties());
        filteredProperties.addSource(searchQuery, query -> filterProperties());
        filteredProperties.addSource(selectedPropertyType, type -> filterProperties());
        filteredProperties.addSource(selectedLocation, location -> filterProperties());
        filteredProperties.addSource(minPrice, price -> filterProperties());
        filteredProperties.addSource(maxPrice, price -> filterProperties());
    }

    private void filterProperties() {
        List<Property> properties = allProperties.getValue();
        if (properties == null) {
            filteredProperties.setValue(new ArrayList<>());
            return;
        }

        String query = searchQuery.getValue();
        String type = selectedPropertyType.getValue();
        String location = selectedLocation.getValue();
        Double min = minPrice.getValue();
        Double max = maxPrice.getValue();

        List<Property> filtered = properties.stream()
                .filter(property -> matchesSearchQuery(property, query))
                .filter(property -> matchesPropertyType(property, type))
                .filter(property -> matchesLocation(property, location))
                .filter(property -> matchesPriceRange(property, min, max))
                .collect(Collectors.toList());

        filteredProperties.setValue(filtered);
    }

    private boolean matchesSearchQuery(Property property, String query) {
        if (query == null || query.trim().isEmpty())
            return true;

        String lowerQuery = query.toLowerCase().trim();
        return property.getTitle().toLowerCase().contains(lowerQuery) ||
                property.getDescription().toLowerCase().contains(lowerQuery) ||
                property.getLocation().toLowerCase().contains(lowerQuery);
    }

    private boolean matchesPropertyType(Property property, String type) {
        return type == null || type.equals("All") || property.getType().equalsIgnoreCase(type);
    }

    private boolean matchesLocation(Property property, String location) {
        return location == null || location.equals("All") ||
                property.getLocation().toLowerCase().contains(location.toLowerCase());
    }

    private boolean matchesPriceRange(Property property, Double min, Double max) {
        if (min == null)
            min = 0.0;
        if (max == null)
            max = Double.MAX_VALUE;

        double discountedPrice = property.getPrice() * (1 - property.getDiscount() / 100);
        return discountedPrice >= min && discountedPrice <= max;
    }

    private void extractFilterOptions() {
        allProperties.observeForever(properties -> {
            if (properties != null && !properties.isEmpty()) {
                // Extract unique property types
                List<String> types = new ArrayList<>();
                types.add("All");
                types.addAll(properties.stream()
                        .map(Property::getType)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList()));
                propertyTypes.setValue(types);

                // Extract unique locations
                List<String> locs = new ArrayList<>();
                locs.add("All");
                locs.addAll(properties.stream()
                        .map(Property::getLocation)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList()));
                locations.setValue(locs);
            }
        });
    }

    // Public methods for UI interaction

    public LiveData<List<Property>> getFilteredProperties() {
        return filteredProperties;
    }

    public LiveData<Integer> getPropertiesCount() {
        return Transformations.map(filteredProperties, list -> list != null ? list.size() : 0);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<List<String>> getPropertyTypes() {
        return propertyTypes;
    }

    public LiveData<List<String>> getLocations() {
        return locations;
    }

    // Filter methods

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void setPropertyType(String type) {
        selectedPropertyType.setValue(type);
    }

    public void setLocation(String location) {
        selectedLocation.setValue(location);
    }

    public void setPriceRange(double min, double max) {
        minPrice.setValue(min);
        maxPrice.setValue(max);
    }

    public void addToFavorites(Property property, String userEmail) {
        // First check if the property is already in favorites
        favoriteRepository.isFavorite(userEmail, property.getPropertyId(), new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isAlreadyFavorite) {
                if (isAlreadyFavorite) {
                    errorMessage.postValue("Property is already in favorites");
                    return;
                }

                // Property is not in favorites, proceed to add it
                Favorite favorite = new Favorite(userEmail, property.getPropertyId());
                favoriteRepository.addFavorite(favorite, new RepositoryCallback<Favorite>() {
                    @Override
                    public void onSuccess() {
                        successMessage.postValue("Added to favorites successfully");
                    }

                    @Override
                    public void onError(Throwable t) {
                        errorMessage.postValue("Failed to add to favorites: " + t.getMessage());
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                errorMessage.postValue("Failed to check favorite status: " + t.getMessage());
            }
        });
    }

    public void removeFromFavorites(Property property, String userEmail) {
        Favorite favorite = new Favorite(userEmail, property.getPropertyId());
        favoriteRepository.deleteFavorite(favorite, new RepositoryCallback<Favorite>() {
            @Override
            public void onSuccess() {
                successMessage.postValue("Removed from favorites successfully");
            }

            @Override
            public void onError(Throwable t) {
                errorMessage.postValue("Failed to remove from favorites: " + t.getMessage());
            }
        });
    }

    public void checkFavoriteStatus(Property property, String userEmail,
            PropertyAdapter.FavoriteStatusCallback callback) {
        favoriteRepository.isFavorite(userEmail, property.getPropertyId(), new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isFavorite) {
                callback.onStatusReceived(isFavorite);
            }

            @Override
            public void onError(Throwable t) {
                // If there's an error checking status, assume not favorite
                callback.onStatusReceived(false);
            }
        });
    }

    // Factory for creating ViewModel with dependencies
    public static class Factory implements ViewModelProvider.Factory {
        private final PropertyRepository propertyRepository;
        private final FavoriteRepository favoriteRepository;

        public Factory(PropertyRepository propertyRepository, FavoriteRepository favoriteRepository) {
            this.propertyRepository = propertyRepository;
            this.favoriteRepository = favoriteRepository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(PropertiesViewModel.class)) {
                return (T) new PropertiesViewModel(propertyRepository, favoriteRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
