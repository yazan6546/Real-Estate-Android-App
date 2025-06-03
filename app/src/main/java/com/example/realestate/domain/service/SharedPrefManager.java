package com.example.realestate.domain.service;
import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "My Shared Preference";
    private static final int SHARED_PREF_PRIVATE = Context.MODE_PRIVATE;
    private static SharedPrefManager ourInstance = null;
    private static SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;
    public static SharedPrefManager getInstance(Context context) {
        if (ourInstance != null) {
            return ourInstance;
        }
        ourInstance=new SharedPrefManager(context);
        return ourInstance;
    }
    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,
                SHARED_PREF_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public boolean writeString(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }
    public String readString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    // Add a method to save objects to SharedPreferences
    public boolean writeObject(String key, Object value) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(value);
            editor.putString(key, json);
            return editor.commit();
        } catch (Exception e) {
            return false;
        }
    }

    // Add a method to retrieve objects from SharedPreferences
    public <T> T readObject(String key, Class<T> classOfT, T defaultValue) {
        try {
            String json = sharedPreferences.getString(key, "");
            if (json.isEmpty()) {
                return defaultValue;
            }
            Gson gson = new Gson();
            return gson.fromJson(json, classOfT);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}