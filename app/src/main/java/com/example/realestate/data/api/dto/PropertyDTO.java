package com.example.realestate.data.api.dto;

import com.google.gson.annotations.SerializedName;

public class PropertyDTO {

    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;
    @SerializedName("price")
    public double price;

    @SerializedName("type")
    public String type;

    @SerializedName("image_url")
    public String imageUrl;

    @SerializedName("location")
    public String location;

    @SerializedName("description")
    public String description;

    @SerializedName("bedrooms")
    public int bedrooms;

    @SerializedName("bathrooms")
    public int bathrooms;

    @SerializedName("area")
    public String area;

}
