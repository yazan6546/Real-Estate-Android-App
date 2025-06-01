package com.example.realestate.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.realestate.data.db.entity.*;

import java.util.List;

@Dao
public interface PropertyDao {
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     void insertProperty(PropertyEntity property);

     @Insert(onConflict = OnConflictStrategy.REPLACE)
     void insertAll(List<PropertyEntity> properties);
     @Query("SELECT * FROM properties WHERE property_id = :propertyId")
     PropertyEntity getPropertyById(int propertyId);
     @Update
     void updateProperty(PropertyEntity property);
     @Delete
     void deleteProperty(PropertyEntity property);
}
