package com.example.realestate.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.realestate.domain.service.UserSession;
import com.example.realestate.DashboardActivity;
import com.example.realestate.R;
import com.example.realestate.RealEstate;
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.ui.register.RegisterActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button registerButton;

    private CheckBox rememberMeCheckbox;

    boolean isRememberMeChecked = false;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find views using findViewById
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        UserSession userSession = sharedPrefManager.readObject("user_session", UserSession.class, null);

        if (userSession != null && userSession.isRememberMe()) {
            // If user is already logged in, navigate to MainActivity
            navigateToMainActivity();
            return;
        }

        viewModel = new ViewModelProvider(this,
                new LoginViewModel.Factory(RealEstate.appContainer.getUserRepository(),
                        sharedPrefManager))
                .get(LoginViewModel.class);

        rememberMeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                isRememberMeChecked = isChecked);

        // Set up the register button to navigate to the registration screen
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        setupLoginButton();
        observeAuthState();
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            viewModel.login(email, password, isRememberMeChecked);
        });
    }

    private void observeAuthState() {
        viewModel.getAuthState().observe(this, state -> {
            switch (state) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    navigateToMainActivity();
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    showErrorMessage(viewModel.getErrorMessage());
                    break;
            }
        });
    }


    private void showErrorMessage(String message) {
        Toast.makeText(this, Objects.requireNonNullElse
                        (message, "Login failed. Please try again."),
                Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);

        startActivity(intent);
        finish();

    }
}