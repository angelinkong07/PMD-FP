package com.example.myapplicationnew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    EditText nameField, phoneField, addressField, medField;
    ImageButton editButton;
    LinearLayout saveButton;
    Button logoutButton;  // Added logout button

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference userDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        String uid = currentUser.getUid();
        userDocRef = db.collection("users").document(uid);

        // Initialize views
        nameField = findViewById(R.id.nameField);
        phoneField = findViewById(R.id.phoneField);
        addressField = findViewById(R.id.addressField);
        medField = findViewById(R.id.medField);
        editButton = findViewById(R.id.editProfileButton);
        saveButton = findViewById(R.id.saveButton);
        LinearLayout logoutButton = findViewById(R.id.logoutButton);  // Initialize logout button

        // Load user data
        loadUserInfo();

        // Enable edit mode
        editButton.setOnClickListener(v -> {
            boolean enabled = !nameField.isEnabled();
            nameField.setEnabled(enabled);
            phoneField.setEnabled(enabled);
            addressField.setEnabled(enabled);
            medField.setEnabled(enabled);
            saveButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
        });

        // Save user data to Firestore
        saveButton.setOnClickListener(v -> {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", nameField.getText().toString());
            userData.put("phone", phoneField.getText().toString());
            userData.put("address", addressField.getText().toString());
            userData.put("medical", medField.getText().toString());

            userDocRef.set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Information saved", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
                    });
        });

        // Logout button functionality
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();  // Sign out user
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Redirect to login or main screen after logout
            Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class); // Replace LoginActivity with your login screen activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
            finish();
        });
    }

    private void loadUserInfo() {
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                nameField.setText(documentSnapshot.getString("name"));
                phoneField.setText(documentSnapshot.getString("phone"));
                addressField.setText(documentSnapshot.getString("address"));
                medField.setText(documentSnapshot.getString("medical"));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
        });
    }
}
