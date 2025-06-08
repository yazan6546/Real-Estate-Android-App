package com.example.realestate.data.api;

import com.example.realestate.data.api.dto.PropertyDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("8b40aff2-ee2b-4407-9c9e-a908eadcc7ac")
    Call<JsonResponse> getJson();
}
