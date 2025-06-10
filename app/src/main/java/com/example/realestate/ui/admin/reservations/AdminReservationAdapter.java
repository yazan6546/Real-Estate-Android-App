package com.example.realestate.ui.admin.reservations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realestate.R;
import com.example.realestate.domain.model.Reservation;
import com.example.realestate.domain.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Adapter for displaying all user reservations in admin view
 * Groups reservations by user with a header for each user
 */
public class AdminReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER_HEADER = 0;
    private static final int VIEW_TYPE_RESERVATION = 1;

    private List<Object> items = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    
    // Track expanded state for each user
    private Map<String, Boolean> expandedUsers = new HashMap<>();
    // Store user reservations mapping for expand/collapse functionality
    private Map<User, List<Reservation>> userReservationsMap = new HashMap<>();

    /**
     * Set reservations data grouped by user
     */
    public void setUserReservations(Map<User, List<Reservation>> userReservationsMap) {
        this.userReservationsMap = userReservationsMap;
        rebuildItemsList();
    }

    /**
     * Rebuild the items list based on current expanded states
     */
    private void rebuildItemsList() {
        items.clear();

        // For each user, add a header and their reservations if expanded
        for (Map.Entry<User, List<Reservation>> entry : userReservationsMap.entrySet()) {
            User user = entry.getKey();
            List<Reservation> reservations = entry.getValue();

            if (reservations != null && !reservations.isEmpty()) {
                // Add user header
                items.add(user);

                // Add reservations only if this user is expanded
                String userKey = getUserKey(user);
                if (expandedUsers.getOrDefault(userKey, false)) {
                    items.addAll(reservations);
                }
            }
        }

        notifyDataSetChanged();
    }

    /**
     * Generate a unique key for each user
     */
    private String getUserKey(User user) {
        return user.getEmail(); // Use email as unique identifier
    }

    /**
     * Toggle the expanded state of a user
     */
    private void toggleUserExpansion(User user) {
        String userKey = getUserKey(user);
        boolean isCurrentlyExpanded = expandedUsers.getOrDefault(userKey, false);
        expandedUsers.put(userKey, !isCurrentlyExpanded);
        rebuildItemsList();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof User) {
            return VIEW_TYPE_USER_HEADER;
        } else {
            return VIEW_TYPE_RESERVATION;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_USER_HEADER) {
            View view = inflater.inflate(R.layout.item_user_header, parent, false);
            return new UserHeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_user_reservation, parent, false);
            return new ReservationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserHeaderViewHolder) {
            UserHeaderViewHolder headerHolder = (UserHeaderViewHolder) holder;
            User user = (User) items.get(position);
            
            // Calculate total reservation count for this user
            List<Reservation> userReservations = userReservationsMap.get(user);
            int reservationCount = userReservations != null ? userReservations.size() : 0;
            
            // Check if user is expanded
            String userKey = getUserKey(user);
            boolean isExpanded = expandedUsers.getOrDefault(userKey, false);
            
            headerHolder.bind(user, reservationCount, isExpanded, this::toggleUserExpansion);
        } else if (holder instanceof ReservationViewHolder) {
            ReservationViewHolder reservationHolder = (ReservationViewHolder) holder;
            Reservation reservation = (Reservation) items.get(position);
            reservationHolder.bind(reservation, dateFormat);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder for user headers
     */
    static class UserHeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName;
        private final TextView tvUserEmail;
        private final TextView tvReservationCount;
        private final ImageView ivExpandIcon;
        private final ImageView ivUserAvatar;

        public UserHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvReservationCount = itemView.findViewById(R.id.tvReservationCount);
            ivExpandIcon = itemView.findViewById(R.id.ivExpandIcon);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
        }

        public void bind(User user, int reservationCount, boolean isExpanded, UserClickListener clickListener) {
            // Display user's full name
            String fullName = user.getFirstName() + " " + user.getLastName();
            tvUserName.setText(fullName);

            // Display email
            tvUserEmail.setText(user.getEmail());

            // Display reservation count
            String reservationText = reservationCount + " " +
                    (reservationCount == 1 ? "reservation" : "reservations");
            tvReservationCount.setText(reservationText);

            // Set avatar image
            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(user.getProfileImage())
                        .placeholder(R.drawable.ic_person)
                        .into(ivUserAvatar);
            } else {
                // Use default avatar
                ivUserAvatar.setImageResource(R.drawable.ic_person);
            }

            // Set expand icon rotation based on expanded state
            ivExpandIcon.setRotation(isExpanded ? 180 : 0);

            // Set click listener for expanding/collapsing
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onUserClick(user);
                }
            });
        }
    }

    /**
     * Interface for handling user header clicks
     */
    interface UserClickListener {
        void onUserClick(User user);
    }

    /**
     * ViewHolder for reservation items
     * Reusing the same layout as the user reservation adapter
     */
    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvPropertyId;
        private final TextView tvPropertyTitle;
        private final TextView tvPropertyType;
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
            tvPropertyType = itemView.findViewById(R.id.tvPropertyType);
            tvPropertyDescription = itemView.findViewById(R.id.tvPropertyDescription);
            tvPropertyLocation = itemView.findViewById(R.id.tvPropertyLocation);
            tvReservationStartDateTime = itemView.findViewById(R.id.tvReservationStartDate);
            tvReservationEndDateTime = itemView.findViewById(R.id.tvReservationEndDate);
            tvReservationStatus = itemView.findViewById(R.id.tvReservationStatus);
            imageView = itemView.findViewById(R.id.ivPropertyImage);
        }

        public void bind(Reservation reservation, SimpleDateFormat dateFormat) {
            // Set property details
            if (reservation.getProperty() != null) {
                tvPropertyId.setText("Property ID: #" + reservation.getProperty().getPropertyId());
                tvPropertyTitle.setText(reservation.getProperty().getTitle());
                tvPropertyType.setText(reservation.getProperty().getType());
                tvPropertyDescription.setText(reservation.getProperty().getDescription());
                tvPropertyLocation.setText(reservation.getProperty().getLocation());
            } else {
                tvPropertyId.setText("Property ID: #" + reservation.getPropertyId());
                tvPropertyTitle.setText("Property Not Available");
                tvPropertyType.setText("Unknown Type");
                tvPropertyDescription.setText("Property details not available");
                tvPropertyLocation.setText("Location not specified");
            }

            // Format dates
            tvReservationStartDateTime.setText("Start: " + dateFormat.format(reservation.getStartDate()));
            tvReservationEndDateTime.setText("End: " + dateFormat.format(reservation.getEndDate()));

            // Set status with appropriate color
            tvReservationStatus.setText(reservation.getStatus());

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

            // Load property image if available
            if (reservation.getProperty() != null && 
                reservation.getProperty().getImageUrl() != null && 
                !reservation.getProperty().getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(reservation.getProperty().getImageUrl())
                        .placeholder(R.drawable.ic_building)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_building);
            }
        }
    }
}
