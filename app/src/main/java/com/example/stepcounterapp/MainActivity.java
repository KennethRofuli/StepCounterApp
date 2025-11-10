package com.example.stepcounterapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private TextView stepCountText, timerText, distanceText, speedText;
    private Button startButton;

    private boolean isSensorPresent = false;
    private int stepCount = 0;
    private boolean isSessionRunning = false;
    private long startTime = 0L;
    private Handler timerHandler = new Handler();

    private static final double STEP_LENGTH_METERS = 0.78;
    private static final int REQUEST_PERMISSIONS_CODE = 1003;

    // Google Maps
    private GoogleMap mMap;
    private Marker userMarker;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    // Walk Path
    private List<LatLng> walkedPath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepCountText = findViewById(R.id.stepCountText);
        timerText = findViewById(R.id.timerText);
        distanceText = findViewById(R.id.distanceText);
        speedText = findViewById(R.id.speedText);
        startButton = findViewById(R.id.startButton);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            isSensorPresent = true;
        } else {
            stepCountText.setText("Step Detector sensor not available");
        }

        requestPermissionsIfNeeded();

        requestIgnoreBatteryOptimizations();

        startButton.setOnClickListener(v -> {
            if (!isSessionRunning) {
                isSessionRunning = true;

                // 🧹 Reset Service data FIRST
                StepTrackingService.stepCount = 0;
                StepTrackingService.walkedPath = new ArrayList<>();  // Fully replace object, not just clear

                // Also reset local MainActivity copies
                stepCount = 0;
                walkedPath = new ArrayList<>();

                // Start service
                Intent serviceIntent = new Intent(this, StepTrackingService.class);
                startService(serviceIntent);

                // Reset UI
                startTime = System.currentTimeMillis();
                stepCountText.setText("Steps: 0");
                distanceText.setText("Distance: 0.00 km");
                speedText.setText("Speed: 0.00 km/h");
                timerText.setText("Time: 00:00");

                // Start timers and tracking
                timerHandler.postDelayed(timerRunnable, 0);
                enableLocationTracking();

                startButton.setText("Stop");
            }

            else {
                isSessionRunning = false;
                timerHandler.removeCallbacks(timerRunnable);
                stopLocationUpdates();

                // 🛑 Stop the foreground service
                Intent serviceIntent = new Intent(this, StepTrackingService.class);
                stopService(serviceIntent);

                // ✅ Use data from the service
                int steps = StepTrackingService.stepCount;
                ArrayList<LatLng> path = StepTrackingService.walkedPath;

                long durationMillis = System.currentTimeMillis() - startTime;
                int totalSeconds = (int) (durationMillis / 1000);
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;

                double distanceKm = (steps * STEP_LENGTH_METERS) / 1000.0;
                double hours = durationMillis / 3600000.0;
                double speedKph = hours > 0 ? distanceKm / hours : 0;

                String summary = String.format(
                        "Session Summary:\nSteps: %d\nDistance: %.2f km\nTime: %02d:%02d\nSpeed: %.2f km/h",
                        steps, distanceKm, minutes, seconds, speedKph
                );

                // 🔄 Send summary and path to session summary activity
                Intent intent = new Intent(MainActivity.this, sessionSummary.class);
                intent.putExtra("SESSION_SUMMARY", summary);
                intent.putParcelableArrayListExtra("WALKED_PATH", path);
                startActivity(intent);

                startButton.setText("Start");
            }

        });
    }

    private void requestPermissionsIfNeeded() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[0]),
                    REQUEST_PERMISSIONS_CODE);
        }
    }

    private void requestIgnoreBatteryOptimizations() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        String packageName = getPackageName();
        if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
                stepTrackingReceiver, new IntentFilter("STEP_TRACKING_UPDATE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorPresent) {
            sensorManager.unregisterListener(this);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stepTrackingReceiver);
        stopLocationUpdates();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isSessionRunning) return;
        stepCount++;
        stepCountText.setText("Steps: " + stepCount);
        updateDistanceAndSpeed();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void updateDistanceAndSpeed() {
        long elapsedMillis = System.currentTimeMillis() - startTime;
        double elapsedHours = elapsedMillis / 3600000.0;
        double distanceKm = (stepCount * STEP_LENGTH_METERS) / 1000.0;
        double speedKph = elapsedHours > 0 ? distanceKm / elapsedHours : 0;
        distanceText.setText(String.format("Distance: %.2f km", distanceKm));
        speedText.setText(String.format("Speed: %.2f km/h", speedKph));
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timerText.setText(String.format("Time: %02d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Just move camera to current location once (if permission granted)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
                    mMap.setMyLocationEnabled(true); // Show blue dot
                }
            });
        }
    }


    private void enableLocationTracking() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
                }
            });

            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) return;
                    for (Location location : locationResult.getLocations()) {
                        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                        walkedPath.add(point);
                        if (userMarker != null) userMarker.remove();
                        userMarker = mMap.addMarker(new MarkerOptions().position(point).title("You are here"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                    }
                    updateDistanceAndSpeed();  // <-- Update distance & speed whenever location changes
                }
            };

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private BroadcastReceiver stepTrackingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("STEP_TRACKING_UPDATE".equals(intent.getAction())) {
                int steps = intent.getIntExtra("stepCount", 0);
                ArrayList<LatLng> path = intent.getParcelableArrayListExtra("walkedPath");

                stepCount = steps;  // Update your local variable if you use it
                walkedPath.clear();
                if (path != null) walkedPath.addAll(path);

                updateDistanceAndSpeed(); // Update your UI display
                stepCountText.setText("Steps: " + stepCount);

            }
        }
    };


}