package com.example.realestate.data.db;

import com.example.realestate.data.repository.FavoriteRepository;
import com.example.realestate.data.repository.PropertyRepository;
import com.example.realestate.data.repository.ReservationRepository;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.Favorite;
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
    private final ReservationRepository reservationRepository;
    private final FavoriteRepository favoriteRepository;

    public DatabaseSeeder(
            UserRepository userRepository,
            ReservationRepository reservationRepository,
            FavoriteRepository favoriteRepository) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.favoriteRepository = favoriteRepository;
    }

    /**
     * Seeds all initial data
     */
    public void seedDatabase() {
        seedUsers();
        seedReservations();
        seedFavorites();
    }

    /**
     * Creates and inserts initial users using the repository
     */
    public void seedUsers() {
        List<User> initialUsers = createInitialUsers();
        userRepository.insertAll(initialUsers);
    }

    /**
     * Creates and inserts initial reservations using the repository
     */
    public void seedReservations() {
        List<Reservation> initialReservations = createInitialReservations();
        reservationRepository.insertAll(initialReservations);
    }

    /**
     * Creates and inserts initial favorites using the repository
     */
    public void seedFavorites() {
        List<Favorite> initialFavorites = createInitialFavorites();
        favoriteRepository.insertAll(initialFavorites);
    }

    /**
     * Creates a list of initial users to be inserted into the database
     */
    private List<User> createInitialUsers() {
        List<User> users = new ArrayList<>();

        // Regular users - adding both sets of users to ensure compatibility
        // First set - Local app users
        User user1 = new User(
                "Yazan",
                "Naser",
                "yazan@example.com",
                "yazan123A!",
                "594049488",
                "Palestine",
                "Ramallah",
                "MALE",
                false
        );
        users.add(user1);

        User user2 = new User(
                "Ahmad",
                "Ali",
                "ahmad@example.com",
                "ahmad123A!",
                "592123456",
                "Palestine",
                "Ramallah",
                "MALE",
                false
        );
        users.add(user2);

        User user3 = new User(
                "Sara",
                "Mohammad",
                "sara@example.com",
                "sara123A!",
                "598765432",
                "Palestine",
                "Nablus",
                "FEMALE",
                false
        );
        users.add(user3);

        User user4 = new User(
                "Omar",
                "Hassan",
                "omar@example.com",
                "omar123A!",
                "599111222",
                "Palestine",
                "Tulkarem",
                "MALE",
                false
        );
        users.add(user4);

        User user5 = new User(
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
        users.add(user5);

        User user6 = new User(
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
        users.add(user6);

        User user7 = new User(
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
        users.add(user7);

        // Second set - SQL file users (for favorites)
        User sqlUser1 = new User(
                "John",
                "Smith",
                "john.smith@example.com",
                "A!password123",
                "789123456",
                "Jordan",
                "Amman",
                "MALE",
                false
        );
        users.add(sqlUser1);

        User sqlUser2 = new User(
                "Sara",
                "Ahmad",
                "sara.ahmad@example.com",
                "securePwd456!",
                "599123456",
                "Palestine",
                "Ramallah",
                "FEMALE",
                false
        );
        users.add(sqlUser2);

        User sqlUser3 = new User(
                "Mohamed",
                "Ali",
                "mohamed.ali@example.com",
                "userPass789@",
                "501234567",
                "UAE",
                "Dubai",
                "MALE",
                false
        );
        users.add(sqlUser3);

        User sqlUser4 = new User(
                "Layla",
                "Hassan",
                "layla.hassan@example.com",
                "laylaPass2023!",
                "778456789",
                "Jordan",
                "Irbid",
                "FEMALE",
                false
        );
        users.add(sqlUser4);

        User sqlUser5 = new User(
                "Omar",
                "Khalid",
                "omar.khalid@example.com",
                "omar2023Secure!",
                "592345678",
                "Palestine",
                "Nablus",
                "MALE",
                false
        );
        users.add(sqlUser5);

        // Admin users from SQL file
        User admin1 = new User(
                "Admin",
                "User",
                "admin.user@realestate.com",
                "adminSecure123!",
                "777888999",
                "Jordan",
                "Al Karak",
                "MALE",
                true
        );
        users.add(admin1);

        return users;
    }

    private List<Reservation> createInitialReservations() {
        List<Reservation> reservations = new ArrayList<>();

        // Yazan's reservations (formerly admin.user's)
        reservations.add(createReservation("yazan@example.com", 101, 1751317200000L, 1751749200000L, "Confirmed"));
        reservations.add(createReservation("yazan@example.com", 106, 1753908000000L, 1754340000000L, "Pending"));
        reservations.add(createReservation("yazan@example.com", 112, 1757602800000L, 1759071600000L, "Confirmed"));
        reservations.add(createReservation("yazan@example.com", 104, 1759589600000L, 1760458800000L, "Cancelled"));
        reservations.add(createReservation("yazan@example.com", 109, 1761196800000L, 1762060800000L, "Pending"));
        reservations.add(createReservation("yazan@example.com", 115, 1764993600000L, 1765943600000L, "Confirmed"));
        reservations.add(createReservation("yazan@example.com", 118, 1767290400000L, 1768240400000L, "Pending"));
        reservations.add(createReservation("yazan@example.com", 101, 1770055200000L, 1770487200000L, "Confirmed"));
        reservations.add(createReservation("yazan@example.com", 106, 1772074800000L, 1772919600000L, "Pending"));
        reservations.add(createReservation("yazan@example.com", 112, 1776037200000L, 1777506000000L, "Confirmed"));

        // Ahmad's reservations
        reservations.add(createReservation("ahmad@example.com", 102, 1751749200000L, 1752613200000L, "Confirmed"));
        reservations.add(createReservation("ahmad@example.com", 107, 1754340000000L, 1755204000000L, "Pending"));
        reservations.add(createReservation("ahmad@example.com", 110, 1756684800000L, 1757548800000L, "Confirmed"));
        reservations.add(createReservation("ahmad@example.com", 114, 1759157600000L, 1760021600000L, "Completed"));
        reservations.add(createReservation("ahmad@example.com", 116, 1762060800000L, 1762924800000L, "Cancelled"));
        reservations.add(createReservation("ahmad@example.com", 119, 1763788800000L, 1764652800000L, "Confirmed"));
        reservations.add(createReservation("ahmad@example.com", 102, 1768154400000L, 1769018400000L, "Pending"));
        reservations.add(createReservation("ahmad@example.com", 107, 1770487200000L, 1771351200000L, "Confirmed"));
        reservations.add(createReservation("ahmad@example.com", 110, 1772919600000L, 1773783600000L, "Pending"));
        reservations.add(createReservation("ahmad@example.com", 114, 1775299200000L, 1776163200000L, "Confirmed"));

        // Sara's reservations
        reservations.add(createReservation("sara@example.com", 103, 1751299200000L, 1751904000000L, "Confirmed"));
        reservations.add(createReservation("sara@example.com", 108, 1754340000000L, 1754944800000L, "Pending"));
        reservations.add(createReservation("sara@example.com", 111, 1757602800000L, 1758207600000L, "Confirmed"));
        reservations.add(createReservation("sara@example.com", 113, 1760458800000L, 1761063600000L, "Cancelled"));
        reservations.add(createReservation("sara@example.com", 117, 1763788800000L, 1764393600000L, "Confirmed"));
        reservations.add(createReservation("sara@example.com", 120, 1766685600000L, 1767290400000L, "Pending"));
        reservations.add(createReservation("sara@example.com", 103, 1769450400000L, 1770055200000L, "Confirmed"));
        reservations.add(createReservation("sara@example.com", 108, 1772919600000L, 1773524400000L, "Pending"));
        reservations.add(createReservation("sara@example.com", 111, 1776037200000L, 1776642000000L, "Confirmed"));
        reservations.add(createReservation("sara@example.com", 117, 1778629200000L, 1779234000000L, "Pending"));

        // Omar's reservations
        reservations.add(createReservation("omar@example.com", 104, 1751317200000L, 1751749200000L, "Confirmed"));
        reservations.add(createReservation("omar@example.com", 109, 1753908000000L, 1754340000000L, "Pending"));
        reservations.add(createReservation("omar@example.com", 115, 1757602800000L, 1759071600000L, "Confirmed"));
        reservations.add(createReservation("omar@example.com", 118, 1760458800000L, 1760926800000L, "Completed"));
        reservations.add(createReservation("omar@example.com", 101, 1762060800000L, 1762492800000L, "Cancelled"));
        reservations.add(createReservation("omar@example.com", 106, 1764993600000L, 1765947600000L, "Confirmed"));
        reservations.add(createReservation("omar@example.com", 112, 1768154400000L, 1769018400000L, "Pending"));
        reservations.add(createReservation("omar@example.com", 104, 1770487200000L, 1770919200000L, "Confirmed"));
        reservations.add(createReservation("omar@example.com", 109, 1772919600000L, 1773351600000L, "Pending"));
        reservations.add(createReservation("omar@example.com", 115, 1775299200000L, 1776163200000L, "Confirmed"));

        // Lina's reservations
        reservations.add(createReservation("lina@example.com", 105, 1751317200000L, 1752613200000L, "Confirmed"));
        reservations.add(createReservation("lina@example.com", 110, 1756684800000L, 1757548800000L, "Pending"));
        reservations.add(createReservation("lina@example.com", 114, 1759157600000L, 1760021600000L, "Confirmed"));
        reservations.add(createReservation("lina@example.com", 116, 1762060800000L, 1762924800000L, "Cancelled"));
        reservations.add(createReservation("lina@example.com", 119, 1764993600000L, 1765947600000L, "Confirmed"));
        reservations.add(createReservation("lina@example.com", 102, 1768154400000L, 1769018400000L, "Pending"));
        reservations.add(createReservation("lina@example.com", 107, 1770487200000L, 1771351200000L, "Confirmed"));
        reservations.add(createReservation("lina@example.com", 105, 1772919600000L, 1773351600000L, "Pending"));
        reservations.add(createReservation("lina@example.com", 110, 1775299200000L, 1776163200000L, "Confirmed"));
        reservations.add(createReservation("lina@example.com", 114, 1777602000000L, 1778246400000L, "Pending"));

        return reservations;
    }

    /**
     * Creates a list of initial favorites for users
     */
    private List<Favorite> createInitialFavorites() {
        List<Favorite> favorites = new ArrayList<>();

        // Add some sample favorites for each user
        // Yazan's favorites
        favorites.add(createFavorite("yazan@example.com", 101, new Date(1750000000000L)));
        favorites.add(createFavorite("yazan@example.com", 105, new Date(1750100000000L)));
        favorites.add(createFavorite("yazan@example.com", 112, new Date(1750200000000L)));

        // Ahmad's favorites
        favorites.add(createFavorite("ahmad@example.com", 102, new Date(1750010000000L)));
        favorites.add(createFavorite("ahmad@example.com", 110, new Date(1750110000000L)));
        favorites.add(createFavorite("ahmad@example.com", 116, new Date(1750210000000L)));

        // Sara's favorites
        favorites.add(createFavorite("sara@example.com", 103, new Date(1750020000000L)));
        favorites.add(createFavorite("sara@example.com", 108, new Date(1750120000000L)));
        favorites.add(createFavorite("sara@example.com", 113, new Date(1750220000000L)));

        // Omar's favorites
        favorites.add(createFavorite("omar@example.com", 104, new Date(1750030000000L)));
        favorites.add(createFavorite("omar@example.com", 109, new Date(1750130000000L)));
        favorites.add(createFavorite("omar@example.com", 115, new Date(1750230000000L)));

        // Lina's favorites
        favorites.add(createFavorite("lina@example.com", 105, new Date(1750040000000L)));
        favorites.add(createFavorite("lina@example.com", 107, new Date(1750140000000L)));
        favorites.add(createFavorite("lina@example.com", 119, new Date(1750240000000L)));

        return favorites;
    }

    /**
     * Helper method to create a reservation object
     */
    private Reservation createReservation(String email, int propertyId, long startDateMillis, long endDateMillis, String status) {
        Reservation reservation = new Reservation();
        reservation.setEmail(email);
        reservation.setStartDate(new Date(startDateMillis));
        reservation.setEndDate(new Date(endDateMillis));
        reservation.setStatus(status);
        return reservation;
    }

    /**
     * Helper method to create a favorite object
     */
    private Favorite createFavorite(String email, int propertyId, Date addedDate) {
        Favorite favorite = new Favorite();
        favorite.setEmail(email);
        favorite.setPropertyId(propertyId);
        favorite.setAddedDate(addedDate);
        return favorite;
    }
}