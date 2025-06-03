package com.example.realestate.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.mapper.UserMapper;
import com.example.realestate.domain.service.AuthenticationService;
import com.example.realestate.domain.service.Hashing;

import java.util.HashMap;
import java.util.Map;

public class RegisterViewModel extends ViewModel {
    private final UserRepository userRepository;

    public enum RegisterState { IDLE, LOADING, SUCCESS, ERROR }

    // List of countries and their cities
    private final Map<String, String[]> countriesWithCities = new HashMap<String, String[]>() {{
        put("USA", new String[]{"New York", "Los Angeles", "Chicago"});
        put("UK", new String[]{"London", "Manchester", "Birmingham"});
        put("UAE", new String[]{"Dubai", "Abu Dhabi", "Sharjah"});
    }};

    // Country code map
    private final Map<String, String> countryCodeMap = new HashMap<String, String>() {{
        put("USA", "+1");
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
        String defaultCountry = "USA";
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

        // Reset error state
        _registerState.setValue(RegisterState.IDLE);

        // First do all validations
        if (!validateRegistrationInput(email, password, confirmPassword, firstName, lastName, phone)) {
            _registerState.setValue(RegisterState.ERROR);
            return;
        }

        _registerState.setValue(RegisterState.LOADING);

        // Check if user already exists
        userRepository.getUserByEmail(email, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                // User with this email already exists
                _errorMessage.postValue("Email is already registered");
                _registerState.postValue(RegisterState.ERROR);
            }

            @Override
            public void onError(Exception e) {
                if (e.getMessage().equals("User not found")) {
                    // This is good - we can create a new user
                    createNewUser(email, password, firstName, lastName, gender, country, city, phone);
                } else {
                    // Some other error occurred
                    _errorMessage.postValue("Registration failed: " + e.getMessage());
                    _registerState.postValue(RegisterState.ERROR);
                }
            }
        });
    }

    private void createNewUser(String email, String password, String firstName,
                              String lastName, User.Gender gender, String country,
                              String city, String phone) {
        try {
            // Hash the password before storing
            String hashedPassword = Hashing.createPasswordHash(password);

            // Create a User object with the basic information
            User newUser = new User(
                firstName,
                lastName,
                email,
                hashedPassword,
                phone,
                country,
                city,
                false // Default to non-admin
            );

            // Set additional fields that aren't in the constructor
            newUser.setCity(city);
            newUser.setGender(gender);

            // We'll use UserMapper to convert our User object to UserEntity
            // The mapper should handle converting the gender
            userRepository.insertUser(UserMapper.toEntity(newUser));

            // Registration successful
            _registerState.postValue(RegisterState.SUCCESS);
        } catch (Exception e) {
            _errorMessage.postValue("Failed to create account: " + e.getMessage());
            _registerState.postValue(RegisterState.ERROR);
        }
    }

    private boolean validateRegistrationInput(String email, String password, String confirmPassword,
                                             String firstName, String lastName, String phone) {
        // Validate email
        if (!AuthenticationService.validateEmail(email)) {
            _errorMessage.setValue("Please enter a valid email address");
            return false;
        }

        // Validate password
        if (!AuthenticationService.validatePassword(password)) {
            _errorMessage.setValue("Password must be at least 6 characters and include one letter, one number, and one special character");
            return false;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            _errorMessage.setValue("Passwords do not match");
            return false;
        }

        // Validate first name
        if (!AuthenticationService.validateName(firstName)) {
            _errorMessage.setValue("First name must be at least 3 characters");
            return false;
        }

        // Validate last name
        if (!AuthenticationService.validateName(lastName)) {
            _errorMessage.setValue("Last name must be at least 3 characters");
            return false;
        }

        // Validate phone
        if (!AuthenticationService.validatePhone(phone)) {
            _errorMessage.setValue("Please enter a valid 10-digit phone number");
            return false;
        }

        return true;
    }

    // Factory for creating ViewModel with dependencies
    public static class Factory implements ViewModelProvider.Factory {
        private final UserRepository userRepository;

        public Factory(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

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
