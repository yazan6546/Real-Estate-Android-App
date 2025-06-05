package com.example.realestate.ui.admin.properties;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;
import com.example.realestate.RealEstate;

public class ManagePropertiesFragment extends Fragment {

    private ManagePropertiesViewModel viewModel;
    private RecyclerView propertiesRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Using a simple layout with just a RecyclerView for now
        View root = inflater.inflate(R.layout.fragment_manage_properties, container, false);

        // Initialize views
        propertiesRecyclerView = root.findViewById(R.id.propertiesRecyclerView);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this,
                new ManagePropertiesViewModel.Factory(
                        RealEstate.appContainer.getPropertyRepository()))
                .get(ManagePropertiesViewModel.class);

        // Set up recycler view
        propertiesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observe changes to the properties list
        viewModel.getProperties().observe(getViewLifecycleOwner(), properties -> {
            // Update UI with properties (would update adapter in a real implementation)
        });

        // Load all properties
        viewModel.loadAllProperties();
    }
}
