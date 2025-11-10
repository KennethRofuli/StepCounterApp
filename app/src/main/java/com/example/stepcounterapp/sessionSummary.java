package com.example.stepcounterapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stepcounterapp.AppDatabase;
import com.example.stepcounterapp.StepSession;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sessionSummary extends AppCompatActivity implements OnMapReadyCallback {

    private TextView summaryText;
    private Button backToMainButton, saveSessionButton;

    private GoogleMap mMap;
    private ArrayList<LatLng> walkedPath;

    // Declare summary as a class field
    private String summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_summary);

        summaryText = findViewById(R.id.summaryText);
        backToMainButton = findViewById(R.id.backToMainButton);
        saveSessionButton = findViewById(R.id.saveSessionButton);

        // Initialize class field
        summary = getIntent().getStringExtra("SESSION_SUMMARY");
        walkedPath = getIntent().getParcelableArrayListExtra("WALKED_PATH");

        // Show summary text
        summaryText.setText(summary);

        // Calculate and show estimated calories
        double estimatedCalories = extractCaloriesFromSummary(summary);
        if (estimatedCalories > 0) {
            summaryText.append(String.format("\nEstimated Calories Burned: %.0f kcal", estimatedCalories));
        }

        // Set up map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Back button behavior
        backToMainButton.setOnClickListener(v -> {
            Intent intent = new Intent(sessionSummary.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        // Save session button
        saveSessionButton.setOnClickListener(v -> saveSessionToDatabase());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (walkedPath != null && walkedPath.size() > 1) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(walkedPath)
                    .color(Color.RED)
                    .width(10f);
            mMap.addPolyline(polylineOptions);

            // Fit map camera to path bounds
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : walkedPath) {
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();
            int padding = 100; // px
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        } else {
            Log.w("SessionSummary", "Walked path is empty or too short");
        }
    }

    // Estimate calories from distance in summary
    private double extractCaloriesFromSummary(String summary) {
        try {
            Pattern pattern = Pattern.compile("Distance:\\s([\\d.]+)\\skm");
            Matcher matcher = pattern.matcher(summary);
            if (matcher.find()) {
                double distanceKm = Double.parseDouble(matcher.group(1));
                return distanceKm * 60; // kcal estimate
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Save session data to Room DB on button click
    private void saveSessionToDatabase() {
        // Extract values from summary string
        int stepCount = extractSteps(summary);
        double distanceKm = extractDistance(summary);
        long durationMillis = extractDurationMillis(summary);
        double speedKph = extractSpeed(summary);
        double calories = extractCaloriesFromSummary(summary);
        String walkedPathJson = new Gson().toJson(walkedPath);
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                StepSession session = new StepSession();

                session.stepCount = stepCount;
                session.distanceKm = distanceKm;
                session.durationMillis = durationMillis;
                session.speedKph = speedKph;
                session.calories = calories;
                session.walkedPathJson = walkedPathJson;
                session.date = todayDate;  // save the date here

                db.StepSessionDao().insert(session);

                runOnUiThread(() -> {
                    Toast toast = Toast.makeText(sessionSummary.this, "Session saved!", Toast.LENGTH_SHORT);
                    toast.show();

                    // Go back to HomeActivity after short delay
                    new android.os.Handler().postDelayed(() -> {
                        toast.cancel();

                        Intent intent = new Intent(sessionSummary.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish(); // Finish this activity so it doesn't stay in backstack
                    }, 1000);
                });


            } catch (Exception e) {
                Log.e("SessionSummary", "Error saving session", e);
                runOnUiThread(() ->
                        Toast.makeText(sessionSummary.this, "Failed to save session", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }


    // Extractors from summary string using regex
    private int extractSteps(String text) {
        Pattern pattern = Pattern.compile("Steps:\\s(\\d+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private double extractDistance(String text) {
        Pattern pattern = Pattern.compile("Distance:\\s([\\d.]+)\\skm");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 0;
    }

    private long extractDurationMillis(String text) {
        // Parsing "Time: mm:ss"
        Pattern pattern = Pattern.compile("Time:\\s(\\d{2}):(\\d{2})");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            int minutes = Integer.parseInt(matcher.group(1));
            int seconds = Integer.parseInt(matcher.group(2));
            return (minutes * 60 + seconds) * 1000L;
        }
        return 0;
    }

    private double extractSpeed(String text) {
        Pattern pattern = Pattern.compile("Speed:\\s([\\d.]+)\\skm/h");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 0;
    }
}
