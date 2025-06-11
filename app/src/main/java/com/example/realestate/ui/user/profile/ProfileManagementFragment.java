package com.example.realestate.ui.user.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.realestate.DashboardActivity;
import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.service.SharedPrefManager;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProfileManagementFragment extends Fragment {

    private ProfileManagementViewModel viewModel;

    // Counter for success toast to prevent showing when fragment is recreated
    private static final AtomicInteger successToastCounter = new AtomicInteger(0);

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
    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Legacy image picker launcher for older Android versions
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        handleSelectedImageUri(imageUri);
                    }
                });

        // New photo picker launcher for Android 13+ (API 33+)
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                this::handleSelectedImageUri);

        // Permission request launcher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        selectImage();
                    } else {
                        Toast.makeText(requireContext(), "Permission denied to access photos", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    // Process the selected image
    private void handleSelectedImageUri(Uri imageUri) {
        if (imageUri != null) {
            // Display the image
            Glide.with(requireContext())
                    .load(imageUri)
                    .placeholder(R.drawable.ic_person)
                    .into(profileImageView);

            // Set the image URI in the ViewModel for later use
            viewModel.setProfileImageUri(imageUri);
        } else {
            Toast.makeText(requireContext(), "Failed to select image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        successToastCounter.set(0); // Reset counter when fragment is created
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

        // Initialize with default image first
        profileImageView.setImageResource(R.drawable.ic_person);

        // We'll load the actual profile image in the observeViewModel method
        // when the user data becomes available through LiveData observation

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
        changeProfileImageButton.setOnClickListener(v -> selectImage());

        saveProfileButton.setOnClickListener(v -> saveProfile());

        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ (API 33+), use the photo picker
            photoPickerLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            // For Android 12 and below, check and request READ_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openLegacyImagePicker();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            // This shouldn't happen given our permission handling, but just in case
            Toast.makeText(requireContext(), "Unable to access photos on this Android version", Toast.LENGTH_SHORT).show();
        }
    }

    private void openLegacyImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
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

            if (state == ProfileManagementViewModel.UpdateState.SUCCESS
                    && successToastCounter.get() > 0) {

                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                // When user clicks save, immediately update the navigation header with the new image
                // This ensures the header image is consistent with the profile changes
                if (requireActivity() instanceof DashboardActivity) {
                    DashboardActivity dashboardActivity = (DashboardActivity) requireActivity();
                    dashboardActivity.refreshNavigationHeader();
                }

                successToastCounter.incrementAndGet();

                clearPasswordFields();

            } else if (state == ProfileManagementViewModel.UpdateState.ERROR
                    && successToastCounter.get() > 0) {

                Toast.makeText(requireContext(), viewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
                successToastCounter.incrementAndGet();

            } else if (state == ProfileManagementViewModel.UpdateState.LOADING) {
                // Increment counter when starting a new update operation
                successToastCounter.incrementAndGet();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Reset success toast counter to 0 when leaving the fragment
        // This prevents the toast from showing when the user returns after navigating away
        successToastCounter.set(0);
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
        emailInput.setEnabled(false);

        // Load profile image if available
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            try {
                // Create file object from the stored filename
                File imageFile = new File(requireContext().getFilesDir(), user.getProfileImage());

                // Use Glide to load the profile image from internal storage
                if (imageFile.exists()) {
                    Glide.with(requireContext())
                            .load(imageFile)
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .into(profileImageView);
                } else {
                    profileImageView.setImageResource(R.drawable.ic_person);
                }
            } catch (Exception e) {
                Log.e("ProfileManagementFragment", "Error loading profile image", e);
                profileImageView.setImageResource(R.drawable.ic_person);
            }
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
