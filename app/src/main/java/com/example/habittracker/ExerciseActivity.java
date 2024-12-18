package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ExerciseActivity extends AppCompatActivity {
    private static final String TAG = "ExerciseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        Button upperBodyButton = findViewById(R.id.upper_body_button1);
        Button lowerBodyButton = findViewById(R.id.lower_body_button1);

        upperBodyButton.setOnClickListener(v -> {
            String videoUri = "android.resource://" + getPackageName() + "/" + R.raw.video1;
            Log.d(TAG, "Playing Upper Body Video: " + videoUri);
            playVideo(videoUri);
        });

        lowerBodyButton.setOnClickListener(v -> {
            String videoUri = "android.resource://" + getPackageName() + "/" + R.raw.video1;
            Log.d(TAG, "Playing Lower Body Video: " + videoUri);
            playVideo(videoUri);
        });
    }

    private void playVideo(String videoUri) {
        Intent intent = new Intent(this, VideoPlaybackActivity.class);
        intent.putExtra(VideoPlaybackActivity.VIDEO_URI_KEY, videoUri);
        startActivity(intent);
    }
}
