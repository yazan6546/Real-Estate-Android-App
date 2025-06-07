package com.example.realestate.ui.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.domain.model.User;
import com.example.realestate.ui.base.BaseRegistrationViewModel;
import com.example.realestate.ui.admin.add_admins.AddNewAdminViewModel;

public class RegisterFragment extends Fragment {

    private static final String ARG_IS_ADMIN = "is_admin";
    private static final String ARG_TITLE = "title";
    private static final String ARG_BUTTON_TEXT = "button_text";

    private BaseRegistrationViewModel viewModel;
    private boolean isAdminRegistration;

    // UI components
    private TextView titleTextView;
    private EditText emailInput;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Spinner genderSpinner;
    private Spinner countrySpinner;
    private Spinner citySpinner;
    private EditText phoneNumberInput;
    private TextView countryCodeText;
    private Button registrationButton;
    private ProgressBar progressBar;

    // Selected values
    private User.Gender selectedGender = User.Gender.MALE;
    private String selectedCountry;
    private String selectedCity;

    public static RegisterFragment newInstance(boolean isAdmin, String title, String buttonText) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_ADMIN, isAdmin);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_BUTTON_TEXT, buttonText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check for navigation arguments first, then bundle arguments
        Bundle args = getArguments();
        if (args != null) {
            isAdminRegistration = args.getBoolean(ARG_IS_ADMIN, false);
            
            // If no explicit arguments were passed, check if this was called from navigation
            if (!args.containsKey(ARG_IS_ADMIN) && args.containsKey("is_admin")) {
                isAdminRegistration = args.getBoolean("is_admin", false);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Reuse the existing add_new_admin layout structure
        View root = inflater.inflate(R.layout.fragment_add_new_admin, container, false);
        
        findViews(root);
        setupTitle();
        
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the appropriate ViewModel based on registration type
        if (isAdminRegistration) {
            viewModel = new ViewModelProvider(this,
                    new AddNewAdminViewModel.Factory(RealEstate.appContainer.getUserRepository()))
                    .get(AddNewAdminViewModel.class);
        } else {
            viewModel = new ViewModelProvider(this,
                    new RegisterViewModel.Factory(RealEstate.appContainer.getUserRepository()))
                    .get(RegisterViewModel.class);
        }

        setupSpinners();
        setupButton();
        observeViewModel();
    }

    private void findViews(View root) {
        titleTextView = root.findViewById(R.id.titleTextView);
        emailInput = root.findViewById(R.id.emailInput);
        firstNameInput = root.findViewById(R.id.firstNameInput);
        lastNameInput = root.findViewById(R.id.lastNameInput);
        passwordInput = root.findViewById(R.id.passwordInput);
        confirmPasswordInput = root.findViewById(R.id.confirmPasswordInput);
        genderSpinner = root.findViewById(R.id.genderSpinner);
        countrySpinner = root.findViewById(R.id.countrySpinner);
        citySpinner = root.findViewById(R.id.citySpinner);
        phoneNumberInput = root.findViewById(R.id.phoneNumberInput);
        countryCodeText = root.findViewById(R.id.countryCodeText);
        registrationButton = root.findViewById(R.id.addAdminButton); // This is the button ID in the layout
        progressBar = root.findViewById(R.id.progressBar);
    }

    private void setupTitle() {
        Bundle args = getArguments();
        if (args != null) {
            // Check for navigation arguments first
            String title = args.getString("title", null);
            String buttonText = args.getString("button_text", null);
            
            // If no navigation arguments, check for bundle arguments
            if (title == null) {
                title = args.getString(ARG_TITLE, "Registration");
            }
            if (buttonText == null) {
                buttonText = args.getString(ARG_BUTTON_TEXT, "Register");
            }
            
            titleTextView.setText(title);
            registrationButton.setText(buttonText);
        }
    }

    private void setupSpinners() {
        // Setup gender spinner
        String[] genders = {"Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = position == 0 ? User.Gender.MALE : User.Gender.FEMALE;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGender = User.Gender.MALE;
            }
        });

        // Setup country spinner
        String[] countries = viewModel.getCountries();
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);
        selectedCountry = countries[0]; // Default to first country

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = countries[position];
                viewModel.onCountrySelected(selectedCountry);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupButton() {
        registrationButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();
            String phone = phoneNumberInput.getText().toString().trim();

            if (isAdminRegistration) {
                ((AddNewAdminViewModel) viewModel).register(
                        email, password, confirmPassword, firstName, lastName,
                        selectedGender, selectedCountry, selectedCity, phone);
            } else {
                ((RegisterViewModel) viewModel).register(
                        email, password, confirmPassword, firstName, lastName,
                        selectedGender, selectedCountry, selectedCity, phone);
            }
        });
    }

    private void observeViewModel() {
        // Observe cities based on country selection
        viewModel.cities.observe(getViewLifecycleOwner(), cities -> {
            if (cities != null && cities.length > 0) {
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, cities);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(cityAdapter);
                selectedCity = cities[0]; // Default to first city

                citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCity = cities[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });
            }
        });

        // Observe country code for phone number
        viewModel.countryCode.observe(getViewLifecycleOwner(), code -> {
            if (code != null) {
                countryCodeText.setText(code);
            }
        });

        // Observe registration state
        viewModel.registerState.observe(getViewLifecycleOwner(), state -> {
            progressBar.setVisibility(state == BaseRegistrationViewModel.RegisterState.LOADING ?
                    View.VISIBLE : View.GONE);

            if (state == BaseRegistrationViewModel.RegisterState.SUCCESS) {
                String successMessage = isAdminRegistration ? 
                    "New admin added successfully!" : "Registration successful!";
                Toast.makeText(requireContext(), successMessage, Toast.LENGTH_LONG).show();
                
                // Clear form or close fragment based on use case
                if (isAdminRegistration) {
                    clearForm();
                } else {
                    // For user registration, you might want to navigate back or close
                    requireActivity().onBackPressed();
                }
            }
        });

        // Observe error messages
        viewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearForm() {
        emailInput.getText().clear();
        firstNameInput.getText().clear();
        lastNameInput.getText().clear();
        passwordInput.getText().clear();
        confirmPasswordInput.getText().clear();
        phoneNumberInput.getText().clear();
        genderSpinner.setSelection(0);
        countrySpinner.setSelection(0);
    }
}