package com.appdev.statusdownloader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;

public class Settings extends AppCompatActivity implements View.OnClickListener{

    LinearLayout layoutOne, layoutTwo, layoutThree, layoutFour, layoutFive, layoutSix, layoutSeven;
    Switch switchDefaultFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Init ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        toolbar.setTitle("Settings");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        layoutOne = (LinearLayout) findViewById(R.id.layoutOne);
        layoutTwo = (LinearLayout) findViewById(R.id.layoutTwo);
        layoutThree = (LinearLayout) findViewById(R.id.layoutThree);
        layoutFour = (LinearLayout) findViewById(R.id.layoutFour);
        layoutFive = (LinearLayout) findViewById(R.id.layoutFive);
        layoutSix = (LinearLayout) findViewById(R.id.layoutSix);
        layoutSeven = (LinearLayout) findViewById(R.id.layoutSeven);

        switchDefaultFileName = (Switch) findViewById(R.id.switchDefaultFileName);

        layoutOne.setOnClickListener(this);
        layoutTwo.setOnClickListener(this);
        layoutThree.setOnClickListener(this);
        layoutFour.setOnClickListener(this);
        layoutFive.setOnClickListener(this);
        layoutSix.setOnClickListener(this);
        layoutSeven.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.layoutOne:
                switchDefaultFileName.setChecked(true);
                Toast.makeText(this, "Yey....", Toast.LENGTH_LONG).show();
                break;

            case R.id.layoutTwo:
                Toast.makeText(this, "Yey....", Toast.LENGTH_LONG).show();
                break;

            case R.id.layoutThree:
                Toast.makeText(this, "Yey....", Toast.LENGTH_LONG).show();
                break;

            case R.id.layoutFour:
                Toast.makeText(this, "Yey....", Toast.LENGTH_LONG).show();
                break;

            case R.id.layoutFive:
                // Clear app cache
                deleteCache(this);
                break;

            case R.id.layoutSix:
                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:+2347065198485"));

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Settings.this, "Please grant permission to make call", Toast.LENGTH_LONG).show();
                    requestPermission();
                } else {
                    startActivity(call);
                }
                break;

            case R.id.layoutSeven:
                // Launch about activity
                launchAboutActivity();
                break;

        }

    }

    private void launchAboutActivity() {

        startActivity(new Intent(Settings.this, AboutApp.class));

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

    /*-------------------------------------------------
    |  Function request runtime permission for call
    *--------------------------------------------------*/
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
    }

}
