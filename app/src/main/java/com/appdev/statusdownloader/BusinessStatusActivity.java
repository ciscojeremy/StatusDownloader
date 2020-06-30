package com.appdev.statusdownloader;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.appdev.statusdownloader.Adapter.BusinessStatusFragmentAdapter;
import com.appdev.statusdownloader.Common.Common;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.UUID;

import de.mateware.snacky.Snacky;

public class BusinessStatusActivity extends AppCompatActivity {

    Toolbar toolbar_business_status;
    AdView adView_business_status;
    SharedPreferences sharedPrefsDarkTheme;
    ViewPager viewPager_business_status;
    TabLayout tabLayout_business_status;
    CoordinatorLayout business_status_snack_bar;
    LinearLayout layout_remove_ads;
    TextView click_here;

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
        setContentView(R.layout.activity_business_status);

        //Init Google AdMob
        MobileAds.initialize(this, getResources().getString(R.string.adMob_app_id));

        //Init ToolBar
        toolbar_business_status = findViewById(R.id.toolbar_business_status);
        toolbar_business_status.setTitle("Business Status");
        toolbar_business_status.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar_business_status.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar_business_status);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initView();

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
                adView_business_status.setVisibility(View.GONE);
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

        //Check if whatsApp business is installed
        boolean isAppInstalled = appInstalledOrNot("com.whatsapp.w4b");
        if (isAppInstalled) {

            //Setup ViewPager For Business Status
            viewPager_business_status = findViewById(R.id.viewPager_business_status);
            BusinessStatusFragmentAdapter businessStatusFragmentAdapter = new BusinessStatusFragmentAdapter(getSupportFragmentManager(), this);
            viewPager_business_status.setAdapter(businessStatusFragmentAdapter);

            //Setup TabLayout
            tabLayout_business_status = findViewById(R.id.tabLayout_business_status);
            tabLayout_business_status.setupWithViewPager(viewPager_business_status);
            tabLayout_business_status.setSelectedTabIndicatorColor(Color.WHITE);
            tabLayout_business_status.setTabTextColors(Color.WHITE, Color.WHITE);
            tabLayout_business_status.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        } else {

            Snackbar.make(business_status_snack_bar, "WhatsApp Business Not Installed", Snackbar.LENGTH_INDEFINITE).show();

        }

        //Show Google AdMob Proper then Remove Ads if Product Item is Purchased
        adView_business_status = findViewById(R.id.adView_business_status);
        if (ProductIsPurchased)
        {
            adView_business_status.setVisibility(View.GONE);
            layout_remove_ads.setVisibility(View.GONE);
        }
        else
        {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView_business_status.loadAd(adRequest);
            adView_business_status.setAdListener(new AdListener() {
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
                    layout_remove_ads.setVisibility(View.VISIBLE);
                    click_here.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //layout_remove_ads.setVisibility(View.GONE);
                            //Monetize here
                            ShowMonetizationDialog();
                        }
                    });
                    super.onAdLoaded();
                }
            });
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
                                bp.purchase(BusinessStatusActivity.this, Common.PRODUCT_ID, DEVELOPER_PAYLOAD);
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

    private void initView() {
        business_status_snack_bar = findViewById(R.id.business_status_snack_bar);
        layout_remove_ads = findViewById(R.id.layout_remove_ads);
        click_here = findViewById(R.id.click_here);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        if (adView_business_status != null) {
            adView_business_status.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView_business_status != null) {
            adView_business_status.resume();
        }
    }

    @Override
    public void onDestroy() {

        if (adView_business_status != null) {
            adView_business_status.destroy();
        } else if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

}
