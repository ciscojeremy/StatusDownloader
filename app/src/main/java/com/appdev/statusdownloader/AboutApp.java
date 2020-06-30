package com.appdev.statusdownloader;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.thefinestartist.finestwebview.FinestWebView;

public class AboutApp extends AppCompatActivity {

    AdView adViewAbout;
    TextView privacyPolicy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        MobileAds.initialize(this, getResources().getString(R.string.adMob_app_id));
        adViewAbout = (AdView) findViewById(R.id.adViewAbout);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewAbout.loadAd(adRequest);

        privacyPolicy = (TextView) findViewById(R.id.privacyPolicy);

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String privacyPolicyUrl = "https://appdev24.blogspot.com/2019/02/effective-date-february-3-2019.html?m=1";
                new FinestWebView.Builder(AboutApp.this).show(privacyPolicyUrl);
            }
        });

    }

    @Override
    public void onPause() {
        if (adViewAbout != null) {
            adViewAbout.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adViewAbout != null) {
            adViewAbout.resume();
        }
    }

    @Override
    public void onDestroy() {

        if (adViewAbout != null) {
            adViewAbout.destroy();
        }
        super.onDestroy();
    }

}
