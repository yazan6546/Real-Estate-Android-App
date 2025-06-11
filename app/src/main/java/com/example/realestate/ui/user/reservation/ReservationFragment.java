package com.example.realestate.ui.user.reservation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.domain.model.Property;
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.domain.service.UserSession;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReservationFragment extends Fragment {

    private ReservationViewModel viewModel;
    private Property property;

    // UI Components
    private ImageView ivPropertyImage;
    private TextView tvPropertyTitle;
    private TextView tvPropertyType;
    private TextView tvPropertyLocation;

    private Button btnSelectStartDate;
    private Button btnSelectStartTime;
    private Button btnSelectEndDate;
    private Button btnSelectEndTime;
    private TextView tvSelectedDateRange;

    private CardView cardConflictWarning;
    private TextView tvConflictMessage;

    private ProgressBar progressBar;
    private Button btnCancel;
    private Button btnConfirmReservation;

    // Date/Time tracking
    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();
    private Calendar startTimeCalendar = Calendar.getInstance();
    private Calendar endTimeCalendar = Calendar.getInstance();

    private boolean startDateSelected = false;
    private boolean startTimeSelected = false;
    private boolean endDateSelected = false;
    private boolean endTimeSelected = false;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initializeViews(view);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this,
                new ReservationViewModel.Factory(RealEstate.appContainer.getReservationRepository()))
                .get(ReservationViewModel.class);
        // Get property from arguments
        if (getArguments() != null) {
            property = (Property) getArguments().getSerializable("property");
        }

        if (property == null) {
            Toast.makeText(getContext(), "Property information not available", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
            return;
        }

        // Setup UI
        setupPropertyInformation();
        setupDateTimePickers();
        setupObservers();
        setupClickListeners();
    }

    private void initializeViews(View view) {
        // Property info views
        ivPropertyImage = view.findViewById(R.id.ivPropertyImage);
        tvPropertyTitle = view.findViewById(R.id.tvPropertyTitle);
        tvPropertyType = view.findViewById(R.id.tvPropertyType);
        tvPropertyLocation = view.findViewById(R.id.tvPropertyLocation);

        // Date/time picker buttons
        btnSelectStartDate = view.findViewById(R.id.btnSelectStartDate);
        btnSelectStartTime = view.findViewById(R.id.btnSelectStartTime);
        btnSelectEndDate = view.findViewById(R.id.btnSelectEndDate);
        btnSelectEndTime = view.findViewById(R.id.btnSelectEndTime);
        tvSelectedDateRange = view.findViewById(R.id.tvSelectedDateRange);

        // Conflict warning
        cardConflictWarning = view.findViewById(R.id.cardConflictWarning);
        tvConflictMessage = view.findViewById(R.id.tvConflictMessage);

        // Action components
        progressBar = view.findViewById(R.id.progressBar);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnConfirmReservation = view.findViewById(R.id.btnConfirmReservation);
    }

    private void setupPropertyInformation() {
        if (property != null) {
            tvPropertyTitle.setText(property.getTitle());
            tvPropertyType.setText(property.getType());
            tvPropertyLocation.setText(property.getLocation());

            // Load property image
            if (property.getImageUrl() != null && !property.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(property.getImageUrl())
                        .placeholder(R.drawable.ic_building)
                        .error(R.drawable.ic_building)
                        .into(ivPropertyImage);
            } else {
                ivPropertyImage.setImageResource(R.drawable.ic_building);
            }
        }
    }

    private void setupDateTimePickers() {
        // Initialize calendar with current time + 1 hour as minimum
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR_OF_DAY, 1);

        startDateCalendar.setTime(now.getTime());
        startTimeCalendar.setTime(now.getTime());

        // Set end time to start + 2 hours by default
        endDateCalendar.setTime(now.getTime());
        endTimeCalendar.setTime(now.getTime());
        endTimeCalendar.add(Calendar.HOUR_OF_DAY, 2);
    }

    private void setupObservers() {
        // Observe conflict state
        viewModel.getHasConflict().observe(getViewLifecycleOwner(), hasConflict -> {
            if (hasConflict != null) {
                cardConflictWarning.setVisibility(hasConflict ? View.VISIBLE : View.GONE);
                btnConfirmReservation.setEnabled(!hasConflict && viewModel.canSubmitReservation());
            }
        });

        // Observe conflict message
        viewModel.getConflictMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                tvConflictMessage.setText(message);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                btnConfirmReservation.setEnabled(!isLoading && viewModel.canSubmitReservation());
            }
        });

        // Observe success message
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                viewModel.clearMessages();
                // Navigate back to properties
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        // Observe error message
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                viewModel.clearMessages();
            }
        });
    }

    private void setupClickListeners() {
        btnSelectStartDate.setOnClickListener(v -> showStartDatePicker());
        btnSelectStartTime.setOnClickListener(v -> showStartTimePicker());
        btnSelectEndDate.setOnClickListener(v -> showEndDatePicker());
        btnSelectEndTime.setOnClickListener(v -> showEndTimePicker());

        btnCancel.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        btnConfirmReservation.setOnClickListener(v -> {
            String userEmail = getCurrentUserEmail();
            if (userEmail != null) {
                viewModel.createReservation(property, userEmail);
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showStartDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    startDateCalendar.set(Calendar.YEAR, year);
                    startDateCalendar.set(Calendar.MONTH, month);
                    startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    startDateSelected = true;
                    updateStartDateButton();
                    updateDateTimeSelection();
                },
                startDateCalendar.get(Calendar.YEAR),
                startDateCalendar.get(Calendar.MONTH),
                startDateCalendar.get(Calendar.DAY_OF_MONTH));

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showStartTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    startTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startTimeCalendar.set(Calendar.MINUTE, minute);
                    startTimeSelected = true;
                    updateStartTimeButton();
                    updateDateTimeSelection();
                },
                startTimeCalendar.get(Calendar.HOUR_OF_DAY),
                startTimeCalendar.get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }

    private void showEndDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    endDateCalendar.set(Calendar.YEAR, year);
                    endDateCalendar.set(Calendar.MONTH, month);
                    endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    endDateSelected = true;
                    updateEndDateButton();
                    updateDateTimeSelection();
                },
                endDateCalendar.get(Calendar.YEAR),
                endDateCalendar.get(Calendar.MONTH),
                endDateCalendar.get(Calendar.DAY_OF_MONTH));

        // Set minimum date to start date or today
        long minDate = startDateSelected ? startDateCalendar.getTimeInMillis() : System.currentTimeMillis();
        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.show();
    }

    private void showEndTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    endTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    endTimeCalendar.set(Calendar.MINUTE, minute);
                    endTimeSelected = true;
                    updateEndTimeButton();
                    updateDateTimeSelection();
                },
                endTimeCalendar.get(Calendar.HOUR_OF_DAY),
                endTimeCalendar.get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }

    private void updateStartDateButton() {
        btnSelectStartDate.setText(dateFormat.format(startDateCalendar.getTime()));
    }

    private void updateStartTimeButton() {
        btnSelectStartTime.setText(timeFormat.format(startTimeCalendar.getTime()));
    }

    private void updateEndDateButton() {
        btnSelectEndDate.setText(dateFormat.format(endDateCalendar.getTime()));
    }

    private void updateEndTimeButton() {
        btnSelectEndTime.setText(timeFormat.format(endTimeCalendar.getTime()));
    }

    private void updateDateTimeSelection() {
        if (startDateSelected && startTimeSelected && endDateSelected && endTimeSelected) {
            // Combine date and time
            Calendar startDateTime = Calendar.getInstance();
            startDateTime.set(
                    startDateCalendar.get(Calendar.YEAR),
                    startDateCalendar.get(Calendar.MONTH),
                    startDateCalendar.get(Calendar.DAY_OF_MONTH),
                    startTimeCalendar.get(Calendar.HOUR_OF_DAY),
                    startTimeCalendar.get(Calendar.MINUTE));

            Calendar endDateTime = Calendar.getInstance();
            endDateTime.set(
                    endDateCalendar.get(Calendar.YEAR),
                    endDateCalendar.get(Calendar.MONTH),
                    endDateCalendar.get(Calendar.DAY_OF_MONTH),
                    endTimeCalendar.get(Calendar.HOUR_OF_DAY),
                    endTimeCalendar.get(Calendar.MINUTE));

            // Update ViewModel
            viewModel.setStartDateTime(startDateTime.getTime());
            viewModel.setEndDateTime(endDateTime.getTime());

            // Update display
            String dateRangeText = "From: " + dateTimeFormat.format(startDateTime.getTime()) +
                    "\nTo: " + dateTimeFormat.format(endDateTime.getTime());
            tvSelectedDateRange.setText(dateRangeText);

            // Enable/disable confirm button
            btnConfirmReservation.setEnabled(viewModel.canSubmitReservation());

            // Check for conflicts if property is available
            if (property != null) {
                viewModel.checkPropertyReservationConflicts(property.getPropertyId());
            }

        } else {
            tvSelectedDateRange.setText("Please select start and end date/time");
            btnConfirmReservation.setEnabled(false);
        }
    }

    private String getCurrentUserEmail() {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        UserSession userSession = sharedPrefManager.readObject("user_session", UserSession.class, null);
        return userSession != null ? userSession.getEmail() : null;
    }
}
