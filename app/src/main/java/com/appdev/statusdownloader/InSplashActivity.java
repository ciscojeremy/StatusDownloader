package com.appdev.statusdownloader;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class InSplashActivity extends AppCompatActivity {

    private ProgressBar progressBar_InSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_splash);

        // Linking Member Variables to MainActivity.java
        progressBar_InSplash = (ProgressBar) findViewById(R.id.progressBar_InSplash);

        // Initiating Splash Screen Activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 300);

        Thread ProgressBar_thread = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i <= 100;) {
                    try {
                        sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    progressBar_InSplash.setProgress(i);
                    i = i+10;
                }

            }
        };
        ProgressBar_thread.start();


    }
}
