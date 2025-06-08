package com.example.realestate.ui.user.profile;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.exception.ValidationException;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.service.AuthenticationService;
import com.example.realestate.domain.service.Hashing;

import java.util.HashMap;
import java.util.Map;

public class ProfileManagementViewModel extends ViewModel {

    private final UserRepository userRepository;

    public enum UpdateState {
        IDLE, LOADING, SUCCESS, ERROR
    }

    // LiveData for UI state
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<UpdateState> updateState = new MutableLiveData<>(UpdateState.IDLE);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String[]> cities = new MutableLiveData<>();
    private final MutableLiveData<String> countryCode = new MutableLiveData<>();

    // Profile image URI
    private Uri profileImageUri;

    // Country and city data (same as registration)
    private final Map<String, String[]> countryCityMap = new HashMap<>();
    private final Map<String, String> countryCodeMap = new HashMap<>();

    public ProfileManagementViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        initializeCountryData();
    }

    // Public getters for LiveData
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }
    public LiveData<UpdateState> getUpdateState() {
        return updateState;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<String[]> getCities() {
        return cities;
    }
    public LiveData<String> getCountryCode() {
        return countryCode;
    }

    // Profile image URI setter
    public void setProfileImageUri(Uri uri) {
        this.profileImageUri = uri;
    }
    public Uri getProfileImageUri() {
        return profileImageUri;
    } // Load user profile

    public void loadUserProfile(String email) {
        updateState.postValue(UpdateState.LOADING);

        userRepository.getUserByEmail(email, new RepositoryCallback<>() {
            @Override
            public void onSuccess(User user) {
                currentUser.postValue(user);
                updateState.postValue(UpdateState.IDLE);

                // Set country and trigger city loading
                onCountrySelected(user.getCountry());
            }

            @Override
            public void onError(Throwable t) {
                errorMessage.postValue("Failed to load profile: " + t.getMessage());
                updateState.postValue(UpdateState.ERROR);
            }
        });
    } // Update profile information

    public void updateProfile(String firstName, String lastName, String phone,
            User.Gender gender, String country, String city) {

        User user = currentUser.getValue();
        if (user == null) {
            errorMessage.postValue("No user data available");
            return;
        }

        // Reset state
        updateState.postValue(UpdateState.LOADING);

        try {
            // Create updated user object
            User updatedUser = new User(
                    firstName, lastName, user.getEmail(), user.getPassword(),
                    phone, country, city, gender, user.isAdmin());

            // Preserve profile image
            updatedUser.setProfileImage(user.getProfileImage());

            // If a new profile image was selected, update it
            if (profileImageUri != null) {
                updatedUser.setProfileImage(profileImageUri.toString());
            }

            // Update in repository
            userRepository.updateUser(updatedUser, new RepositoryCallback<User>() {
                @Override
                public void onSuccess() {
                    currentUser.postValue(updatedUser);
                    updateState.postValue(UpdateState.SUCCESS);
                    profileImageUri = null; // Reset after successful update
                }

                @Override
                public void onError(Throwable t) {
                    errorMessage.postValue("Failed to update profile: " + t.getMessage());
                    updateState.postValue(UpdateState.ERROR);
                }
            });

        } catch (ValidationException e) {
            errorMessage.postValue(e.getMessage());
            updateState.postValue(UpdateState.ERROR);
        }
    }

    // Change password
    public void changePassword(String currentPassword, String newPassword, String confirmPassword) {
        User user = currentUser.getValue();
        if (user == null) {
            errorMessage.postValue("No user data available");
            return;
        } // Reset state
        updateState.postValue(UpdateState.LOADING);

        // Validate current password
        if (!Hashing.verifyPassword(currentPassword, user.getPassword())) {
            errorMessage.postValue("Current password is incorrect");
            updateState.postValue(UpdateState.ERROR);
            return;
        }

        // Validate new password
        if (!AuthenticationService.validatePassword(newPassword)) {
            errorMessage.postValue(
                    "New password must be at least 6 characters with uppercase, lowercase, digit, and special character");
            updateState.postValue(UpdateState.ERROR);
            return;
        }

        // Check password confirmation
        if (!newPassword.equals(confirmPassword)) {
            errorMessage.postValue("New passwords do not match");
            updateState.postValue(UpdateState.ERROR);
            return;
        }
        try {
            // Create updated user with new password
            User updatedUser = new User(
                    user.getFirstName(), user.getLastName(), user.getEmail(), newPassword,
                    user.getPhone(), user.getCountry(), user.getCity(), user.getGender(), user.isAdmin());

            // Preserve profile image
            updatedUser.setProfileImage(user.getProfileImage());

            // Hash the new password before storing
            updatedUser.hashAndSetPassword();

            // Update in repository
            userRepository.updateUser(updatedUser, new RepositoryCallback<User>() {
                @Override
                public void onSuccess() {
                    currentUser.postValue(updatedUser);
                    updateState.postValue(UpdateState.SUCCESS);
                }

                @Override
                public void onError(Throwable t) {
                    errorMessage.postValue("Failed to change password: " + t.getMessage());
                    updateState.postValue(UpdateState.ERROR);
                }
            });

        } catch (ValidationException e) {
            errorMessage.postValue(e.getMessage());
            updateState.postValue(UpdateState.ERROR);
        }
    }

    // Country and city handling
    public String[] getCountries() {
        return countryCityMap.keySet().toArray(new String[0]);
    }

    public void onCountrySelected(String country) {
        String[] countryCities = countryCityMap.get(country);
        if (countryCities != null) {
            cities.postValue(countryCities);
        }

        String code = countryCodeMap.get(country);
        if (code != null) {
            countryCode.postValue(code);
        }
    }

    private void initializeCountryData() {
        // Initialize country-city mapping (same as registration)
        countryCityMap.put("Palestine", new String[] { "Tulkarem", "Nablus", "Ramallah", "Jerusalem", "Gaza", "Hebron",
                "Bethlehem", "Jenin", "Qalqilya", "Salfit" });
        countryCityMap.put("Jordan", new String[] { "Amman", "Zarqa", "Irbid", "Russeifa", "Wadi as-Sir", "Aqaba",
                "Madaba", "As-Salt", "Al-Mafraq", "Jerash" });
        countryCityMap.put("Lebanon", new String[] { "Beirut", "Tripoli", "Sidon", "Tyre", "Jounieh", "Baalbek",
                "Byblos", "Zahle", "Aley", "Nabatieh" });
        countryCityMap.put("Syria", new String[] { "Damascus", "Aleppo", "Homs", "Latakia", "Hama", "Deir ez-Zor",
                "Raqqa", "Daraa", "Al-Hasakah", "Qamishli" });
        countryCityMap.put("Egypt", new String[] { "Cairo", "Alexandria", "Giza", "Luxor", "Aswan", "Sharm El Sheikh",
                "Hurghada", "Port Said", "Suez", "Mansoura" });

        // Initialize country codes
        countryCodeMap.put("Palestine", "970");
        countryCodeMap.put("Jordan", "962");
        countryCodeMap.put("Lebanon", "961");
        countryCodeMap.put("Syria", "963");
        countryCodeMap.put("Egypt", "20");
    }

    // Factory for ViewModel creation
    public static class Factory implements ViewModelProvider.Factory {
        private final UserRepository userRepository;

        public Factory(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ProfileManagementViewModel.class)) {
                return (T) new ProfileManagementViewModel(userRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}
