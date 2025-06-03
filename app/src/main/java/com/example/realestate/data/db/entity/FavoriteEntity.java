package com.example.realestate.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import java.util.Date;

@Entity(
        tableName = "favorites",
        primaryKeys = {"email", "property_id"},
        foreignKeys = {
            @ForeignKey(
                    entity = UserEntity.class,
                    parentColumns = "email",
                    childColumns = "email",
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

    @ColumnInfo(name = "email")
    @NonNull
    public String email;

    @ColumnInfo(name = "property_id")
    public int property_id;

    @ColumnInfo(name = "added_date")
    public Date added_date;
}
