package com.example.habittracker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "HabitTrackerDB";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USER_HABITS = "user_habits";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_DAILY_SCORE = "daily_score";
    private static final String COLUMN_SCORE_HISTORY = "score_history";
    private static final String COLUMN_LAST_SYNC = "last_sync_date";

    private static final String COLUMN_STUDY_VALID = "study_valid";
    private static final String COLUMN_SLEEP_VALID = "sleep_valid";
    private static final String COLUMN_FITNESS_VALID = "fitness_valid";
    private static final String COLUMN_WATER_VALID = "water_valid";

    private static final String COLUMN_STUDY_TIME_LEFT = "study_time_left";
    private static final String COLUMN_WATER_PROGRESS = "water_progress";

    private static final String COLUMN_CHECK_EXERCISE = "check_exercise";
    private static final String COLUMN_CHECK_MEDITATE = "check_meditate";
    private static final String COLUMN_CHECK_DRINK_WATER = "check_drink_water";


    private static final String TABLE_USERS_AUTH = "user_authentication";

    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD_HASH = "password_hash";


    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createAuthTableQuery = "CREATE TABLE " + TABLE_USERS_AUTH + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD_HASH + " TEXT)";
        db.execSQL(createAuthTableQuery);


        String createHabitsTableQuery = "CREATE TABLE " + TABLE_USER_HABITS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_DAILY_SCORE + " INTEGER DEFAULT 0, " +
            COLUMN_SCORE_HISTORY + " TEXT DEFAULT '0,0,0,0,0,0,0', " +
            COLUMN_LAST_SYNC + " TEXT, " +
            COLUMN_STUDY_VALID + " INTEGER DEFAULT 0, " +
            COLUMN_SLEEP_VALID + " INTEGER DEFAULT 0, " +
            COLUMN_FITNESS_VALID + " INTEGER DEFAULT 0, " +
            COLUMN_WATER_VALID + " INTEGER DEFAULT 0, " +
            COLUMN_STUDY_TIME_LEFT + " INTEGER DEFAULT 3600000, " +
            COLUMN_WATER_PROGRESS + " INTEGER DEFAULT 0, " +
            COLUMN_CHECK_EXERCISE + " INTEGER DEFAULT 0, " +
            COLUMN_CHECK_MEDITATE + " INTEGER DEFAULT 0, " +
            COLUMN_CHECK_DRINK_WATER + " INTEGER DEFAULT 0)";

        db.execSQL(createHabitsTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS_AUTH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_HABITS);

        onCreate(db);
    }

    public boolean isUsernameTaken(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS_AUTH,
                new String[]{COLUMN_USERNAME},
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public long registerUser(String username, String passwordHash)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_PASSWORD_HASH, passwordHash);

            long userId = db.insert(TABLE_USERS_AUTH, null, values);

            Log.d("DatabaseHelper", "User registered with ID: " + userId);
            return userId;
        }
        catch (Exception e)
        {
            Log.e("DatabaseHelper", "Error registering user", e);
            return -1;
        }
    }

    @SuppressLint("Range")
    public int authenticateUser(String username, String passwordHash)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS_AUTH,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD_HASH + " = ?",
                new String[]{username, passwordHash},
                null, null, null);

        int userId = -1;
        if (cursor.moveToFirst())
        {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
        }

        cursor.close();
        return userId;
    }

    public void insertOrUpdateUser(int userId, ContentValues values)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsAffected = db.update(TABLE_USER_HABITS, values,
            COLUMN_USER_ID + " = ?",
            new String[]{String.valueOf(userId)});

        if (rowsAffected == 0)
        {
            values.put(COLUMN_USER_ID, userId);
            db.insert(TABLE_USER_HABITS, null, values);
        }

        db.close();
    }

    public Cursor getUserData(int userId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USER_HABITS,
            null, 
            COLUMN_USER_ID + " = ?", 
            new String[]{String.valueOf(userId)}, 
            null, null, null);
    }

    public String getCurrentDate()
    {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Calendar.getInstance().getTime());
    }
}
