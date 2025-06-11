package com.example.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.domain.service.UserSession;
import com.example.realestate.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Determine user role first
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        UserSession userSession = sharedPrefManager.readObject("user_session", UserSession.class, null);
        isAdmin = userSession != null && userSession.isAdmin();

        // Set the appropriate layout based on role
        setContentView(isAdmin ? R.layout.activity_admin_dashboard : R.layout.activity_main);

        // Find views with proper IDs based on role
        Toolbar toolbar = findViewById(isAdmin ? R.id.toolbar_admin : R.id.toolbar_user);
        drawer = findViewById(isAdmin ? R.id.drawer_layout_admin : R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Find FAB if it exists (might be null for some layouts)
        FloatingActionButton fab = findViewById(isAdmin ? R.id.fab_admin : R.id.fab_user);

        setSupportActionBar(toolbar);

        // Set up the FAB if it exists
        if (fab != null) {
            fab.setOnClickListener(
                    view -> Snackbar.make(view, isAdmin ? "Add new property" : "Action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .setAnchorView(fab).show());
        }

        // Setup navigation based on role
        if (isAdmin) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_admin_dashboard, R.id.nav_view_all_reservations,
                    R.id.nav_delete_customers, R.id.nav_add_new_admin,
                    R.id.nav_special_offers)
                    .setOpenableLayout(drawer)
                    .build();

            // Clear and inflate the admin menu
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.admin_drawer_menu);

            // Find NavController for admin layout
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin);
        } else {
            // Build app bar configuration but EXCLUDE the logout item so it doesn't appear
            // as a destination
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_properties, R.id.nav_your_reservations,
                    R.id.nav_featured_properties, R.id.nav_favorites, R.id.nav_profile_management, R.id.nav_contact_us)
                    .setOpenableLayout(drawer)
                    .build();

            // Clear and inflate the user menu
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);

            // Find NavController for user layout
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        }

        // Setup action bar with nav controller
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        // Use custom navigation item listener
        navigationView.setNavigationItemSelectedListener(this);

        // Add destination change listener to keep drawer selection in sync with navigation
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            updateNavigationSelection(destination.getId());
        });

        // Update header with user info
        updateNavigationHeader(navigationView, userSession);
    }

    // Method to update the navigation selection based on destination ID
    private void updateNavigationSelection(int destinationId) {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == destinationId) {
                item.setChecked(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh navigation header when returning to dashboard (e.g., after profile
        // update)
        NavigationView navigationView = findViewById(R.id.nav_view);
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        UserSession userSession = sharedPrefManager.readObject("user_session", UserSession.class, null);

        if (navigationView != null && userSession != null) {
            updateNavigationHeader(navigationView, userSession);
        }
    }

    private void updateNavigationHeader(NavigationView navigationView, UserSession userSession) {
        View headerView = navigationView.getHeaderView(0);

        // Find header views
        TextView usernameTextView = headerView.findViewById(R.id.nav_header_username);
        TextView emailTextView = headerView.findViewById(R.id.nav_header_email);
        ImageView profileImageView = headerView.findViewById(R.id.nav_header_profile_image);

        if (userSession != null) {
            String displayName = userSession.getFirstName() + " " + userSession.getLastName();
            if (isAdmin) {
                displayName = "Admin: " + displayName;
            }
            usernameTextView.setText(displayName);
            emailTextView.setText(userSession.getEmail());

            // Load profile image from stored user data
            loadProfileImageForUser(userSession.getProfileImage(), profileImageView);
        }
    }

    private void loadProfileImageForUser(String image, ImageView profileImageView) {
        // First set the default image to ensure it's not blank/white if loading fails
        profileImageView.setImageResource(R.drawable.ic_person);

        // If no image is set, we already have the default set
        if (image == null || image.isEmpty()) {
            return;
        }

        try {
            File imageFile = new File(getFilesDir(), image);

            // Only try to load if the file actually exists
            if (imageFile.exists()) {
                Glide.with(DashboardActivity.this)
                        .load(imageFile)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .circleCrop()  // Optional: makes the image circular
                        .into(profileImageView);
            }
        } catch (Exception e) {
            // Log the error but keep the default image already set
            Log.e("DashboardActivity", "Error loading profile image: " + e.getMessage());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // Handle logout for both user types
        if (itemId == R.id.nav_admin_logout || itemId == R.id.nav_logout) {
            logout();
            return true;
        }

        // Let NavigationUI handle other navigation
        boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
        if (handled) {
            drawer.closeDrawers();
        }
        return handled;
    }

    private void logout() {
        SharedPrefManager.getInstance(this).clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(isAdmin ? R.menu.admin_menu : R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Public method to refresh the navigation header with the latest user data.
     * This is called after profile updates to immediately reflect changes.
     */
    public void refreshNavigationHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        UserSession userSession = sharedPrefManager.readObject("user_session", UserSession.class, null);

        if (navigationView != null && userSession != null) {
            updateNavigationHeader(navigationView, userSession);
        }
    }
}