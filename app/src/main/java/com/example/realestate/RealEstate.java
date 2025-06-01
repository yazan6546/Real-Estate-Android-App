package com.example.realestate;

import android.app.Application;

import com.example.realestate.di.AppContainer;

public class RealEstate extends Application {
    // Reference to the AppContainer for dependency access
    public static AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the AppContainer
        appContainer = new AppContainer(this);
    }
}
