package com.example.stepcounterapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StepSessionDao {

    @Insert
    void insert(StepSession session);

    @Delete
    void delete(StepSession session);
    @Query("SELECT COUNT(*) FROM step_sessions")
    int getSessionCount();


    @Query("SELECT * FROM step_sessions ORDER BY id DESC")
    List<StepSession> getAllSessions();
}

