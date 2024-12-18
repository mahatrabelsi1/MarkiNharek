package com.example.habittracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
{
    private SharedPreferences sharedPreferences;
    private UserDataManager userDataManager;

    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("HabitTrackerPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("current_user_id", -1);

        if (currentUserId == -1)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        userDataManager = new UserDataManager(this);
        userDataManager.syncFromDatabaseToSharedPrefs(currentUserId);

        DisplayUserName();
        checkAndResetDailyScore();
        displayDailyScore();
        setupChecklist();

        ImageButton studyButton = findViewById(R.id.button_study);
        ImageButton sleepButton = findViewById(R.id.button_sleep);
        ImageButton fitnessButton = findViewById(R.id.button_fitness);
        ImageButton waterButton = findViewById(R.id.button_water);

        studyButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, StudyActivity.class)));
        sleepButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SleepActivity.class)));
        fitnessButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ExerciseActivity.class)));
        waterButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WaterActivity.class)));

        findViewById(R.id.button_summary).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SummaryActivity.class)));

        findViewById(R.id.button_sync).setOnClickListener(v -> {
            userDataManager.syncFromSharedPrefsToDB(currentUserId);
        });

        scheduleMinuteNotifications();
    }

    private void DisplayUserName() {
        TextView usernameDisplay = findViewById(R.id.username_display);
        String username = sharedPreferences.getString("current_username", "Guest");
        usernameDisplay.setText("Welcome, " + username + "!");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        userDataManager.syncFromSharedPrefsToDB(currentUserId);
    }

    private void setupChecklist()
    {
        RadioButton radioExercise = findViewById(R.id.radio_exercise);
        RadioButton radioMeditate = findViewById(R.id.radio_meditate);
        RadioButton radioDrinkWater = findViewById(R.id.radio_drink_water);

        radioExercise.setChecked(sharedPreferences.getBoolean("check_exercise", false));
        radioMeditate.setChecked(sharedPreferences.getBoolean("check_meditate", false));
        radioDrinkWater.setChecked(sharedPreferences.getBoolean("check_drink_water", false));

        radioExercise.setOnCheckedChangeListener((buttonView, isChecked) -> updateChecklist("check_exercise", isChecked));
        radioMeditate.setOnCheckedChangeListener((buttonView, isChecked) -> updateChecklist("check_meditate", isChecked));
        radioDrinkWater.setOnCheckedChangeListener((buttonView, isChecked) -> updateChecklist("check_drink_water", isChecked));
    }

    private void updateChecklist(String key, boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
        displayDailyScore();
    }

    private void checkAndResetDailyScore()
    {
        String lastSavedDate = sharedPreferences.getString("last_saved_date", "");
        String currentDate = getCurrentDate();

        if (!lastSavedDate.equals(currentDate))
        {
            resetDailyScore();
            resetChecklist();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("last_saved_date", currentDate);
            editor.apply();
        }
    }

    private void resetChecklist()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("check_exercise", false);
        editor.putBoolean("check_meditate", false);
        editor.putBoolean("check_drink_water", false);
        editor.apply();
    }

    private void resetDailyScore()
    {
        int dailyScore = sharedPreferences.getInt("daily_score", 0);
        saveDailyScoreToHistory(dailyScore);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("water_progress",0);
        editor.putInt("daily_score", 0);
        editor.putBoolean("Study", false);
        editor.putBoolean("Sleep", false);
        editor.putBoolean("Fitness", false);
        editor.putBoolean("Water", false);
        editor.apply();
    }

    private void saveDailyScoreToHistory(int dailyScore)
    {
        String scoreHistory = sharedPreferences.getString("score_history", "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
        String[] scores = scoreHistory.split(",");
        StringBuilder updatedHistory = new StringBuilder();

        for (int i = 1; i < scores.length; i++)
        {
            updatedHistory.append(scores[i]).append(",");
        }
        updatedHistory.append(dailyScore);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("score_history", updatedHistory.toString());
        editor.apply();
    }

    private void displayDailyScore()
    {
        TextView dailyScoreView = findViewById(R.id.daily_score_text);

        boolean studyCompleted = sharedPreferences.getBoolean("Study", false);
        boolean sleepCompleted = sharedPreferences.getBoolean("Sleep", false);
        boolean fitnessCompleted = sharedPreferences.getBoolean("Fitness", false);
        boolean waterCompleted = sharedPreferences.getBoolean("Water", false);
        boolean checkExercise = sharedPreferences.getBoolean("check_exercise", false);
        boolean checkMeditate = sharedPreferences.getBoolean("check_meditate", false);
        boolean checkDrinkWater = sharedPreferences.getBoolean("check_drink_water", false);

        int completedItems = 0;
        if (studyCompleted) completedItems++;
        if (sleepCompleted) completedItems++;
        if (fitnessCompleted) completedItems++;
        if (waterCompleted) completedItems++;
        if (checkExercise) completedItems++;
        if (checkMeditate) completedItems++;
        if (checkDrinkWater) completedItems++;

        int dailyScore = (completedItems * 100) / 7;
        dailyScoreView.setText(dailyScore + "%");
    }

    private String getCurrentDate()
    {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }

    private void scheduleMinuteNotifications()
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        long startMillis = Calendar.getInstance().getTimeInMillis();
        long intervalMillis = 600 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startMillis, intervalMillis, pendingIntent);
    }
}
