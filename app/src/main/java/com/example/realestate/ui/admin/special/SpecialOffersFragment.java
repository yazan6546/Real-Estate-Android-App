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
import com.example.realestate.domain.model.Property;

import java.util.concurrent.atomic.AtomicInteger;

public class SpecialOffersFragment extends Fragment implements SpecialOffersAdapter.OnOfferActionListener {

    private SpecialOffersViewModel viewModel;
    private RecyclerView recyclerView;
    private SpecialOffersAdapter adapter;

    private static final AtomicInteger successToastCounter = new AtomicInteger(0);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_special_offers, container, false);

        // Initialize views
        recyclerView = root.findViewById(R.id.offersRecyclerView);
        successToastCounter.set(0);

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

            if (isLoading)
                successToastCounter.incrementAndGet();
            // You could show/hide a progress bar here
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty() && successToastCounter.get() > 0) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                successToastCounter.incrementAndGet();
            }
        });

        // Observe success messages
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if (success != null && !success.isEmpty() && successToastCounter.get() > 0) {
                Toast.makeText(requireContext(), success, Toast.LENGTH_SHORT).show();
                successToastCounter.incrementAndGet();
            }
        });
    }

    @Override
    public void onToggleOffer(Property property, double discountPercentage) {
        // Trigger animations before making the database call
        if (discountPercentage > 0) {
            // Creating offer - animate strikethrough appearance
            adapter.animateOfferCreation(property.getPropertyId(), discountPercentage);
        } else {
            // Removing offer - animate strikethrough removal
            adapter.animateOfferRemoval(property.getPropertyId());
        }

        viewModel.toggleOffer(property, discountPercentage);
    }

    @Override
    public void onValidationError(String errorMessage) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
