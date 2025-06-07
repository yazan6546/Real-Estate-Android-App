package com.example.realestate.ui.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.exception.ValidationException;
import com.example.realestate.domain.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// 1. Create a base ViewModel class with common registration logic
public abstract class BaseRegistrationViewModel extends ViewModel {
    protected final UserRepository userRepository;

    public enum RegisterState {IDLE, LOADING, SUCCESS, ERROR}

    // Common data structures for countries and cities
    protected final Map<String, String[]> countriesWithCities = new HashMap<>() {{
        put("PS", new String[]{"Nablus", "Tulkarem", "Ramallah"});
        put("UK", new String[]{"London", "Manchester", "Birmingham"});
        put("UAE", new String[]{"Dubai", "Abu Dhabi", "Sharjah"});
    }};

    protected final Map<String, String> countryCodeMap = new HashMap<>() {{
        put("PS", "+970");
        put("UK", "+44");
        put("UAE", "+971");
    }};

    protected final MutableLiveData<RegisterState> _registerState = new MutableLiveData<>(RegisterState.IDLE);
    public final LiveData<RegisterState> registerState = _registerState;

    protected final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;

    protected final MutableLiveData<String[]> _cities = new MutableLiveData<>();
    public final LiveData<String[]> cities = _cities;

    protected final MutableLiveData<String> _countryCode = new MutableLiveData<>();
    public final LiveData<String> countryCode = _countryCode;

    protected BaseRegistrationViewModel(UserRepository userRepository) {
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

    // The shared registration logic, with isAdmin parameter
    protected void registerUser(String email, String password, String confirmPassword,
                                String firstName, String lastName, User.Gender gender,
                                String country, String city, String phone, boolean isAdmin) {
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
            user = new User(
                    firstName,
                    lastName,
                    email,
                    password,
                    phone,
                    country,
                    city,
                    gender,
                    isAdmin);  // Set isAdmin based on parameter

        } catch (ValidationException e) {
            _errorMessage.setValue(e.getMessage());
            _registerState.setValue(RegisterState.ERROR);
            return;
        }

        _registerState.setValue(RegisterState.LOADING);

        // Check if email exists
        userRepository.getUserByEmail(email, new RepositoryCallback<>() {
            @Override
            public void onSuccess(User existingUser) {
                _errorMessage.postValue("Email is already registered");
                _registerState.postValue(RegisterState.ERROR);
            }

            @Override
            public void onError(Throwable t) {
                if (Objects.equals(t.getMessage(), "User not found")) {
                    userRepository.insertUser(user);
                    _registerState.postValue(RegisterState.SUCCESS);
                } else {
                    _errorMessage.postValue("Registration failed: " + t.getMessage());
                    _registerState.postValue(RegisterState.ERROR);
                }
            }
        });
    }
}
