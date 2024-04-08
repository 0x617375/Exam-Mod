package com.exam.exammodwithx;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;
import java.util.Random;

public class ColorfulTextViewAnimator {
    private static final int ANIMATION_INTERVAL = 400; // 400ms for each color change
    private static final int[] COLORS = {
    0xFFFF0000, // Red
    0xFF00FF00, // Green
    0xFF0000FF, // Blue
    0xFFFFFF00, // Yellow
    0xFFFF00FF, // Magenta
    0xFF00FFFF, // Cyan
    0xFF000000, // Black
    0xFFFFFFFF, // White
    0xFFFFA500, // Orange
    0xFFA52A2A, // Brown
    0xFF808080, // Gray
    0xFF800080, // Purple
    0xFF008000, // Dark Green
    0xFF00FF7F, // Spring Green
    0xFFFFC0CB, // Pink
    0xFFFFFFA5, // Light Yellow
    0xFF20B2AA, // Light Sea Green
    0xFF87CEEB, // Sky Blue
    0xFF778899, // Light Slate Gray
    0xFFB0C4DE, // Light Steel Blue
    0xFF32CD32, // Lime Green
    0xFFF4A460, // Sandy Brown
    0xFF4682B4, // Steel Blue
    0xFFD2B48C, // Tan
    0xFF008080, // Teal
    0xFFD8BFD8, // Thistle
    0xFFFF6347, // Tomato
    0xFF40E0D0, // Turquoise
    0xFFEE82EE, // Violet
    0xFFF5DEB3, // Wheat
    0xFFF5F5F5, // White Smoke
    0xFF9ACD32  // Yellow Green
    };


    private TextView textView;
    private String text;
    private Handler handler = new Handler();
    private int colorIndex = 0;

    public ColorfulTextViewAnimator(TextView textView) {
        this.textView = textView;
        this.text = textView.getText().toString();
    }

    public void startAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Update the color of each character in the text without delay
                StringBuilder coloredText = new StringBuilder();
                for (int i = 0; i < text.length(); i++) {
                    // Apply the next color in the array to each character
                    coloredText.append("<font color='").append(COLORS[(colorIndex + i) % COLORS.length]).append("'><b>")
                    .append(text.charAt(i))
                    .append("</b></font>");
                    }
                textView.setText(android.text.Html.fromHtml(coloredText.toString()));
                colorIndex = (colorIndex + 1) % COLORS.length; // Move to the next color for the next animation cycle
                handler.postDelayed(this, ANIMATION_INTERVAL); // Continue the animation cycle
            }

        }, ANIMATION_INTERVAL);
    }
    
    public void startAnimationTwo() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                // Update the color of each character in the text without delay
                StringBuilder coloredText = new StringBuilder();
                for (int i = 0; i < text.length(); i++) {
                    // Apply the next color in the array to each character
                    coloredText.append("<font color='").append(COLORS[random.nextInt(COLORS.length)]).append("'><b>")
                    .append(text.charAt(i))
                    .append("</b></font>");
                    }
                textView.setText(android.text.Html.fromHtml(coloredText.toString()));
                colorIndex = (colorIndex + 1) % COLORS.length; // Move to the next color for the next animation cycle
                handler.postDelayed(this, ANIMATION_INTERVAL); // Continue the animation cycle
            }

        }, ANIMATION_INTERVAL);
    }

    public void stopAnimation() {
        handler.removeCallbacksAndMessages(null);
    }
}
