package com.example.realestate.ui.user.reservations;

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
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.domain.service.UserSession;
import com.google.android.material.tabs.TabLayout;

public class UserReservationsFragment extends Fragment {
    private UserReservationsViewModel viewModel;
    private RecyclerView reservationsRecyclerView;
    private LinearLayout emptyStateLayout;
    private ProgressBar progressBar;
    private UserReservationAdapter adapter;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        reservationsRecyclerView = view.findViewById(R.id.recyclerViewReservations);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        progressBar = view.findViewById(R.id.progressBar);
        tabLayout = view.findViewById(R.id.tabLayout);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this,
                new UserReservationsViewModel.Factory(RealEstate.appContainer.getReservationRepository()))
                .get(UserReservationsViewModel.class);

        setupRecyclerView();
        setupTabLayout();
        observeViewModel();

        // Load user's reservations
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail != null) {
            showLoading(true);
            viewModel.loadUserReservations(currentUserEmail);
        }
    }

    private void setupRecyclerView() {
        adapter = new UserReservationAdapter();
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reservationsRecyclerView.setAdapter(adapter);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String currentUserEmail = getCurrentUserEmail();
                if (currentUserEmail == null)
                    return;

                showLoading(true);
                switch (tab.getPosition()) {
                    case 0: // All
                        viewModel.loadUserReservations(currentUserEmail);
                        break;
                    case 1: // Pending
                        viewModel.loadUserReservationsByStatus(currentUserEmail, "pending");
                        break;
                    case 2: // Confirmed
                        viewModel.loadUserReservationsByStatus(currentUserEmail, "confirmed");
                        break;
                    case 3: // Cancelled
                        viewModel.loadUserReservationsByStatus(currentUserEmail, "cancelled");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void observeViewModel() {
        viewModel.getReservations().observe(getViewLifecycleOwner(), reservations -> {
            showLoading(false);
            if (reservations != null && !reservations.isEmpty()) {
                adapter.setReservations(reservations);
                reservationsRecyclerView.setVisibility(View.VISIBLE);
                emptyStateLayout.setVisibility(View.GONE);
            } else {
                reservationsRecyclerView.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        reservationsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private String getCurrentUserEmail() {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        UserSession userSession = sharedPrefManager.readObject("user_session", UserSession.class, null);
        return userSession != null ? userSession.getEmail() : null;
    }
}