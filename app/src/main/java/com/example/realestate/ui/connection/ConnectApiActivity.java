//package com.example.realestate.ui.connection;
//
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.ProgressBar;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.example.realestate.R;
//import com.example.realestate.data.repository.ApiRepositoryImpl;
//
//public class ConnectApiActivity extends AppCompatActivity {
//
//    private ConnectApiViewModel viewModel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_connect_api);
//
//        viewModel = new ViewModelProvider(
//                this, new ConnectApiViewModelFactory(new ApiRepositoryImpl())
//        ).get(ConnectApiViewModel.class);
//
//        Button connectButton = findViewById(R.id.connectButton);
//        ProgressBar progressBar = findViewById(R.id.progressBar);
//
//        connectButton.setOnClickListener(v -> viewModel.connect());
//
//        viewModel.connectionState.observe(this, state -> {
//            switch (state) {
//                case LOADING:
//                    progressBar.setVisibility(View.VISIBLE);
//                    break;
//                case CONNECTED:
//                    progressBar.setVisibility(View.GONE);
//                    startActivity(new Intent(this, DashboardActivity.class));
//                    finish();
//                    break;
//                case FAILED:
//                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(this, "Failed to connect.", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        });
//    }
//}
