package com.example.realestate.ui.admin.reservations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.domain.model.Reservation;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewAllReservationsFragment extends Fragment {

    private ViewAllReservationsViewModel viewModel;
    private RecyclerView reservationsRecyclerView;
    private TextView emptyView;
    private ReservationAdapter adapter;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_all_reservations, container, false);

        // Initialize views
        reservationsRecyclerView = root.findViewById(R.id.reservationsRecyclerView);
        emptyView = root.findViewById(R.id.emptyView);
        tabLayout = root.findViewById(R.id.tabLayout);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this,
                new ViewAllReservationsViewModel.Factory(
                        RealEstate.appContainer.getReservationRepository()))
                .get(ViewAllReservationsViewModel.class);

        setupRecyclerView();
        setupTabLayout();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new ReservationAdapter();
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        reservationsRecyclerView.setAdapter(adapter);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Filter reservations based on selected tab
                switch (tab.getPosition()) {
                    case 0: // All
                        viewModel.loadAllReservations();
                        break;
                    case 1: // Pending
                        viewModel.loadReservationsByStatus("pending");
                        break;
                    case 2: // Confirmed
                        viewModel.loadReservationsByStatus("confirmed");
                        break;
                    case 3: // Cancelled
                        viewModel.loadReservationsByStatus("cancelled");
                        break;
                }
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

    private void observeViewModel() {
        viewModel.getReservations().observe(getViewLifecycleOwner(), reservations -> {
            adapter.setReservations(reservations);

            // Show/hide empty view based on whether there are reservations
            if (reservations.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                reservationsRecyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                reservationsRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        // Load all reservations initially
        viewModel.loadAllReservations();
    }

    private static class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
        private List<Reservation> reservations = new ArrayList<>();
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        public void setReservations(List<Reservation> reservations) {
            this.reservations = reservations;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reservation, parent, false);
            return new ReservationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
            Reservation reservation = reservations.get(position);
            holder.bind(reservation, dateFormat);
        }

        @Override
        public int getItemCount() {
            return reservations.size();
        }

        static class ReservationViewHolder extends RecyclerView.ViewHolder {
            private final TextView customerTextView;
            private final TextView propertyTextView;
            private final TextView dateRangeTextView;
            private final TextView statusTextView;

            public ReservationViewHolder(@NonNull View itemView) {
                super(itemView);
                customerTextView = itemView.findViewById(R.id.customerTextView);
                propertyTextView = itemView.findViewById(R.id.propertyTextView);
                dateRangeTextView = itemView.findViewById(R.id.dateRangeTextView);
                statusTextView = itemView.findViewById(R.id.statusTextView);
            }

            public void bind(Reservation reservation, SimpleDateFormat dateFormat) {
                customerTextView.setText(reservation.getEmail());

                String propertyTitle = reservation.getProperty() != null ?
                        reservation.getProperty().getTitle() :
                        "Property #" + reservation.getPropertyId();
                propertyTextView.setText(propertyTitle);

                String dateRange = dateFormat.format(reservation.getStartDate()) +
                        " - " + dateFormat.format(reservation.getEndDate());
                dateRangeTextView.setText(dateRange);

                statusTextView.setText(reservation.getStatus());

                // Set status color based on reservation status
                int color;
                switch (reservation.getStatus().toLowerCase()) {
                    case "confirmed":
                        color = itemView.getResources().getColor(android.R.color.holo_green_dark);
                        break;
                    case "pending":
                        color = itemView.getResources().getColor(android.R.color.holo_blue_dark);
                        break;
                    case "cancelled":
                        color = itemView.getResources().getColor(android.R.color.holo_red_dark);
                        break;
                    default:
                        color = itemView.getResources().getColor(android.R.color.darker_gray);
                        break;
                }
                statusTextView.setTextColor(color);
            }
        }
    }
}
