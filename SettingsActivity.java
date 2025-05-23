package com.example.myapplicationnew;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                speak("Settings page. Please ask someone to help you set up. Voice Settings, Customize Alerts, History, Return Home");
            }
        });

        // Voice Settings Layout
        LinearLayout voiceSettings = findViewById(R.id.voiceSettingsButton);
        voiceSettings.setOnClickListener(v -> {
            speak("Voice Settings");
            startActivity(new Intent(SettingsActivity.this, VoiceSettingsActivity.class));
            //Toast.makeText(this, "Voice Settings will be implemented soon", Toast.LENGTH_SHORT).show();
        });

        // Alerts Layout
        LinearLayout alertsButton = findViewById(R.id.alertsButton);
        alertsButton.setOnClickListener(v -> {
            speak("Customize Alerts");
            startActivity(new Intent(SettingsActivity.this, CustomiseAlertsActivity.class));
            //Toast.makeText(this, "Customise Alerts will be implemented soon", Toast.LENGTH_SHORT).show();
        });

        // History Layout
        LinearLayout historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(v -> {
            speak("Navigation History");
            startActivity(new Intent(SettingsActivity.this, NavigationHistoryActivity.class));
            //Toast.makeText(this, "Navigation History will be implemented soon", Toast.LENGTH_SHORT).show();
        });

        // Home Layout
        LinearLayout homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            speak("Returning home");
            finish();
        });
    }

    private void readScreenContent() {
        String text = "Settings page contains: " +
                "Voice Settings button, " +
                "Customize Alerts button, " +
                "History button, " +
                "and Return Home button";
        speak(text);
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
