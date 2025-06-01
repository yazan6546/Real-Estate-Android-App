package com.example.realestate.data.api;

import com.example.realestate.data.api.dto.PropertyDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("properties")
    Call<List<PropertyDTO>> getProperties();
}
