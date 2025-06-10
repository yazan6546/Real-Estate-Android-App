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

    /**
     * Set reservations data grouped by user
     */
    public void setUserReservations(Map<User, List<Reservation>> userReservationsMap) {
        items.clear();

        // For each user, add a header followed by their reservations
        for (Map.Entry<User, List<Reservation>> entry : userReservationsMap.entrySet()) {
            User user = entry.getKey();
            List<Reservation> reservations = entry.getValue();

            if (reservations != null && !reservations.isEmpty()) {
                // Add user header
                items.add(user);

                // Add all reservations for this user
                items.addAll(reservations);
            }
        }

        notifyDataSetChanged();
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
            // Calculate reservation count for this user
            int reservationCount = 0;
            int index = position + 1;

            while (index < items.size() && items.get(index) instanceof Reservation) {
                reservationCount++;
                index++;
            }
            headerHolder.bind(user, reservationCount);
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

        public void bind(User user, int reservationCount) {
            // Display user's full name
            String fullName = user.getFirstName() + " " + user.getLastName();
            tvUserName.setText(fullName);

            // Display email
            tvUserEmail.setText(user.getEmail());

            // Display reservation count
            String reservationText = reservationCount + " " +
                    (reservationCount == 1 ? "reservation" : "reservations");
            tvReservationCount.setText(reservationText);

            // Set avatar image - could use Glide here if user has a profile image URL
            // For now we'll use a placeholder with the first letter of their name
            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(user.getProfileImage())
                        .placeholder(R.drawable.ic_person)
                        .into(ivUserAvatar);
            } else {
                // Use default avatar
                ivUserAvatar.setImageResource(R.drawable.ic_person);
            }

            // Set click listener for expanding/collapsing
            itemView.setOnClickListener(v -> {
                // Toggle visibility of this user's reservations
                // This would require additional implementation to track expanded state
                ivExpandIcon.setRotation(ivExpandIcon.getRotation() == 0 ? 180 : 0);
            });
        }
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
            tvPropertyId.setText("Property ID: #" + reservation.getProperty().getPropertyId());
            tvPropertyTitle.setText(reservation.getProperty().getTitle());
            tvPropertyType.setText(reservation.getProperty().getType());
            tvPropertyDescription.setText(reservation.getProperty().getDescription());
            tvPropertyLocation.setText(reservation.getProperty().getLocation());

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
            if (reservation.getProperty().getImageUrl() != null && !reservation.getProperty().getImageUrl().isEmpty()) {
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
