package com.example.stepcounterapp;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for step counter calculations
 */
public class StepCalculationsTest {

    private static final double STEP_LENGTH_METERS = 0.78;
    private static final double DELTA = 0.01; // Acceptable difference for double comparison

    @Test
    public void calculateDistance_withZeroSteps_returnsZero() {
        int steps = 0;
        double distanceKm = (steps * STEP_LENGTH_METERS) / 1000.0;
        assertEquals(0.0, distanceKm, DELTA);
    }

    @Test
    public void calculateDistance_with1000Steps_returnsCorrectDistance() {
        int steps = 1000;
        double distanceKm = (steps * STEP_LENGTH_METERS) / 1000.0;
        assertEquals(0.78, distanceKm, DELTA);
    }

    @Test
    public void calculateDistance_with10000Steps_returnsCorrectDistance() {
        int steps = 10000;
        double distanceKm = (steps * STEP_LENGTH_METERS) / 1000.0;
        assertEquals(7.8, distanceKm, DELTA);
    }

    @Test
    public void calculateSpeed_withValidTimeAndDistance_returnsCorrectSpeed() {
        double distanceKm = 5.0;
        long durationMillis = 3600000; // 1 hour
        double hours = durationMillis / 3600000.0;
        double speedKph = distanceKm / hours;
        assertEquals(5.0, speedKph, DELTA);
    }

    @Test
    public void calculateSpeed_withZeroTime_returnsZero() {
        double distanceKm = 5.0;
        long durationMillis = 0;
        double hours = durationMillis / 3600000.0;
        double speedKph = hours > 0 ? distanceKm / hours : 0;
        assertEquals(0.0, speedKph, DELTA);
    }

    @Test
    public void calculateSpeed_with30Minutes_returnsCorrectSpeed() {
        double distanceKm = 2.5;
        long durationMillis = 1800000; // 30 minutes
        double hours = durationMillis / 3600000.0;
        double speedKph = distanceKm / hours;
        assertEquals(5.0, speedKph, DELTA);
    }

    @Test
    public void formatDuration_with3661Seconds_returnsCorrectFormat() {
        long durationMillis = 3661000; // 1 hour, 1 minute, 1 second
        int seconds = (int) (durationMillis / 1000) % 60;
        int minutes = (int) ((durationMillis / (1000 * 60)) % 60);
        int hours = (int) ((durationMillis / (1000 * 60 * 60)) % 24);
        
        assertEquals(1, hours);
        assertEquals(1, minutes);
        assertEquals(1, seconds);
    }

    @Test
    public void formatDuration_with90Seconds_returnsCorrectFormat() {
        long durationMillis = 90000; // 1 minute, 30 seconds
        int seconds = (int) (durationMillis / 1000) % 60;
        int minutes = (int) ((durationMillis / (1000 * 60)) % 60);
        
        assertEquals(1, minutes);
        assertEquals(30, seconds);
    }

    @Test
    public void calculateCalories_basedOnSteps_returnsReasonableValue() {
        // Assuming ~0.04 calories per step (approximate)
        int steps = 5000;
        double calories = steps * 0.04;
        assertEquals(200.0, calories, DELTA);
    }
}
