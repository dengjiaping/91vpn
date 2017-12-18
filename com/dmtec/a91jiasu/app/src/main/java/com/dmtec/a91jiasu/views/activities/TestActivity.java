package com.dmtec.a91jiasu.views.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.common.Timer;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        final TextView time = (TextView)findViewById(R.id.timer);
        final Timer timer = new Timer(60, new Timer.TimerCallback() {
            @Override
            public void onTimeOut() {

            }

            @Override
            public void onTimeFailed() {

            }

            @Override
            public void onSecondPassed(final int timeLeft) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time.setText(timeLeft+"");
                    }
                });
            }
        });
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.start();
            }
        });
        findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.pause();
            }
        });
        findViewById(R.id.contin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.contin();
            }
        });
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.reset();
            }
        });
    }
}
