package com.example.realestate.ui.user.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.databinding.FragmentHomeBinding;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private Handler handler;
    private Runnable welcomeMessageRunnable;
    private String[] welcomeMessages = {
            "Discover your dream home with us! 🏠",
            "Find the perfect property for your family! 🏡",
            "Your next investment opportunity awaits! 💰",
            "Explore premium locations across the region! 🌟",
            "Making real estate dreams come true! ✨"
    };
    private int currentMessageIndex = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // Create ViewModel with repository dependencies
        homeViewModel = new ViewModelProvider(this,
                new HomeViewModel.Factory(
                        RealEstate.appContainer.getUserRepository(),
                        RealEstate.appContainer.getPropertyRepository()))
                .get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handler = new Handler(Looper.getMainLooper());

        initializeViews();
        startAnimations();
        startDynamicWelcomeMessages();
        addCounterAnimations();
    }

    private void initializeViews() {
        // Set personalized welcome message based on time of day
        TextView welcomeTitle = binding.getRoot().findViewById(R.id.welcomeTitle);
        TextView welcomeSubtitle = binding.getRoot().findViewById(R.id.welcomeSubtitle);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour < 12) {
            greeting = "Good Morning!\nWelcome to Real Estate";
        } else if (hour < 17) {
            greeting = "Good Afternoon!\nWelcome to Real Estate";
        } else {
            greeting = "Good Evening!\nWelcome to Real Estate";
        }

        welcomeTitle.setText(greeting);
    }

    private void startAnimations() {
        // Animate welcome card
        MaterialCardView welcomeCard = binding.getRoot().findViewById(R.id.welcomeCard);
        welcomeCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));

        // Animate about us card with delay
        MaterialCardView aboutUsCard = binding.getRoot().findViewById(R.id.aboutUsCard);
        handler.postDelayed(() -> {
            aboutUsCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left));
        }, 300);

        // Animate history card with delay
        MaterialCardView historyCard = binding.getRoot().findViewById(R.id.historyCard);
        handler.postDelayed(() -> {
            historyCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right));
        }, 600);

        // Animate stats card with delay
        MaterialCardView statsCard = binding.getRoot().findViewById(R.id.statsCard);
        handler.postDelayed(() -> {
            statsCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
        }, 900);
    }

    private void startDynamicWelcomeMessages() {
        TextView dynamicWelcome = binding.getRoot().findViewById(R.id.dynamicWelcome);

        welcomeMessageRunnable = new Runnable() {
            @Override
            public void run() {
                // Fade out
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(dynamicWelcome, "alpha", 1f, 0f);
                fadeOut.setDuration(500);
                fadeOut.start();

                // Change text and fade in
                handler.postDelayed(() -> {
                    dynamicWelcome.setText(welcomeMessages[currentMessageIndex]);
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(dynamicWelcome, "alpha", 0f, 1f);
                    fadeIn.setDuration(500);
                    fadeIn.start();

                    currentMessageIndex = (currentMessageIndex + 1) % welcomeMessages.length;
                }, 500);

                // Schedule next message change
                handler.postDelayed(this, 3000);
            }
        };

        // Start the cycle
        handler.postDelayed(welcomeMessageRunnable, 3000);
    }

    private void addCounterAnimations() {
        // Observe data from ViewModel and start animations when data is available
        homeViewModel.getUserCount().observe(getViewLifecycleOwner(), userCount -> {
            if (userCount != null) {
                // Ensure we're on the main thread for UI operations
                if (handler != null) {
                    handler.postDelayed(() -> {
                        // Check if the fragment is still active before updating UI
                        if (isAdded() && binding != null) {
                            // user count (clients)
                            int startValue = Math.max(0, userCount - 20);
                            View view = binding.getRoot().findViewById(R.id.stat1);
                            if (view instanceof LinearLayout) {
                                animateCounter((LinearLayout) view, userCount + "", startValue, userCount);
                            }
                        }
                    }, 1500);
                }
            }
        });

        homeViewModel.getPropertyCount().observe(getViewLifecycleOwner(), propertyCount -> {
            if (propertyCount != null) {
                // Ensure we're on the main thread for UI operations
                if (handler != null) {
                    handler.postDelayed(() -> {
                        // Check if the fragment is still active before updating UI
                        if (isAdded() && binding != null) {
                            // property count
                            int startValue = Math.max(0, propertyCount - 20);
                            View view = binding.getRoot().findViewById(R.id.stat2);
                            if (view instanceof LinearLayout) {
                                animateCounter((LinearLayout) view, propertyCount + "", startValue, propertyCount);
                            }
                        }
                    }, 1500);
                }
            }
        });

        // Ensure we're on the main thread for UI operations
        if (handler != null) {
            handler.postDelayed(() -> {
                // Check if the fragment is still active before updating UI
                if (isAdded() && binding != null) {
                    View view = binding.getRoot().findViewById(R.id.stat3);
                    if (view instanceof LinearLayout) {
                        animateCounter((LinearLayout) view, "15", 10, 15);
                    }
                }
            }, 1500);
        }
    }

    private void animateCounter(LinearLayout statContainer, String finalText, int startValue, int endValue) {
        // Double-check that we're still attached to an activity before updating UI
        if (!isAdded() || statContainer == null) {
            return;
        }

        TextView numberView = (TextView) statContainer.getChildAt(0);
        if (numberView == null) {
            return;
        }

        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(2000);
        animator.addUpdateListener(animation -> {
            // Check if the fragment is still attached before updating UI
            if (isAdded()) {
                int value = (int) animation.getAnimatedValue();
                if (finalText.contains("+")) {
                    numberView.setText(value + "+");
                } else {
                    numberView.setText(String.valueOf(value));
                }
            }
        });

        // Add scale animation to make it more engaging
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(numberView, "scaleX", 0.8f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(numberView, "scaleY", 0.8f, 1.2f, 1f);
        scaleX.setDuration(2000);
        scaleY.setDuration(2000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, scaleX, scaleY);
        animatorSet.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Clean up all handler callbacks to prevent memory leaks and crashes
        if (handler != null) {
            handler.removeCallbacksAndMessages(null); // Remove ALL callbacks and messages
        }

        binding = null;
    }
}
