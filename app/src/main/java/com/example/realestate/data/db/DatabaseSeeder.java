package com.example.realestate.data.db;

import com.example.realestate.data.repository.PropertyRepository;
import com.example.realestate.data.repository.ReservationRepository;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.Property;
import com.example.realestate.domain.model.Reservation;
import com.example.realestate.domain.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DatabaseSeeder class to populate initial data using repositories instead of SQL
 */
public class DatabaseSeeder {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final ReservationRepository reservationRepository;

    public DatabaseSeeder(UserRepository userRepository, PropertyRepository propertyRepository, ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Seeds all initial data
     */
    public void seedDatabase() {
        seedUsers();
        seedProperties();
        seedReservations();
    }

    /**
     * Creates and inserts initial users using the repository
     */
    public void seedUsers() {
        List<User> initialUsers = createInitialUsers();
        userRepository.insertAll(initialUsers);
    }

    /**
     * Creates and inserts initial properties using the repository
     */
    public void seedProperties() {
        List<Property> initialProperties = createInitialProperties();
        propertyRepository.insertAll(initialProperties);
    }

    /**
     * Creates and inserts initial reservations using the repository
     */
    public void seedReservations() {
        List<Reservation> initialReservations = createInitialReservations();
        reservationRepository.insertAll(initialReservations);
    }

    /**
     * Creates a list of initial users to be inserted into the database
     */
    private List<User> createInitialUsers() {
        List<User> users = new ArrayList<>();

        // Admin user
        User admin = new User(
                "Yazan",           // firstName
                "Naser",           // lastName
                "yazan@example.com", // email
                "yazan123A!",        // password
                "594049488", // phone
                "Palestine",       // country
                "Ramallah",            // city
                "MALE",            // gender
                true               // isAdmin
        );
        users.add(admin);

        // Regular customer users
        User customer1 = new User(
                "Ahmad",
                "Ali",
                "ahmad@example.com",
                "ahmad123",
                "592123456",
                "Palestine",
                "Ramallah",
                "MALE",
                false
        );
        users.add(customer1);

        User customer2 = new User(
                "Sara",
                "Mohammad",
                "sara@example.com",
                "sara123",
                "598765432",
                "Palestine",
                "Nablus",
                "FEMALE",
                false
        );
        users.add(customer2);

        User customer3 = new User(
                "Omar",
                "Hassan",
                "omar@example.com",
                "omar123",
                "599111222",
                "Palestine",
                "Tulkarem",
                "MALE",
                false
        );
        users.add(customer3);

        User customer4 = new User(
                "Lina",
                "Khalil",
                "lina@example.com",
                "lina123A!",
                "597333444",
                "Jordan",
                "Amman",
                "FEMALE",
                false
        );
        users.add(customer4);

        User customer5 = new User(
                "Fadi",
                "Salim",
                "fadi@example.com",
                "fadi123A!",
                "595555666",
                "UAE",
                "Dubai",
                "MALE",
                false
        );
        users.add(customer5);

        User customer6 = new User(
                "Nour",
                "Ahmad",
                "nour@example.com",
                "nour123A!",
                "593777888",
                "Jordan",
                "Amman",
                "FEMALE",
                false
        );
        users.add(customer6);

        return users;
    }

    /**
     * Creates a list of initial properties similar to the SQL file
     */
    private List<Property> createInitialProperties() {
        List<Property> properties = new ArrayList<>();

        // Create properties similar to SQL file
        Property property1 = new Property();
        property1.setPropertyId(101);
        property1.setTitle("Modern 2-Bedroom Apartment");
        property1.setType("Apartment");
        property1.setPrice(85000);
        property1.setLocation("Ramallah, Palestine");
        property1.setArea("120 m²");
        property1.setBedrooms(2);
        property1.setBathrooms(2);
        property1.setImage("https://example.com/images/apartment1.jpg");
        property1.setDescription("Beautiful apartment with city view, close to shopping malls.");
        property1.setFeatured(true);
        property1.setDiscount(0);
        properties.add(property1);

        Property property2 = new Property();
        property2.setPropertyId(102);
        property2.setTitle("Luxury Family Villa");
        property2.setType("Villa");
        property2.setPrice(450000);
        property2.setLocation("Amman, Jordan");
        property2.setArea("350 m²");
        property2.setBedrooms(5);
        property2.setBathrooms(4);
        property2.setImage("https://example.com/images/villa1.jpg");
        property2.setDescription("Spacious villa with private pool and large garden.");
        property2.setFeatured(false);
        property2.setDiscount(0);
        properties.add(property2);

        Property property3 = new Property();
        property3.setPropertyId(103);
        property3.setTitle("Residential Land Plot");
        property3.setType("Land");
        property3.setPrice(120000);
        property3.setLocation("Aleppo, Syria");
        property3.setArea("500 m²");
        property3.setBedrooms(0);
        property3.setBathrooms(0);
        property3.setImage("https://example.com/images/land1.jpg");
        property3.setDescription("Prime land suitable for residential development.");
        property3.setFeatured(false);
        property3.setDiscount(0);
        properties.add(property3);

        // Add more properties as needed to match your SQL file

        return properties;
    }

    /**
     * Creates a list of initial reservations similar to the SQL file
     */
    private List<Reservation> createInitialReservations() {
        List<Reservation> reservations = new ArrayList<>();

        // First user reservations
        reservations.add(createReservation(
                "yazan@example.com",
                101,
                new Date(1751317200000L),
                new Date(1751749200000L),
                "Confirmed"
        ));

        reservations.add(createReservation(
                "yazan@example.com",
                102,
                new Date(1753908000000L),
                new Date(1754340000000L),
                "Pending"
        ));

        // Second user reservations
        reservations.add(createReservation(
                "ahmad@example.com",
                102,
                new Date(1751749200000L),
                new Date(1752613200000L),
                "Confirmed"
        ));

        reservations.add(createReservation(
                "ahmad@example.com",
                103,
                new Date(1754340000000L),
                new Date(1755204000000L),
                "Pending"
        ));

        // Add more reservations as needed to match your SQL file

        return reservations;
    }

    /**
     * Helper method to create a reservation object
     */
    private Reservation createReservation(String email, int propertyId, Date startDate, Date endDate, String status) {
        Reservation reservation = new Reservation();
        reservation.setEmail(email);
        reservation.setPropertyId(propertyId);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setStatus(status);
        return reservation;
    }
}