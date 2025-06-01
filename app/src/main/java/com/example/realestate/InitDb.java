package com.example.realestate;

import android.app.Application;

import com.example.realestate.data.db.AppDatabase;

public class InitDb extends Application {
    public static AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = AppDatabase.getDatabase(this);
    }
}