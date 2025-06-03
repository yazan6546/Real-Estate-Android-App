package com.example.realestate.data.db.result;

import androidx.room.ColumnInfo;

public class GenderCount {
    @ColumnInfo(name = "gender")
    public String gender;

    @ColumnInfo(name = "count")
    public int count;
}