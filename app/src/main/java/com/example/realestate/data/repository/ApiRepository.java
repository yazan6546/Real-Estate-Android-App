package com.example.realestate.data.repository;

import com.example.realestate.data.api.dto.CategoryDTO;
import com.example.realestate.data.api.dto.PropertyDTO;

import java.util.List;

import retrofit2.Callback;

public interface ApiRepository {
//    void fetchProperties(Callback<List<PropertyDTO>> callback);
//    void fetchCategories(Callback<List<CategoryDTO>> callback);
//
//    void syncPropertiesWithDatabase(Callback<Boolean> callback);
//    void syncCategoriesWithDatabase(Callback<Boolean> callback);

    public void fetchAndSaveProperties(Callback<Boolean> callback);

}
