package com.example.realestate.ui.user.featured;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.domain.model.Property;
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.domain.service.UserSession;
import com.example.realestate.ui.user.properties.PropertyAdapter;

public class FeaturedPropertiesFragment extends Fragment implements PropertyAdapter.OnPropertyActionListener {

    private FeaturedPropertiesViewModel viewModel;
    private PropertyAdapter adapter;
    private SharedPrefManager sharedPrefManager;
    
    // UI Components
    private RecyclerView recyclerViewFeatured;
    private View layoutEmptyState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_featured_properties, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initViewModel();
        setupRecyclerView();
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerViewFeatured = view.findViewById(R.id.recyclerViewFeatured);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this,
                new FeaturedPropertiesViewModel.Factory(
                        RealEstate.appContainer.getPropertyRepository(),
                        RealEstate.appContainer.getFavoriteRepository()))
                .get(FeaturedPropertiesViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new PropertyAdapter(requireContext());
        adapter.setOnPropertyActionListener(this);

        recyclerViewFeatured.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewFeatured.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Observe featured properties
        viewModel.getFeaturedProperties().observe(getViewLifecycleOwner(), this::updatePropertiesList);

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                if (errorMessage.contains("already in favorites")) {
                    Toast.makeText(requireContext(), "Property is already in your favorites", Toast.LENGTH_SHORT)
                            .show();
                } else if (errorMessage.contains("Failed to add to favorites")) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observe success messages
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty()) {
                Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void updatePropertiesList(java.util.List<Property> properties) {
        if (properties != null && !properties.isEmpty()) {
            adapter.setProperties(properties);
            recyclerViewFeatured.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        } else {
            recyclerViewFeatured.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        }
    }

    // PropertyAdapter.OnPropertyActionListener implementation

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
                .navigate(R.id.action_nav_featured_properties_to_reservationFragment, args);
    }
}
