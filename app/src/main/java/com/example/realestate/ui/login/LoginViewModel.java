package com.example.realestate.ui.login;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.service.Hashing;
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.domain.service.UserSession;

public class LoginViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final SharedPrefManager sharedPrefManager;

    public enum AuthState { IDLE, LOADING, SUCCESS, ERROR }

    private final MutableLiveData<AuthState> _authState;

    private String _errorMessage;
    public UserSession userSession = null;



    public LoginViewModel(UserRepository userRepository, SharedPrefManager sharedPrefManager) {
        this.userRepository = userRepository;
        this.sharedPrefManager = sharedPrefManager;

        this._authState = new MutableLiveData<>(AuthState.IDLE);
        this._errorMessage = "Default error message";
    }

    public LiveData<AuthState> getAuthState() {
        return _authState;
    }

    public String getErrorMessage() {
        return _errorMessage;
    }

    public void login(String email, String password, boolean isRememberMeChecked) {
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage = "Email and password cannot be empty";
            _authState.setValue(AuthState.ERROR);
            return;
        }

        _authState.setValue(AuthState.LOADING);

        userRepository.getUserByEmail(email, new RepositoryCallback<>() {
            @Override
            public void onSuccess(User user) {
                if (user != null && Hashing.verifyPassword(password, user.getPassword())) {
                    // Password matches
                    _authState.postValue(AuthState.SUCCESS);

                    userSession = new UserSession(
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.isAdmin()
                    );

                    if (isRememberMeChecked) {
                        // Log in the user without password next time
                        userSession.setRememberMe(true);
                        // Save user credentials to SharedPreferences
                        sharedPrefManager.writeObject("user_session", userSession);

                    } else {
                        // Clear saved credentials if "Remember Me" is not checked
                        sharedPrefManager.writeObject("user_session", userSession);
                    }
                    // Save logged in user to session or preferences here
                } else {
                    // Invalid credentials
                    _errorMessage = ("Invalid email or password");
                    _authState.postValue(AuthState.ERROR);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("LoginViewModel", "Login failed", t);
                _errorMessage = t.getMessage();
                _authState.postValue(AuthState.ERROR);
            }
        });
    }

    // Factory for ViewModel creation with dependencies
    public static class Factory implements ViewModelProvider.Factory {
        private final UserRepository userRepository;
        private final SharedPrefManager sharedPrefManager;

        public Factory(UserRepository userRepository, SharedPrefManager sharedPrefManager) {
            this.userRepository = userRepository;
            this.sharedPrefManager = sharedPrefManager;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LoginViewModel.class)) {
                return (T) new LoginViewModel(userRepository, sharedPrefManager);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}