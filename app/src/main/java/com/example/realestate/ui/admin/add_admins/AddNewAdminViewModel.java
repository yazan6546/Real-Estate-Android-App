package com.example.realestate.ui.admin.add_admins;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.exception.ValidationException;
import com.example.realestate.domain.model.User;

public class AddNewAdminViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> operationStatus = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AddNewAdminViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Boolean> getOperationStatus() {
        return operationStatus;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void addNewAdmin(String email, String password, String firstName, String lastName,
                           String phone, String country, String city, User.Gender gender) {
        try {
            // Create a new user with admin privileges
            User newAdmin = new User(
                    firstName,
                    lastName,
                    email,
                    password,
                    phone,
                    country,
                    city,
                    gender,
                    true  // isAdmin = true
            );

            // In a real implementation, we would check if the email exists first
            // and handle any errors

            // Temporary placeholder - just report success
            operationStatus.setValue(true);

        } catch (ValidationException e) {
            errorMessage.setValue(e.getMessage());
            operationStatus.setValue(false);
        }
    }

    // Factory for creating ViewModel with dependencies
    public static class Factory implements ViewModelProvider.Factory {
        private final UserRepository userRepository;

        public Factory(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AddNewAdminViewModel.class)) {
                return (T) new AddNewAdminViewModel(userRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
