package com.example.realestate.ui.admin.special;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;
import com.example.realestate.RealEstate;

public class SpecialOffersFragment extends Fragment implements SpecialOffersAdapter.OnOfferActionListener {

    private SpecialOffersViewModel viewModel;
    private RecyclerView recyclerView;
    private SpecialOffersAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_special_offers, container, false);

        // Initialize views
        recyclerView = root.findViewById(R.id.offersRecyclerView);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel with PropertyRepository
        viewModel = new ViewModelProvider(this,
                new SpecialOffersViewModel.Factory(RealEstate.appContainer.getPropertyRepository()))
                .get(SpecialOffersViewModel.class);

        // Set up recycler view
        setupRecyclerView();

        // Set up observers
        setupObservers();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SpecialOffersAdapter(requireContext());
        adapter.setOnOfferActionListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        // Observe properties
        viewModel.getProperties().observe(getViewLifecycleOwner(), properties -> {
            if (properties != null) {
                adapter.setProperties(properties);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // You could show/hide a progress bar here
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe success messages
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if (success != null && !success.isEmpty()) {
                Toast.makeText(requireContext(), success, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onToggleOffer(com.example.realestate.domain.model.Property property, double discountPercentage) {
        viewModel.toggleOffer(property, discountPercentage);
    }

    @Override
    public void onValidationError(String errorMessage) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
