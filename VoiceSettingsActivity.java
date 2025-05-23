package com.example.myapplicationnew;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class VoiceSettingsActivity extends AppCompatActivity {
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_settings);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        SeekBar speedSeekBar = findViewById(R.id.voiceSpeedSeekBar);
        SeekBar pitchSeekBar = findViewById(R.id.voicePitchSeekBar);

        findViewById(R.id.testVoiceButton).setOnClickListener(v -> {
            float speed = 0.5f + (speedSeekBar.getProgress() * 0.25f);
            float pitch = 0.5f + (pitchSeekBar.getProgress() * 0.25f);

            tts.setPitch(pitch);
            tts.setSpeechRate(speed);
            tts.speak("This is a test of voice settings", TextToSpeech.QUEUE_FLUSH, null, null);
        });
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
