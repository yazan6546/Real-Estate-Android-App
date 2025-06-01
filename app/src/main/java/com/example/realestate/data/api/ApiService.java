package com.example.realestate.data.api;

import com.example.realestate.data.api.dto.PropertyDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("de64caab-e146-480e-ad01-14a792488977")
    Call<List<PropertyDTO>> getProperties();
}
