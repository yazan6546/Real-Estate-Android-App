package com.example.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.domain.service.UserSession;
import com.example.realestate.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);


        // Find all views manually using findViewById
        Toolbar toolbar = findViewById(R.id.toolbar_admin);
        FloatingActionButton fab = findViewById(R.id.fab_admin);
        drawer = findViewById(R.id.drawer_layout_admin);
        NavigationView navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(view -> Snackbar.make(view, "Add new property", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab_admin).show());

        // Build app bar configuration but EXCLUDE the logout item so it doesn't appear as a destination
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_admin_dashboard, R.id.nav_view_all_reservations,
                R.id.nav_delete_customers, R.id.nav_add_new_admin,
                R.id.nav_special_offers)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        // Set up manual navigation item selection listener
        navigationView.setNavigationItemSelectedListener(this);

        // Update navigation header with user information
        updateNavigationHeader(navigationView);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Check if the selected item is the logout option
        if (item.getItemId() == R.id.nav_admin_logout) {
            logout();
            return true;
        }

        // For all other menu items, let the NavigationUI handle it
        boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
        if (handled) {
            drawer.closeDrawers();
        }
        return handled;
    }

    private void logout() {
        // Clear user session data
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        sharedPrefManager.clear();

        // Navigate to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        // Clear back stack so user can't go back to the app after logout
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateNavigationHeader(NavigationView navigationView) {
        // Get the header view
        View headerView = navigationView.getHeaderView(0);

        // Find the TextViews in the header
        TextView usernameTextView = headerView.findViewById(R.id.nav_header_username);
        TextView emailTextView = headerView.findViewById(R.id.nav_header_email);

        // Get user session from SharedPreferences
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        UserSession userSession = sharedPrefManager.readObject("user_session", UserSession.class, null);

        // Update with actual user data
        String fullName = userSession.getFirstName() + " " + userSession.getLastName();
        usernameTextView.setText(fullName);
        emailTextView.setText(userSession.getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
