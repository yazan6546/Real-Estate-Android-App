package com.example.realestate.ui.user.reservation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.R;
import com.example.realestate.databinding.FragmentReservationBinding;
import com.example.realestate.domain.model.Property;
import com.example.realestate.domain.model.Reservation;
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.domain.service.UserSession;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReservationFragment extends Fragment {

    private static final String ARG_PROPERTY = "property";

    private FragmentReservationBinding binding;
    private ReservationViewModel viewModel;
    private Property property;
    private Calendar selectedDateTime;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public static ReservationFragment newInstance(Property property) {
        ReservationFragment fragment = new ReservationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROPERTY, property);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            property = (Property) getArguments().getSerializable(ARG_PROPERTY);
        }

        selectedDateTime = Calendar.getInstance();
        // Set minimum time to current time + 1 hour
        selectedDateTime.add(Calendar.HOUR_OF_DAY, 1);

        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReservationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReservationViewModel.class);

        setupPropertyInfo();
        setupDateTimePickers();
        setupObservers();
        setupClickListeners();
        updateDateTimeDisplay();
    }

    private void setupPropertyInfo() {
        if (property != null) {
            binding.propertyTitleTextView.setText(property.getTitle());
            binding.propertyLocationTextView.setText(property.getLocation());
            binding.propertyPriceTextView.setText(String.format("$%,.0f", property.getPrice()));

            // Load property image
            if (property.getImageUrl() != null && !property.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(property.getImageUrl())
                        .placeholder(R.drawable.ic_home)
                        .error(R.drawable.ic_home)
                        .centerCrop()
                        .into(binding.propertyImageView);
            } else {
                binding.propertyImageView.setImageResource(R.drawable.ic_home);
            }
        }
    }

    private void setupDateTimePickers() {
        binding.selectDateButton.setOnClickListener(v -> showDatePicker());
        binding.selectTimeButton.setOnClickListener(v -> showTimePicker());
    }

    private void setupObservers() {
        viewModel.getReservationResult().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.submitReservationButton.setEnabled(true);

            if (result != null && result) {
                Toast.makeText(getContext(), "Reservation submitted successfully!", Toast.LENGTH_LONG).show();
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            } else {
                Toast.makeText(getContext(), "Failed to submit reservation. Please try again.", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                binding.progressBar.setVisibility(View.GONE);
                binding.submitReservationButton.setEnabled(true);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        binding.submitReservationButton.setOnClickListener(v -> submitReservation());
    }

    private void showDatePicker() {
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 1); // Minimum date is tomorrow

        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeDisplay();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        dialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog dialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    updateDateTimeDisplay();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true);

        dialog.show();
    }

    private void updateDateTimeDisplay() {
        String dateText = dateFormat.format(selectedDateTime.getTime());
        String timeText = timeFormat.format(selectedDateTime.getTime());

        binding.selectedDateTextView.setText(dateText);
        binding.selectedTimeTextView.setText(timeText);
    }

    private void submitReservation() {
        String customerName = binding.customerNameEditText.getText().toString().trim();
        String customerEmail = binding.customerEmailEditText.getText().toString().trim();
        String customerPhone = binding.customerPhoneEditText.getText().toString().trim();

        // Validate input
        if (customerName.isEmpty()) {
            binding.customerNameEditText.setError("Name is required");
            binding.customerNameEditText.requestFocus();
            return;
        }

        if (customerEmail.isEmpty()) {
            binding.customerEmailEditText.setError("Email is required");
            binding.customerEmailEditText.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(customerEmail).matches()) {
            binding.customerEmailEditText.setError("Please enter a valid email");
            binding.customerEmailEditText.requestFocus();
            return;
        }

        if (customerPhone.isEmpty()) {
            binding.customerPhoneEditText.setError("Phone number is required");
            binding.customerPhoneEditText.requestFocus();
            return;
        }

        // Check if selected time is in the future
        Calendar now = Calendar.getInstance();
        if (selectedDateTime.before(now)) {
            Toast.makeText(getContext(), "Please select a future date and time", Toast.LENGTH_SHORT).show();
            return;
        } // Create reservation object
        Reservation reservation = new Reservation();
        reservation.getProperty().setPropertyId(Integer.parseInt(property.getId()));
        reservation.getProperty().setTitle(property.getTitle());
        reservation.setCustomerName(customerName);
        reservation.setCustomerEmail(customerEmail);
        reservation.setCustomerPhone(customerPhone);
        reservation.setReservationDateTime(selectedDateTime.getTime());
        reservation.setStatus("PENDING");
        reservation.setCreatedAt(new Date());

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.submitReservationButton.setEnabled(false);

        // Submit reservation
        viewModel.submitReservation(reservation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
