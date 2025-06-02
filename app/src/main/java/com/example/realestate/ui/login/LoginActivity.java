package com.example.realestate.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.MainActivity;
import com.example.realestate.R;
import com.example.realestate.RealEstate;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find views using findViewById
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        viewModel = new ViewModelProvider(this,
                new LoginViewModel.Factory(RealEstate.appContainer.getUserRepository()))
                .get(LoginViewModel.class);

        setupLoginButton();
        observeAuthState();
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            viewModel.login(email, password);
        });
    }

    private void observeAuthState() {
        viewModel.authState.observe(this, state -> {
            progressBar.setVisibility(state == LoginViewModel.AuthState.LOADING ?
                    View.VISIBLE : View.GONE);

            if (state == LoginViewModel.AuthState.SUCCESS) {
                startActivity(new Intent(this, MainActivity.class));
                finish(); // Close login activity
            } else if (state == LoginViewModel.AuthState.ERROR) {
                showErrorMessage(viewModel.errorMessage.getValue());
            }
        });
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}