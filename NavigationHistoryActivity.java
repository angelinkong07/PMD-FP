package com.example.myapplicationnew;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationHistoryActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    ListView historyList;
    ArrayAdapter<String> adapter;
    List<String> historyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_history);

        historyList = findViewById(R.id.historyListView);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        historyData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyData);
        historyList.setAdapter(adapter);

        loadNavigationHistory();

        historyList.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(this, "Selected: " + historyData.get(position), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadNavigationHistory() {
        String uid = currentUser.getUid();
        db.collection("users").document(uid).collection("navigation_history")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    historyData.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String entry = doc.getString("entry");
                            if (entry != null) {
                                historyData.add(entry);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        addDefaultHistory(); // Add sample if empty
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load history", Toast.LENGTH_SHORT).show());
    }

    private void addDefaultHistory() {
        String uid = currentUser.getUid();
        CollectionReference historyRef = db.collection("users").document(uid).collection("navigation_history");

        String[] defaults = {
                "Home → Supermarket (Today, 10:30 AM)",
                "Office → Home (Yesterday, 6:15 PM)",
                "Home → Park (Yesterday, 4:00 PM)"
        };

        for (String item : defaults) {
            Map<String, Object> data = new HashMap<>();
            data.put("entry", item);
            historyRef.add(data);
            historyData.add(item);
        }

        adapter.notifyDataSetChanged();
    }
}
