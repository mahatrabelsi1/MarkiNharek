package com.example.habittracker;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlaybackActivity extends AppCompatActivity {
    public static final String VIDEO_URI_KEY = "video_uri_key";
    private static final String TAG = "VideoPlaybackActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_playback_activity);

        VideoView videoView = findViewById(R.id.video_view);

        // Get the video URI from the intent
        String videoUri = getIntent().getStringExtra(VIDEO_URI_KEY);
        if (videoUri != null) {
            Log.d(TAG, "Received Video URI: " + videoUri);
            videoView.setVideoURI(Uri.parse(videoUri));
            videoView.start();
        } else {
            Log.e(TAG, "Video URI is null");
        }

        // Close activity when video completes
        videoView.setOnCompletionListener(mp -> {
            Log.d(TAG, "Video playback completed");
            finish();
        });
    }
}
