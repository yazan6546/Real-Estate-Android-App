package com.example.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.ui.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find all views manually using findViewById
        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show());

        // Build app bar configuration but EXCLUDE the logout item so it doesn't appear as a destination
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_properties, R.id.nav_your_reservations,
                R.id.nav_featured_properties, R.id.nav_profile_management, R.id.nav_contact_us)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        // Set up manual navigation item selection listener
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Check if the selected item is the logout option
        if (item.getItemId() == R.id.nav_logout) {
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

        // Show a logout message
        Snackbar.make(findViewById(R.id.nav_host_fragment_content_main),
                "Successfully logged out", Snackbar.LENGTH_SHORT).show();

        // Navigate to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        // Clear back stack so user can't go back to the app after logout
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
