package com.example.realestate.ui.admin.special;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.PropertyRepository;
import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.domain.model.Property;

import java.util.ArrayList;
import java.util.List;

public class SpecialOffersViewModel extends ViewModel {

    private final PropertyRepository propertyRepository;
    private final MutableLiveData<List<Property>> properties = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public SpecialOffersViewModel(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
        loadProperties();
    }

    private void loadProperties() {
        propertyRepository.getAllProperties().observeForever(properties::setValue);
    }

    public LiveData<List<Property>> getProperties() {
        return properties;
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

    public void toggleOffer(Property property, double discountPercentage) {
        isLoading.setValue(true);
        
        // Calculate the correct original price and discounted price
        double originalPrice;
        double newPrice;
        
        if (discountPercentage > 0) {
            // When creating an offer:
            // If property currently has no discount, use current price as original
            // If property already has discount, keep the original price
            if (property.getDiscount() == 0) {
                originalPrice = property.getPrice(); // Current price becomes original
            } else {
                // Property already has discount, calculate back to original
                originalPrice = property.getPrice() / (1 - property.getDiscount() / 100);
            }
            // Calculate new discounted price
            newPrice = originalPrice * (1 - discountPercentage / 100);
        } else {
            // When removing offer, restore original price
            if (property.getDiscount() > 0) {
                originalPrice = property.getPrice() / (1 - property.getDiscount() / 100);
                newPrice = originalPrice; // No discount
            } else {
                originalPrice = property.getPrice();
                newPrice = property.getPrice();
            }
        }

        // Create updated property with correct pricing
        Property updatedProperty = new Property(
            property.getPropertyId(),
            property.getDescription(),
            property.getTitle(),
            property.getPrice(),
            property.getLocation(),
            property.getImageUrl(),
            property.getType(),
            property.getBedrooms(),
            property.getBathrooms(),
            property.getArea(),
            discountPercentage > 0, // is_featured becomes true when discount > 0
            discountPercentage
        );

        propertyRepository.updateProperty(updatedProperty, new RepositoryCallback<Property>() {
            @Override
            public void onSuccess(Property result) {
                isLoading.postValue(false);
                if (discountPercentage > 0) {
                    successMessage.postValue("Special offer created successfully!");
                } else {
                    successMessage.postValue("Special offer removed successfully!");
                }
            }

            @Override
            public void onError(Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Failed to update offer: " + t.getMessage());
            }
        });
    }

    // Factory for creating ViewModel
    public static class Factory implements ViewModelProvider.Factory {
        private final PropertyRepository propertyRepository;

        public Factory(PropertyRepository propertyRepository) {
            this.propertyRepository = propertyRepository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(SpecialOffersViewModel.class)) {
                return (T) new SpecialOffersViewModel(propertyRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
