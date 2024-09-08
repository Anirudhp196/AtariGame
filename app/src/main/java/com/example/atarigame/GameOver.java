package com.example.atarigame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class GameOver extends AppCompatActivity {

    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        score = findViewById(R.id.textView2);

        Intent launchAppIntent = getIntent();
        int scoreCount = launchAppIntent.getIntExtra("score", 0);
        score.setText("" + scoreCount);
    }
}