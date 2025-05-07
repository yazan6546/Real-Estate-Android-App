package com.example.realestate.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "reservations",
        foreignKeys = {
            @ForeignKey(
                    entity = UserEntity.class,
                    parentColumns = "user_id",
                    childColumns = "user_id",
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
    public int user_id;
    public int property_id;

    @ColumnInfo(name = "start_date")
    public String startDate;

    @ColumnInfo(name = "end_date")
    public String endDate;

}
