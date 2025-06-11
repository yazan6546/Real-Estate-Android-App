package com.example.realestate.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * POJO class for updating property fields excluding discount and isFeatured
 */
@Entity
public class PropertyUpdate {
    @PrimaryKey
    @ColumnInfo(name = "property_id")
    public int propertyId;

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
}
