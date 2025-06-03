package com.example.realestate.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.domain.model.User;
import com.example.realestate.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel viewModel;

    // UI components
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
    private Button registerButton;
    private Button loginButton;
    private ProgressBar progressBar;

    // Using User.Gender enum from the User class
    private User.Gender selectedGender = User.Gender.MALE;
    private String selectedCountry;
    private String selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this,
                new RegisterViewModel.Factory(RealEstate.appContainer.getUserRepository()))
                .get(RegisterViewModel.class);

        // Find UI components
        findViews();

        // Setup spinners
        setupSpinners();

        // Setup observers
        observeViewModel();

        // Setup button click listeners
        setupButtons();
    }

    private void findViews() {
        emailInput = findViewById(R.id.emailInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        genderSpinner = findViewById(R.id.genderSpinner);
        countrySpinner = findViewById(R.id.countrySpinner);
        citySpinner = findViewById(R.id.citySpinner);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        countryCodeText = findViewById(R.id.countryCodeText);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupSpinners() {
        // Gender Spinner
        ArrayAdapter<CharSequence> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[] {"Male", "Female"});
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = position == 0 ?
                        User.Gender.MALE : User.Gender.FEMALE;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGender = User.Gender.MALE; // Default
            }
        });

        // Country Spinner
        String[] countries = viewModel.getCountries();
        selectedCountry = countries[0]; // Default to first country

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = countries[position];
                viewModel.onCountrySelected(selectedCountry);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, use default
            }
        });
    }

    private void observeViewModel() {
        // Observe cities based on country selection
        viewModel.cities.observe(this, cities -> {
            if (cities != null && cities.length > 0) {
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(RegisterActivity.this,
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
                        // Do nothing, use default
                    }
                });
            }
        });

        // Observe country code for phone number
        viewModel.countryCode.observe(this, code -> {
            if (code != null) {
                countryCodeText.setText(code);
            }
        });

        // Observe registration state
        viewModel.registerState.observe(this, state -> {
            progressBar.setVisibility(state == RegisterViewModel.RegisterState.LOADING ?
                    View.VISIBLE : View.GONE);

            if (state == RegisterViewModel.RegisterState.SUCCESS) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show();
                navigateToLogin();
            }
        });

        // Observe error messages
        viewModel.errorMessage.observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupButtons() {
        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();
            String phone = phoneNumberInput.getText().toString();

            viewModel.register(
                email,
                password,
                confirmPassword,
                firstName,
                lastName,
                selectedGender, // Using the User.Gender enum
                selectedCountry,
                selectedCity,
                phone
            );
        });

        loginButton.setOnClickListener(v -> navigateToLogin());
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
