package com.example.myapplicationnew;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Initialize views
        TextView navInstruction = findViewById(R.id.navInstruction);
        TextView navDistance = findViewById(R.id.navDistance);

        // Get destination from intent
        String destination = getIntent().getStringExtra("DESTINATION");
        navInstruction.setText("Navigating to: " + destination);
        navDistance.setText("Estimated time: 5 minutes");

    }
}
