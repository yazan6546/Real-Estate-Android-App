package com.example.realestate.ui.login;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.service.Hashing;
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.domain.service.UserSession;

public class LoginViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final SharedPrefManager sharedPrefManager;

    public enum AuthState { IDLE, LOADING, SUCCESS, ERROR }

    private final MutableLiveData<AuthState> _authState = new MutableLiveData<>(AuthState.IDLE);
    public final LiveData<AuthState> authState = _authState;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;



    public LoginViewModel(UserRepository userRepository, SharedPrefManager sharedPrefManager,
                          boolean isRememberMeChecked) {
        this.userRepository = userRepository;
        this.sharedPrefManager = sharedPrefManager;
    }

    public void login(String email, String password, boolean isRememberMeChecked) {
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



                    if (isRememberMeChecked) {

                    } else {
                        // Clear saved credentials if "Remember Me" is not checked
                        sharedPrefManager.clearUserCredentials();
                    }
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
        private final SharedPrefManager sharedPrefManager;
        private final boolean isRememberMeChecked;

        public Factory(UserRepository userRepository, SharedPrefManager sharedPrefManager,
                       boolean isRememberMeChecked) {
            this.userRepository = userRepository;
            this.sharedPrefManager = sharedPrefManager;
            this.isRememberMeChecked = isRememberMeChecked;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LoginViewModel.class)) {
                boolean isRememberMeChecked = false;
                return (T) new LoginViewModel(userRepository, sharedPrefManager, isRememberMeChecked);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}