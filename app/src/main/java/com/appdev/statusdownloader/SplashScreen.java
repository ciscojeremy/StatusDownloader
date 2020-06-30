package com.appdev.statusdownloader;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Linking Member Variables to MainActivity.java
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Initiating Splash Screen Activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 500);

        Thread ProgressBar_thread = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i <= 100;) {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    progressBar.setProgress(i);
                    i = i+10;
                }

            }
        };
        ProgressBar_thread.start();


    }
}
