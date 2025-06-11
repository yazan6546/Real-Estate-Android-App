package com.example.realestate.ui.admin.special;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;
import com.example.realestate.domain.model.Property;
import com.example.realestate.ui.common.BasePropertyAdapter;

import java.text.NumberFormat;
import java.util.Locale;

public class SpecialOffersAdapter extends BasePropertyAdapter<SpecialOffersAdapter.SpecialOfferViewHolder> {

    private OnOfferActionListener listener;

    public interface OnOfferActionListener {
        void onToggleOffer(Property property, double discountPercentage);
        void onValidationError(String errorMessage);
    }

    public SpecialOffersAdapter(Context context) {
        super(context);
    }

    public void setOnOfferActionListener(OnOfferActionListener listener) {
        this.listener = listener;
    }

    public void animateOfferCreation(int propertyId, double discountPercentage) {
        for (int i = 0; i < properties.size(); i++) {
            if (properties.get(i).getPropertyId() == propertyId) {
                // Find the RecyclerView from the context
                if (context instanceof androidx.fragment.app.FragmentActivity) {
                    androidx.fragment.app.FragmentActivity activity = (androidx.fragment.app.FragmentActivity) context;
                    RecyclerView recyclerView = activity.findViewById(R.id.offersRecyclerView);
                    if (recyclerView != null) {
                        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
                        if (holder instanceof SpecialOfferViewHolder) {
                            SpecialOfferViewHolder offerHolder = (SpecialOfferViewHolder) holder;
                            // Then trigger strikethrough and price animations
                            offerHolder.animateStrikethrough(true);
                            // Trigger button animation first
                            offerHolder.animateButtonChange(true);

                        }
                    }
                }
                break;
            }
        }
    }

    public void animateOfferRemoval(int propertyId) {
        for (int i = 0; i < properties.size(); i++) {
            if (properties.get(i).getPropertyId() == propertyId) {
                // Find the RecyclerView from the context
                if (context instanceof androidx.fragment.app.FragmentActivity) {
                    androidx.fragment.app.FragmentActivity activity = (androidx.fragment.app.FragmentActivity) context;
                    RecyclerView recyclerView = activity.findViewById(R.id.offersRecyclerView);
                    if (recyclerView != null) {
                        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
                        if (holder instanceof SpecialOfferViewHolder) {
                            SpecialOfferViewHolder offerHolder = (SpecialOfferViewHolder) holder;
                            // Trigger button animation first
                            offerHolder.animateButtonChange(false);
                            // Then trigger strikethrough removal animation
                            offerHolder.animateStrikethrough(false);
                        }
                    }
                }
                break;
            }
        }
    }

    @NonNull
    @Override
    public SpecialOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_special_offer, parent, false);
        return new SpecialOfferViewHolder(view, context);
    }

    class SpecialOfferViewHolder extends BasePropertyAdapter.BasePropertyViewHolder {
        private SeekBar seekBarDiscount;
        private TextView tvDiscountAmount;
        private Button btnCreateOffer;
        private double currentDiscount = 0;

        public SpecialOfferViewHolder(@NonNull View itemView, Context context) {
            super(itemView, context);
            initAdditionalViews(itemView);
            setupSeekBarListener();
        }

        private void initAdditionalViews(View itemView) {
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

        @Override
        protected void bindSpecificData(Property property) {
            // Set current discount from property
            currentDiscount = property.getDiscount();
            seekBarDiscount.setProgress((int) currentDiscount);

            // Special offer-specific handling
            updateDiscountDisplay();
            updateButtonState(property);
            setupButtonClickListener(property);

            // Override default behavior for discount text
            if (property.getDiscount() > 0) {
                tvDiscountText.setText("Special Offer Active!");
            } else {
                tvDiscountText.setText("Create Special Offer!");
            }

        }

        private void setupButtonClickListener(Property property) {
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
            animateButtonChange(property.getDiscount() > 0);
        }

        public void animateButtonChange(boolean isRemoveState) {
            // Fade out button
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(btnCreateOffer, "alpha", 1f, 0.3f);
            fadeOut.setDuration(150);
            
            fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    // Change button properties while faded
                    if (isRemoveState) {
                        btnCreateOffer.setText("REMOVE OFFER");
                        btnCreateOffer.setBackgroundTintList(context.getColorStateList(android.R.color.holo_red_light));
                    } else {
                        btnCreateOffer.setText("CREATE OFFER");
                        btnCreateOffer.setBackgroundTintList(context.getColorStateList(R.color.green_700));
                    }
                    
                    // Fade back in
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(btnCreateOffer, "alpha", 0.3f, 1f);
                    fadeIn.setDuration(150);
                    fadeIn.start();
                }
            });
            
            fadeOut.start();
        }

        public void animateStrikethrough(boolean show) {
            if (show) {
                // Animate strikethrough appearance
                tvOriginalPrice.setVisibility(View.VISIBLE);
                tvOriginalPrice.setAlpha(0f);
                tvOriginalPrice.setScaleY(0.8f);
                
                AnimatorSet strikethroughSet = new AnimatorSet();
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(tvOriginalPrice, "alpha", 0f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvOriginalPrice, "scaleY", 0.8f, 1f);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvOriginalPrice, "scaleX", 0.95f, 1f);
                
                strikethroughSet.playTogether(fadeIn, scaleY, scaleX);
                strikethroughSet.setDuration(300);
                strikethroughSet.setInterpolator(new AccelerateDecelerateInterpolator());
                
                // Animate the strikethrough line drawing
                ValueAnimator strikeAnimator = ValueAnimator.ofFloat(0f, 1f);
                strikeAnimator.setDuration(1000);
                strikeAnimator.setStartDelay(500);
                strikeAnimator.addUpdateListener(animation -> {
                    float progress = (float) animation.getAnimatedValue();
                    if (progress > 0.7f) {
                        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                });
                
                strikethroughSet.start();
                strikeAnimator.start();
                
            } else {
                // Animate strikethrough removal
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(tvOriginalPrice, "alpha", 1f, 0f);
                ObjectAnimator scaleDown = ObjectAnimator.ofFloat(tvOriginalPrice, "scaleY", 1f, 0.8f);
                
                AnimatorSet hideSet = new AnimatorSet();
                hideSet.playTogether(fadeOut, scaleDown);
                hideSet.setDuration(200);
                hideSet.addListener(new android.animation.AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        tvOriginalPrice.setVisibility(View.GONE);
                        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                });
                hideSet.start();
            }
        }

        public void animatePriceChange(String newPrice, boolean isDiscounted) {
            // Animate price text change
            ObjectAnimator scaleDown = ObjectAnimator.ofFloat(tvPropertyPrice, "scaleY", 1f, 0.8f);
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(tvPropertyPrice, "alpha", 1f, 0.6f);
            
            AnimatorSet changeOut = new AnimatorSet();
            changeOut.playTogether(scaleDown, fadeOut);
            changeOut.setDuration(150);
            
            changeOut.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    tvPropertyPrice.setText(newPrice);
                    
                    // Change color if it's a discounted price
                    if (isDiscounted) {
                        tvPropertyPrice.setTextColor(context.getColor(R.color.green_700));
                    } else {
                        tvPropertyPrice.setTextColor(context.getColor(android.R.color.black));
                    }
                    
                    ObjectAnimator scaleUp = ObjectAnimator.ofFloat(tvPropertyPrice, "scaleY", 0.8f, 1f);
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(tvPropertyPrice, "alpha", 0.6f, 1f);
                    
                    AnimatorSet changeIn = new AnimatorSet();
                    changeIn.playTogether(scaleUp, fadeIn);
                    changeIn.setDuration(200);
                    changeIn.setInterpolator(new AccelerateDecelerateInterpolator());
                    changeIn.start();
                }
            });
            
            changeOut.start();
        }
    }
}

