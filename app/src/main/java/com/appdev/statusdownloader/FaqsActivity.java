package com.appdev.statusdownloader;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class FaqsActivity extends AppCompatActivity {

    Toolbar toolbar_faqs;
    SharedPreferences sharedPrefs;
    boolean isDarkThemeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        isDarkThemeEnabled = sharedPrefs.getBoolean("switch_dark_theme", false);

        if (isDarkThemeEnabled){
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);

        //Init ToolBar
        toolbar_faqs = (Toolbar) findViewById(R.id.toolbar_faqs);
        toolbar_faqs.setTitle("FAQs");
        toolbar_faqs.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar_faqs);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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
