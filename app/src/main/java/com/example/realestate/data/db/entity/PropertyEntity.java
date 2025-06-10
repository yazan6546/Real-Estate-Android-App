package com.example.realestate.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "properties")
public class PropertyEntity {

    @PrimaryKey(autoGenerate = true)
    public int property_id;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "price")
    public double price;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "image")
    public String image;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "bedrooms")
    public int bedrooms;

    @ColumnInfo(name = "bathrooms")
    public int bathrooms;

    @ColumnInfo(name = "area")
    public String area;

    @ColumnInfo(name = "is_featured")
    public boolean isFeatured;

    @ColumnInfo(name = "discount")
    public double discount;



    public int getPropertyId() {
        return property_id;
    }

    public String getDescription()  {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public String getImageUrl() {
        return image;
    }

    public String getType() {
        return type;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public String getArea() {
        return area;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public double getDiscount() {
        return discount;
    }







}
