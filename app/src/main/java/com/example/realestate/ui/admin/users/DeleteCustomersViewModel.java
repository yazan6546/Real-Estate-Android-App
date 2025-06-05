package com.example.realestate.ui.admin.users;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.User;

import java.util.ArrayList;
import java.util.List;

public class DeleteCustomersViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<List<User>> customers = new MutableLiveData<>(new ArrayList<>());
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
        // Temporary implementation - would normally load from repository
        customers.setValue(new ArrayList<>());
    }

    public void deleteCustomer(User customer) {
        // Temporary implementation - would normally call repository
        // For now, just simulate success
        operationStatus.setValue(true);
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
