package com.example.realestate.ui.user.properties;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.realestate.R;
import com.example.realestate.domain.model.Property;
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.domain.service.UserSession;

public class PropertyDetailFragment extends Fragment {
    private static final String ARG_PROPERTY = "property";

    private Property property;
    private PropertiesViewModel propertiesViewModel;

    private ImageView propertyImage;
    private TextView propertyTitle;
    private TextView propertyLocation;
    private TextView propertyPrice;
    private TextView propertyDescription;
    private TextView propertyType;
    private TextView propertyArea;
    private TextView propertyBedrooms;
    private TextView propertyBathrooms;
    private Button favoriteButton;
    private Button reserveButton;

    public static PropertyDetailFragment newInstance(Property property) {
        PropertyDetailFragment fragment = new PropertyDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROPERTY, property);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        propertiesViewModel = new ViewModelProvider(this).get(PropertiesViewModel.class);

        if (getArguments() != null) {
            property = (Property) getArguments().getSerializable(ARG_PROPERTY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_property_detail, container, false);

        initViews(root);
        setupData();
        setupClickListeners();

        return root;
    }

    private void initViews(View root) {
        propertyImage = root.findViewById(R.id.propertyImageView);
        propertyTitle = root.findViewById(R.id.propertyTitleTextView);
        propertyLocation = root.findViewById(R.id.propertyLocationTextView);
        propertyPrice = root.findViewById(R.id.propertyPriceTextView);
        propertyDescription = root.findViewById(R.id.propertyDescriptionTextView);
        propertyType = root.findViewById(R.id.propertyTypeTextView);
        propertyArea = root.findViewById(R.id.areaTextView);
        propertyBedrooms = root.findViewById(R.id.bedroomsTextView);
        propertyBathrooms = root.findViewById(R.id.bathroomsTextView);
        favoriteButton = root.findViewById(R.id.favoriteButton);
        reserveButton = root.findViewById(R.id.reserveButton);
    }

    private void setupData() {
        if (property != null) {
            propertyTitle.setText(property.getTitle());
            propertyLocation.setText(property.getLocation());
            propertyPrice.setText(String.format("$%,.0f", property.getPrice()));
            propertyDescription.setText(property.getDescription());
            propertyType.setText(property.getType());
            propertyArea.setText(String.format("%.0f sq ft", property.getArea()));
            propertyBedrooms.setText(String.valueOf(property.getBedrooms()));
            propertyBathrooms.setText(String.valueOf(property.getBathrooms()));

            // Load property image using Glide
            if (property.getImageUrl() != null && !property.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(property.getImageUrl())
                        .placeholder(R.drawable.ic_home)
                        .error(R.drawable.ic_home)
                        .centerCrop()
                        .into(propertyImage);
            } else {
                propertyImage.setImageResource(R.drawable.ic_home);
            }

            // Update favorite button state
            updateFavoriteButton();
        }
    }

    private void setupClickListeners() {
        favoriteButton.setOnClickListener(v -> {
            // TODO: Get current user email from UserSession
            SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getContext());
            UserSession userSession = sharedPrefManager.readObject("user_session", UserSession.class, null);
            String userEmail = userSession != null ? userSession.getEmail() : "guest@example.com";

            // Toggle favorite status
            propertiesViewModel.addToFavorites(property, userEmail);
            Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
        });

        reserveButton.setOnClickListener(v -> {
            // Navigate to reservation fragment
            Bundle bundle = new Bundle();
            bundle.putSerializable("property", property);
            Navigation.findNavController(v).navigate(R.id.action_propertyDetailFragment_to_reservationFragment, bundle);
        });
    }

    private void updateFavoriteButton() {
        // Simplified - just show "Add to Favorites" for now
        favoriteButton.setText("Add to Favorites");
        favoriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite, 0, 0, 0);
    }
}
