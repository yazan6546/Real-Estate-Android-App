package com.example.realestate.ui.admin.dashboard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.google.android.material.card.MaterialCardView;

public class AdminDashboardFragment extends Fragment {

    private AdminDashboardViewModel viewModel;
    private TextView userCountTextView;
    private TextView reservationCountTextView;
    private TextView propertyCountTextView;
    private TextView malePercentageTextView;
    private TextView femalePercentageTextView;
    private ProgressBar genderProgressBar;
    private RecyclerView countriesRecyclerView;
    
    // Animation components
    private TextView dynamicMessage;
    private Handler handler;
    private Runnable messageRunnable;
    private String[] adminMessages = {
        "Monitor and manage your real estate platform ⚡",
        "Track user engagement and property performance 📊",
        "Analyze reservations and optimize operations 🎯",
        "Manage special offers and boost sales 💰",
        "Review analytics and make data-driven decisions 📈"
    };
    private int currentMessageIndex = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        // Initialize views
        userCountTextView = root.findViewById(R.id.userCountTextView);
        reservationCountTextView = root.findViewById(R.id.reservationCountTextView);
        propertyCountTextView = root.findViewById(R.id.propertyCountTextView);
        malePercentageTextView = root.findViewById(R.id.malePercentageTextView);
        femalePercentageTextView = root.findViewById(R.id.femalePercentageTextView);
        genderProgressBar = root.findViewById(R.id.genderProgressBar);
        countriesRecyclerView = root.findViewById(R.id.countriesRecyclerView);
        dynamicMessage = root.findViewById(R.id.dynamicMessage);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize handler
        handler = new Handler(Looper.getMainLooper());

        // Create ViewModel with repository dependencies
        viewModel = new ViewModelProvider(this,
                new AdminDashboardViewModel.Factory(
                        RealEstate.appContainer.getUserRepository(),
                        RealEstate.appContainer.getReservationRepository(),
                        RealEstate.appContainer.getPropertyRepository()
                )).get(AdminDashboardViewModel.class);

        setupRecyclerView();
        observeViewModelData();
        startAnimations();
        startDynamicMessages();
    }

    private void setupRecyclerView() {
        // Set up countries RecyclerView with adapter
        CountryAdapter adapter = new CountryAdapter();
        countriesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        countriesRecyclerView.setAdapter(adapter);

        // Observe country data
        viewModel.getTopCountries().observe(getViewLifecycleOwner(), adapter::setCountries);
    }

    @SuppressLint("SetTextI18n")
    private void observeViewModelData() {
        // Observe user count with counter animation
        viewModel.getUserCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                animateCounter(userCountTextView, 0, count, "");
            }
        });

        // Observe reservation count with counter animation
        viewModel.getReservationCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                animateCounter(reservationCountTextView, 0, count, "");
            }
        });

        // Observe property count with counter animation
        viewModel.getPropertyCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                animateCounter(propertyCountTextView, 0, count, "");
            }
        });

        // Observe gender distribution with counter animation
        viewModel.getGenderDistribution().observe(getViewLifecycleOwner(), distribution -> {
            if (distribution != null) {
                int malePercentage = distribution.getMalePercentage();
                int femalePercentage = distribution.getFemalePercentage();

                // Animate percentage counters
                animateCounter(malePercentageTextView, 0, malePercentage, "%");
                animateCounter(femalePercentageTextView, 0, femalePercentage, "%");
                
                // Animate progress bar
                animateProgressBar(genderProgressBar, malePercentage);
            }
        });
    }

    private void animateCounter(TextView textView, int startValue, int endValue, String suffix) {
        if (!isAdded() || textView == null) {
            return;
        }

        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(1000);
        animator.setStartDelay(1000); // Start after card animations
        animator.addUpdateListener(animation -> {
            if (isAdded()) {
                int value = (int) animation.getAnimatedValue();
                textView.setText(value + suffix);
            }
        });

        // Add scale animation to make it more engaging
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 0.8f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 0.8f, 1.2f, 1f);
        scaleX.setDuration(1000);
        scaleX.setStartDelay(1000);
        scaleY.setDuration(1000);
        scaleY.setStartDelay(1000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, scaleX, scaleY);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    private void animateProgressBar(ProgressBar progressBar, int targetProgress) {
        if (!isAdded() || progressBar == null) {
            return;
        }

        ValueAnimator progressAnimator = ValueAnimator.ofInt(0, targetProgress);
        progressAnimator.setDuration(2500);
        progressAnimator.setStartDelay(1500);
        progressAnimator.addUpdateListener(animation -> {
            if (isAdded()) {
                int progress = (int) animation.getAnimatedValue();
                progressBar.setProgress(progress);
            }
        });
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.start();
    }
    
    private void startAnimations() {
        // Animate welcome card
        MaterialCardView welcomeCard = getView().findViewById(R.id.welcomeCard);
        welcomeCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));

        // Animate stats card with delay
        MaterialCardView statsCard = getView().findViewById(R.id.statsCard);
        handler.postDelayed(() -> {
            statsCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left));
        }, 300);

        // Animate gender card with delay
        MaterialCardView genderCard = getView().findViewById(R.id.genderCard);
        handler.postDelayed(() -> {
            genderCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right));
        }, 600);

        // Animate countries card with delay
        MaterialCardView countriesCard = getView().findViewById(R.id.countriesCard);
        handler.postDelayed(() -> {
            countriesCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
        }, 900);
    }

    private void startDynamicMessages() {
        messageRunnable = new Runnable() {
            @Override
            public void run() {
                if (isAdded() && dynamicMessage != null) {
                    // Fade out current message
                    ObjectAnimator fadeOut = ObjectAnimator.ofFloat(dynamicMessage, "alpha", 1f, 0f);
                    fadeOut.setDuration(500);
                    fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(android.animation.Animator animation) {
                            if (isAdded()) {
                                // Change text
                                dynamicMessage.setText(adminMessages[currentMessageIndex]);
                                currentMessageIndex = (currentMessageIndex + 1) % adminMessages.length;

                                // Fade in new message
                                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(dynamicMessage, "alpha", 0f, 1f);
                                fadeIn.setDuration(500);
                                fadeIn.start();
                            }
                        }
                    });
                    fadeOut.start();

                    // Schedule next message change
                    if (handler != null) {
                        handler.postDelayed(this, 4000);
                    }
                }
            }
        };
        handler.postDelayed(messageRunnable, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up handler callbacks
        handler.removeCallbacks(messageRunnable);
    }
}
