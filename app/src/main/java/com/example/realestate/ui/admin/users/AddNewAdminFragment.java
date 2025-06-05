package com.example.realestate.ui.admin.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.domain.model.User;

public class AddNewAdminFragment extends Fragment {

    private AddNewAdminViewModel viewModel;

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText phoneEditText;
    private Spinner countrySpinner;
    private Spinner citySpinner;
    private RadioGroup genderRadioGroup;
    private Button addAdminButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_new_admin, container, false);

        // Initialize views
        emailEditText = root.findViewById(R.id.emailEditText);
        passwordEditText = root.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = root.findViewById(R.id.confirmPasswordEditText);
        firstNameEditText = root.findViewById(R.id.firstNameEditText);
        lastNameEditText = root.findViewById(R.id.lastNameEditText);
        phoneEditText = root.findViewById(R.id.phoneEditText);
        countrySpinner = root.findViewById(R.id.countrySpinner);
        citySpinner = root.findViewById(R.id.citySpinner);
        genderRadioGroup = root.findViewById(R.id.genderRadioGroup);
        addAdminButton = root.findViewById(R.id.addAdminButton);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this,
                new AddNewAdminViewModel.Factory(
                        RealEstate.appContainer.getUserRepository()))
                .get(AddNewAdminViewModel.class);

        // Setup country and city spinners with dummy data for now
        setupSpinners();

        // Setup button click listener
        addAdminButton.setOnClickListener(v -> addNewAdmin());

        // Observe operation status
        viewModel.getOperationStatus().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(requireContext(), "New admin added successfully", Toast.LENGTH_SHORT).show();
                clearForm();
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSpinners() {
        // Sample data - in a real app, this would come from a repository or service
        String[] countries = {"United States", "United Kingdom", "Canada", "Australia", "UAE", "Palestine"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, countries);
        countrySpinner.setAdapter(countryAdapter);

        String[] cities = {"New York", "London", "Toronto", "Sydney", "Dubai", "Nablus"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, cities);
        citySpinner.setAdapter(cityAdapter);
    }

    private void addNewAdmin() {
        // Get values from form
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String country = countrySpinner.getSelectedItem().toString();
        String city = citySpinner.getSelectedItem().toString();

        // Get selected gender
        User.Gender gender = User.Gender.MALE; // Default
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton radioButton = genderRadioGroup.findViewById(selectedId);
            if (radioButton != null) {
                String genderText = radioButton.getText().toString();
                if (genderText.equalsIgnoreCase("female")) {
                    gender = User.Gender.FEMALE;
                }
            }
        }

        // Basic validation
        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call ViewModel to add new admin
        viewModel.addNewAdmin(email, password, firstName, lastName, phone, country, city, gender);
    }

    private void clearForm() {
        emailEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        firstNameEditText.setText("");
        lastNameEditText.setText("");
        phoneEditText.setText("");
        countrySpinner.setSelection(0);
        citySpinner.setSelection(0);
    }
}
