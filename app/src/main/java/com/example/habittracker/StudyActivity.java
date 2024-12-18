package com.example.habittracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StudyActivity extends AppCompatActivity {
    private CountDownTimer countDownTimer;
    private boolean running = false;
    private long timeLeftInMillis;
    private long pauseTime; // Used to track the elapsed time when paused
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        sharedPreferences = getSharedPreferences("HabitTrackerPrefs", MODE_PRIVATE);
        timeLeftInMillis = sharedPreferences.getLong("study_time_left", 3600000); // Default: 1 hour

        TextView timerText = findViewById(R.id.timer_text);
        Button startButton = findViewById(R.id.start_button1);
        Button pauseButton = findViewById(R.id.pause_button1);

        updateTimer(timerText);

        startButton.setOnClickListener(v -> {
            if (!running) {
                startTimer(timerText);
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (running) {
                pauseTimer();
            }
        });
    }

    private void startTimer(TextView timerText) {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer(timerText);
            }

            @Override
            public void onFinish() {
                setHabitValid("Study");
            }
        }.start();
        running = true;

        // Save the start time for background tracking
        pauseTime = SystemClock.elapsedRealtime() + timeLeftInMillis;
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        running = false;

        // Save the remaining time to SharedPreferences
        sharedPreferences.edit().putLong("study_time_left", timeLeftInMillis).apply();
    }

    private void updateTimer(TextView timerText) {
        // Calculate the remaining minutes and seconds from timeLeftInMillis
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        // Format the time into MM:SS
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);

        // Update the TextView to display the formatted time
        timerText.setText(timeFormatted);

        // Update global variables or chronometer values here if needed
        sharedPreferences.edit().putLong("study_time_left", timeLeftInMillis).apply();
    }


    private void setHabitValid(String habit) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(habit, true);
        editor.putInt("daily_score", Math.min(sharedPreferences.getInt("daily_score", 0) + 25, 100));
        editor.putLong("study_time_left", 3600000); // Reset timer for the next day
        editor.apply();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (running) {
            pauseTimer(); // Pause the timer
            sharedPreferences.edit().putLong("pause_time", pauseTime).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        long lastPauseTime = sharedPreferences.getLong("pause_time", 0);
        if (lastPauseTime > 0 && running)
        {
            long elapsedTime = SystemClock.elapsedRealtime() - lastPauseTime;
            timeLeftInMillis -= elapsedTime;
            sharedPreferences.edit().putLong("study_time_left", timeLeftInMillis).apply();
        }

        // Update the timer and start if it was running
        TextView timerText = findViewById(R.id.timer_text);
        updateTimer(timerText);

        if (running)
        {
            startTimer(timerText);
        }
    }
}
