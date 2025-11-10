package com.example.stepcounterapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StepSessionAdapter extends RecyclerView.Adapter<StepSessionAdapter.ViewHolder> {

    private List<StepSession> sessions;
    private OnSessionDeleteListener deleteListener;

    public interface OnSessionDeleteListener {
        void onDelete(StepSession session);
    }

    public StepSessionAdapter(List<StepSession> sessions) {
        this.sessions = sessions;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public StepSessionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_step_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepSessionAdapter.ViewHolder holder, int position) {
        StepSession session = sessions.get(position);

        holder.dateText.setText(session.date);
        holder.stepsText.setText("Steps: " + session.stepCount);
        holder.distanceText.setText(String.format("Distance: %.2f km", session.distanceKm));
        holder.durationText.setText("Duration: " + formatDuration(session.durationMillis));
        holder.speedText.setText(String.format("Speed: %.2f km/h", session.speedKph));
        holder.caloriesText.setText(String.format("Calories: %.0f kcal", session.calories));

        // Detail click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SessionDetailActivity.class);
            intent.putExtra("session", session);
            v.getContext().startActivity(intent);
        });

        // Delete click
        holder.deleteBtn.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(session);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public void updateList(List<StepSession> updatedList) {
        sessions = updatedList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, stepsText, distanceText, durationText, speedText, caloriesText;
        Button deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.textDate);
            stepsText = itemView.findViewById(R.id.textSteps);
            distanceText = itemView.findViewById(R.id.textDistance);
            durationText = itemView.findViewById(R.id.textDuration);
            speedText = itemView.findViewById(R.id.textSpeed);
            caloriesText = itemView.findViewById(R.id.textCalories);
            deleteBtn = itemView.findViewById(R.id.btnDelete);
        }
    }

    private String formatDuration(long durationMillis) {
        int seconds = (int) (durationMillis / 1000) % 60;
        int minutes = (int) ((durationMillis / (1000 * 60)) % 60);
        int hours = (int) ((durationMillis / (1000 * 60 * 60)) % 24);
        if (hours > 0) {
            return String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
        }
        return String.format("%02dm:%02ds", minutes, seconds);
    }

    public StepSessionAdapter(List<StepSession> sessions, OnSessionDeleteListener deleteListener) {
        this.sessions = sessions;
        this.deleteListener = deleteListener;
    }

}

