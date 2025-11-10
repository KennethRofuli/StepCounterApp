package com.example.stepcounterapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;

@Entity(tableName = "step_sessions")
public class StepSession implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int stepCount;
    public double distanceKm;
    public long durationMillis;
    public double speedKph;
    public double calories;

    public String walkedPathJson; // We’ll store the list of LatLng as a JSON string

    public String date;
}
