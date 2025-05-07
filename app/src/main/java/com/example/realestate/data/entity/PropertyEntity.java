package com.example.realestate.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "properties")
public class PropertyEntity {

    @PrimaryKey(autoGenerate = true)
    public int property_id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "price")
    public String price;

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
