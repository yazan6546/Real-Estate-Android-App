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
        ReservationRepository reservationRepository = ((RealEstate) requireActivity().getApplication()).getReservationRepository();

        ViewAllReservationsViewModel.Factory factory = new ViewAllReservationsViewModel.Factory(
                reservationRepository);

        viewModel = new ViewModelProvider(this, factory).get(ViewAllReservationsViewModel.class);

        // Setup tabs for filtering
        setupTabs();

        // Observe reservations data
        observeReservations();

        // Load initial data
        loadReservations(null);

        return root;
    }

    private void setupTabs() {
        // Clear existing tabs
        tabLayout.removeAllTabs();

        // Add status filter tabs
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Confirmed"));
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        tabLayout.addTab(tabLayout.newTab().setText("Cancelled"));

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String status = null;
                if (tab.getPosition() > 0) {
                    status = tab.getText().toString();
                }
                loadReservations(status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
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

    private void loadReservations(String status) {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        reservationsRecyclerView.setVisibility(View.GONE);

        // For now, just load all reservations
        // In a full implementation, we would filter by status
        viewModel.loadAllReservations();
    }
}
