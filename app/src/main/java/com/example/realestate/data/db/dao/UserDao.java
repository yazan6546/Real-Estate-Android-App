package com.example.realestate.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.OnConflictStrategy;

import com.example.realestate.data.db.entity.*;
import com.example.realestate.data.db.result.GenderCount;

import java.util.List;

@Dao
public interface UserDao {


     @Insert(onConflict = OnConflictStrategy.IGNORE)
     void insertUser(UserEntity user);

     @Update
     void updateUser(UserEntity user);

     @Delete
     void deleteUser(UserEntity user);

     @Query("SELECT * FROM users WHERE email = :email AND password = :password")
     UserEntity getUserByEmailAndPassword(String email, String password);

     @Query("SELECT * FROM users WHERE email = :email")
     UserEntity getUserByEmail(String email);

     @Query("SELECT * FROM users WHERE phone = :phone")
     UserEntity getUserByPhone(String phone);

     @Query("SELECT * FROM users")
     LiveData<List<UserEntity>> getAllUsers();

     @Query("SELECT COUNT(*) FROM users WHERE is_admin=0")
     LiveData<Integer> getUserCount();

     //Get percentage of men and women
     @Query("SELECT " +
             "SUM(CASE WHEN gender = 'MALE' THEN 1 ELSE 0 END) as male_count, " +
             "SUM(CASE WHEN gender = 'FEMALE' THEN 1 ELSE 0 END) as female_count " +
             "FROM users WHERE is_admin=0")
     LiveData<GenderCount> getGenderCounts();

}
