package com.example.realestate.ui.user.featured;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
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

public class FeaturedPropertiesViewModel extends ViewModel {

    private final PropertyRepository propertyRepository;
    private final FavoriteRepository favoriteRepository;

    // Original data sources
    private final LiveData<List<Property>> allProperties;

    // Featured properties (with discounts or marked as featured)
    private final MediatorLiveData<List<Property>> featuredProperties = new MediatorLiveData<>();

    // UI state
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public FeaturedPropertiesViewModel(PropertyRepository propertyRepository, FavoriteRepository favoriteRepository) {
        this.propertyRepository = propertyRepository;
        this.favoriteRepository = favoriteRepository;

        // Get all properties from repository
        this.allProperties = propertyRepository.getAllProperties();

        // Set up featured properties filtering
        setupFeaturedProperties();
    }

    private void setupFeaturedProperties() {
        featuredProperties.addSource(allProperties, this::filterFeaturedProperties);
    }

    private void filterFeaturedProperties(List<Property> properties) {
        if (properties == null) {
            featuredProperties.setValue(new ArrayList<>());
            return;
        }

        // Filter properties that have discounts > 0 OR are marked as featured
        List<Property> filtered = properties.stream()
                .filter(property -> property.getDiscount() > 0 || property.isFeatured())
                .collect(Collectors.toList());

        featuredProperties.setValue(filtered);
    }

    // Public methods for UI interaction

    public LiveData<List<Property>> getFeaturedProperties() {
        return featuredProperties;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
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
                    public void onSuccess(Favorite result) {
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
            public void onSuccess(Favorite result) {
                successMessage.postValue("Removed from favorites successfully");
            }

            @Override
            public void onError(Throwable t) {
                errorMessage.postValue("Failed to remove from favorites: " + t.getMessage());
            }
        });
    }

    public void checkFavoriteStatus(Property property, String userEmail,
            com.example.realestate.ui.user.properties.PropertyAdapter.FavoriteStatusCallback callback) {
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
            if (modelClass.isAssignableFrom(FeaturedPropertiesViewModel.class)) {
                return (T) new FeaturedPropertiesViewModel(propertyRepository, favoriteRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
