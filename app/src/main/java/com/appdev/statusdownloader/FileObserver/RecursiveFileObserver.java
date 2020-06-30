package com.appdev.statusdownloader.FileObserver;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.FileObserver;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.appdev.statusdownloader.Common.Common;
import com.appdev.statusdownloader.MainActivity;
import com.appdev.statusdownloader.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class RecursiveFileObserver extends FileObserver {

    /** Only modification events */
    public static int CHANGES_ONLY = CREATE | DELETE | CLOSE_WRITE | MOVE_SELF | MOVED_FROM | MOVED_TO;

    private List<SingleFileObserver> mObservers;
    private String mPath;
    private int mMask;
    private Activity activity;
    NotificationManager notificationManager;

    //ALL_EVENTS
    public RecursiveFileObserver(String path, Activity activity) {
        super(path, ALL_EVENTS);
        mPath = path;
        mMask = ALL_EVENTS;

        this.activity = activity;
    }

    public RecursiveFileObserver(String path, int mask, Activity activity) {
        super(path, mask);
        mPath = path;
        mMask = mask;

        this.activity = activity;
    }

    @Override
    public void startWatching() {
        if (mObservers != null) return;

        mObservers = new ArrayList<SingleFileObserver>();
        Stack<String> stack = new Stack<String>();
        stack.push(mPath);

        while (!stack.isEmpty()) {
            String parent = stack.pop();
            mObservers.add(new SingleFileObserver(parent, mMask));
            File path = new File(parent);
            File[] files = path.listFiles();
            if (null == files) continue;

            for (File f : files)
            {
                if (f.isDirectory() && !f.getName().equals(".") && !f.getName().equals("..")) {
                    stack.push(f.getPath());
                }
            }
        }

        for (SingleFileObserver sfo : mObservers) {
            sfo.startWatching();
        }
    }

    @Override
    public void stopWatching() {
        if (mObservers == null) return;

        for (SingleFileObserver sfo : mObservers) {
            sfo.stopWatching();
        }
        mObservers.clear();
        mObservers = null;
    }

    @Override
    public void onEvent(int event, final String path) {
        switch (event)
        {
            case FileObserver.ACCESS:
                Log.i("RecursiveFileObserver", "ACCESS: " + path);
                break;
            case FileObserver.ATTRIB:
                Log.i("RecursiveFileObserver", "ATTRIB: " + path);
                break;
            case FileObserver.CLOSE_NOWRITE:
                Log.i("RecursiveFileObserver", "CLOSE_NOWRITE: " + path);
                break;
            case FileObserver.CLOSE_WRITE:
                Log.i("RecursiveFileObserver", "CLOSE_WRITE: " + path);
                break;
            case FileObserver.CREATE:
                Log.i("RecursiveFileObserver", "CREATE: " + path);

                // show Toast message
                activity.runOnUiThread(new Runnable() {
                    public void run() {

                        handleNotification();

                        /*
                        Setting up Notification channels for android O and above
                       */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        {
                            if (Build.VERSION.SDK_INT >= 26)
                            {
                                setupChannels();
                            }

                        }

                        Toast.makeText(activity, "File created: " + path, Toast.LENGTH_LONG).show();
                    }
                });

                break;
            case FileObserver.DELETE:
                Log.i("RecursiveFileObserver", "DELETE: " + path);
                break;
            case FileObserver.DELETE_SELF:
                Log.i("RecursiveFileObserver", "DELETE_SELF: " + path);
                break;
            case FileObserver.MODIFY:
                Log.i("RecursiveFileObserver", "MODIFY: " + path);

                // show Toast message
                activity.runOnUiThread(new Runnable() {
                    public void run() {

                        handleNotification();

                        /*
                        Setting up Notification channels for android O and above
                       */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        {
                            if (Build.VERSION.SDK_INT >= 26)
                            {
                                setupChannels();
                            }

                        }

                        Toast.makeText(activity, "You will be notified when a new status is added: " + path, Toast.LENGTH_LONG).show();

                    }
                });

                break;
            case FileObserver.MOVE_SELF:
                Log.i("RecursiveFileObserver", "MOVE_SELF: " + path);
                break;
            case FileObserver.MOVED_FROM:
                Log.i("RecursiveFileObserver", "MOVED_FROM: " + path);
                break;
            case FileObserver.MOVED_TO:
                Log.i("RecursiveFileObserver", "MOVED_TO: " + path);
                break;
            case FileObserver.OPEN:
                Log.i("RecursiveFileObserver", "OPEN: " + path);
                break;
            default:
                Log.i("RecursiveFileObserver", "DEFAULT(" + event + "): " + path);
                break;
        }
    }

    private void handleNotification() {

        Intent pushNotification = new Intent(Common.STR_PUSH);
        pushNotification.putExtra("New WhatsApp Status Added", 20);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(pushNotification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){

        CharSequence adminChannelName = ("Status Downloader Channel");
        String adminChannelDescription = ("Notifications sent from Status Downloader");

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(Common.ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_MAX);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.enableVibration(true);
        adminChannel.setLightColor(Color.BLUE);
        adminChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        adminChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    /**
     * Monitor single directory and dispatch all events to its parent, with full path.
     * @author    uestc.Mobius <mobius@toraleap.com>
     * @version  2011.0121
     */
    class SingleFileObserver extends FileObserver {

        String mPath;

        public SingleFileObserver(String path) {
            this(path, ALL_EVENTS);
            mPath = path;
        }

        public SingleFileObserver(String path, int mask) {
            super(path, mask);
            mPath = path;
        }

        // + "/"
        @Override
        public void onEvent(int event, String path) {
            String newPath = mPath + path;
            RecursiveFileObserver.this.onEvent(event, newPath);
        }
    }

}
