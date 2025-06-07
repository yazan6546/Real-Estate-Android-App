package com.example.realestate.ui.admin.delete_customers;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.User;

import java.util.ArrayList;
import java.util.List;

public class DeleteCustomersViewModel extends ViewModel {
    private final UserRepository userRepository;
    private LiveData<List<User>> customers;
    private final MutableLiveData<Boolean> operationStatus = new MutableLiveData<>(false);

    public DeleteCustomersViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        // In a real app, we would load customers from the repository
        loadCustomers();
    }

    public LiveData<List<User>> getCustomers() {
        return customers;
    }

    public LiveData<Boolean> getOperationStatus() {
        return operationStatus;
    }

    public void loadCustomers() {
        customers = userRepository.getAllUsers();
    }

    public void deleteCustomer(User customer) {
        userRepository.deleteUser(customer, new RepositoryCallback<>() {

            @Override
            public void onSuccess() {
                operationStatus.setValue(true);
                // Reload customers after deletion
                loadCustomers();
            }

            @Override
            public void onError(Throwable throwable) {
                operationStatus.setValue(false);
            }
        });
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
            if (modelClass.isAssignableFrom(DeleteCustomersViewModel.class)) {
                return (T) new DeleteCustomersViewModel(userRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
