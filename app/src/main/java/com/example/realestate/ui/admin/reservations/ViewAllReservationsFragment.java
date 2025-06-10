package com.example.realestate.ui.admin.reservations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.data.repository.ReservationRepository;
import com.google.android.material.tabs.TabLayout;

public class ViewAllReservationsFragment extends Fragment {

    private ViewAllReservationsViewModel viewModel;
    private RecyclerView reservationsRecyclerView;
    private LinearLayout emptyView;
    private ProgressBar progressBar;
    private AdminReservationAdapter adapter;
    private TabLayout tabLayout;

    // Status filter values
    private static final String[] STATUS_VALUES = {"All", "Confirmed", "Pending", "Cancelled", "Completed"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_all_reservations, container, false);

        // Initialize views
        reservationsRecyclerView = root.findViewById(R.id.reservationsRecyclerView);
        emptyView = root.findViewById(R.id.emptyView);
        progressBar = root.findViewById(R.id.progressBar);
        tabLayout = root.findViewById(R.id.tabLayout);

        // Initialize adapter
        adapter = new AdminReservationAdapter();
        reservationsRecyclerView.setAdapter(adapter);
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize ViewModel
        ReservationRepository reservationRepository = RealEstate.appContainer.getReservationRepository();

        ViewAllReservationsViewModel.Factory factory = new ViewAllReservationsViewModel.Factory(
                reservationRepository);

        viewModel = new ViewModelProvider(this, factory).get(ViewAllReservationsViewModel.class);

        // Setup tabs for filtering
        setupTabs();

        // Observe reservations data
        observeReservations();

        // Load initial data with the current filter (or null if none)
        loadReservations();

        return root;
    }

    private void setupTabs() {
        // Clear existing tabs
        tabLayout.removeAllTabs();

        // Add status filter tabs
        for (String status : STATUS_VALUES) {
            tabLayout.addTab(tabLayout.newTab().setText(status));
        }

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String status = tab.getText().toString();
                // Use null for "All" to show all statuses
                loadReservations(status.equals("All") ? null : status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Refresh the data when tab is reselected
                String status = tab.getText().toString();
                loadReservations(status.equals("All") ? null : status);
            }
        });

        // Select the appropriate tab based on current filter
        String currentStatus = viewModel.getCurrentStatus();
        int tabIndex = 0; // Default to "All"
        if (currentStatus != null) {
            for (int i = 0; i < STATUS_VALUES.length; i++) {
                if (STATUS_VALUES[i].equals(currentStatus)) {
                    tabIndex = i;
                    break;
                }
            }
        }
        tabLayout.selectTab(tabLayout.getTabAt(tabIndex));
    }

    private void observeReservations() {
        viewModel.getUserReservationsMap().observe(getViewLifecycleOwner(), userReservationsMap -> {
            progressBar.setVisibility(View.GONE);

            if (userReservationsMap != null && !userReservationsMap.isEmpty()) {
                emptyView.setVisibility(View.GONE);
                reservationsRecyclerView.setVisibility(View.VISIBLE);
                adapter.setUserReservations(userReservationsMap);
            } else {
                emptyView.setVisibility(View.VISIBLE);
                reservationsRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void loadReservations() {
        // Load with current filter status
        loadReservations(viewModel.getCurrentStatus());
    }

    private void loadReservations(String status) {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        reservationsRecyclerView.setVisibility(View.GONE);

        // Load reservations with optional status filter
        viewModel.loadReservations(status);
    }
}
