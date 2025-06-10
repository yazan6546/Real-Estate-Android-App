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

public class ViewAllReservationsFragment extends Fragment {

    private ViewAllReservationsViewModel viewModel;
    private RecyclerView reservationsRecyclerView;
    private LinearLayout emptyView;
    private ProgressBar progressBar;
    private AdminReservationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_all_reservations, container, false);

        // Initialize views
        reservationsRecyclerView = root.findViewById(R.id.reservationsRecyclerView);
        emptyView = root.findViewById(R.id.emptyView);
        progressBar = root.findViewById(R.id.progressBar);

        // Initialize adapter
        adapter = new AdminReservationAdapter();
        reservationsRecyclerView.setAdapter(adapter);
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize ViewModel
        ReservationRepository reservationRepository = RealEstate.appContainer.getReservationRepository();

        ViewAllReservationsViewModel.Factory factory = new ViewAllReservationsViewModel.Factory(
                reservationRepository);

        viewModel = new ViewModelProvider(this, factory).get(ViewAllReservationsViewModel.class);

        // Observe reservations data
        observeReservations();

        // Load all reservations
        loadAllReservations();

        return root;
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

    private void loadAllReservations() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        reservationsRecyclerView.setVisibility(View.GONE);

        // Load all reservations without any status filter
        viewModel.loadAllReservations();
    }
}
