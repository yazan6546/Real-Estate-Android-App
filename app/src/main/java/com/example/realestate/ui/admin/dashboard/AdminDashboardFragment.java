package com.example.realestate.ui.admin.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AdminDashboardFragment extends Fragment {

    private AdminDashboardViewModel viewModel;
    private TextView userCountTextView;
    private TextView reservationCountTextView;
    private TextView propertyCountTextView;
    private TextView malePercentageTextView;
    private TextView femalePercentageTextView;
    private ProgressBar genderProgressBar;
    private RecyclerView countriesRecyclerView;

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

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create ViewModel with repository dependencies
        viewModel = new ViewModelProvider(this,
                new AdminDashboardViewModel.Factory(
                        RealEstate.appContainer.getUserRepository(),
                        RealEstate.appContainer.getReservationRepository(),
                        RealEstate.appContainer.getPropertyRepository()
                )).get(AdminDashboardViewModel.class);

        setupRecyclerView();
        observeViewModelData();
    }

    private void setupRecyclerView() {
        // Set up countries RecyclerView with adapter
        CountryAdapter adapter = new CountryAdapter();
        countriesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        countriesRecyclerView.setAdapter(adapter);

        // Observe country data
        viewModel.getTopCountries().observe(getViewLifecycleOwner(), adapter::setCountries);
    }

    private void observeViewModelData() {
        // Observe user count
        viewModel.getUserCount().observe(getViewLifecycleOwner(), count ->
                userCountTextView.setText(String.valueOf(count)));

        // Observe reservation count
        viewModel.getReservationCount().observe(getViewLifecycleOwner(), count ->
                reservationCountTextView.setText(String.valueOf(count)));

        // Observe property count
        viewModel.getPropertyCount().observe(getViewLifecycleOwner(), count ->
                propertyCountTextView.setText(String.valueOf(count)));

        // Observe gender distribution
        viewModel.getGenderDistribution().observe(getViewLifecycleOwner(), distribution -> {
            if (distribution != null) {
                int malePercentage = distribution.getMalePercentage();
                int femalePercentage = distribution.getFemalePercentage();

                malePercentageTextView.setText(malePercentage + "%");
                femalePercentageTextView.setText(femalePercentage + "%");
                genderProgressBar.setProgress(malePercentage);
            }
        });
    }

    // Adapter class for country statistics
    private static class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {
        private List<CountryStatItem> countries = new ArrayList<>();

        public void setCountries(List<CountryStatItem> countries) {
            this.countries = countries;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_country_stat, parent, false);
            return new CountryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
            CountryStatItem item = countries.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return countries.size();
        }

        static class CountryViewHolder extends RecyclerView.ViewHolder {
            private final TextView countryNameTextView;
            private final TextView countTextView;

            CountryViewHolder(@NonNull View itemView) {
                super(itemView);
                countryNameTextView = itemView.findViewById(R.id.countryNameTextView);
                countTextView = itemView.findViewById(R.id.countTextView);
            }

            void bind(CountryStatItem item) {
                countryNameTextView.setText(item.getCountry());
                countTextView.setText(String.valueOf(item.getCount()));
            }
        }
    }
}
