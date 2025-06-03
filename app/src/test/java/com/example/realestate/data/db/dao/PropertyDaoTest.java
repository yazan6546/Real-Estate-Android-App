package com.example.realestate.data.db.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.realestate.data.db.AppDatabase;
import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.util.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test class for the PropertyDao.
 * Uses an in-memory database for testing.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class PropertyDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private PropertyDao propertyDao;

    @Before
    public void createDb() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // For simplicity in testing
                .build();
        propertyDao = database.propertyDao();
    }

    @After
    public void closeDb() throws IOException {
        database.close();
    }

    @Test
    public void insertAndGetAllProperties() throws Exception {
        // Create test data
        List<PropertyEntity> properties = createTestProperties();

        // Insert properties
        propertyDao.insertAll(properties);

        // Get all properties
        List<PropertyEntity> allProperties = LiveDataTestUtil.getValue(propertyDao.getAllProperties());

        // Verify data
        assertEquals(3, allProperties.size());
        assertEquals("Luxury Apartment", allProperties.get(0).title);
        assertEquals("Modern House", allProperties.get(1).title);
        assertEquals("Beach Villa", allProperties.get(2).title);
    }

    @Test
    public void getPropertyById() throws Exception {
        // Create test data
        List<PropertyEntity> properties = createTestProperties();

        // Insert properties
        propertyDao.insertAll(properties);

        // Get the property by ID (assuming ID is set to 1 for the first property)
        PropertyEntity property = LiveDataTestUtil.getValue(propertyDao.getPropertyById(1));

        // Verify data
        assertEquals("Luxury Apartment", property.title);
        assertEquals("New York", property.location);
        assertEquals(2500000.0, property.price, 0.01);
    }

    @Test
    public void deleteProperty() throws Exception {
        // Create test data
        List<PropertyEntity> properties = createTestProperties();

        // Insert properties
        propertyDao.insertAll(properties);

        // Get the first property
        List<PropertyEntity> allProperties = LiveDataTestUtil.getValue(propertyDao.getAllProperties());
        PropertyEntity firstProperty = allProperties.get(0);

        // Delete the first property
        propertyDao.delete(firstProperty);

        // Get all properties again
        allProperties = LiveDataTestUtil.getValue(propertyDao.getAllProperties());

        // Verify data
        assertEquals(2, allProperties.size());
        assertEquals("Modern House", allProperties.get(0).title);
    }

    /**
     * Helper method to create test property data
     */
    private List<PropertyEntity> createTestProperties() {
        List<PropertyEntity> properties = new ArrayList<>();

        PropertyEntity property1 = new PropertyEntity();
        property1.title = "Luxury Apartment";
        property1.description = "A luxurious apartment in the heart of the city";
        property1.price = 2500000.0;
        property1.location = "New York";
        property1.image = "apartment.jpg";
        property1.type = "Apartment";
        property1.bedrooms = 3;
        property1.bathrooms = 2;
        property1.area = "1500 sq ft";

        PropertyEntity property2 = new PropertyEntity();
        property2.title = "Modern House";
        property2.description = "A modern house with a beautiful garden";
        property2.price = 3500000.0;
        property2.location = "Los Angeles";
        property2.image = "house.jpg";
        property2.type = "House";
        property2.bedrooms = 4;
        property2.bathrooms = 3;
        property2.area = "2500 sq ft";

        PropertyEntity property3 = new PropertyEntity();
        property3.title = "Beach Villa";
        property3.description = "A beautiful villa by the beach";
        property3.price = 5000000.0;
        property3.location = "Miami";
        property3.image = "villa.jpg";
        property3.type = "Villa";
        property3.bedrooms = 5;
        property3.bathrooms = 4;
        property3.area = "3500 sq ft";

        properties.add(property1);
        properties.add(property2);
        properties.add(property3);

        return properties;
    }
}

