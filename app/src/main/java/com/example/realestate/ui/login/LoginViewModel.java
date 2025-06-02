package com.example.realestate.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.service.Hashing;

public class LoginViewModel extends ViewModel {
    private final UserRepository userRepository;

    public enum AuthState { IDLE, LOADING, SUCCESS, ERROR }

    private final MutableLiveData<AuthState> _authState = new MutableLiveData<>(AuthState.IDLE);
    public final LiveData<AuthState> authState = _authState;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;

    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.setValue("Email and password cannot be empty");
            _authState.setValue(AuthState.ERROR);
            return;
        }

        _authState.setValue(AuthState.LOADING);

        userRepository.getUserByEmail(email, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user != null && Hashing.verifyPassword(password, user.getPassword())) {
                    // Password matches
                    _authState.postValue(AuthState.SUCCESS);
                    // Save logged in user to session or preferences here
                } else {
                    // Invalid credentials
                    _errorMessage.postValue("Invalid email or password");
                    _authState.postValue(AuthState.ERROR);
                }
            }

            @Override
            public void onError(Exception e) {
                _errorMessage.postValue("Login failed: " + e.getMessage());
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

        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LoginViewModel.class)) {
                return (T) new LoginViewModel(userRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}