package com.example.realestate.ui.user.reservations;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying user reservations in a RecyclerView
 */
public class UserReservationAdapter extends RecyclerView.Adapter<UserReservationAdapter.ReservationViewHolder> {
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
                .inflate(R.layout.item_user_reservation, parent, false);
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

    /**
     * ViewHolder for reservation items
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
            // Property ID
            tvPropertyId.setText("Property ID: #" + reservation.getPropertyId());

            // Property details
            if (reservation.getProperty() != null) {
                tvPropertyTitle.setText(reservation.getProperty().getTitle());
                tvPropertyType.setText(reservation.getProperty().getType());
                tvPropertyDescription.setText(reservation.getProperty().getDescription());
                tvPropertyLocation.setText(reservation.getProperty().getLocation());
            } else {
                tvPropertyTitle.setText("--");
                tvPropertyType.setText("Unknown Type");
                tvPropertyDescription.setText(R.string.property_details_not_available);
                tvPropertyLocation.setText(R.string.location_not_specified);
            }

            // Reservation date and time
            String startDateTime = dateFormat.format(reservation.getStartDate());
            String endDateTime = dateFormat.format(reservation.getEndDate());

            tvReservationStartDateTime.setText(startDateTime);
            tvReservationEndDateTime.setText(endDateTime);

            // Load property image if available
            if (reservation.getProperty() != null && reservation.getProperty().getImage() != null) {
                Glide.with(itemView.getContext())
                        .load(reservation.getProperty().getImage())
                        .placeholder(R.drawable.ic_building)
                        .error(R.drawable.ic_building)
                        .into(imageView);
            } else {
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
