package com.example.realestate.ui.admin.special;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;

public class SpecialOffersFragment extends Fragment {

    private SpecialOffersViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_special_offers, container, false);

        // Initialize views
        recyclerView = root.findViewById(R.id.offersRecyclerView);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this,
                new SpecialOffersViewModel.Factory())
                .get(SpecialOffersViewModel.class);

        // Set up recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observe changes to the special offers
        viewModel.getSpecialOffers().observe(getViewLifecycleOwner(), specialOffers -> {
            // Update UI with special offers (would update adapter in a real implementation)
        });
    }
}
