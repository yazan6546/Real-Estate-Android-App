package com.example.realestate.data.db.result;

import androidx.room.ColumnInfo;

public class CountryCount {

    @ColumnInfo(name = "country")
    public String country;

    @ColumnInfo(name = "count")
    public int count;
}
