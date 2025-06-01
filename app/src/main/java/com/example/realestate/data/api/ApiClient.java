package com.example.realestate.data.api;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://run.mocky.io/v3/";
    private static Retrofit retrofit;

    public static ApiService getApiService() {

        Log.d("ApiClient", "Getting API service");
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // using Gson
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
