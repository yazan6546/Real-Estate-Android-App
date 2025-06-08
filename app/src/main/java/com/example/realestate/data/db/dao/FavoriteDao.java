package com.example.realestate.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.realestate.data.db.entity.*;

import java.util.Date;
import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert
    void insertFavorite(FavoriteEntity favorite);

    @Insert
    void insertAll(List<FavoriteEntity> favorites);

    @Query("SELECT * FROM favorites WHERE email = :email")
    LiveData<List<FavoriteEntity>> getFavoriteByEmail(String email);

    @Update
    void updateFavorite(FavoriteEntity favorite);

    @Delete
    void deleteFavorite(FavoriteEntity favorite);

    @Query("SELECT * FROM favorites WHERE added_date = :addedDate")
    LiveData<List<FavoriteEntity>> getFavoriteByAddedDate(Date addedDate);

    @Query("SELECT COUNT(*) > 0 FROM favorites WHERE email = :email AND property_id = :propertyId")
    boolean isFavorite(String email, int propertyId);

    @Query("SELECT * FROM favorites WHERE email = :email AND property_id = :propertyId")
    LiveData<FavoriteEntity> getFavoriteByEmailAndPropertyId(String email, int propertyId);
}
