package com.example.realestate.ui.common;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realestate.R;
import com.example.realestate.domain.model.Property;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A base adapter class for displaying property data in different contexts.
 * This reduces code duplication between PropertyAdapter and SpecialOffersAdapter.
 */
public abstract class BasePropertyAdapter<VH extends BasePropertyAdapter.BasePropertyViewHolder>
    extends RecyclerView.Adapter<VH> {

    protected List<Property> properties = new ArrayList<>();
    protected Context context;

    public BasePropertyAdapter(Context context) {
        this.context = context;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties != null ? properties : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Property property = properties.get(position);
        holder.bindCommonData(property);
        holder.bindSpecificData(property);
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    /**
     * Base view holder for property items that handles common property data binding
     */
    public abstract static class BasePropertyViewHolder extends RecyclerView.ViewHolder {
        // Common views across property layouts
        protected ImageView ivPropertyImage;
        protected TextView tvDiscountBadge;
        protected ImageView ivFeaturedStar;
        protected TextView tvPropertyType;
        protected TextView tvDiscountText;
        protected TextView tvOriginalPrice;
        protected TextView tvPropertyPrice;
        protected TextView tvPropertyTitle;
        protected TextView tvPropertyDescription;
        protected TextView tvPropertyLocation;
        protected TextView tvBedrooms;
        protected TextView tvBathrooms;
        protected TextView tvArea;

        protected Context context;

        public BasePropertyViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            initCommonViews(itemView);
        }

        private void initCommonViews(View itemView) {
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
        }

        /**
         * Binds common property data to views shared across all property item layouts
         */
        public void bindCommonData(Property property) {
            // Basic property information
            tvPropertyTitle.setText(property.getTitle());
            tvPropertyDescription.setText(property.getDescription());
            tvPropertyLocation.setText(property.getLocation());
            tvPropertyType.setText(property.getType());

            // Property details
            tvBedrooms.setText(property.getBedrooms() + " Beds");
            tvBathrooms.setText(property.getBathrooms() + " Baths");
            tvArea.setText(property.getArea());

            // Featured property indicator
            ivFeaturedStar.setVisibility(property.isFeatured() ? View.VISIBLE : View.GONE);

            // Load property image if available
            if (property.getImageUrl() != null && !property.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(property.getImageUrl())
                        .placeholder(R.drawable.ic_building)
                        .error(R.drawable.ic_building)
                        .into(ivPropertyImage);
            } else {
                ivPropertyImage.setImageResource(R.drawable.ic_building);
            }

            // Setup pricing display
            setupPricing(property);
        }

        /**
         * Implement in child classes to handle specific data binding
         */
        protected abstract void bindSpecificData(Property property);

        /**
         * Common pricing logic that can be customized by overriding
         */
        protected void setupPricing(Property property) {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            double originalPrice = property.getPrice();
            double discount = property.getDiscount();

            if (discount > 0) {
                // Calculate discounted price
                double discountedPrice = originalPrice * (1 - discount / 100);

                // Show discount badge
                tvDiscountBadge.setText(String.format("%.0f%% OFF", discount));
                tvDiscountBadge.setVisibility(View.VISIBLE);

                // Show discount text (if available)
                tvDiscountText.setVisibility(View.VISIBLE);

                // Show original price with strikethrough
                tvOriginalPrice.setText(currencyFormat.format(originalPrice) + "/month");
                tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvOriginalPrice.setVisibility(View.VISIBLE);

                // Show discounted price
                tvPropertyPrice.setText(currencyFormat.format(discountedPrice) + "/month");
            } else {
                // No discount - default display
                tvDiscountBadge.setVisibility(View.GONE);
                tvDiscountText.setVisibility(View.GONE);
                tvOriginalPrice.setVisibility(View.GONE);
                tvPropertyPrice.setText(currencyFormat.format(originalPrice) + "/month");
            }
        }
    }
}
