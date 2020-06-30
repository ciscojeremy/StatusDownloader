package com.appdev.statusdownloader;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AppUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnUpdate, btnNotNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnNotNow = (Button) findViewById(R.id.btnNotNow);

        btnUpdate.setOnClickListener(this);
        btnNotNow.setOnClickListener(this);

        //*311*2*number#  08126931166

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnUpdate:
                Bundle values = getIntent().getExtras();
                if (values != null)
                {
                    String urlApp = values.getString("AppUpdate");
                    Intent updateApp = new Intent(Intent.ACTION_VIEW);
                    updateApp.setData(Uri.parse(urlApp));
                    startActivity(updateApp);
                    finish();
                }
                break;

            case R.id.btnNotNow:
                finish();
                break;

        }

    }

}
