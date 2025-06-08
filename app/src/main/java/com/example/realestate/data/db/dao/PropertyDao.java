package com.example.realestate.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.realestate.data.db.entity.*;

import java.util.List;

@Dao
public interface PropertyDao {
     // Queries that need to be observed should return LiveData
     @Query("SELECT * FROM properties")
     LiveData<List<PropertyEntity>> getAllProperties();

     @Query("SELECT * FROM properties WHERE property_id = :propertyId")
     LiveData<PropertyEntity> getPropertyById(int propertyId);

     // One-time operations should NOT use LiveData
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     @Transaction
     void insertAll(List<PropertyEntity> properties);

     @Insert(onConflict = OnConflictStrategy.REPLACE)
     @Transaction
     void insert(PropertyEntity property);

     @Delete
     void delete(PropertyEntity property);

     @Query("SELECT COUNT(*) FROM properties")
     LiveData<Integer> getPropertyCount();
}