package com.example.realestate;

import android.app.Application;

import com.example.realestate.di.AppContainer;

public class RealEstate extends Application {
    public static AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        appContainer = new AppContainer(this);
    }
}
