package com.example.realestate.ui.admin.reservations;

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
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

        // Load all reservations initially with a loading indicator
        showLoading(true);
        viewModel.loadAllReservations();
    }

    private void setupRecyclerView() {
        adapter = new AdminReservationAdapter();
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        reservationsRecyclerView.setAdapter(adapter);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Show loading indicator
                showLoading(true);

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
            // Hide loading indicator
            showLoading(false);

            // Group the reservations by user and update adapter
            Map<String, List<Reservation>> userReservations = viewModel.getReservationsByUser(reservations);
            adapter.setUserReservations(userReservations);

            // Show/hide empty view based on whether there are reservations
            if (reservations == null || reservations.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                reservationsRecyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                reservationsRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        reservationsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    /**
     * Adapter for reservations grouped by user with expandable sections
     */
    private class AdminReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_USER_HEADER = 0;
        private static final int TYPE_RESERVATION = 1;

        // Data structures
        private final List<Object> visibleItems = new ArrayList<>();
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        private Map<String, List<Reservation>> userReservationsMap = new HashMap<>();
        private final Set<String> expandedUsers = new HashSet<>();

        public void setUserReservations(Map<String, List<Reservation>> userReservations) {
            this.userReservationsMap = userReservations;
            updateVisibleItems();
        }

        /**
         * Updates visible items based on which users are expanded
         */
        private void updateVisibleItems() {
            visibleItems.clear();

            for (Map.Entry<String, List<Reservation>> entry : userReservationsMap.entrySet()) {
                String userEmail = entry.getKey();
                List<Reservation> userReservations = entry.getValue();

                // Always add user header
                visibleItems.add(userEmail);

                // Add reservations only if this user is expanded
                if (expandedUsers.contains(userEmail)) {
                    visibleItems.addAll(userReservations);
                }
            }

            notifyDataSetChanged();
        }

        /**
         * Toggle expansion of a user's reservations
         */
        private void toggleUserExpansion(String userEmail) {
            if (expandedUsers.contains(userEmail)) {
                expandedUsers.remove(userEmail);
            } else {
                expandedUsers.add(userEmail);
            }
            updateVisibleItems();
        }

        @Override
        public int getItemViewType(int position) {
            return (visibleItems.get(position) instanceof String) ? TYPE_USER_HEADER : TYPE_RESERVATION;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_USER_HEADER) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user_header, parent, false);
                return new UserHeaderViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user_reservation, parent, false);
                return new ReservationViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserHeaderViewHolder) {
                String userEmail = (String) visibleItems.get(position);
                ((UserHeaderViewHolder) holder).bind(userEmail, expandedUsers.contains(userEmail));
            } else if (holder instanceof ReservationViewHolder) {
                ((ReservationViewHolder) holder).bind((Reservation) visibleItems.get(position), dateFormat);
            }
        }

        @Override
        public int getItemCount() {
            return visibleItems.size();
        }

        /**
         * ViewHolder for user headers
         */
        class UserHeaderViewHolder extends RecyclerView.ViewHolder {
            private final TextView userEmailTextView;
            private final ImageView expandIcon;
            private String userEmail;

            UserHeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                userEmailTextView = itemView.findViewById(R.id.tvUserEmail);
                expandIcon = itemView.findViewById(R.id.ivExpandIcon);

                // Set click listener for the entire header
                itemView.setOnClickListener(v -> {
                    if (userEmail != null) {
                        toggleUserExpansion(userEmail);
                    }
                });
            }

            void bind(String userEmail, boolean isExpanded) {
                this.userEmail = userEmail;
                userEmailTextView.setText(userEmail);

                // Update the expand/collapse icon
                if (expandIcon != null) {
                    expandIcon.setImageResource(isExpanded ?
                            R.drawable.ic_expand_less : R.drawable.ic_expand_more);
                }
            }
        }

        /**
         * ViewHolder for reservation items
         */
        class ReservationViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvPropertyId;
            private final TextView tvPropertyTitle;
            private final TextView tvPropertyDescription;
            private final TextView tvPropertyLocation;
            private final TextView tvReservationStartDate;
            private final TextView tvReservationEndDate;
            private final TextView tvReservationStatus;
            private final ImageView ivPropertyImage;

            ReservationViewHolder(@NonNull View itemView) {
                super(itemView);
                tvPropertyId = itemView.findViewById(R.id.tvPropertyId);
                tvPropertyTitle = itemView.findViewById(R.id.tvPropertyTitle);
                tvPropertyDescription = itemView.findViewById(R.id.tvPropertyDescription);
                tvPropertyLocation = itemView.findViewById(R.id.tvPropertyLocation);
                tvReservationStartDate = itemView.findViewById(R.id.tvReservationStartDate);
                tvReservationEndDate = itemView.findViewById(R.id.tvReservationEndDate);
                tvReservationStatus = itemView.findViewById(R.id.tvReservationStatus);
                ivPropertyImage = itemView.findViewById(R.id.ivPropertyImage);
            }

            void bind(Reservation reservation, SimpleDateFormat dateFormat) {
                // Property ID
                tvPropertyId.setText("Property ID: #" + reservation.getPropertyId());

                // Property details
                if (reservation.getProperty() != null) {
                    tvPropertyTitle.setText(reservation.getProperty().getTitle());
                    tvPropertyDescription.setText(reservation.getProperty().getDescription());
                    tvPropertyLocation.setText(reservation.getProperty().getLocation());

                    // Load property image if available
                    if (reservation.getProperty() != null && reservation.getProperty().getImage() != null) {

                        Glide.with(itemView.getContext())
                                .load(reservation.getProperty().getImage())
                                .placeholder(R.drawable.ic_building)
                                .error(R.drawable.ic_building)
                                .into(ivPropertyImage);
                    }
                    else {
                        ivPropertyImage.setImageResource(R.drawable.ic_building);
                    }
                } else {
                    tvPropertyTitle.setText("--");
                    tvPropertyDescription.setText("Property details not available");
                    tvPropertyLocation.setText("Location not specified");
                    ivPropertyImage.setImageResource(R.drawable.ic_building);
                }

                // Reservation dates
                tvReservationStartDate.setText(dateFormat.format(reservation.getStartDate()));
                tvReservationEndDate.setText(dateFormat.format(reservation.getEndDate()));

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
