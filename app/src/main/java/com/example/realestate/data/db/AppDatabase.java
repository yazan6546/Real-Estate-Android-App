package com.example.realestate.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.realestate.data.db.converters.Converters;
import com.example.realestate.data.db.dao.*;
import com.example.realestate.data.db.entity.*;

import androidx.room.TypeConverters;

@Database(entities = {
        UserEntity.class,
        PropertyEntity.class,
        FavoriteEntity.class,
        ReservationEntity.class
}, version = 2
)

@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract PropertyDao propertyDao();
    public abstract FavoriteDao favoriteDao();
    public abstract ReservationDao reservationDao();

    private static volatile AppDatabase instance;

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "realestate_database")
                            .build();
                }
            }
        }
        return instance;
    }
}