package com.example.realestate.ui.admin.dashboard;

public class CountryStatItem {
    private String country;
    private int count;

    public CountryStatItem(String country, int count) {
        this.country = country;
        this.count = count;
    }

    public String getCountry() {
        return country;
    }

    public int getCount() {
        return count;
    }
}
