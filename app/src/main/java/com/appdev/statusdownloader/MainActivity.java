package com.appdev.statusdownloader;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.appdev.statusdownloader.Adapter.FragmentAdapter;
import com.appdev.statusdownloader.Common.Common;
import com.appdev.statusdownloader.FileObserver.RecursiveFileObserver;
import com.appdev.statusdownloader.Helper.UpdateHelper;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.Random;
import java.util.UUID;

import de.mateware.snacky.Snacky;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements UpdateHelper.OnUpdateCheckListener{

    Toolbar toolbar;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    AdView adView;
    TapTargetSequence sequence;
    ViewPager viewPager;
    TabLayout tabLayout;
    LinearLayout layout_remove_ads_main;
    TextView click_here_main;

    boolean isNotificationChecked, isFirstStart;

    SharedPreferences sharedPrefs, sharedPrefsDarkTheme, sharedPrefsAppUpdate, mPref;

    // BILLING CONSTANTS
    private static final String LOG_TAG = "iabv3";
    private BillingProcessor bp;
    private boolean readyToPurchase, ProductIsPurchased = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Dark Theme Preferences
        sharedPrefsDarkTheme = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkChecked = sharedPrefsDarkTheme.getBoolean("switch_dark_theme", false);
        if (isDarkChecked) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Google AdMob
        MobileAds.initialize(this, getResources().getString(R.string.adMob_app_id));

        //Register Notifications
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        isNotificationChecked = sharedPrefs.getBoolean("switch_notification", false);
        registerNotification();

        //Init ToolBar
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setTitle("Status Downloader");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        //init view
        InitViews();

        //App Update Trigger
        sharedPrefsAppUpdate = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isChecked = sharedPrefsAppUpdate.getBoolean("application_updates", false);
        if (isChecked){

            UpdateHelper.with(this).onUpdateCheck(this).check();

        }

        //Request Runtime Permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            watchPathAndWriteFile();
        }

        //Show User first Launch Intro
        mPref = getSharedPreferences(getString(R.string.preference_file_key_first_time), Context.MODE_PRIVATE);
        isFirstStart = mPref.getBoolean("isFirstStart", true);
        if (isFirstStart)
        {
            ShowAppIntroMain();
        }

        // Initiate Billing Process
        if (!BillingProcessor.isIabServiceAvailable(this)) {
            showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
            return;
        }

        bp = new BillingProcessor(this, Common.LICENSE_KEY, Common.MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                showToast("You Purchased: " + details);
                ProductIsPurchased = true;
                adView.setVisibility(View.GONE);
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                //Integer.toString(errorCode)
                if (error != null)
                showToast("onBillingError: " + error.getMessage());
            }

            @Override
            public void onBillingInitialized() {
                //showToast("onBillingInitialized");
                readyToPurchase = true;
            }

            @Override
            public void onPurchaseHistoryRestored() {
                //showToast("onPurchaseHistoryRestored");
                for (String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
            }
        });


        //Setup ViewPager
        viewPager = findViewById(R.id.viewPager);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(fragmentAdapter);

        //Setup TabLayout
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        //Show Google AdMob Proper then Remove Ads if Product Item is Purchased
        adView = findViewById(R.id.adView);
        if (ProductIsPurchased)
        {
            adView.setVisibility(View.GONE);
            layout_remove_ads_main.setVisibility(View.GONE);
        }
        else
        {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    layout_remove_ads_main.setVisibility(View.VISIBLE);
                    click_here_main.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Monetize here
                            ShowMonetizationDialog();
                        }
                    });
                    super.onAdLoaded();
                }
            });
        }

        //Whats new dialog
        SharedPreferences prefs_whats_new = getSharedPreferences("prefs_whats_new", Context.MODE_PRIVATE);
        boolean whatsNew = prefs_whats_new.getBoolean("whatsNew", true);
        if (whatsNew)
        {
            ShowWhatsNewDialog();
        }


    }

    private void InitViews() {
        layout_remove_ads_main = findViewById(R.id.layout_remove_ads_main);
        click_here_main = findViewById(R.id.click_here_main);
    }

    private void ShowWhatsNewDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("What's New");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogViewWhatsNew = inflater.inflate(R.layout.layout_whats_new, null);
        dialog.setView(dialogViewWhatsNew);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });
        dialog.create();
        dialog.show();

        SharedPreferences prefs_whats_new = getSharedPreferences("prefs_whats_new", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs_whats_new.edit();
        editor.putBoolean("whatsNew", false);
        editor.apply();

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void ShowAppIntroMain() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                sequence = new TapTargetSequence(MainActivity.this).targets(
                        TapTarget.forToolbarMenuItem(toolbar, R.id.action_share, "Share", "Click on this icon to share app download link to friends on social media")
                                .cancelable(false)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(android.R.color.white)
                                .textColor(android.R.color.white),
                        TapTarget.forToolbarOverflow(toolbar, "More Options", "For more options like Settings, Rate App, Remove Ads and Help; Click on this icon.")
                                .cancelable(false)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(android.R.color.white)
                                .textColor(android.R.color.white))
                        .listener(new TapTargetSequence.Listener() {
                            @Override
                            public void onSequenceFinish() {
                                Toast.makeText(MainActivity.this, "Hope that was helpful!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                                Log.d("TapTargetView", "Clicked on " + lastTarget.id());

                            }

                            @Override
                            public void onSequenceCanceled(TapTarget lastTarget) {
                                Toast.makeText(MainActivity.this, "You canceled the sequence" + lastTarget, Toast.LENGTH_SHORT).show();
                            }
                        });
                sequence.start();

            }
        }, 500);

        mPref = getSharedPreferences(getString(R.string.preference_file_key_first_time), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("isFirstStart", false);
        editor.apply();

    }

    private void registerNotification() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Common.STR_PUSH))
                {
                    if (isNotificationChecked){

                        String message = intent.getStringExtra("New WhatsApp Status Added");
                        showNotification("Status Downloader", message);

                    }

                }

            }
        };

    }

    private void showNotification(String title, String message) {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_status)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(), builder.build());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case Common.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //setDefaultFragment();

                    // user granted permission
                    // start watching path & trigger notification.
                    watchPathAndWriteFile();
                }
                else
                {
                    Toast.makeText(this, "Please grant permission to enable the app work effectively", Toast.LENGTH_LONG).show();
                }
            }

            // Other case lines to check for other permissions this app might request

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings)
        {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));

        } else if (item.getItemId() == R.id.action_rate) {

            // Take user to play store to rate app
            RateApp();

        } else if (item.getItemId() == R.id.action_share) {

            ShareAppDownloadLink();

        } else if (item.getItemId() == R.id.action_help) {

            // Show help dialog
            //DisplayDialog();
            if (findViewById(R.id.imageDownloadButton) != null){

                ShowAppIntroHelp();

            } else if (findViewById(R.id.videoDownloadButton) != null){

                ShowAppIntroHelpVideoFragment();

            }


        } else if (item.getItemId() == R.id.action_business_status){

            //Launch Business Status Activity
            startActivity(new Intent(MainActivity.this, BusinessStatusActivity.class));

        } else if (item.getItemId() == R.id.action_gb_status){

            //Launch GB WhatsApp Status Activity
            Toast.makeText(this, "coming soon...", Toast.LENGTH_LONG).show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowAppIntroHelp() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                sequence = new TapTargetSequence(MainActivity.this).targets(
                        TapTarget.forToolbarMenuItem(toolbar, R.id.action_share, "Share", "Click on this icon to share app download link to friends on social media")
                                .cancelable(false)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(android.R.color.white)
                                .textColor(android.R.color.white),
                        TapTarget.forToolbarOverflow(toolbar, "More Options", "For more options like Settings, Rate App, Remove Ads and Help; Click on this icon.")
                                .cancelable(false)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(android.R.color.white)
                                .textColor(android.R.color.white),
                        TapTarget.forView(findViewById(R.id.imageDownloadButton), "Download Status", "Use this button to download a status image or video file")
                                .cancelable(false)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(android.R.color.white)
                                .textColor(android.R.color.white))
                        .listener(new TapTargetSequence.Listener() {
                            @Override
                            public void onSequenceFinish() {
                                Toast.makeText(MainActivity.this, "Hope that was helpful!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                                Log.d("TapTargetView", "Clicked on " + lastTarget.id());

                            }

                            @Override
                            public void onSequenceCanceled(TapTarget lastTarget) {
                                Toast.makeText(MainActivity.this, "You canceled the sequence" + lastTarget, Toast.LENGTH_SHORT).show();
                            }
                        });
                sequence.start();

            }
        }, 500);

    }

    private void ShowAppIntroHelpVideoFragment(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                sequence = new TapTargetSequence(MainActivity.this).targets(
                        TapTarget.forToolbarMenuItem(toolbar, R.id.action_share, "Share", "Click on this icon to share app download link to friends on social media")
                                .cancelable(false)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(android.R.color.white)
                                .textColor(android.R.color.white),
                        TapTarget.forToolbarOverflow(toolbar, "More Options", "For more options like, Settings, Rate App, Remove Ads and Help: Click on this icon")
                                .cancelable(false)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(android.R.color.white)
                                .textColor(android.R.color.white),
                        TapTarget.forView(findViewById(R.id.videoDownloadButton), "Download Status", "Use this button to download a status image or video file")
                                .cancelable(false)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(android.R.color.white)
                                .textColor(android.R.color.white))
                        .listener(new TapTargetSequence.Listener() {
                            @Override
                            public void onSequenceFinish() {
                                Toast.makeText(MainActivity.this, "Hope that was helpful!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                                Log.d("TapTargetView", "Clicked on " + lastTarget.id());

                            }

                            @Override
                            public void onSequenceCanceled(TapTarget lastTarget) {
                                Toast.makeText(MainActivity.this, "You canceled the sequence" + lastTarget, Toast.LENGTH_SHORT).show();
                            }
                        });
                sequence.start();

            }
        }, 500);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void ShowMonetizationDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.ic_monetization_on_black_24dp);
        dialog.setTitle("Want to remove banner adverts?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                        if (!readyToPurchase)
                        {
                            showToast("Billing not initialized.");
                        }
                        else
                        {
                            boolean isOneTimePurchaseSupported = bp.isOneTimePurchaseSupported();
                            if(isOneTimePurchaseSupported)
                            {
                                // launch payment flow
                                // if you want to include a developer payload
                                String DEVELOPER_PAYLOAD = UUID.randomUUID().toString().substring(17);
                                bp.purchase(MainActivity.this, Common.PRODUCT_ID, DEVELOPER_PAYLOAD);
                            }
                        }

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();

    }

    private void ShareAppDownloadLink(){

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out status downloader, for downloading WhatsApp status(es)" + " https://play.google.com/store/apps/details?id=com.appdev.statusdownloader");
        startActivity(Intent.createChooser(shareIntent, "Share Via:"));

    }

    private void RateApp() {

        String urlApp = "https://play.google.com/store/apps/details?id=com.appdev.statusdownloader";
        Intent updateApp = new Intent(Intent.ACTION_VIEW);
        updateApp.setData(Uri.parse(urlApp));
        startActivity(updateApp);

    }

    public void watchPathAndWriteFile() {

        //.getPath()
        String pathToWatch = Environment.getExternalStorageDirectory() + Common.WHATSAPP_DIR_LOCATION;
        RecursiveFileObserver observer = new RecursiveFileObserver(pathToWatch, this);

        // start watching the path
        observer.startWatching();

        // if file "MyTestFile" not exist, new file is created. Else file is modified
        /*try {
            FileWriter out = new FileWriter(new File(Environment.getExternalStorageDirectory().getPath() + Common.WHATSAPP_DIR_LOCATION + "MyTestFile"));
            out.write("my file content for test...");
            out.close();
        } catch (IOException e) {
            android.util.Log.e("writeStringAsFile", "Exception: " + e);
        }*/
    }

    @Override
    public void onUpdateCheckListener(String urlApp) {

        Intent updateIntent = new Intent(MainActivity.this, AppUpdateActivity.class);
        updateIntent.putExtra("AppUpdate", urlApp);
        startActivity(updateIntent);

        //showUpdateDialog(urlApp);

    }

    private void showUpdateDialog(final String urlApp) {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage("Please update the app to the latest version to enjoy new features.")
                .setCancelable(false)
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent updateApp = new Intent(Intent.ACTION_VIEW);
                        updateApp.setData(Uri.parse(urlApp));
                        startActivity(updateApp);

                    }
                })
                .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                }).create();

        alertDialog.show();

    }

    @Override
    public void onPause() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter("New WhatsApp Status Added"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Common.STR_PUSH));
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {

        if (adView != null) {
            adView.destroy();
        } else if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

}
