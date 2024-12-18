package com.example.habittracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SleepActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        Button readyButton = findViewById(R.id.ready_button);
        readyButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Good Night!")
                    .setMessage("Are you ready to go to sleep?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        setHabitValid("Sleep");
                        finishAffinity(); // Close the app
                    })
                    .setNegativeButton("Not yet", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void setHabitValid(String habit) {
        SharedPreferences sharedPreferences = getSharedPreferences("HabitTrackerPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Check if the habit is already marked as valid
        boolean isAlreadyValid = sharedPreferences.getBoolean(habit, false);

        if (!isAlreadyValid) {
            // Mark the habit as valid
            editor.putBoolean(habit, true);

            // Increment the daily score by 25%
            int currentScore = sharedPreferences.getInt("daily_score", 0);
            int updatedScore = Math.min(currentScore + 25, 100); // Ensure the score does not exceed 100%
            editor.putInt("daily_score", updatedScore);

            // Save the changes
            editor.apply();
        }

        // Finish the activity and return to the main screen
        finish();
    }
}
