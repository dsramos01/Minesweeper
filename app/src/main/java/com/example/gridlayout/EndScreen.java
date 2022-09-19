package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

public class EndScreen extends AppCompatActivity {

    private TextView endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);

        endTime = (TextView) findViewById(R.id.EndTime);
        String timeUsed = getIntent().getStringExtra("TimeUsed");
        String winCheck = getIntent().getStringExtra("GameState");
        String message;
        if (winCheck.equals("Win")) {
            message = "Used " + timeUsed + " seconds.\nYou Win!\nGood Job!";
        }
        else {
            message = "Used " + timeUsed + " seconds.\nYou lost.\nNice try!";
        }
        endTime.setText(message);

        Button playAgain = findViewById(R.id.PlayAgain);
        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EndScreen.this,MainActivity.class));
            }
        });
    }
}