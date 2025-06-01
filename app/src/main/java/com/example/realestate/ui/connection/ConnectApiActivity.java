package com.example.realestate.ui.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realestate.MainActivity;
import com.example.realestate.R;
import com.example.realestate.data.api.ApiClient;
import com.example.realestate.data.db.AppDatabase;
import com.example.realestate.data.repository.ApiRepositoryImpl;

public class ConnectApiActivity extends AppCompatActivity {

    private ConnectApiViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_api);

        // Create repository and ViewModel directly
        ApiRepositoryImpl repository = new ApiRepositoryImpl(
                ApiClient.getApiService(),
                AppDatabase.getInstance(this).propertyDao()
        );
        viewModel = new ConnectApiViewModel(repository);

        Button connectButton = findViewById(R.id.connectButton);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        connectButton.setOnClickListener(v -> viewModel.connect());

        viewModel.connectionState.observe(this, state -> {
            switch (state) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case CONNECTED:
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(this, MainActivity.class)); // Changed from DashboardActivity to MainActivity
                    finish();
                    break;
                case FAILED:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to connect.", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}