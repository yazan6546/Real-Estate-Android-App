package com.example.realestate.ui.user.favorites;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.domain.model.Property;
import com.example.realestate.domain.model.Favorite;
import com.example.realestate.data.repository.PropertyRepository;
import com.example.realestate.data.repository.FavoriteRepository;
import com.example.realestate.data.repository.RepositoryCallback;

import java.util.List;
import java.util.ArrayList;

public class FavoritesViewModel extends ViewModel {
    private final PropertyRepository propertyRepository;
    private final FavoriteRepository favoriteRepository;
    private final MutableLiveData<List<Property>> favoriteProperties = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public FavoritesViewModel(PropertyRepository propertyRepository, FavoriteRepository favoriteRepository) {
        this.propertyRepository = propertyRepository;
        this.favoriteRepository = favoriteRepository;
        isLoading.setValue(false);
    }

    public LiveData<List<Property>> getFavoriteProperties() {
        return favoriteProperties;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void loadFavoriteProperties(String userEmail) {
        isLoading.setValue(true);

        // Get favorites from repository - this returns LiveData
        favoriteRepository.getFavoritesByEmail(userEmail).observeForever(favorites -> {
            if (favorites == null || favorites.isEmpty()) {
                favoriteProperties.postValue(new ArrayList<>());
                isLoading.postValue(false);
                return;
            }

            // Get property details for each favorite
            List<Property> properties = new ArrayList<>();
            final int totalFavorites = favorites.size();
            final int[] processedCount = { 0 };

            for (Favorite favorite : favorites) {
                // Get property by ID - this also returns LiveData
                propertyRepository.getPropertyById(favorite.getPropertyId()).observeForever(property -> {
                    if (property != null) {
                        properties.add(property);
                    }
                    processedCount[0]++;

                    if (processedCount[0] == totalFavorites) {
                        favoriteProperties.postValue(properties);
                        isLoading.postValue(false);
                        errorMessage.postValue(null);
                    }
                });
            }
        });
    }

    public void removeFromFavorites(Property property, String userEmail) {
        Favorite favorite = new Favorite(userEmail, property.getPropertyId());
        favoriteRepository.deleteFavorite(favorite, new RepositoryCallback<Favorite>() {
            @Override
            public void onSuccess(Favorite result) {
                successMessage.postValue("Removed from favorites successfully");
                // Update the list by removing the property
                List<Property> currentFavorites = favoriteProperties.getValue();
                if (currentFavorites != null) {
                    currentFavorites.removeIf(p -> p.getPropertyId() == property.getPropertyId());
                    favoriteProperties.postValue(currentFavorites);
                }
            }

            @Override
            public void onError(Throwable t) {
                errorMessage.postValue("Failed to remove from favorites: " + t.getMessage());
            }
        });
    }

    public void addToFavorites(Property property, String userEmail) {
        Favorite favorite = new Favorite(userEmail, property.getPropertyId());
        favoriteRepository.addFavorite(favorite, new RepositoryCallback<Favorite>() {
            @Override
            public void onSuccess(Favorite result) {
                // Reload favorites to get updated list
                loadFavoriteProperties(userEmail);
            }

            @Override
            public void onError(Throwable t) {
                errorMessage.postValue("Failed to add to favorites: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // No executor to clean up
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
            if (modelClass.isAssignableFrom(FavoritesViewModel.class)) {
                return (T) new FavoritesViewModel(propertyRepository, favoriteRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
