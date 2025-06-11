package com.example.realestate.ui.user.profile;


import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.exception.ValidationException;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.service.CountryService;
import com.example.realestate.domain.service.Hashing;
import com.example.realestate.domain.service.SharedPrefManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProfileManagementViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final SharedPrefManager sharedPrefManager;

    public enum UpdateState {
        IDLE, LOADING, SUCCESS, ERROR
    }

    // LiveData for UI state
    private LiveData<User> currentUser;
    private final MutableLiveData<UpdateState> updateState = new MutableLiveData<>(UpdateState.IDLE);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String[]> cities = new MutableLiveData<>();
    private final MutableLiveData<String> countryCode = new MutableLiveData<>();

    // Profile image URI
    private Uri profileImageUri;


    public ProfileManagementViewModel(Application application, UserRepository userRepository, SharedPrefManager sharedPrefManager) {
        super(application);
        this.userRepository = userRepository;
        this.sharedPrefManager = sharedPrefManager;
    }

    // Public getters for LiveData
    public LiveData<User> getCurrentUser() {

        if (currentUser == null && sharedPrefManager.getCurrentUserEmail() == null) {
            errorMessage.postValue("No user is currently logged in");
            updateState.postValue(UpdateState.ERROR);
            return null;
        }

        if (currentUser == null) {
            String email = sharedPrefManager.getCurrentUserEmail();
            currentUser = userRepository.getUserByEmailLive(email);
        }
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
    }

    // Reset update state to IDLE
    public void resetUpdateState() {
        updateState.postValue(UpdateState.IDLE);
    }

    // Load user profile
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
                // Save the image to internal storage and get the filename
                String imageFileName = saveImageToInternalStorage(profileImageUri);
                if (imageFileName != null) {
                    updatedUser.setProfileImage(imageFileName);
                } else {
                    errorMessage.postValue("Failed to save profile image");
                    updateState.postValue(UpdateState.ERROR);
                    return;
                }
            }

            // Update in repository
            userRepository.updateUser(updatedUser, false, new RepositoryCallback<>() {
                @Override
                public void onSuccess() {
                    updateState.postValue(UpdateState.SUCCESS);
                    sharedPrefManager.saveUserSession(updatedUser);
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

            // Update in repository
            userRepository.updateUser(updatedUser, new RepositoryCallback<>() {
                @Override
                public void onSuccess() {
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
        return CountryService.countriesWithCities.keySet().toArray(new String[0]);
    }

    public void onCountrySelected(String country) {
        String[] countryCities = CountryService.countriesWithCities.get(country);
        if (countryCities != null) {
            cities.postValue(countryCities);
        }

        String code = CountryService.countryCodeMap.get(country);
        if (code != null) {
            countryCode.postValue(code);
        }
    }


    /**
     * Saves the selected image to internal storage and returns the filename
     * @param imageUri The URI of the image to save
     * @return The filename of the saved image (relative to the app's files directory)
     */
    public String saveImageToInternalStorage(Uri imageUri) {
        Context context = getApplication().getApplicationContext();
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";

        try {
            // Open the input stream from the URI
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                return null;
            }

            // Create file in internal storage
            File file = new File(context.getFilesDir(), fileName);

            // Create an output stream to write the image
            OutputStream outputStream = new FileOutputStream(file);

            // Copy the data
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close streams properly to prevent resource leaks
            outputStream.close();
            inputStream.close();

            // Return just the filename, which can be used to construct the full path later
            return fileName;
        } catch (IOException e) {
            Log.e("Error saving image to internal storage", e.getMessage());
            return null;
        }
    }

    // Factory for ViewModel creation
    public static class Factory implements ViewModelProvider.Factory {
        private final Application application;
        private final UserRepository userRepository;
        private final SharedPrefManager sharedPrefManager;

        public Factory(Application application, UserRepository userRepository, SharedPrefManager sharedPrefManager) {
            this.application = application;
            this.userRepository = userRepository;
            this.sharedPrefManager = sharedPrefManager;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ProfileManagementViewModel.class)) {
                return (T) new ProfileManagementViewModel(application, userRepository, sharedPrefManager);
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}
