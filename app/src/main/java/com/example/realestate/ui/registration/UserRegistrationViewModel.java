package com.example.realestate.ui.registration;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.User;
import com.example.realestate.ui.base.BaseRegistrationViewModel;

public class UserRegistrationViewModel extends BaseRegistrationViewModel {

    public UserRegistrationViewModel(UserRepository userRepository) {
        super(userRepository);
    }

    public void register(String email, String password, String confirmPassword,
                         String firstName, String lastName, User.Gender gender,
                         String country, String city, String phone) {
        // Call the base method with isAdmin = false
        registerUser(email, password, confirmPassword, firstName, lastName,
                gender, country, city, phone, false);
    }

    // Factory for user registration
    public static class Factory implements ViewModelProvider.Factory {
        private final UserRepository userRepository;

        public Factory(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(UserRegistrationViewModel.class)) {
                return (T) new UserRegistrationViewModel(userRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}