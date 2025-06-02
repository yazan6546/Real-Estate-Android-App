package com.example.realestate.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = "reservations",
        foreignKeys = {
            @ForeignKey(
                    entity = UserEntity.class,
                    parentColumns = "email",
                    childColumns = "email",
                    onDelete = ForeignKey.RESTRICT
            ),
            @ForeignKey(
                    entity = PropertyEntity.class,
                    parentColumns = "property_id",
                    childColumns = "property_id",
                    onDelete = ForeignKey.RESTRICT
            )
        }
)
public class ReservationEntity {

    @PrimaryKey(autoGenerate = true)
    public int reservation_id;

    @NonNull
    @ColumnInfo(name = "email")
    public String email;
    public int property_id;

    @ColumnInfo(name = "start_date")
    public Date startDate;

    @ColumnInfo(name = "end_date")
    public Date endDate;

    @ColumnInfo(name = "status")
    public String status; // e.g., "pending", "confirmed", "cancelled"

}
