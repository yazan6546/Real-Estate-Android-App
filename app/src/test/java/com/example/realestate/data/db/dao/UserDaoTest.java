package com.example.realestate.data.db.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;

import com.example.realestate.data.db.AppDatabase;
import com.example.realestate.data.db.entity.UserEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test class for the UserDao.
 * Uses an in-memory database for testing.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class UserDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private UserDao userDao;

    @Before
    public void createDb() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // For simplicity in testing
                .build();
        userDao = database.userDao();
    }

    @After
    public void closeDb() throws IOException {
        database.close();
    }

    @Test
    public void insertAndGetUserByEmail() {
        // Create test data
        UserEntity user = createTestUser();

        // Insert user
        userDao.insertUser(user);

        // Get user by email
        UserEntity retrievedUser = userDao.getUserByEmail("john.doe@example.com");

        // Verify data
        assertNotNull(retrievedUser);
        assertEquals("John", retrievedUser.firstName);
        assertEquals("Doe", retrievedUser.lastName);
        assertEquals("john.doe@example.com", retrievedUser.email);
    }

    @Test
    public void updateUser() {
        // Create and insert test user
        UserEntity user = createTestUser();
        userDao.insertUser(user);

        // Get user and update
        UserEntity retrievedUser = userDao.getUserByEmail("john.doe@example.com");
        retrievedUser.firstName = "Jonathan";
        retrievedUser.phone = "9876543210";

        // Update user
        userDao.updateUser(retrievedUser);

        // Get updated user
        UserEntity updatedUser = userDao.getUserByEmail("john.doe@example.com");

        // Verify data
        assertEquals("Jonathan", updatedUser.firstName);
        assertEquals("9876543210", updatedUser.phone);
    }

    @Test
    public void deleteUser() {
        // Create and insert test user
        UserEntity user = createTestUser();
        userDao.insertUser(user);

        // Get user
        UserEntity retrievedUser = userDao.getUserByEmail("john.doe@example.com");
        assertNotNull(retrievedUser);

        // Delete user
        userDao.deleteUser(retrievedUser);

        // Try to get deleted user
        UserEntity deletedUser = userDao.getUserByEmail("john.doe@example.com");
        assertNull(deletedUser);
    }

    @Test
    public void getUserByEmailAndPassword() {
        // Create and insert test user
        UserEntity user = createTestUser();
        userDao.insertUser(user);

        // Get user with correct credentials
        UserEntity retrievedUser = userDao.getUserByEmailAndPassword("john.doe@example.com", "hashedPassword123");
        assertNotNull(retrievedUser);

        // Get user with incorrect password
        UserEntity userWithWrongPassword = userDao.getUserByEmailAndPassword("john.doe@example.com", "wrongPassword");
        assertNull(userWithWrongPassword);
    }

    @Test
    public void getUserByPhone() {
        // Create and insert test user
        UserEntity user = createTestUser();
        userDao.insertUser(user);

        // Get user by phone
        UserEntity retrievedUser = userDao.getUserByPhone("1234567890");
        assertNotNull(retrievedUser);
        assertEquals("john.doe@example.com", retrievedUser.email);
    }

    @Test
    public void getAllUsers() {
        // Create and insert multiple test users
        userDao.insertUser(createTestUser());

        UserEntity user2 = new UserEntity();
        user2.firstName = "Jane";
        user2.lastName = "Smith";
        user2.email = "jane.smith@example.com";
        user2.password = "hashedPassword456";
        user2.phone = "0987654321";
        user2.country = "Canada";
        userDao.insertUser(user2);

        // Get all users
        List<UserEntity> allUsers = userDao.getAllUsers();

        // Verify data
        assertEquals(2, allUsers.size());
    }

    /**
     * Helper method to create a test user
     */
    private UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.firstName = "John";
        user.lastName = "Doe";
        user.email = "john.doe@example.com";
        user.password = "hashedPassword123";
        user.phone = "1234567890";
        user.country = "USA";
        return user;
    }
}
