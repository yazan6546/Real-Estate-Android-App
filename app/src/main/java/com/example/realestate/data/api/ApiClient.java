package com.example.realestate.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://run.mocky.io/v3/41be9e8d-c953-4e05-b124-251d18da3548";
    private static Retrofit retrofit;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // using Gson
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
