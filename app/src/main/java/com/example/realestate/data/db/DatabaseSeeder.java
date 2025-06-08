package com.example.realestate.data.db;

import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseSeeder class to populate initial users using repository instead of SQL
 */
public class DatabaseSeeder {

    private final UserRepository userRepository;

    public DatabaseSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates and inserts initial users using the repository
     */
    public void seedUsers() {
        List<User> initialUsers = createInitialUsers();
        userRepository.insertAll(initialUsers);
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
}