package com.example.realestate.data.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.realestate.data.db.converters.Converters;
import com.example.realestate.data.db.dao.*;
import com.example.realestate.data.db.entity.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Database(entities = {
        UserEntity.class,
        PropertyEntity.class,
        FavoriteEntity.class,
        ReservationEntity.class
}, version = 2
)

@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract PropertyDao propertyDao();
    public abstract FavoriteDao favoriteDao();
    public abstract ReservationDao reservationDao();

    private static volatile AppDatabase instance;

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "realestate_database")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Load test data from SQL file
                                    executeSqlFile(context, db);
                                }
                            })
                            .fallbackToDestructiveMigration() // Add this line to handle version changes
                            .build();
                }
            }
        }
        return instance;
    }

    /**
     * Loads and executes SQL statements from an SQL file in the assets folder
     * Now using transactions to maintain integrity
     */
    private static void executeSqlFile(Context context, SupportSQLiteDatabase db) {

//        resetWithTestData(context);
        try {
            InputStream is = context.getAssets().open("test_data_inserts.sql");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder statement = new StringBuilder();
            String line;

            // Begin transaction for all statements
            db.beginTransaction();
            try {
                while ((line = reader.readLine()) != null) {
                    // Skip empty lines and comments
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--")) {
                        continue;
                    }

                    statement.append(line);

                    // Execute statement when it's complete (ends with semicolon)
                    if (line.endsWith(";")) {
                        try {

                            db.execSQL(statement.toString());
                        } catch (Exception e) {
                            Log.e("AppDatabase", "Error executing SQL: " + statement, e);
                        }
                        statement = new StringBuilder();
                    } else {
                        statement.append(" ");
                    }
                }
                // Mark transaction successful if we get here
                db.setTransactionSuccessful();
            } finally {
                // End transaction
                db.endTransaction();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("AppDatabase", "Failed to load SQL file: " + "test_data_inserts.sql", e);
        }
    }

    /**
     * Utility method to reset the database with test data
     * Now ensures foreign key references are properly handled
     */
    public static void resetWithTestData(Context context) {
        if (instance != null) {
            new Thread(() -> {
                SupportSQLiteDatabase db = instance.getOpenHelper().getWritableDatabase();

                try {
                    // Begin transaction for all delete operations
                    db.beginTransaction();
                    try {
                        // Clear existing data in reverse order of foreign key dependencies
                        db.execSQL("DELETE FROM reservations");
                        db.execSQL("DELETE FROM properties");
                        db.execSQL("DELETE FROM favorites");
                        db.execSQL("DELETE FROM users");
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }

                    // Split the SQL file into sections for properties and reservations
                    executeSqlFile(context, db);

                    Log.d("AppDatabase", "Test data loaded successfully");
                } catch (Exception e) {
                    Log.e("AppDatabase", "Error resetting database with test data", e);
                }
            }).start();
        }
    }
}
