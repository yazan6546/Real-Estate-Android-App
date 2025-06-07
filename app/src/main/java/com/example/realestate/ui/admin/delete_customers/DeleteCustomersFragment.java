package com.example.realestate.ui.admin.delete_customers;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.example.realestate.domain.model.User;

public class DeleteCustomersFragment extends Fragment implements CustomerAdapter.OnCustomerDeleteListener {

    private DeleteCustomersViewModel viewModel;
    private RecyclerView customersRecyclerView;
    private CustomerAdapter customerAdapter;
    private TextView emptyView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_delete_customers, container, false);

        // Initialize views
        customersRecyclerView = root.findViewById(R.id.customersRecyclerView);
        emptyView = root.findViewById(R.id.emptyView);
        progressBar = root.findViewById(R.id.progressBar);

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

        // Set up adapter
        customerAdapter = new CustomerAdapter(this);

        // Set up recycler view
        customersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        customersRecyclerView.setAdapter(customerAdapter);

        // Observe changes to the customers list
        viewModel.getCustomers().observe(getViewLifecycleOwner(), customers -> {
            if (customers != null && !customers.isEmpty()) {
                customerAdapter.setCustomers(customers);
                customersRecyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                customersRecyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        });

        // Observe operation status for feedback
        viewModel.getOperationStatus().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(requireContext(), "Customer deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to delete customer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        // No need to manually load customers since we're using LiveData
    }

    @Override
    public void onDeleteCustomer(User customer) {
        // Show confirmation dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Customer")
                .setMessage("Are you sure you want to delete " + customer.getFirstName() + " " + customer.getLastName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteCustomer(customer);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
