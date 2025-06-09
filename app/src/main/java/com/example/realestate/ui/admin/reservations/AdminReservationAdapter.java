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
            headerHolder.bind(user);
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
        private final TextView tvUserEmail;

        public UserHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
        }

        public void bind(User user) {
            // Display user's full name and email
            String displayText = user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")";
            tvUserEmail.setText(displayText);
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
            tvReservationStartDateTime = itemView.findViewById(R.id.tvReservationStartDateTime);
            tvReservationEndDateTime = itemView.findViewById(R.id.tvReservationEndDateTime);
            tvReservationStatus = itemView.findViewById(R.id.tvReservationStatus);
            imageView = itemView.findViewById(R.id.ivPropertyImage);
        }

        public void bind(Reservation reservation, SimpleDateFormat dateFormat) {
            // Set property details
            tvPropertyId.setText("Property ID: #" + reservation.getPropertyId());
            tvPropertyTitle.setText(reservation.getPropertyTitle());
            tvPropertyType.setText(reservation.getPropertyType());
            tvPropertyDescription.setText(reservation.getPropertyDescription());
            tvPropertyLocation.setText(reservation.getPropertyLocation());

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
            if (reservation.getPropertyImageUrl() != null && !reservation.getPropertyImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(reservation.getPropertyImageUrl())
                        .placeholder(R.drawable.ic_building)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_building);
            }
        }
    }
}
