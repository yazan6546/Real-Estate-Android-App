package com.example.realestate.ui.admin.delete_customers;

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

public class DeleteCustomersFragment extends Fragment {

    private DeleteCustomersViewModel viewModel;
    private RecyclerView customersRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_delete_customers, container, false);

        // Initialize views
        customersRecyclerView = root.findViewById(R.id.customersRecyclerView);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this,
                new DeleteCustomersViewModel.Factory(
                        RealEstate.appContainer.getUserRepository()))
                .get(DeleteCustomersViewModel.class);

        // Set up recycler view
        customersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observe changes to the customers list
        viewModel.getCustomers().observe(getViewLifecycleOwner(), customers -> {
            // Update UI with customers (would update adapter in a real implementation)
        });

        // Observe operation status for feedback
        viewModel.getOperationStatus().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(requireContext(), "Customer deleted successfully", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(requireContext(), "Failed to delete customer", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
