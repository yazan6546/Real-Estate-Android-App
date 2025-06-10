package com.example.realestate.ui.admin.special;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
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

public class SpecialOffersAdapter extends RecyclerView.Adapter<SpecialOffersAdapter.SpecialOfferViewHolder> {

    private List<Property> properties = new ArrayList<>();
    private OnOfferActionListener listener;
    private Context context;

    public interface OnOfferActionListener {
        void onToggleOffer(Property property, double discountPercentage);
        void onValidationError(String errorMessage);
    }

    public SpecialOffersAdapter(Context context) {
        this.context = context;
    }

    public void setOnOfferActionListener(OnOfferActionListener listener) {
        this.listener = listener;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties != null ? properties : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SpecialOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_special_offer, parent, false);
        return new SpecialOfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialOfferViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.bind(property);
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    class SpecialOfferViewHolder extends RecyclerView.ViewHolder {

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
        private SeekBar seekBarDiscount;
        private TextView tvDiscountAmount;
        private Button btnCreateOffer;

        private double currentDiscount = 0;

        public SpecialOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews(itemView);
            setupSeekBarListener();
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
            seekBarDiscount = itemView.findViewById(R.id.seekBarDiscount);
            tvDiscountAmount = itemView.findViewById(R.id.tvDiscountAmount);
            btnCreateOffer = itemView.findViewById(R.id.btnCreateOffer);
        }

        private void setupSeekBarListener() {
            seekBarDiscount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        currentDiscount = progress;
                        updateDiscountDisplay();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
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

            // Set current discount from property
            currentDiscount = property.getDiscount();
            seekBarDiscount.setProgress((int) currentDiscount);

            // Setup pricing and discount display
            setupPricing(property);
            updateDiscountDisplay();

            // Featured property indicator
            if (property.isFeatured()) {
                ivFeaturedStar.setVisibility(View.VISIBLE);
            } else {
                ivFeaturedStar.setVisibility(View.GONE);
            }

            // Load property image if available
            if (property.getImageUrl() != null && !property.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(property.getImageUrl())
                        .placeholder(R.drawable.ic_building)
                        .error(R.drawable.ic_building)
                        .into(ivPropertyImage);
            } else {
                ivPropertyImage.setImageResource(R.drawable.ic_building);
            }

            // Setup button behavior
            updateButtonState(property);
            btnCreateOffer.setOnClickListener(v -> {
                if (listener != null) {
                    if (property.getDiscount() > 0) {
                        // Remove offer
                        listener.onToggleOffer(property, 0);
                    } else {
                        // Validate discount before creating offer
                        if (currentDiscount == 0) {
                            listener.onValidationError("Discount should be greater than 0%");
                            return;
                        }
                        if (currentDiscount >= 100) {
                            listener.onValidationError("Discount cannot be 100% or more");
                            return;
                        }
                        if (currentDiscount < 1) {
                            listener.onValidationError("Discount should be at least 1%");
                            return;
                        }
                        
                        // Create offer with current discount
                        listener.onToggleOffer(property, currentDiscount);
                    }
                }
            });
        }

        private void setupPricing(Property property) {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            double currentPrice = property.getPrice();
            double discount = property.getDiscount();

            if (discount > 0) {

                // Show discount badge
                tvDiscountBadge.setText(String.format("%.0f%% OFF", discount));
                tvDiscountBadge.setVisibility(View.VISIBLE);

                // Show discount text
                tvDiscountText.setText("Special Offer Active!");
                tvDiscountText.setVisibility(View.VISIBLE);

                // Show original price with strikethrough
                tvOriginalPrice.setText(currencyFormat.format(currentPrice) + "/month");
                tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvOriginalPrice.setVisibility(View.VISIBLE);

                // Show discounted price (current price)
                tvPropertyPrice.setText(currencyFormat.format(currentPrice * (1-discount/100)) + "/month");
            } else {
                // No discount - show regular price
                tvDiscountBadge.setText("0% OFF");
                tvDiscountBadge.setVisibility(View.VISIBLE);
                tvDiscountText.setText("Create Special Offer!");
                tvDiscountText.setVisibility(View.VISIBLE);
                tvOriginalPrice.setVisibility(View.GONE);

                // Clear any previous paint flags
                tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                tvPropertyPrice.setText(currencyFormat.format(currentPrice) + "/month");
            }
        }

        private void updateDiscountDisplay() {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            Property property = properties.get(getAdapterPosition());
            double basePrice = property.getPrice();
            

            if (currentDiscount > 0) {
                double discountedPrice = basePrice * (1-currentDiscount/(100));
                double savingsAmount = basePrice - discountedPrice;
                tvDiscountAmount.setText(String.format("Discount: %.0f%% (%s off)", 
                    currentDiscount, currencyFormat.format(savingsAmount)));
            } else {
                tvDiscountAmount.setText("Discount: 0% ($0 off)");
            }
        }

        private void updateButtonState(Property property) {
            if (property.getDiscount() > 0) {
                btnCreateOffer.setText("REMOVE OFFER");
                btnCreateOffer.setBackgroundTintList(context.getColorStateList(android.R.color.holo_red_light));
            } else {
                btnCreateOffer.setText("CREATE OFFER");
                btnCreateOffer.setBackgroundTintList(context.getColorStateList(R.color.green_700));
            }
        }
    }
}