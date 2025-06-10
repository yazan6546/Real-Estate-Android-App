package com.example.realestate.ui.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.ui.login.LoginActivity;

public class ConnectApiActivity extends AppCompatActivity {

    private ConnectApiViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_api);

        ConnectApiViewModel.Factory factory = new ConnectApiViewModel.Factory(
                RealEstate.appContainer.getPropertyRepository(),
                RealEstate.appContainer.getReservationRepository(),
                RealEstate.appContainer.getFavoriteRepository(),
                RealEstate.appContainer.getUserRepository()
        );

        // Create ViewModel using the factory
        viewModel = new ViewModelProvider(this, factory).get(ConnectApiViewModel.class);

        Button connectButton = findViewById(R.id.connectButton);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        connectButton.setOnClickListener(v -> {
            viewModel.connect();
//            viewModel.load_database();

        });

        viewModel.connectionState.observe(this, state -> {
            switch (state) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case CONNECTED:
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(this, LoginActivity.class));
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
