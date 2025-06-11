package com.example.realestate.ui.user.properties;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
//import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.domain.model.Property;
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.domain.service.UserSession;
import androidx.navigation.Navigation;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PropertiesFragment extends Fragment implements PropertyAdapter.OnPropertyActionListener {

    private PropertiesViewModel viewModel;
    private PropertyAdapter adapter;
    private SharedPrefManager sharedPrefManager;

    // UI Components
    private EditText etSearch;
    private Spinner spinnerPropertyType;
    private Spinner spinnerLocation;
    private EditText etMinPrice;
    private EditText etMaxPrice;
    private Button btnFilter;
    private TextView tvPropertiesCount;
    private RecyclerView recyclerViewProperties;
    private LinearLayout layoutEmptyState;

    static AtomicInteger successToastCounter = new AtomicInteger(0);
//    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        successToastCounter.set(0);
        return inflater.inflate(R.layout.fragment_properties, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initViewModel();
        setupRecyclerView();
        setupFilterControls();
        observeViewModel();
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        spinnerPropertyType = view.findViewById(R.id.spinnerPropertyType);
        spinnerLocation = view.findViewById(R.id.spinnerLocation);
        etMinPrice = view.findViewById(R.id.etMinPrice);
        etMaxPrice = view.findViewById(R.id.etMaxPrice);
        btnFilter = view.findViewById(R.id.btnFilter);
        tvPropertiesCount = view.findViewById(R.id.tvPropertiesCount);
        recyclerViewProperties = view.findViewById(R.id.recyclerViewProperties);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
//        progressBar = view.findViewById(R.id.progressBar);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this,
                new PropertiesViewModel.Factory(
                        RealEstate.appContainer.getPropertyRepository(),
                        RealEstate.appContainer.getFavoriteRepository()))
                .get(PropertiesViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new PropertyAdapter(requireContext());
        adapter.setOnPropertyActionListener(this);

        recyclerViewProperties.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewProperties.setAdapter(adapter);
    }

    private void setupFilterControls() {
        // Setup search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Setup filter button
        btnFilter.setOnClickListener(v ->  {
            applyFilters();
        });
    }

    private void applyFilters() {
        // Get selected values from spinners
        String selectedType = spinnerPropertyType.getSelectedItem() != null
                ? spinnerPropertyType.getSelectedItem().toString()
                : "All";
        String selectedLocation = spinnerLocation.getSelectedItem() != null
                ? spinnerLocation.getSelectedItem().toString()
                : "All";

        // Get price range
        double minPrice = 0.0;
        double maxPrice = Double.MAX_VALUE;

        try {
            if (!etMinPrice.getText().toString().trim().isEmpty()) {
                minPrice = Double.parseDouble(etMinPrice.getText().toString().trim());
            }
        } catch (NumberFormatException e) {
            etMinPrice.setError("Invalid minimum price");
            return;
        }

        try {
            if (!etMaxPrice.getText().toString().trim().isEmpty()) {
                maxPrice = Double.parseDouble(etMaxPrice.getText().toString().trim());
            }
        } catch (NumberFormatException e) {
            etMaxPrice.setError("Invalid maximum price");
            return;
        }

        if (minPrice > maxPrice) {
            etMaxPrice.setError("Maximum price must be greater than minimum price");
            return;
        }

        // Apply filters
        viewModel.setPropertyType(selectedType);
        viewModel.setLocation(selectedLocation);
        viewModel.setPriceRange(minPrice, maxPrice);
    }

    private void observeViewModel() {
        // Observe filtered properties
        viewModel.getFilteredProperties().observe(getViewLifecycleOwner(), this::updatePropertiesList);

        // Observe properties count
        viewModel.getPropertiesCount().observe(getViewLifecycleOwner(), count -> {
            String countText = count == 1 ? count + " property found" : count + " properties found";
            tvPropertiesCount.setText(countText);
        });

//        // Observe loading state
//        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
//            if (isLoading) {
//                progressBar.setVisibility(View.VISIBLE);
//                recyclerViewProperties.setVisibility(View.GONE);
//                layoutEmptyState.setVisibility(View.GONE);
//            } else {
//                progressBar.setVisibility(View.GONE);
//                recyclerViewProperties.setVisibility(View.VISIBLE);
//            }
//        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty() && successToastCounter.get() > 0) {
                if (errorMessage.contains("already in favorites")) {
                    Toast.makeText(requireContext(), "Property is already in your favorites", Toast.LENGTH_SHORT)
                            .show();
                } else if (errorMessage.contains("Failed to add to favorites") && successToastCounter.get() > 0) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                } else if (successToastCounter.get() > 0) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observe success messages
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty() && successToastCounter.get() > 0) {
                Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });

        // Observe filter options
        viewModel.getPropertyTypes().observe(getViewLifecycleOwner(), this::updatePropertyTypeSpinner);
        viewModel.getLocations().observe(getViewLifecycleOwner(), this::updateLocationSpinner);
    }

    private void updatePropertiesList(List<Property> properties) {
        if (properties != null && !properties.isEmpty()) {
            adapter.setProperties(properties);
            recyclerViewProperties.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        } else {
            recyclerViewProperties.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        }
    }

    private void updatePropertyTypeSpinner(List<String> propertyTypes) {
        if (propertyTypes != null && !propertyTypes.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, propertyTypes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPropertyType.setAdapter(adapter);
        }
    }

    private void updateLocationSpinner(List<String> locations) {
        if (locations != null && !locations.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, locations);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLocation.setAdapter(adapter);
        }
    } // PropertyAdapter.OnPropertyActionListener implementation

    @Override
    public void onFavoriteClick(Property property, boolean isCurrentlyFavorite) {
        UserSession userSession = sharedPrefManager.readObject("user_session", UserSession.class, null);
        if (userSession == null || userSession.getEmail() == null) {
            Toast.makeText(requireContext(), "Please log in to manage favorites", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = userSession.getEmail();
        if (isCurrentlyFavorite) {
            viewModel.removeFromFavorites(property, userEmail);
        } else {
            viewModel.addToFavorites(property, userEmail);
        }
    }

    @Override
    public void checkFavoriteStatus(Property property, PropertyAdapter.FavoriteStatusCallback callback) {
        UserSession userSession = sharedPrefManager.readObject("user_session", UserSession.class, null);
        if (userSession == null || userSession.getEmail() == null) {
            callback.onStatusReceived(false);
            return;
        }

        String userEmail = userSession.getEmail();
        viewModel.checkFavoriteStatus(property, userEmail, callback);
    }

    @Override
    public void onReserveClick(Property property) {
        // Navigate to reservation screen
        Bundle args = new Bundle();
        args.putSerializable("property", property);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_properties_to_reservationFragment, args);
    }
}
