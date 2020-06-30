package com.appdev.statusdownloader.Common;


import com.appdev.statusdownloader.R;

public class Common {
    public static final String WHATSAPP_DIR_LOCATION = "/WhatsApp/Media/.Statuses/";
    public static final String WHATSAPP_BUSINESS_DIR_LOCATION = "/WhatsApp Business/Media/.Statuses/";
    public static final String DIR_SAVE = "/WSDownloader/";
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 10;

    public static final String STR_PUSH = "pushNotification";
    public static final String ADMIN_CHANNEL_ID ="admin_channel";


    // PRODUCT & SUBSCRIPTION IDS
    public static final String PRODUCT_ID = "com.status.downloader.iab.i5.o6";
    public static final String SUBSCRIPTION_ID = "com.status.downloader.iab.subs1";

    /*
    Your License Key from Google Developer console.
    This will be used to verify purchase signatures.
    You can pass NULL if you would like to skip this check
    (You can find your key in Google Play Console -> Your App Name -> Services & APIs)
    https://developer.android.com/google/play/billing/billing_testing.html
    */
    public static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs0iFp/5FZwYbKeTYnH/sD1pTeosrIU+48MiMGlfpFlya+cYBq8HcpsScgNtUHA0ntavJzLZRyfQi+/3sYt9yl/IErZyurIC18G6NZA2qOvpo6k2++OxT1z02LP4KgGeYr0E+wbpArg0SCRo0qOvYA0WMJR2Hpp240StRJSekTjatIiEmvt0qNRQqNJs5jz16WwZkGGz9dhfvvbxpzw7yaNB+xC6nxImKQASC6f4ghN7NxOVrOM+eOx2wO7fn35dGOw3s0B5W0yla185V2jwD/P0kJolG+RUG2Ou5l71lNUAplUc8PnKdHN82iq1letzburAtXzpR+rilBHgBLStwjwIDAQAB"; // PUT YOUR MERCHANT KEY HERE;

    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled, library will provide protection against Freedom like Play Market simulators
    public static final String MERCHANT_ID = "08080457214245317765";

    public static int color = 0xff3b5998;
    public static int theme = R.style.AppTheme;

}
