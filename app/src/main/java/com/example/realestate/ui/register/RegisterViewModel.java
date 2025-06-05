package com.example.realestate.ui.register;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.exception.ValidationException;
import com.example.realestate.domain.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterViewModel extends ViewModel {
    private final UserRepository userRepository;

    public enum RegisterState {IDLE, LOADING, SUCCESS, ERROR}

    // List of countries and their cities
    private final Map<String, String[]> countriesWithCities = new HashMap<>() {{
        put("PS", new String[]{"Nablus", "Tulkarem", "Ramallah"});
        put("UK", new String[]{"London", "Manchester", "Birmingham"});
        put("UAE", new String[]{"Dubai", "Abu Dhabi", "Sharjah"});
    }};

    // Country code map
    private final Map<String, String> countryCodeMap = new HashMap<>() {{
        put("PS", "+970");
        put("UK", "+44");
        put("UAE", "+971");
    }};

    private final MutableLiveData<RegisterState> _registerState = new MutableLiveData<>(RegisterState.IDLE);
    public final LiveData<RegisterState> registerState = _registerState;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<String[]> _cities = new MutableLiveData<>();
    public final LiveData<String[]> cities = _cities;

    private final MutableLiveData<String> _countryCode = new MutableLiveData<>();
    public final LiveData<String> countryCode = _countryCode;

    public RegisterViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        // Set default country and cities
        String defaultCountry = "PS";
        _cities.setValue(countriesWithCities.get(defaultCountry));
        _countryCode.setValue(countryCodeMap.get(defaultCountry));
    }

    public String[] getCountries() {
        return countriesWithCities.keySet().toArray(new String[0]);
    }

    public void onCountrySelected(String country) {
        _cities.setValue(countriesWithCities.get(country));
        _countryCode.setValue(countryCodeMap.get(country));
    }

    public void register(String email, String password, String confirmPassword,
                         String firstName, String lastName, User.Gender gender,
                         String country, String city, String phone) {
        // Reset state
        _registerState.setValue(RegisterState.IDLE);

        // Check password confirmation first (not handled by User model)
        if (!password.equals(confirmPassword)) {
            _errorMessage.setValue("Passwords do not match");
            _registerState.setValue(RegisterState.ERROR);
            return;
        }

        User user;

        try {
            // Create a single User object - its constructor and setters will handle validation
            user = new User(
                    firstName,
                    lastName,
                    email,
                    password,
                    phone,
                    country,
                    city,
                    gender,
                    false);

        } catch (ValidationException e) {
            _errorMessage.setValue(e.getMessage());
            _registerState.setValue(RegisterState.ERROR);
            return;
        }

        // If we reach here, the user object is valid
        _registerState.setValue(RegisterState.LOADING);

        // Check if email exists
        userRepository.getUserByEmail(email, new RepositoryCallback<>() {
            @Override
            public void onSuccess(User existingUser) {
                // User already exists
                _errorMessage.postValue("Email is already registered");
                _registerState.postValue(RegisterState.ERROR);
            }

            @Override
            public void onError(Throwable t) {
                if (Objects.equals(t.getMessage(), "User not found")) {
                    // This is good - email doesn't exist
                    userRepository.insertUser(user);
                    _registerState.postValue(RegisterState.SUCCESS);
                } else {
                    // Some other error occurred
                    _errorMessage.postValue("Registration failed: " + t.getMessage());
                    _registerState.postValue(RegisterState.ERROR);
                }
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
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
                return (T) new RegisterViewModel(userRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
