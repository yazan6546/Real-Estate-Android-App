package com.example.realestate.data.repository;

import com.example.realestate.data.api.ApiService;
import com.example.realestate.data.api.dto.PropertyDTO;
import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.domain.mapper.PropertyMapper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRepositoryImpl implements ApiRepository {
    private final ApiService apiService;

    public ApiRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void fetchProperties(Callback<List<PropertyEntity>> callback) {
        apiService.getProperties().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<PropertyDTO>> call, Response<List<PropertyDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PropertyEntity> entities = new ArrayList<>();
                    for (PropertyDTO dto : response.body()) {
                        entities.add(PropertyMapper.toEntity(dto));
                    }
                    // Using null for the call parameter as we can't convert between different call types
                    callback.onResponse(null, Response.success(entities));
                } else {
                    callback.onFailure(null, new Throwable("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<List<PropertyDTO>> call, Throwable t) {
                // Using null for the call parameter as we can't convert between different call types
                callback.onFailure(null, t);
            }
        });
    }
}