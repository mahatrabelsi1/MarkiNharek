package com.example.habittracker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UserDataManager {
    private static final String PREFS_NAME = "HabitTrackerPrefs";
    private DatabaseHelper databaseHelper;
    private Context context;

    public UserDataManager(Context context)
    {
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @SuppressLint("Range")
    public void syncFromDatabaseToSharedPrefs(int userId)
    {
        Cursor cursor = databaseHelper.getUserData(userId);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String currentDate = databaseHelper.getCurrentDate();

        if (cursor == null || !cursor.moveToFirst()) {
            // User not found in the database - set default values
            editor.clear();

            editor.putString("score_history", "0,0,0,0,0,0,0");
            editor.putInt("daily_score", 0);
            editor.putBoolean("Study", false);
            editor.putBoolean("Sleep", false);
            editor.putBoolean("Fitness", false);
            editor.putBoolean("Water", false);
            editor.putLong("study_time_left", 3600000);
            editor.putInt("water_progress", 0);
            editor.putBoolean("check_exercise", false);
            editor.putBoolean("check_meditate", false);
            editor.putBoolean("check_drink_water", false);

            editor.apply(); // Save changes to SharedPreferences
        } else {
            // User found in the database
            String lastSyncDate = cursor.getString(cursor.getColumnIndex("last_sync_date"));

            if (lastSyncDate == null || !lastSyncDate.equals(currentDate)) {
                String currentScoreHistory = sharedPreferences.getString("score_history", "0,0,0,0,0,0,0");
                String[] scores = currentScoreHistory.split(",");
                StringBuilder updatedHistory = new StringBuilder();

                int currentDailyScore = sharedPreferences.getInt("daily_score", 0);
                for (int i = 1; i < scores.length; i++) {
                    updatedHistory.append(scores[i]).append(",");
                }
                updatedHistory.append(currentDailyScore);

                editor.clear();

                editor.putString("score_history", updatedHistory.toString());
                editor.putInt("daily_score", 0);
                editor.putBoolean("Study", false);
                editor.putBoolean("Sleep", false);
                editor.putBoolean("Fitness", false);
                editor.putBoolean("Water", false);
                editor.putLong("study_time_left", 3600000);
                editor.putInt("water_progress", 0);
                editor.putBoolean("check_exercise", false);
                editor.putBoolean("check_meditate", false);
                editor.putBoolean("check_drink_water", false);
            } else {
                editor.putInt("daily_score", cursor.getInt(cursor.getColumnIndex("daily_score")));
                editor.putString("score_history", cursor.getString(cursor.getColumnIndex("score_history")));
                editor.putBoolean("Study", cursor.getInt(cursor.getColumnIndex("study_valid")) == 1);
                editor.putBoolean("Sleep", cursor.getInt(cursor.getColumnIndex("sleep_valid")) == 1);
                editor.putBoolean("Fitness", cursor.getInt(cursor.getColumnIndex("fitness_valid")) == 1);
                editor.putBoolean("Water", cursor.getInt(cursor.getColumnIndex("water_valid")) == 1);
                editor.putLong("study_time_left", cursor.getLong(cursor.getColumnIndex("study_time_left")));
                editor.putInt("water_progress", cursor.getInt(cursor.getColumnIndex("water_progress")));

                editor.putBoolean("check_exercise", cursor.getInt(cursor.getColumnIndex("check_exercise")) == 1);
                editor.putBoolean("check_meditate", cursor.getInt(cursor.getColumnIndex("check_meditate")) == 1);
                editor.putBoolean("check_drink_water", cursor.getInt(cursor.getColumnIndex("check_drink_water")) == 1);
            }
            editor.apply(); // Save changes to SharedPreferences
        }

    }
    public void syncFromSharedPrefsToDB(int userId)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        ContentValues values = new ContentValues();

        values.put("daily_score", sharedPreferences.getInt("daily_score", 0));
        values.put("score_history", sharedPreferences.getString("score_history", "0,0,0,0,0,0,0"));

        values.put("study_valid", sharedPreferences.getBoolean("Study", false) ? 1 : 0);
        values.put("sleep_valid", sharedPreferences.getBoolean("Sleep", false) ? 1 : 0);
        values.put("fitness_valid", sharedPreferences.getBoolean("Fitness", false) ? 1 : 0);
        values.put("water_valid", sharedPreferences.getBoolean("Water", false) ? 1 : 0);

        values.put("study_time_left", sharedPreferences.getLong("study_time_left", 3600000));
        values.put("water_progress", sharedPreferences.getInt("water_progress", 0));

        values.put("check_exercise", sharedPreferences.getBoolean("check_exercise", false) ? 1 : 0);
        values.put("check_meditate", sharedPreferences.getBoolean("check_meditate", false) ? 1 : 0);
        values.put("check_drink_water", sharedPreferences.getBoolean("check_drink_water", false) ? 1 : 0);

        values.put("last_sync_date", databaseHelper.getCurrentDate());

        databaseHelper.insertOrUpdateUser(userId, values);
    }
}