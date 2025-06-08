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

        viewModel.getAuthState().observe(getViewLifecycleOwner(), authState -> {
            switch (authState) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    Toast.makeText(requireContext(), "Customer deleted successfully", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    viewModel.resetAuthState();
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error Occurred: " + viewModel.getErrorMessage() , Toast.LENGTH_SHORT).show();
                    break;
                default:
                    progressBar.setVisibility(View.GONE);
                    break;
            }
        });

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
