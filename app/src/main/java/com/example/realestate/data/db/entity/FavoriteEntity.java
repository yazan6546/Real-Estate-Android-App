package com.example.realestate.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import java.util.Date;

@Entity(
        tableName = "favorites",
        primaryKeys = {"user_id", "property_id"},
        foreignKeys = {
            @ForeignKey(
                    entity = UserEntity.class,
                    parentColumns = "user_id",
                    childColumns = "user_id",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = PropertyEntity.class,
                    parentColumns = "property_id",
                    childColumns = "property_id",
                    onDelete = ForeignKey.CASCADE
            )
        }

)
public class FavoriteEntity {

    @ColumnInfo(name = "user_id")
    public int user_id;

    @ColumnInfo(name = "property_id")
    public int property_id;

    @ColumnInfo(name = "added_date")
    public Date added_date;
}
