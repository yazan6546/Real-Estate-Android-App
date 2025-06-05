package com.example.realestate.ui.admin.properties;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.PropertyRepository;
import com.example.realestate.domain.model.Property;

import java.util.ArrayList;
import java.util.List;

public class ManagePropertiesViewModel extends ViewModel {
    private final PropertyRepository propertyRepository;
    private final MutableLiveData<List<Property>> properties = new MutableLiveData<>(new ArrayList<>());

    public ManagePropertiesViewModel(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
        // In a real app, we would load properties from the repository
    }

    public LiveData<List<Property>> getProperties() {
        return properties;
    }

    public void loadAllProperties() {
        // Temporary implementation - would normally load from repository
        properties.setValue(new ArrayList<>());
    }

    // Factory for creating ViewModel with dependencies
    public static class Factory implements ViewModelProvider.Factory {
        private final PropertyRepository propertyRepository;

        public Factory(PropertyRepository propertyRepository) {
            this.propertyRepository = propertyRepository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ManagePropertiesViewModel.class)) {
                return (T) new ManagePropertiesViewModel(propertyRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
