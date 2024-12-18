package com.example.habittracker;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class WaterActivity extends AppCompatActivity {
    private int progress;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        SharedPreferences sharedPreferences = getSharedPreferences("HabitTrackerPrefs", MODE_PRIVATE);
        progress = sharedPreferences.getInt("water_progress", 0); // Fetch progress from SharedPreferences

        progressBar = findViewById(R.id.progress_bar1);

        // Set progress bar color to blue
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        }

        progressBar.setProgress(progress); // Set the initial progress to 0 or stored value

        Button drinkButton = findViewById(R.id.drink_button1);
        drinkButton.setOnClickListener(v -> {
            if (progress < 5) {
                progress++;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("water_progress", progress); // Update progress
                editor.apply();
                progressBar.setProgress(progress);

                if (progress == 5) {
                    setHabitValid("Water");
                }
            }
        });
    }

    private void setHabitValid(String habit) {
        SharedPreferences sharedPreferences = getSharedPreferences("HabitTrackerPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean isAlreadyValid = sharedPreferences.getBoolean(habit, false);
        if (!isAlreadyValid) {
            editor.putBoolean(habit, true);
            int currentScore = sharedPreferences.getInt("daily_score", 0);
            editor.putInt("daily_score", Math.min(currentScore + 25, 100)); // Increment by 25%
            editor.apply();
        }

        finish(); // Return to MainActivity
    }
}
