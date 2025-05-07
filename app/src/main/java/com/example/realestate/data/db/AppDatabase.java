package com.example.realestate.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.realestate.data.db.dao.*;
import com.example.realestate.data.entity.*;
import androidx.room.TypeConverters;

@Database(entities = {
        UserEntity.class,
        PropertyEntity.class,
        FavoriteEntity.class,
        ReservationEntity.class
}, version = 1, exportSchema = false
)

public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract PropertyDao propertyDao();
    public abstract FavoriteDao favoriteDao();
    public abstract ReservationDao reservationDao();
}
