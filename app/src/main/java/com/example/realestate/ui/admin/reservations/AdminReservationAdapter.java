package com.example.realestate.ui.admin.reservations;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realestate.R;
import com.example.realestate.domain.model.Reservation;
import com.example.realestate.domain.model.User;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AdminReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER_HEADER = 0;
    private static final int VIEW_TYPE_RESERVATION = 1;

    // Items shown in the RecyclerView
    private final List<Object> items = new ArrayList<>();

    // Map of users to their reservations
    private Map<User, List<Reservation>> userReservationsMap = new HashMap<>();

    // Track expanded users by email
    private Set<String> expandedUsers = new HashSet<>();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    /**
     * Set the user reservations data
     */
    public void setUserReservations(Map<User, List<Reservation>> map) {
        userReservationsMap = map != null ? new HashMap<>(map) : new HashMap<>();
        rebuildItemsList();
    }

    /**
     * Rebuild the items list based on current expanded state
     */
    private void rebuildItemsList() {
        items.clear();

        // Get users and sort them by name for consistent ordering
        List<User> sortedUsers = new ArrayList<>(userReservationsMap.keySet());
        sortedUsers.sort((user1, user2) -> {
            // Primary sort by first name
            int firstNameCompare = user1.getFirstName().compareToIgnoreCase(user2.getFirstName());
            if (firstNameCompare != 0) {
                return firstNameCompare;
            }
            // Secondary sort by last name
            int lastNameCompare = user1.getLastName().compareToIgnoreCase(user2.getLastName());
            if (lastNameCompare != 0) {
                return lastNameCompare;
            }
            // Final sort by email to ensure complete consistency
            return user1.getEmail().compareToIgnoreCase(user2.getEmail());
        });

        // For each user in sorted order, add header and reservations if expanded
        for (User user : sortedUsers) {
            List<Reservation> reservations = userReservationsMap.get(user);

            if (user != null && reservations != null && !reservations.isEmpty()) {
                // Always add user header
                items.add(user);

                // Add reservations if user is expanded
                if (expandedUsers.contains(user.getEmail())) {
                    // Sort reservations by start date (descending)
                    List<Reservation> sortedReservations = new ArrayList<>(reservations);
                    sortedReservations.sort((r1, r2) -> {
                        // Most recent first
                        int dateCompare = r2.getStartDate().compareTo(r1.getStartDate());
                        if (dateCompare != 0) {
                            return dateCompare;
                        }
                        // If dates are the same, sort by property ID for consistency
                        return Integer.compare(r1.getPropertyId(), r2.getPropertyId());
                    });
                    items.addAll(sortedReservations);
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 0 || position >= items.size()) {
            return VIEW_TYPE_RESERVATION;
        }
        return items.get(position) instanceof User ? VIEW_TYPE_USER_HEADER : VIEW_TYPE_RESERVATION;
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
        if (position < 0 || position >= items.size()) {
            return;
        }

        if (holder instanceof UserHeaderViewHolder) {
            User user = (User) items.get(position);
            boolean isExpanded = expandedUsers.contains(user.getEmail());

            ((UserHeaderViewHolder) holder).bind(user,
                userReservationsMap.get(user).size(),
                isExpanded);

            // Set click listener with animations
            holder.itemView.setOnClickListener(view -> {
                ImageView expandIcon = holder.itemView.findViewById(R.id.ivExpandIcon);
                
                // Toggle expansion state
                boolean wasExpanded = expandedUsers.contains(user.getEmail());
                
                if (wasExpanded) {
                    // Collapsing - animate arrow rotation
                    Animation rotateCollapse = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_collapse);
                    expandIcon.startAnimation(rotateCollapse);
                    
                    // Remove from expanded set
                    expandedUsers.remove(user.getEmail());
                } else {
                    // Expanding - animate arrow rotation
                    Animation rotateExpand = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_expand);
                    expandIcon.startAnimation(rotateExpand);
                    
                    // Add to expanded set
                    expandedUsers.add(user.getEmail());
                }
                
                // Rebuild the list to show/hide reservations
                rebuildItemsList();
            });

        } else if (holder instanceof ReservationViewHolder) {
            ((ReservationViewHolder) holder).bind((Reservation) items.get(position), dateFormat);
            
            // Apply expand animation only for newly visible items
            Animation slideDown = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_down);
            holder.itemView.startAnimation(slideDown);
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

        public void bind(User user, int reservationCount, boolean isExpanded) {
            // Set user name
            String fullName = user.getFirstName() + " " + user.getLastName();
            tvUserName.setText(fullName);

            // Set email
            tvUserEmail.setText(user.getEmail());

            // Set reservation count
            String countText = reservationCount + " " +
                (reservationCount == 1 ? "reservation" : "reservations");
            tvReservationCount.setText(countText);

            // Set initial arrow state without animation (for recycled views)
            ivExpandIcon.setRotation(isExpanded ? 180 : 0);

            // Load profile image if available
            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                try {
                    // Create file object from the stored filename using itemView context
                    File imageFile = new File(itemView.getContext().getFilesDir(), user.getProfileImage());

                    // Use Glide to load the profile image from internal storage
                    if (imageFile.exists()) {
                        // Remove tint for actual profile images
                        ivUserAvatar.setImageTintList(null);
                        
                        Glide.with(itemView.getContext())
                                .load(imageFile)
                                .placeholder(R.drawable.ic_person)
                                .error(R.drawable.ic_person)
                                .circleCrop() // Make the image circular
                                .into(ivUserAvatar);
                    } else {
                        // Restore tint for default icon
                        ivUserAvatar.setImageTintList(itemView.getContext().getColorStateList(R.color.primary_blue));
                        ivUserAvatar.setImageResource(R.drawable.ic_person);
                    }
                } catch (Exception e) {
                    Log.e("AdminReservationAdapter", "Error loading profile image", e);
                    // Restore tint for default icon
                    ivUserAvatar.setImageTintList(itemView.getContext().getColorStateList(R.color.primary_blue));
                    ivUserAvatar.setImageResource(R.drawable.ic_person);
                }
            } else {
                // Use default placeholder if no profile image is set and restore tint
                ivUserAvatar.setImageTintList(itemView.getContext().getColorStateList(R.color.primary_blue));
                ivUserAvatar.setImageResource(R.drawable.ic_person);
            }
        }
    }

    /**
     * ViewHolder for reservations
     */
    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        // Retain existing fields and methods
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
            try {
                // Set property details
                tvPropertyId.setText("Property ID: #" + reservation.getPropertyId());

                if (reservation.getProperty() != null) {
                    tvPropertyTitle.setText(reservation.getProperty().getTitle());
                    tvPropertyType.setText(reservation.getProperty().getType());
                    tvPropertyDescription.setText(reservation.getProperty().getDescription());
                    tvPropertyLocation.setText(reservation.getProperty().getLocation());

                    // Load image if available
                    if (reservation.getProperty().getImageUrl() != null &&
                        !reservation.getProperty().getImageUrl().isEmpty()) {

                        Glide.with(itemView.getContext())
                                .load(reservation.getProperty().getImageUrl())
                                .placeholder(R.drawable.ic_building)
                                .into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.ic_building);
                    }
                } else {
                    tvPropertyTitle.setText("Property Not Available");
                    tvPropertyType.setText("Unknown Type");
                    tvPropertyDescription.setText("No description available");
                    tvPropertyLocation.setText("Unknown location");
                    imageView.setImageResource(R.drawable.ic_building);
                }

                // Set dates
                tvReservationStartDateTime.setText("Start: " + dateFormat.format(reservation.getStartDate()));
                tvReservationEndDateTime.setText("End: " + dateFormat.format(reservation.getEndDate()));

                // Set status with color
                tvReservationStatus.setText(reservation.getStatus());

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
                tvReservationStatus.setTextColor(color);
            } catch (Exception e) {
                // Handle binding errors
            }
        }
    }
}
