package com.example.myapplicationnew;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                speakWelcomePrompt();  // Speak after TTS initialized
            }
        });

        // Make the app name TextView scroll (marquee)
        TextView appName = findViewById(R.id.appNameText);
        appName.setSelected(true);

        // Setup buttons with listeners
        setupButtons();
    }

    // TextToSpeech speak method
    private void speakWelcomePrompt() {
        if (tts != null) {
            tts.speak(getString(R.string.welcome_prompt), TextToSpeech.QUEUE_FLUSH, null, "WELCOME_ID");
        }

        // Also send accessibility announcement for screen readers
        AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (am != null && am.isEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain();
            event.setEventType(AccessibilityEvent.TYPE_ANNOUNCEMENT);
            event.getText().add(getString(R.string.welcome_prompt));
            am.sendAccessibilityEvent(event);
        }
    }

    private void setupButtons() {
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        ImageButton profileButton = findViewById(R.id.profileButton);
        MaterialButton voiceButton = findViewById(R.id.voiceCommandButton);

        settingsButton.setOnClickListener(v -> {
            playClickSound();
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            playClickSound();
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        voiceButton.setOnLongClickListener(v -> {
            v.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.blue_dark)));

            new Handler().postDelayed(this::showDestinationDialog, 300);
            return true;
        });
    }

    private void showDestinationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Voice Destination");

        final EditText input = new EditText(this);
        input.setHint("Say your destination");
        builder.setView(input);

        builder.setPositiveButton("NAVIGATE", (dialog, which) -> {
            String destination = input.getText().toString().trim();
            if (!destination.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("DESTINATION", destination);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please enter a destination", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }

    private void playClickSound() {
        // Implement sound feedback if desired
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
