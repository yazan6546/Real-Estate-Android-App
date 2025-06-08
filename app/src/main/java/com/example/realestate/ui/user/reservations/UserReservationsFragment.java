package com.example.realestate.ui.user.reservations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.domain.model.Reservation;
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.domain.service.UserSession;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private static class UserReservationAdapter
            extends RecyclerView.Adapter<UserReservationAdapter.ReservationViewHolder> {
        private List<Reservation> reservations = new ArrayList<>();
        private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a",
                Locale.getDefault());

        public void setReservations(List<Reservation> reservations) {
            this.reservations = reservations;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_reservation, parent, false);
            return new ReservationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
            Reservation reservation = reservations.get(position);
            holder.bind(reservation, dateTimeFormat);
        }

        @Override
        public int getItemCount() {
            return reservations.size();
        }

        static class ReservationViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvPropertyId;
            private final TextView tvPropertyTitle;
            private final TextView tvPropertyType; // Added property type
            private final TextView tvPropertyDescription;
            private final TextView tvPropertyLocation;
            private final TextView tvReservationStartDateTime;
            private final TextView tvReservationEndDateTime;
            private final TextView tvReservationStatus;
            private final ImageView imageView;

            public ReservationViewHolder(@NonNull View itemView) {
                super(itemView);
                tvPropertyId = itemView.findViewById(R.id.tvPropertyId);
                tvPropertyTitle = itemView.findViewById(R.id.tvPropertyTitle);
                tvPropertyType = itemView.findViewById(R.id.tvPropertyType); // Added property type
                tvPropertyDescription = itemView.findViewById(R.id.tvPropertyDescription);
                tvPropertyLocation = itemView.findViewById(R.id.tvPropertyLocation);
                tvReservationStartDateTime = itemView.findViewById(R.id.tvReservationStartDate);
                tvReservationEndDateTime = itemView.findViewById(R.id.tvReservationEndDate);
                tvReservationStatus = itemView.findViewById(R.id.tvReservationStatus);
                imageView = itemView.findViewById(R.id.ivPropertyImage);
            }

            public void bind(Reservation reservation, SimpleDateFormat dateTimeFormat) {
                // Property ID
                tvPropertyId.setText("Property ID: #" + reservation.getPropertyId());

                // Property details
                if (reservation.getProperty() != null) {
                    tvPropertyTitle.setText(reservation.getProperty().getTitle());
                    tvPropertyType.setText(reservation.getProperty().getType()); // Set property type
                    tvPropertyDescription.setText(reservation.getProperty().getDescription());
                    tvPropertyLocation.setText(reservation.getProperty().getLocation());
                } else {
                    tvPropertyTitle.setText("--");
                    tvPropertyType.setText("Unknown Type"); // Default value when property is null
                    tvPropertyDescription.setText(R.string.property_details_not_available);
                    tvPropertyLocation.setText(R.string.location_not_specified);
                }

                // Reservation date and time
                String startdateTime = dateTimeFormat.format(reservation.getStartDate());
                String enddateTime = dateTimeFormat.format(reservation.getEndDate());

                tvReservationStartDateTime.setText(startdateTime);
                tvReservationEndDateTime.setText(enddateTime);

                // Load property image if available
                if (reservation.getProperty() != null && reservation.getProperty().getImage() != null) {

                    Glide.with(itemView.getContext())
                            .load(reservation.getProperty().getImage())
                            .placeholder(R.drawable.ic_building)
                            .error(R.drawable.ic_building)
                            .into(imageView);
                }
                else {
                    imageView.setImageResource(R.drawable.ic_building);
                }

                // Reservation status
                tvReservationStatus.setText(reservation.getStatus().toUpperCase());

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
                    case "completed":
                        color = itemView.getResources().getColor(android.R.color.darker_gray);
                        break;
                    default:
                        color = itemView.getResources().getColor(android.R.color.darker_gray);
                        break;
                }
                tvReservationStatus.setTextColor(color);
            }
        }
    }
}