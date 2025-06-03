package com.example.realestate.data.db.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;

import com.example.realestate.data.db.AppDatabase;
import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.data.db.entity.ReservationEntity;
import com.example.realestate.data.db.entity.UserEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test class for the ReservationDao.
 * Uses an in-memory database for testing.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {26})
public class ReservationDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private ReservationDao reservationDao;
    private UserDao userDao;
    private PropertyDao propertyDao;

    // Constants for test data
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final int TEST_PROPERTY_ID = 1;
    private static final int TEST_RESERVATION_ID = 1;
    private static final String TEST_START_DATE = "2025-06-10";
    private static final String TEST_END_DATE = "2025-06-17";
    private static final String TEST_STATUS = "confirmed";

    @Before
    public void createDb() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // For simplicity in testing
                .build();
        reservationDao = database.reservationDao();
        userDao = database.userDao();
        propertyDao = database.propertyDao();

        // Setup prerequisites - since ReservationEntity has foreign keys, we need to create the related entities first
        createTestUserAndProperty();
    }

    @After
    public void closeDb() throws IOException {
        database.close();
    }

    @Test
    public void insertAndGetReservationById() {
        // Create and insert test reservation
        ReservationEntity reservation = createTestReservation();
        reservationDao.insertReservation(reservation);

        // Get reservation by ID
        ReservationEntity retrievedReservation = reservationDao.getReservationById(TEST_RESERVATION_ID);

        // Verify data
        assertNotNull(retrievedReservation);
        assertEquals(TEST_EMAIL, retrievedReservation.email);
        assertEquals(TEST_PROPERTY_ID, retrievedReservation.property_id);
        assertEquals(TEST_STATUS, retrievedReservation.status);
    }

    @Test
    public void updateReservation() throws ParseException {
        // Create and insert test reservation
        ReservationEntity reservation = createTestReservation();
        reservationDao.insertReservation(reservation);

        // Get reservation and update its status
        ReservationEntity retrievedReservation = reservationDao.getReservationById(TEST_RESERVATION_ID);
        retrievedReservation.status = "cancelled";

        // Update reservation
        reservationDao.updateReservation(retrievedReservation);

        // Get updated reservation
        ReservationEntity updatedReservation = reservationDao.getReservationById(TEST_RESERVATION_ID);

        // Verify data is updated
        assertEquals("cancelled", updatedReservation.status);
    }

    @Test
    public void deleteReservation() {
        // Create and insert test reservation
        ReservationEntity reservation = createTestReservation();
        reservationDao.insertReservation(reservation);

        // Get reservation
        ReservationEntity retrievedReservation = reservationDao.getReservationById(TEST_RESERVATION_ID);
        assertNotNull(retrievedReservation);

        // Delete reservation
        reservationDao.deleteReservation(retrievedReservation);

        // Try to get deleted reservation
        ReservationEntity deletedReservation = reservationDao.getReservationById(TEST_RESERVATION_ID);
        assertNull(deletedReservation);
    }

    @Test
    public void getReservationsByUserId() {
        // Create and insert test reservation
        ReservationEntity reservation = createTestReservation();
        reservationDao.insertReservation(reservation);

        // Get reservations by user email
        List<ReservationEntity> reservations = reservationDao.getReservationsByUserId(TEST_EMAIL);

        // Verify data
        assertEquals(1, reservations.size());
        assertEquals(TEST_PROPERTY_ID, reservations.get(0).property_id);
    }

    @Test
    public void multipleReservationsForSameUser() {
        // Create and insert first test reservation
        ReservationEntity reservation1 = createTestReservation();
        reservationDao.insertReservation(reservation1);

        // Create and insert second test reservation with different property
        ReservationEntity reservation2 = new ReservationEntity();
        reservation2.reservation_id = 2;
        reservation2.email = TEST_EMAIL;
        reservation2.property_id = 2; // Different property
        try {
            reservation2.startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse("2025-07-01");
            reservation2.endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse("2025-07-08");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        reservation2.status = "pending";

        // Create another property for the second reservation
        PropertyEntity property2 = new PropertyEntity();
        property2.property_id = 2;
        property2.title = "Second Test Property";
        property2.description = "Another property for testing";
        property2.price = 350000.0;
        property2.location = "Second Test Location";
        property2.type = "House";

        // Insert the second property
        propertyDao.insertAll(java.util.Collections.singletonList(property2));

        // Insert the second reservation
        reservationDao.insertReservation(reservation2);

        // Get reservations by user email
        List<ReservationEntity> reservations = reservationDao.getReservationsByUserId(TEST_EMAIL);

        // Verify data
        assertEquals(2, reservations.size());
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
     * Helper method to create a test reservation
     */
    private ReservationEntity createTestReservation() {
        ReservationEntity reservation = new ReservationEntity();
        reservation.reservation_id = TEST_RESERVATION_ID;
        reservation.email = TEST_EMAIL;
        reservation.property_id = TEST_PROPERTY_ID;
        try {
            reservation.startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(TEST_START_DATE);
            reservation.endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(TEST_END_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        reservation.status = TEST_STATUS;
        return reservation;
    }
}
