package com.example.habittracker;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class SummaryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Custom BarChartView
        BarChartView barChartView = findViewById(R.id.bar_chart_view);

        // Load last 7 days' scores
        ArrayList<Integer> scores = generateRandomScores(7);
        barChartView.setScores(scores);

        // Add a legend for colors
        setupLegend();
    }

    // Generate random scores for 7 days
    private ArrayList<Integer> generateRandomScores(int days) {
        ArrayList<Integer> scores = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < days; i++) {
            scores.add(random.nextInt(101)); // Generate scores from 0 to 100
        }
        return scores;
    }

    // Setup a legend to explain score colors
    private void setupLegend() {
        LinearLayout legendLayout = findViewById(R.id.legend_layout);

        // Add legend items
        addLegendItem(legendLayout, "Less than 25%", Color.RED);
        addLegendItem(legendLayout, "25% to 50%", Color.parseColor("#FFA500")); // Orange
        addLegendItem(legendLayout, "50% to 75%", Color.YELLOW);
        addLegendItem(legendLayout, "75% to 100%", Color.GREEN);
    }

    private void addLegendItem(LinearLayout legendLayout, String label, int color) {
        TextView legendItem = new TextView(this);
        legendItem.setText(label);
        legendItem.setTextSize(16);
        legendItem.setTextColor(color);
        legendLayout.addView(legendItem);
    }
}
