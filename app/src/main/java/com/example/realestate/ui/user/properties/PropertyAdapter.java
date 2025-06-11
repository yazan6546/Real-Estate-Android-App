package com.example.realestate.ui.user.properties;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;
import com.example.realestate.domain.model.Property;
import com.example.realestate.ui.common.BasePropertyAdapter;

public class PropertyAdapter extends BasePropertyAdapter<PropertyAdapter.PropertyViewHolder> {

    private OnPropertyActionListener listener;

    public interface OnPropertyActionListener {
        void onFavoriteClick(Property property, boolean isCurrentlyFavorite);

        void onReserveClick(Property property);

        void checkFavoriteStatus(Property property, FavoriteStatusCallback callback);
    }

    public interface FavoriteStatusCallback {
        void onStatusReceived(boolean isFavorite);
    }

    public PropertyAdapter(Context context) {
        super(context);
    }

    public void setOnPropertyActionListener(OnPropertyActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_property_browse, parent, false);
        return new PropertyViewHolder(view, context);
    }

    class PropertyViewHolder extends BasePropertyAdapter.BasePropertyViewHolder {

        private Button btnAddToFavorites;
        private Button btnReserveProperty;

        private boolean isCurrentlyFavorite = false;

        public PropertyViewHolder(@NonNull View itemView, Context context) {
            super(itemView, context);
            initAdditionalViews(itemView);
            setClickListeners();
        }

        private void initAdditionalViews(View itemView) {
            btnAddToFavorites = itemView.findViewById(R.id.btnAddToFavorites);
            btnReserveProperty = itemView.findViewById(R.id.btnReserveProperty);
        }

        private void setClickListeners() {
            btnAddToFavorites.setOnClickListener(v -> {
                PropertiesFragment.successToastCounter.incrementAndGet();
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onFavoriteClick(properties.get(getAdapterPosition()), isCurrentlyFavorite);
                }
            });

            btnReserveProperty.setOnClickListener(v -> {
                PropertiesFragment.successToastCounter.incrementAndGet();
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onReserveClick(properties.get(getAdapterPosition()));
                }
            });
        }

        @Override
        protected void bindSpecificData(Property property) {
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
                btnAddToFavorites.setText("Unfavorite");
                btnAddToFavorites.setBackgroundTintList(context.getColorStateList(android.R.color.holo_red_dark));
            } else {
                btnAddToFavorites.setText("Favorite");
            }
        }
    }
}
