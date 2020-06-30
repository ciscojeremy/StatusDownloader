package com.appdev.statusdownloader.Common;


import android.app.Activity;
import android.content.Intent;

import com.appdev.statusdownloader.R;

public class themeUtils {

    private static int cTheme;

    public final static int BLACK = 1;

    public static void changeToTheme(Activity activity, int theme) {

        cTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));


    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (cTheme)
        {
            default:
            case BLACK:
                activity.setTheme(R.style.DarkTheme);
                break;

        }

    }

}
