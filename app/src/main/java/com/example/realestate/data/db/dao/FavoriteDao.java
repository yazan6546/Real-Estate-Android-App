package com.example.realestate.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.realestate.data.db.entity.*;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert
    void insertFavorite(FavoriteEntity favorite);

    @Query("SELECT * FROM favorites WHERE user_id = :userId")
    FavoriteEntity getFavoriteByUserId(int userId);

    @Update
    void updateFavorite(FavoriteEntity favorite);

    @Delete
    void deleteFavorite(FavoriteEntity favorite);

    @Query("SELECT * FROM favorites WHERE added_date = :addedDate")
    List<FavoriteEntity> getFavoriteByAddedDate(String addedDate);

}
