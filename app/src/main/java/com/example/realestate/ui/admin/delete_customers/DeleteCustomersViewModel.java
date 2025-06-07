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

    public enum AuthState { IDLE, LOADING, SUCCESS, ERROR }

    private final MutableLiveData<AuthState> _authState;

    private String _errorMessage;

    // Use LiveData directly from repository and filter customers
    public final LiveData<List<User>> customers;

    public DeleteCustomersViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        customers = userRepository.getAllNormalUsers();
        this._authState = new MutableLiveData<>(AuthState.IDLE);
        this._errorMessage = "Default error message";

    }

    public LiveData<List<User>> getCustomers() {
        return customers;
    }

    public LiveData<AuthState> getAuthState() {
        return _authState;
    }

    public String getErrorMessage() {
        return _errorMessage;
    }


    public void deleteCustomer(User customer) {
        _authState.postValue(AuthState.LOADING);

        userRepository.deleteUser(customer, new RepositoryCallback<>() {
            @Override
            public void onSuccess() {
                _authState.postValue(AuthState.SUCCESS);
            }

            @Override
            public void onError(Throwable t) {
                _errorMessage = t.getMessage();
                _authState.postValue(AuthState.ERROR);
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
