package com.example.realestate.ui.user.properties;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;
import com.example.realestate.domain.model.Property;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private List<Property> properties = new ArrayList<>();
    private OnPropertyActionListener listener;
    private Context context;

    public interface OnPropertyActionListener {
        void onFavoriteClick(Property property, boolean isCurrentlyFavorite);

        void onReserveClick(Property property);

        void onPropertyClick(Property property);

        void checkFavoriteStatus(Property property, FavoriteStatusCallback callback);
    }

    public interface FavoriteStatusCallback {
        void onStatusReceived(boolean isFavorite);
    }

    public PropertyAdapter(Context context) {
        this.context = context;
    }

    public void setOnPropertyActionListener(OnPropertyActionListener listener) {
        this.listener = listener;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties != null ? properties : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void refreshFavoriteStatus(int propertyId) {
        for (int i = 0; i < properties.size(); i++) {
            if (properties.get(i).getPropertyId() == propertyId) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_property_browse, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.bind(property);
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    class PropertyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPropertyImage;
        private TextView tvDiscountBadge;
        private ImageView ivFeaturedStar;
        private TextView tvPropertyType;
        private TextView tvDiscountText;
        private TextView tvOriginalPrice;
        private TextView tvPropertyPrice;
        private TextView tvPropertyTitle;
        private TextView tvPropertyDescription;
        private TextView tvPropertyLocation;
        private TextView tvBedrooms;
        private TextView tvBathrooms;
        private TextView tvArea;
        private Button btnAddToFavorites;
        private Button btnReserveProperty;

        private boolean isCurrentlyFavorite = false;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews(itemView);
            setClickListeners();
        }

        private void initViews(View itemView) {
            ivPropertyImage = itemView.findViewById(R.id.ivPropertyImage);
            tvDiscountBadge = itemView.findViewById(R.id.tvDiscountBadge);
            ivFeaturedStar = itemView.findViewById(R.id.ivFeaturedStar);
            tvPropertyType = itemView.findViewById(R.id.tvPropertyType);
            tvDiscountText = itemView.findViewById(R.id.tvDiscountText);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvPropertyPrice = itemView.findViewById(R.id.tvPropertyPrice);
            tvPropertyTitle = itemView.findViewById(R.id.tvPropertyTitle);
            tvPropertyDescription = itemView.findViewById(R.id.tvPropertyDescription);
            tvPropertyLocation = itemView.findViewById(R.id.tvPropertyLocation);
            tvBedrooms = itemView.findViewById(R.id.tvBedrooms);
            tvBathrooms = itemView.findViewById(R.id.tvBathrooms);
            tvArea = itemView.findViewById(R.id.tvArea);
            btnAddToFavorites = itemView.findViewById(R.id.btnAddToFavorites);
            btnReserveProperty = itemView.findViewById(R.id.btnReserveProperty);
        }

        private void setClickListeners() {
            btnAddToFavorites.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onFavoriteClick(properties.get(getAdapterPosition()), isCurrentlyFavorite);
                }
            });

            btnReserveProperty.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onReserveClick(properties.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Property property) {
            // Basic property information
            tvPropertyTitle.setText(property.getTitle());
            tvPropertyDescription.setText(property.getDescription());
            tvPropertyLocation.setText(property.getLocation());
            tvPropertyType.setText(property.getType());

            // Property details
            tvBedrooms.setText(property.getBedrooms() + " Beds");
            tvBathrooms.setText(property.getBathrooms() + " Baths");
            tvArea.setText(property.getArea());

            // Price handling with discount logic
            setupPricing(property);

            // Featured property indicator
            if (property.isFeatured()) {
                ivFeaturedStar.setVisibility(View.VISIBLE);
            } else {
                ivFeaturedStar.setVisibility(View.GONE);
            }

            // Property image - For now using default icon
            // In a real app, you would load the image using a library like Glide or Picasso
            // Example: Glide.with(context).load(property.getImage()).into(ivPropertyImage);
            ivPropertyImage.setImageResource(R.drawable.ic_building);

            // Check and set favorite status
            setupFavoriteButton(property);
        }

        private void setupFavoriteButton(Property property) {
            // Set initial state while checking
            btnAddToFavorites.setText("Checking...");
            btnAddToFavorites.setEnabled(false);

            if (listener != null) {
                listener.checkFavoriteStatus(property, isFavorite -> {
                    isCurrentlyFavorite = isFavorite;
                    updateFavoriteButtonState();
                });
            }
        }

        private void updateFavoriteButtonState() {
            btnAddToFavorites.setEnabled(true);
            if (isCurrentlyFavorite) {
                btnAddToFavorites.setText("Remove from Favorites");
                btnAddToFavorites.setBackgroundTintList(context.getColorStateList(android.R.color.holo_red_light));
            } else {
                btnAddToFavorites.setText("Add to Favorites");
                btnAddToFavorites.setBackgroundTintList(context.getColorStateList(android.R.color.holo_blue_light));
            }
        }

        private void setupPricing(Property property) {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            double price = property.getPrice();
            double discount = property.getDiscount();

            if (discount > 0) {
                // Show discount information
                double originalPrice = price / (1 - discount / 100);
                double discountAmount = discount;

                // Show discount badge
                tvDiscountBadge.setText(String.format("%.0f%% OFF", discountAmount));
                tvDiscountBadge.setVisibility(View.VISIBLE);

                // Show discount text
                tvDiscountText.setVisibility(View.VISIBLE);

                // Show original price with strikethrough
                tvOriginalPrice.setText(currencyFormat.format(originalPrice) + "/month");
                tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvOriginalPrice.setVisibility(View.VISIBLE);

                // Show discounted price
                tvPropertyPrice.setText(currencyFormat.format(price) + "/month");
            } else {
                // No discount - show regular price
                tvDiscountBadge.setVisibility(View.GONE);
                tvDiscountText.setVisibility(View.GONE);
                tvOriginalPrice.setVisibility(View.GONE);

                tvPropertyPrice.setText(currencyFormat.format(price) + "/month");
            }
        }
    }
}
