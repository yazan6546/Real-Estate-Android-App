package com.example.realestate.ui.admin.special;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class SpecialOffersViewModel extends ViewModel {

    private final MutableLiveData<List<SpecialOffer>> specialOffers = new MutableLiveData<>(new ArrayList<>());

    public SpecialOffersViewModel() {
        // In a real app, we would load special offers from a repository
    }

    public LiveData<List<SpecialOffer>> getSpecialOffers() {
        return specialOffers;
    }

    // Simple data class to represent a special offer
    public static class SpecialOffer {
        private final String title;
        private final String description;
        private final int discountPercentage;

        public SpecialOffer(String title, String description, int discountPercentage) {
            this.title = title;
            this.description = description;
            this.discountPercentage = discountPercentage;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getDiscountPercentage() {
            return discountPercentage;
        }
    }

    // Factory for creating ViewModel
    public static class Factory implements ViewModelProvider.Factory {

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(SpecialOffersViewModel.class)) {
                return (T) new SpecialOffersViewModel();
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
