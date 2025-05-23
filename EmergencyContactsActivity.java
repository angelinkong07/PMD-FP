package com.example.myapplicationnew;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmergencyContactsActivity extends AppCompatActivity {

    private RecyclerView contactsList;
    private ContactsAdapter adapter;
    private List<Contact> contacts;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        // Firebase init
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // UI setup
        contactsList = findViewById(R.id.contactsRecyclerView);
        contactsList.setLayoutManager(new LinearLayoutManager(this));
        contacts = new ArrayList<>();
        adapter = new ContactsAdapter(contacts, contact -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + contact.getPhone()));
            startActivity(intent);
        });
        contactsList.setAdapter(adapter);

        loadUserContacts();

        // Add contact logic
        findViewById(R.id.addContactButton).setOnClickListener(v -> showAddContactDialog());

        // Remove contact logic
        findViewById(R.id.removeContactButton).setOnClickListener(v -> removeLastContact());

        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void loadUserContacts() {
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .collection("emergency_contacts")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading contacts", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    contacts.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("name");
                        String phone = doc.getString("phone");
                        contacts.add(new Contact(name, phone));
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Emergency Contact");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Name");
        layout.addView(nameInput);

        final EditText phoneInput = new EditText(this);
        phoneInput.setHint("Phone Number");
        layout.addView(phoneInput);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty()) {
                Map<String, Object> contact = new HashMap<>();
                contact.put("name", name);
                contact.put("phone", phone);

                db.collection("users")
                        .document(currentUser.getUid())
                        .collection("emergency_contacts")
                        .add(contact)
                        .addOnSuccessListener(documentReference ->
                                Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to add contact", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Name and phone cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void removeLastContact() {
        if (contacts.isEmpty()) {
            Toast.makeText(this, "No contacts to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        Contact lastContact = contacts.get(contacts.size() - 1);

        db.collection("users")
                .document(currentUser.getUid())
                .collection("emergency_contacts")
                .whereEqualTo("name", lastContact.getName())
                .whereEqualTo("phone", lastContact.getPhone())
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        querySnapshot.getDocuments().get(0).getReference().delete()
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(this, "Contact removed", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to remove contact", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to find contact", Toast.LENGTH_SHORT).show());
    }
}
