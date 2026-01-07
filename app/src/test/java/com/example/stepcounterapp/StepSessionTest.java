package com.example.stepcounterapp;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for StepSession entity
 */
public class StepSessionTest {

    private StepSession session;

    @Before
    public void setUp() {
        session = new StepSession();
    }

    @Test
    public void newSession_hasDefaultId() {
        assertEquals(0, session.id);
    }

    @Test
    public void setStepCount_storesCorrectValue() {
        session.stepCount = 5000;
        assertEquals(5000, session.stepCount);
    }

    @Test
    public void setDistance_storesCorrectValue() {
        session.distanceKm = 3.5;
        assertEquals(3.5, session.distanceKm, 0.01);
    }

    @Test
    public void setDuration_storesCorrectValue() {
        session.durationMillis = 3600000L;
        assertEquals(3600000L, session.durationMillis);
    }

    @Test
    public void setSpeed_storesCorrectValue() {
        session.speedKph = 5.2;
        assertEquals(5.2, session.speedKph, 0.01);
    }

    @Test
    public void setCalories_storesCorrectValue() {
        session.calories = 250.5;
        assertEquals(250.5, session.calories, 0.01);
    }

    @Test
    public void setDate_storesCorrectValue() {
        String testDate = "2026-01-07";
        session.date = testDate;
        assertEquals(testDate, session.date);
    }

    @Test
    public void setWalkedPathJson_storesCorrectValue() {
        String jsonPath = "[{\"lat\":40.7128,\"lng\":-74.0060}]";
        session.walkedPathJson = jsonPath;
        assertEquals(jsonPath, session.walkedPathJson);
    }

    @Test
    public void createCompleteSession_allFieldsSet() {
        session.stepCount = 10000;
        session.distanceKm = 7.8;
        session.durationMillis = 7200000L;
        session.speedKph = 3.9;
        session.calories = 400.0;
        session.date = "2026-01-07";
        session.walkedPathJson = "[]";

        assertNotNull(session);
        assertEquals(10000, session.stepCount);
        assertEquals(7.8, session.distanceKm, 0.01);
        assertEquals(7200000L, session.durationMillis);
        assertEquals(3.9, session.speedKph, 0.01);
        assertEquals(400.0, session.calories, 0.01);
        assertEquals("2026-01-07", session.date);
        assertEquals("[]", session.walkedPathJson);
    }
}
