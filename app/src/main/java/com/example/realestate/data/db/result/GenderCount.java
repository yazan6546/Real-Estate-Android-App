package com.example.realestate.data.db.result;

import androidx.room.ColumnInfo;

public class GenderCount {
    @ColumnInfo(name = "male_count")
    public int maleCount;

    @ColumnInfo(name = "female_count")
    public int femaleCount;
}