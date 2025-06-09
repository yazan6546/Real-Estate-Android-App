package com.example.realestate.ui.user.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.service.SharedPrefManager;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileManagementFragment extends Fragment {

    private ProfileManagementViewModel viewModel;

    // UI Components - Profile Information
    private ImageView profileImageView;
    private Button changeProfileImageButton;
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText emailInput;
    private TextInputEditText phoneInput;
    private TextView countryCodeText;
    private Spinner genderSpinner;
    private Spinner countrySpinner;
    private Spinner citySpinner;

    // UI Components - Password Change
    private TextInputEditText currentPasswordInput;
    private TextInputEditText newPasswordInput;
    private TextInputEditText confirmPasswordInput;

    // UI Components - Actions
    private Button saveProfileButton;
    private Button changePasswordButton;
    private ProgressBar progressBar;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Initialize activity result launchers
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            // Display the image
                            Glide.with(requireContext())
                                    .load(imageUri)
                                    .placeholder(R.drawable.ic_person)
                                    .into(profileImageView);

                            // Set the image URI in the ViewModel for later use
                            viewModel.setProfileImageUri(imageUri);
                        } else {
                            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(requireContext(), "Permission denied to access gallery", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());

        // Initialize ViewModel with application context
        viewModel = new ViewModelProvider(this,
                new ProfileManagementViewModel.Factory(
                        requireActivity().getApplication(),
                        RealEstate.appContainer.getUserRepository(),
                        sharedPrefManager))
                .get(ProfileManagementViewModel.class);

        initializeViews(view);
        setupSpinners();
        setupButtons();
        observeViewModel();
    }

    private void initializeViews(View view) {
        // Profile Information
        profileImageView = view.findViewById(R.id.profileImageView);

        File imageFile = new File(requireContext().getFilesDir(),
                Objects.requireNonNull(viewModel.getCurrentUser().getValue()).getProfileImage());

        Glide.with(requireContext())
                .load(imageFile.exists() ? imageFile : R.drawable.ic_person)
                .placeholder(R.drawable.ic_person)
                .into(profileImageView);

        changeProfileImageButton = view.findViewById(R.id.changeProfileImageButton);
        firstNameInput = view.findViewById(R.id.firstNameInput);
        lastNameInput = view.findViewById(R.id.lastNameInput);
        emailInput = view.findViewById(R.id.emailInput);
        phoneInput = view.findViewById(R.id.phoneInput);
        countryCodeText = view.findViewById(R.id.countryCodeText);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        countrySpinner = view.findViewById(R.id.countrySpinner);
        citySpinner = view.findViewById(R.id.citySpinner);

        // Password Change
        currentPasswordInput = view.findViewById(R.id.currentPasswordInput);
        newPasswordInput = view.findViewById(R.id.newPasswordInput);
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);

        // Actions
        saveProfileButton = view.findViewById(R.id.saveProfileButton);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupSpinners() {
        // Gender Spinner
        List<String> genderOptions = new ArrayList<>();
        genderOptions.add("Male");
        genderOptions.add("Female");
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, genderOptions);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Country Spinner
        String[] countries = viewModel.getCountries();
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        // Setup country selection listener for city updates
        countrySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = countries[position];
                viewModel.onCountrySelected(selectedCountry);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void setupButtons() {
        changeProfileImageButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        saveProfileButton.setOnClickListener(v -> saveProfile());

        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveProfile() {
        String firstName = firstNameInput.getText() != null ? firstNameInput.getText().toString().trim() : "";
        String lastName = lastNameInput.getText() != null ? lastNameInput.getText().toString().trim() : "";
        String phone = phoneInput.getText() != null ? phoneInput.getText().toString().trim() : "";

        User.Gender gender = genderSpinner.getSelectedItemPosition() == 0 ? User.Gender.MALE : User.Gender.FEMALE;
        String country = (String) countrySpinner.getSelectedItem();
        String city = (String) citySpinner.getSelectedItem();

        viewModel.updateProfile(firstName, lastName, phone, gender, country, city);
    }

    private void changePassword() {
        String currentPassword = currentPasswordInput.getText() != null ? currentPasswordInput.getText().toString()
                : "";
        String newPassword = newPasswordInput.getText() != null ? newPasswordInput.getText().toString() : "";
        String confirmPassword = confirmPasswordInput.getText() != null ? confirmPasswordInput.getText().toString()
                : "";

        viewModel.changePassword(currentPassword, newPassword, confirmPassword);
    }

    private void observeViewModel() {
        // Observe cities based on country selection
        viewModel.getCities().observe(getViewLifecycleOwner(), cities -> {
            if (cities != null && cities.length > 0) {
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, cities);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(cityAdapter);
            }
        });

        // Observe country code
        viewModel.getCountryCode().observe(getViewLifecycleOwner(), code -> {
            if (code != null) {
                countryCodeText.setText("+" + code);
            }
        });

        // Observe current user data
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                populateUserData(user);
            }
        });

        // Observe update state
        viewModel.getUpdateState().observe(getViewLifecycleOwner(), state -> {
            progressBar
                    .setVisibility(state == ProfileManagementViewModel.UpdateState.LOADING ? View.VISIBLE : View.GONE);

            if (state == ProfileManagementViewModel.UpdateState.SUCCESS) {
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                clearPasswordFields();
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateUserData(User user) {
        firstNameInput.setText(user.getFirstName());
        lastNameInput.setText(user.getLastName());
        emailInput.setText(user.getEmail());
        phoneInput.setText(user.getPhone());

        // Set gender spinner
        if (user.getGender() == User.Gender.FEMALE) {
            genderSpinner.setSelection(1);
        } else {
            genderSpinner.setSelection(0);
        }

        // Set country spinner
        String[] countries = viewModel.getCountries();
        for (int i = 0; i < countries.length; i++) {
            if (countries[i].equals(user.getCountry())) {
                countrySpinner.setSelection(i);
                break;
            }
        }

        // Email should be read-only for security
        emailInput.setEnabled(false); // Load profile image if available
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            // Use Glide to load the profile image
        } else {
            // Use default placeholder if no profile image is set
            profileImageView.setImageResource(R.drawable.ic_person);
        }
    }

    private void clearPasswordFields() {
        currentPasswordInput.setText("");
        newPasswordInput.setText("");
        confirmPasswordInput.setText("");
    }
}
