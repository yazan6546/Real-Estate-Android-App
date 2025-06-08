package com.example.realestate.ui.admin.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;

import java.util.ArrayList;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {
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
        private final ImageView countryFlagImageView;

        CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            countryNameTextView = itemView.findViewById(R.id.countryNameTextView);
            countTextView = itemView.findViewById(R.id.countTextView);
            countryFlagImageView = itemView.findViewById(R.id.countryFlagImageView);
        }

        void bind(CountryStatItem item) {
            countryNameTextView.setText(item.getCountry());
            countTextView.setText(String.valueOf(item.getCount()));
            // Set country flag image based on the country name

            switch (item.getCountry()) {
                case "Palestine":
                    countryFlagImageView.setImageResource(R.drawable.flag_palestine);
                    break;
                case "Jordan":
                    countryFlagImageView.setImageResource(R.drawable.flag_jordan);
                    break;
                case "UAE":
                    countryFlagImageView.setImageResource(R.drawable.flag_uae);
                    break;
            }

        }
    }
}