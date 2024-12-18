package com.example.habittracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.List;

public class BarChartView extends View {
    private List<Integer> scores; // Scores for the days
    private Paint barPaint;
    private Paint axisPaint;
    private Paint textPaint;

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context) {
        super(context);
        init();
    }

    private void init() {
        barPaint = new Paint();
        axisPaint = new Paint();
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(5f);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setScores(List<Integer> scores) {
        this.scores = scores;
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (scores == null || scores.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        int padding = 50;

        int graphWidth = width - 2 * padding;
        int graphHeight = height - 2 * padding;

        int barWidth = graphWidth / scores.size();
        int maxScore = 100; // Scores range from 0 to 100

        // Draw axes
        canvas.drawLine(padding, height - padding, width - padding, height - padding, axisPaint);
        canvas.drawLine(padding, padding, padding, height - padding, axisPaint);

        // Draw bars
        for (int i = 0; i < scores.size(); i++) {
            int score = scores.get(i);
            int barHeight = (int) ((score / (float) maxScore) * graphHeight);
            int left = padding + i * barWidth;
            int top = height - padding - barHeight;
            int right = left + barWidth - 10;

            // Determine bar color
            if (score < 25) {
                barPaint.setColor(Color.RED);
            } else if (score < 50) {
                barPaint.setColor(Color.parseColor("#FFA500")); // Orange
            } else if (score < 75) {
                barPaint.setColor(Color.YELLOW);
            } else {
                barPaint.setColor(Color.GREEN);
            }

            canvas.drawRect(left, top, right, height - padding, barPaint);

            // Draw day labels below bars
            canvas.drawText("Day " + (i + 1), left + barWidth / 2f - 5, height - padding + 30, textPaint);
        }
    }
}
