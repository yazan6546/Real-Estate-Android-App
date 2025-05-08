package com.example.realestate.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.realestate.data.db.entity.*;

@Dao
public interface PropertyDao {
     @Insert
     void insertProperty(PropertyEntity property);
     @Query("SELECT * FROM properties WHERE property_id = :propertyId")
     PropertyEntity getPropertyById(int propertyId);
     @Update
     void updateProperty(PropertyEntity property);
     @Delete
     void deleteProperty(PropertyEntity property);
}
