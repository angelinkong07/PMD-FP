package com.example.myapplicationnew;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                speak("Profile page. Emergency contacts, User information, Return home");
            }
        });

        // Edit Profile Picture
        ImageButton editProfile = findViewById(R.id.editProfileButton);
        editProfile.setOnClickListener(v -> {
            speak("Edit profile picture");
            Toast.makeText(this, "Edit profile picture", Toast.LENGTH_SHORT).show();
        });

        // Emergency Contacts Button
        LinearLayout emergencyBtn = findViewById(R.id.emergencyButton);
        emergencyBtn.setOnClickListener(v -> {
            speak("Emergency contacts");
            startActivity(new Intent(this, EmergencyContactsActivity.class));
            //Toast.makeText(this, "EmergencyContact will be implemented soon", Toast.LENGTH_SHORT).show();
        });

        // User Information Button
        LinearLayout userInfoBtn = findViewById(R.id.userInfoButton);
        userInfoBtn.setOnClickListener(v -> {
            speak("User information");
            startActivity(new Intent(this, UserInfoActivity.class));
            //Toast.makeText(this, "UserInfoActivity will be implemented soon", Toast.LENGTH_SHORT).show();
        });

        // Home Button
        LinearLayout homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(v -> {
            speak("Returning home");
            finish();
        });
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
