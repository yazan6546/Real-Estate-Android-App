package com.example.realestate.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.realestate.data.entity.UserEntity;

@Dao
public interface UserDao {


     @Insert
     void insertUser(UserEntity user);

     @Query("SELECT * FROM users WHERE id = :userId")
     UserEntity getUserById(int userId);

     @Update
     void updateUser(UserEntity user);

     @Delete
     void deleteUser(UserEntity user);
}
