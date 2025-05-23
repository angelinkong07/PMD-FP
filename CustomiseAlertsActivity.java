package com.example.myapplicationnew;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class CustomiseAlertsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customise_alerts);

        // Initialize views
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch vibrationSwitch = findViewById(R.id.vibrationSwitch);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch soundSwitch = findViewById(R.id.soundSwitch);
        SeekBar volumeSeekBar = findViewById(R.id.volumeSeekBar);

        // Set up listeners
        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle vibration toggle
            if (isChecked) {
                // Vibration enabled
            } else {
                // Vibration disabled
            }
        });

        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle sound toggle
        });

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Handle volume change (progress ranges from 0 to max)
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
