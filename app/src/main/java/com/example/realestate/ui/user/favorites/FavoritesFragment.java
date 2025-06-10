package com.example.realestate.ui.user.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.databinding.FragmentFavoritesBinding;
import com.example.realestate.domain.model.Property;
import com.example.realestate.domain.service.UserSession;
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.ui.user.properties.PropertyDetailFragment;
import com.example.realestate.ui.user.properties.PropertyAdapter;

import android.widget.Toast;
import java.util.ArrayList;

public class FavoritesFragment extends Fragment implements PropertyAdapter.OnPropertyActionListener {
    private FragmentFavoritesBinding binding;
    private FavoritesViewModel viewModel;
    private PropertyAdapter adapter;
    private SharedPrefManager sharedPrefManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState); // Initialize dependencies
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());

        // Initialize ViewModel with proper dependency injection
        viewModel = new ViewModelProvider(this,
                new FavoritesViewModel.Factory(
                        RealEstate.appContainer.getPropertyRepository(),
                        RealEstate.appContainer.getFavoriteRepository()))
                .get(FavoritesViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupClickListeners(); // Load favorite properties with user email
        loadFavorites();
    }

    private void loadFavorites() {
        String email = sharedPrefManager.getCurrentUserEmail();
        if (email != null && !email.isEmpty()) {
            viewModel.loadFavoriteProperties(email);
        } else {
            Toast.makeText(requireContext(), "Please log in to view favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        binding.browsePropertiesButton.setOnClickListener(v -> {
            // Navigate to Properties fragment
            Navigation.findNavController(v).navigate(R.id.nav_properties);
        });
    }

    private void setupRecyclerView() {
        adapter = new PropertyAdapter(getContext());
        adapter.setOnPropertyActionListener(this);
        binding.favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.favoritesRecyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getFavoriteProperties().observe(getViewLifecycleOwner(), properties -> {
            if (properties != null) {
                adapter.setProperties(properties);

                // Show empty state if no favorites
                if (properties.isEmpty()) {
                    binding.emptyStateLayout.setVisibility(View.VISIBLE);
                    binding.favoritesRecyclerView.setVisibility(View.GONE);
                } else {
                    binding.emptyStateLayout.setVisibility(View.GONE);
                    binding.favoritesRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                // Show error state
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
                binding.favoritesRecyclerView.setVisibility(View.GONE);
                binding.emptyTitleTextView.setText("Error Loading Favorites");
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if (success != null && !success.isEmpty()) {
                Toast.makeText(requireContext(), success, Toast.LENGTH_SHORT).show();
                // Reload favorites after successful removal
                loadFavorites();
            }
        });
    }

    @Override
    public void onFavoriteClick(Property property, boolean isCurrentlyFavorite) {
        // In favorites screen, we always remove from favorites regardless of current
        // status
        // because all properties here are already favorited
        UserSession userSession = sharedPrefManager.readObject("user_session", UserSession.class, null);
        if (userSession != null && userSession.getEmail() != null) {
            viewModel.removeFromFavorites(property, userSession.getEmail());
        } else {
            Toast.makeText(requireContext(), "Please log in to remove favorites", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void checkFavoriteStatus(Property property, PropertyAdapter.FavoriteStatusCallback callback) {
        // In favorites screen, all properties are favorites by definition
        callback.onStatusReceived(true);
    }

    @Override
    public void onReserveClick(Property property) {
        // Navigate to reservation fragment
        Bundle args = new Bundle();
        args.putSerializable("property", property);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_favorites_to_reservationFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
