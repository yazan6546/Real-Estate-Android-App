package com.example.realestate.ui.admin.delete_customers;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class DeleteCustomersViewModel extends ViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<Boolean> _operationStatus = new MutableLiveData<>();
    public final LiveData<Boolean> operationStatus = _operationStatus;

    // Use LiveData directly from repository and filter customers
    public final LiveData<List<User>> customers;

    public DeleteCustomersViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;

        // Transform the LiveData to filter out admin users
        this.customers = Transformations.map(userRepository.getAllUsers(), users -> {
            if (users != null) {
                return users.stream()
                        .filter(user -> !user.isAdmin())
                        .collect(Collectors.toList());
            }
            return null;
        });
    }

    public LiveData<List<User>> getCustomers() {
        return customers;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getOperationStatus() {
        return operationStatus;
    }

    public void loadCustomers() {
        // No need to manually load since we're using LiveData
        // The customers LiveData will automatically update when data changes
    }

    public void deleteCustomer(User customer) {
        _isLoading.setValue(true);

        userRepository.deleteUser(customer, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User result) {
                _isLoading.setValue(false);
                _operationStatus.setValue(true);
                // No need to manually reload - LiveData will automatically update
            }

            @Override
            public void onError(Throwable t) {
                _isLoading.setValue(false);
                _operationStatus.setValue(false);
            }
        });
    }

    // Factory for ViewModel creation with dependencies
    public static class Factory implements ViewModelProvider.Factory {
        private final UserRepository userRepository;

        public Factory(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(DeleteCustomersViewModel.class)) {
                return (T) new DeleteCustomersViewModel(userRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}
