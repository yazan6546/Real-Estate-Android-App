package com.example.realestate.ui.admin.special;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
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
                            // Trigger star effect animation
                            offerHolder.animateStarEffect(true);

                            // Calculate new price with discount
                            Property property = properties.get(i);
                            double basePrice = property.getPrice();
                            double discountedPrice = basePrice * (1-discountPercentage/100);

                            // Animate the price change with a counting animation
                            offerHolder.animatePriceCountdown(basePrice, discountedPrice);
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
                            // Stop star effect animation
                            offerHolder.animateStarEffect(false);

                            // Get original and discounted price
                            Property property = properties.get(i);
                            double basePrice = property.getPrice();
                            double currentDiscount = property.getDiscount();
                            double discountedPrice = basePrice * (1-currentDiscount/100);

                            // Animate from discounted price back to original price
                            offerHolder.animatePriceCountdown(discountedPrice, basePrice);
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
        private ImageView ivFeaturedStar;
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
            ivFeaturedStar = itemView.findViewById(R.id.ivFeaturedStar);
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
                // Show star with sparkle effect when offer is active
                animateStarEffect(true);
            } else {
                tvDiscountText.setText("Create Special Offer!");
                // Hide star when no offer
                animateStarEffect(false);
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

        public void animatePriceCountdown(double startPrice, double endPrice) {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

            // Create a value animator to animate from start price to end price
            ValueAnimator priceAnimator = ValueAnimator.ofFloat((float) startPrice, (float) endPrice);
            // Increase the duration to 1500ms (1.5 seconds) for a more noticeable counting effect
            priceAnimator.setDuration(200);
            priceAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

            priceAnimator.addUpdateListener(animation -> {
                float animatedValue = (float) animation.getAnimatedValue();
                // Format the price with currency symbol and display it
                String formattedPrice = currencyFormat.format(animatedValue) + "/month";
                tvPropertyPrice.setText(formattedPrice);

                // Change color based on whether we're increasing or decreasing the price
                if (startPrice > endPrice) {
                    // Going to a discounted price (green)
                    tvPropertyPrice.setTextColor(context.getColor(R.color.green_700));
                } else {
                    // Going back to original price (black)
                    tvPropertyPrice.setTextColor(context.getColor(android.R.color.black));
                }
            });

            priceAnimator.start();
        }

        public void animateStarEffect(boolean show) {
            if (show) {
                // Show the star with appear animation
                ivFeaturedStar.setVisibility(View.VISIBLE);
                ivFeaturedStar.clearAnimation(); // Clear any existing animations
                
                // Load and start the appear animation
                Animation appearAnimation = AnimationUtils.loadAnimation(context, R.anim.star_appear_animation);
                appearAnimation.setFillAfter(true);
                
                // After appear animation, start the sparkle effect
                appearAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Start the continuous sparkle animation
                        Animation sparkleAnimation = AnimationUtils.loadAnimation(context, R.anim.star_sparkle_animation);
                        sparkleAnimation.setFillAfter(true);
                        ivFeaturedStar.startAnimation(sparkleAnimation);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                
                ivFeaturedStar.startAnimation(appearAnimation);
                
            } else {
                // Clear any ongoing animations first
                ivFeaturedStar.clearAnimation();
                
                // Hide the star with fade out animation
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(ivFeaturedStar, "alpha", 1f, 0f);
                ObjectAnimator scaleOut = ObjectAnimator.ofFloat(ivFeaturedStar, "scaleX", 1f, 0f);
                ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(ivFeaturedStar, "scaleY", 1f, 0f);
                
                AnimatorSet hideSet = new AnimatorSet();
                hideSet.playTogether(fadeOut, scaleOut, scaleOutY);
                hideSet.setDuration(300);
                
                hideSet.addListener(new android.animation.AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        ivFeaturedStar.setVisibility(View.GONE);
                        ivFeaturedStar.clearAnimation();
                        // Reset scale and alpha for next time
                        ivFeaturedStar.setScaleX(1f);
                        ivFeaturedStar.setScaleY(1f);
                        ivFeaturedStar.setAlpha(1f);
                    }
                });
                
                hideSet.start();
            }
        }

    }
}
