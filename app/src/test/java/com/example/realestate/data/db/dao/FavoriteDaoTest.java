package com.example.realestate.data.db.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;

import com.example.realestate.data.db.AppDatabase;
import com.example.realestate.data.db.entity.FavoriteEntity;
import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.data.db.entity.UserEntity;
import com.example.realestate.util.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for the FavoriteDao.
 * Uses an in-memory database for testing.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {26})
public class FavoriteDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private FavoriteDao favoriteDao;
    private UserDao userDao;
    private PropertyDao propertyDao;

    // Constants for test data
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final int TEST_PROPERTY_ID = 1;
    private static final String TEST_DATE = "2025-06-03";

    @Before
    public void createDb() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // For simplicity in testing
                .build();
        favoriteDao = database.favoriteDao();
        userDao = database.userDao();
        propertyDao = database.propertyDao();

        // Setup prerequisites - since FavoriteEntity has foreign keys, we need to create the related entities first
        createTestUserAndProperty();
    }

    @After
    public void closeDb() {
        database.close();
    }

    @Test
    public void insertAndGetFavoriteByEmail() throws InterruptedException {
        // Create and insert test favorite
        FavoriteEntity favorite = createTestFavorite();
        favoriteDao.insertFavorite(favorite);

        // Get favorite by email
        FavoriteEntity retrievedFavorite = LiveDataTestUtil.getValue(favoriteDao.getFavoriteByEmail(TEST_EMAIL)).get(0);

        // Verify data
        assertNotNull(retrievedFavorite);
        assertEquals(TEST_EMAIL, retrievedFavorite.email);
        assertEquals(TEST_PROPERTY_ID, retrievedFavorite.property_id);
    }

    @Test
    public void updateFavorite() throws ParseException, InterruptedException {
        // Create and insert test favorite
        FavoriteEntity favorite = createTestFavorite();
        favoriteDao.insertFavorite(favorite);

        // Get favorite and update the date
        FavoriteEntity retrievedFavorite = LiveDataTestUtil.getValue(favoriteDao.getFavoriteByEmail(TEST_EMAIL)).get(0);
        Date newDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse("2025-06-04");
        retrievedFavorite.added_date = newDate;

        // Update favorite
        favoriteDao.updateFavorite(retrievedFavorite);

        // Get updated favorite
        FavoriteEntity updatedFavorite = LiveDataTestUtil.getValue(favoriteDao.getFavoriteByEmail(TEST_EMAIL)).get(0);

        // Verify data is updated
        assertEquals(newDate, updatedFavorite.added_date);
    }

    @Test
    public void deleteFavorite() throws InterruptedException {
        // Create and insert test favorite
        FavoriteEntity favorite = createTestFavorite();
        favoriteDao.insertFavorite(favorite);

        // Get favorite
        FavoriteEntity retrievedFavorite = LiveDataTestUtil.getValue(favoriteDao.getFavoriteByEmail(TEST_EMAIL)).get(0);
        assertNotNull(retrievedFavorite);

        // Delete favorite
        favoriteDao.deleteFavorite(retrievedFavorite);
        FavoriteEntity deletedFavorite = null;

        // Try to get deleted favorite
        try {
            deletedFavorite = LiveDataTestUtil.getValue(favoriteDao.getFavoriteByEmail(TEST_EMAIL)).get(0);
        } catch (Exception e) {
            // Expected exception since the favorite should be deleted
            assertNotEquals(deletedFavorite, retrievedFavorite);
        }
        assertNotEquals(retrievedFavorite, deletedFavorite);
    }

    @Test
    public void getFavoriteByAddedDate() throws ParseException, InterruptedException {
        // Create and insert test favorite with specific date
        FavoriteEntity favorite = createTestFavorite();
        favoriteDao.insertFavorite(favorite);

        // Get favorites by date
        Date newDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(TEST_DATE);
        List<FavoriteEntity> favorites = LiveDataTestUtil.getValue(favoriteDao.getFavoriteByAddedDate(newDate));

        // Verify data
        assertEquals(1, favorites.size());
        assertEquals(TEST_EMAIL, favorites.get(0).email);
    }

    /**
     * Helper method to create test user and property records needed for foreign key constraints
     */
    private void createTestUserAndProperty() {
        // Create and insert a test user
        UserEntity user = new UserEntity();
        user.firstName = "John";
        user.lastName = "Doe";
        user.email = TEST_EMAIL;
        user.password = "password123";
        user.phone = "1234567890";
        user.country = "USA";
        userDao.insertUser(user);

        // Create and insert a test property
        PropertyEntity property = new PropertyEntity();
        property.property_id = TEST_PROPERTY_ID;
        property.title = "Test Property";
        property.description = "A property for testing";
        property.price = 250000.0;
        property.location = "Test Location";
        property.image = "test.jpg";
        property.type = "Apartment";
        property.bedrooms = 2;
        property.bathrooms = 1;
        property.area = "1000 sq ft";

        // Insert the property with manual ID
        propertyDao.insertAll(java.util.Collections.singletonList(property));
    }

    /**
     * Helper method to create a test favorite
     */
    private FavoriteEntity createTestFavorite() {
        FavoriteEntity favorite = new FavoriteEntity();
        favorite.email = TEST_EMAIL;
        favorite.property_id = TEST_PROPERTY_ID;
        try {
            favorite.added_date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(TEST_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return favorite;
    }
}
