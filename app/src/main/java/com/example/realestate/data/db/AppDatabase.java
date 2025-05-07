package com.example.realestate.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.realestate.data.db.dao.PropertyDao;
import com.example.realestate.data.db.dao.UserDao;
import com.example.realestate.data.entity.PropertyEntity;
import com.example.realestate.data.entity.UserEntity;

@Database(entities = {UserEntity.class, PropertyEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract PropertyDao propertyDao();

}
