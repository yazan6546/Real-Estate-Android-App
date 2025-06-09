package com.example.realestate.domain.service;
import com.example.realestate.domain.model.User;
import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "My Shared Preference";
    private static final int SHARED_PREF_PRIVATE = Context.MODE_PRIVATE;
    private static SharedPrefManager ourInstance = null;
    private static SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor;
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

    public String getCurrentUserEmail() {
        // Retrieve the current user email from SharedPreferences
        UserSession userSession = this.readObject("user_session", UserSession.class, null);

        if (userSession != null) {
            return userSession.getEmail();
        }
        else
            return null;
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * Saves a new UserSession from a User object, clearing the previous session first.
     * @param user The User object containing updated information
     * @return true if the session was successfully saved, false otherwise
     */
    public boolean saveUserSession(User user) {
        try {
            // Clear existing user session
            clear();

            // Create new UserSession from User object
            UserSession userSession = new UserSession(
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getProfileImage(),
                    user.isAdmin()
            );

            // Write the new UserSession object
            return writeObject("user_session", userSession);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}