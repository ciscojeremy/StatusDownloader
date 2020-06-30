package com.appdev.statusdownloader;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.appdev.statusdownloader.Common.Common;

import java.io.File;

public class SettingsActivity extends AppCompatPreferenceActivity{

    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isEnabled = sharedPrefs.getBoolean("switch_dark_theme", false);

        if (isEnabled){
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        /*if (sharedPrefs.getBoolean("switch_dark_theme", true)){
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }*/

        super.onCreate(savedInstanceState);
        setUpActionBar();
        addPreferencesFromResource(R.xml.preferences);

        //colorize();

        final Preference sendFeedback = findPreference(getString(R.string.send_feedback));
        sendFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                contactTeam();
                return true;
            }
        });

        final Preference clearCache = findPreference(getString(R.string.clear_cache));
        clearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Clear app cache
                deleteCache(SettingsActivity.this);
                return true;
            }
        });

        final Preference aboutApp = findPreference(getString(R.string.app_about));
        aboutApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Launch about activity
                launchAboutActivity();
                return true;
            }
        });

        Preference faqs = findPreference(getString(R.string.faq_question));
        faqs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LaunchFaqsActivity();
                return true;
            }
        });
        
        /*Preference theme_color_change = findPreference(getString(R.string.key_theme_color_change));
        theme_color_change.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ShowColorPalette();
                return true;
            }
        });*/


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void colorize() {

        ShapeDrawable d = new ShapeDrawable(new OvalShape());
        d.setBounds(58, 58, 58, 58);

        d.getPaint().setStyle(Paint.Style.FILL);
        d.getPaint().setColor(Common.color);

    }

    private void LaunchFaqsActivity() {

        startActivity(new Intent(SettingsActivity.this, FaqsActivity.class));

    }

    private void setUpActionBar() {

        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root);

        if (rootView != null) {

            View view = getLayoutInflater().inflate(R.layout.settings_page, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar_settings = (Toolbar) findViewById(R.id.toolbar_settings);
            toolbar_settings.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            setSupportActionBar(toolbar_settings);

        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);

        }

    }

    private void launchAboutActivity() {

        startActivity(new Intent(SettingsActivity.this, AboutApp.class));

    }

    public static void deleteCache(Context context) {

        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()){
                deleteDir(dir);
                Toast.makeText(context, "Cache Cleared", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static boolean  deleteDir(File dir) {

        if (dir != null && dir.isDirectory()){

            String[] children = dir.list();
            for (int i = 0; i < children.length; i++){

                boolean success = deleteDir(new File(dir, children[i]));
                if (!success){
                    return false;
                }

            }

        }
        return dir.delete();
    }

    private void contactTeam() {

        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:+2347065198485"));

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(SettingsActivity.this, "Please grant permission to make call", Toast.LENGTH_LONG).show();
            requestPermission();
        } else {
            startActivity(call);
        }

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            finish();

        }

        return super.onOptionsItemSelected(item);
    }
}
