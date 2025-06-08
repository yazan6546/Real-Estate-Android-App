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
import com.example.realestate.databinding.FragmentHomeBinding;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
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
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

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
            greeting = "Good Morning! Welcome to Dream Estate";
        } else if (hour < 17) {
            greeting = "Good Afternoon! Welcome to Dream Estate";
        } else {
            greeting = "Good Evening! Welcome to Dream Estate";
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

        // Animate contact card with delay
        MaterialCardView contactCard = binding.getRoot().findViewById(R.id.contactCard);
        handler.postDelayed(() -> {
            contactCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_in));
        }, 1200);

        // Animate welcome icon with continuous rotation
        ImageView welcomeIcon = binding.getRoot().findViewById(R.id.welcomeIcon);
        handler.postDelayed(() -> {
            startIconAnimation(welcomeIcon);
        }, 1500);
    }

    private void startIconAnimation(ImageView icon) {
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(icon, "rotation", 0f, 360f);
        rotateAnimator.setDuration(3000);
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimator.setRepeatMode(ObjectAnimator.RESTART);

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(icon, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(icon, "scaleY", 1f, 1.1f, 1f);
        scaleXAnimator.setDuration(2000);
        scaleYAnimator.setDuration(2000);
        scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.start();

        // Start rotation after scale animation
        handler.postDelayed(rotateAnimator::start, 1000);
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
                handler.postDelayed(this, 4000);
            }
        };

        // Start the cycle
        handler.postDelayed(welcomeMessageRunnable, 3000);
    }

    private void addCounterAnimations() {
        // Add counter animations to statistics
        handler.postDelayed(() -> {
            animateCounter(binding.getRoot().findViewById(R.id.stat1), "10,000+", 8000, 10000);
            animateCounter(binding.getRoot().findViewById(R.id.stat2), "500+", 400, 500);
            animateCounter(binding.getRoot().findViewById(R.id.stat3), "17", 15, 17);
        }, 1500);
    }

    private void animateCounter(LinearLayout statContainer, String finalText, int startValue, int endValue) {
        TextView numberView = (TextView) statContainer.getChildAt(0);

        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(2000);
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            if (finalText.contains("+")) {
                numberView.setText(value + "+");
            } else {
                numberView.setText(String.valueOf(value));
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

        // Clean up handlers to prevent memory leaks
        if (handler != null && welcomeMessageRunnable != null) {
            handler.removeCallbacks(welcomeMessageRunnable);
        }

        binding = null;
    }
}