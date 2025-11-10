package com.example.stepcounterapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PastWalkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StepSessionAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_walk);

        recyclerView = findViewById(R.id.recyclerViewSessions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getInstance(getApplicationContext());

        loadSessions();
    }

    private void loadSessions() {
        new Thread(() -> {
            List<StepSession> sessions = db.StepSessionDao().getAllSessions();

            runOnUiThread(() -> {
                adapter = new StepSessionAdapter(sessions, session -> {
                    // Show confirmation dialog before delete
                    runOnUiThread(() -> {
                        new AlertDialog.Builder(PastWalkActivity.this)
                                .setTitle("Delete Session")
                                .setMessage("Are you sure you want to delete this session?")
                                .setPositiveButton("Delete", (dialog, which) -> {
                                    // User confirmed deletion
                                    new Thread(() -> {
                                        db.StepSessionDao().delete(session);
                                        List<StepSession> updatedList = db.StepSessionDao().getAllSessions();

                                        runOnUiThread(() -> {
                                            adapter.updateList(updatedList);
                                            Toast toast = Toast.makeText(PastWalkActivity.this, "Session deleted", Toast.LENGTH_SHORT);
                                            toast.show();
                                            new android.os.Handler().postDelayed(toast::cancel, 1000); // 1 second
                                        });
                                    }).start();
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    });
                });
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}
