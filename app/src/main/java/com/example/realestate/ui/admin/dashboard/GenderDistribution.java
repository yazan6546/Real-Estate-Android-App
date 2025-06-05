package com.example.realestate.ui.admin.dashboard;

public class GenderDistribution {
    private int malePercentage;
    private int femalePercentage;

    public GenderDistribution(int malePercentage, int femalePercentage) {
        this.malePercentage = malePercentage;
        this.femalePercentage = femalePercentage;
    }

    public int getMalePercentage() {
        return malePercentage;
    }

    public int getFemalePercentage() {
        return femalePercentage;
    }
}
