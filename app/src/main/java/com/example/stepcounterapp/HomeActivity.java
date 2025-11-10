package com.example.stepcounterapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnStartWalk, btnPastWalk;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnStartWalk = findViewById(R.id.buttonStartWalk);
        btnPastWalk = findViewById(R.id.buttonPastWalk);

        db = AppDatabase.getInstance(getApplicationContext());

        btnStartWalk.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        btnPastWalk.setOnClickListener(v -> {
            startActivity(new Intent(this, PastWalkActivity.class));
        });

        checkIfPastWalksExist();
    }

    private void checkIfPastWalksExist() {
        new Thread(() -> {
            int count = db.StepSessionDao().getSessionCount();

            runOnUiThread(() -> {
                btnPastWalk.setEnabled(count > 0);
                if (count == 0) {
                    btnPastWalk.setAlpha(0.5f); // Visually disabled
                } else {
                    btnPastWalk.setAlpha(1f); // Enabled
                }
            });
        }).start();
    }
}

