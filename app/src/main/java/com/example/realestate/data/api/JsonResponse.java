package com.example.realestate.data.api;

import com.example.realestate.data.api.dto.CategoryDTO;
import com.example.realestate.data.api.dto.PropertyDTO;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JsonResponse {
    @SerializedName("properties")
    public List<PropertyDTO> properties;

    @SerializedName("categories")
    public List<CategoryDTO> categories;
}