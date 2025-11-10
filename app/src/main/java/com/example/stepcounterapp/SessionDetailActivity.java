package com.example.stepcounterapp;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class SessionDetailActivity extends AppCompatActivity {

    private TextView textDate, textSteps, textDistance, textDuration, textSpeed, textCalories;
    private GoogleMap map;
    private List<LatLng> walkedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        textDate = findViewById(R.id.textDate);
        textSteps = findViewById(R.id.textSteps);
        textDistance = findViewById(R.id.textDistance);
        textDuration = findViewById(R.id.textDuration);
        textSpeed = findViewById(R.id.textSpeed);
        textCalories = findViewById(R.id.textCalories);

        // Get the session from Intent
        StepSession session = (StepSession) getIntent().getSerializableExtra("session");

        if (session != null) {
            textDate.setText(session.date);
            textSteps.setText("Steps: " + session.stepCount);
            textDistance.setText(String.format("Distance: %.2f km", session.distanceKm));
            textDuration.setText(formatDuration(session.durationMillis));
            textSpeed.setText(String.format("Speed: %.2f km/h", session.speedKph));
            textCalories.setText(String.format("Calories: %.0f kcal", session.calories));

            walkedPath = new Gson().fromJson(session.walkedPathJson, new TypeToken<List<LatLng>>(){}.getType());

            // Optional: if you have a MapFragment
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(googleMap -> {
                    map = googleMap;
                    if (walkedPath != null && !walkedPath.isEmpty()) {
                        //change the color and width of the polyline
                        PolylineOptions polylineOptions = new PolylineOptions().addAll(walkedPath).color(Color.RED).width(8);
                        map.addPolyline(polylineOptions);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(walkedPath.get(0), 16));
                    }
                });
            }
        }
    }

    private String formatDuration(long durationMillis) {
        int seconds = (int) (durationMillis / 1000) % 60 ;
        int minutes = (int) ((durationMillis / (1000*60)) % 60);
        int hours   = (int) ((durationMillis / (1000*60*60)) % 24);
        if (hours > 0) {
            return String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
        }
        return String.format("%02dm:%02ds", minutes, seconds);
    }
}

