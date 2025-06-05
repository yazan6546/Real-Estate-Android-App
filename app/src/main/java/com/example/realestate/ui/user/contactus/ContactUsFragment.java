package com.example.realestate.ui.user.contactus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.databinding.FragmentContactUsBinding;

public class ContactUsFragment extends Fragment {

    private FragmentContactUsBinding binding;
    private ContactUsViewModel contactUsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        contactUsViewModel = new ViewModelProvider(this).get(ContactUsViewModel.class);

        binding = FragmentContactUsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupButtons();

        return root;
    }

    private void setupButtons() {
        LinearLayout btnCallUs = binding.btnCallUs;
        LinearLayout btnLocateUs = binding.btnLocateUs;
        LinearLayout btnEmailUs = binding.btnEmailUs;

        // Call Us Button
        btnCallUs.setOnClickListener(v -> {
            String phoneNumber = contactUsViewModel.getPhoneNumber().getValue();
            if (phoneNumber != null) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                try {
                    startActivity(callIntent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Unable to open phone app", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Locate Us Button
        btnLocateUs.setOnClickListener(v -> {
            String locationUrl = contactUsViewModel.getLocationUrl().getValue();
            Uri gmmIntentUri = Uri.parse(locationUrl);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback to web browser
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=31.5017,34.4668"));
                try {
                    startActivity(webIntent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Unable to open maps", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Email Us Button
        btnEmailUs.setOnClickListener(v -> {
            String email = contactUsViewModel.getEmail().getValue();
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + email));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Real Estate Inquiry");

            try {
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Unable to open email app", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
