package com.example.realestate.ui.user.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.PropertyRepository;
import com.example.realestate.data.repository.UserRepository;

public class HomeViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    private final LiveData<Integer> userCount;
    private final LiveData<Integer> propertyCount;

    public HomeViewModel(UserRepository userRepository, PropertyRepository propertyRepository) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;

        // Get counts from repositories
        this.userCount = userRepository.getUserCount();
        this.propertyCount = propertyRepository.getPropertyCount();
    }

    public LiveData<Integer> getUserCount() {
        return userCount;
    }

    public LiveData<Integer> getPropertyCount() {
        return propertyCount;
    }

    // Factory for creating ViewModels with dependencies
    public static class Factory implements ViewModelProvider.Factory {
        private final UserRepository userRepository;
        private final PropertyRepository propertyRepository;

        public Factory(UserRepository userRepository, PropertyRepository propertyRepository) {
            this.userRepository = userRepository;
            this.propertyRepository = propertyRepository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(HomeViewModel.class)) {
                return (T) new HomeViewModel(userRepository, propertyRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}