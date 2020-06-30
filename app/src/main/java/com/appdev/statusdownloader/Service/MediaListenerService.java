package com.appdev.statusdownloader.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.appdev.statusdownloader.Common.Common;
import com.appdev.statusdownloader.MainActivity;
import com.appdev.statusdownloader.R;

import java.util.Random;


public class MediaListenerService extends Service {

    public static FileObserver observer;
    private int notificationId;

    public MediaListenerService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startWatching();
    }

    private void startWatching() {

        //The desired path to watch or monitor
        //E.g Camera folder
        final String pathToWatch = Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_DIR_LOCATION;
        Toast.makeText(this, "My Service Started and trying to watch " + pathToWatch, Toast.LENGTH_LONG).show();

        // set up a file observer to watch this directory
        observer = new FileObserver(pathToWatch, FileObserver.ALL_EVENTS) {
            @Override
            public void onEvent(int event, final String file) {

                // check that it's not equal to .probe because that's created every time camera is launched (&& !file.equals(".probe"))
                if (event == FileObserver.CREATE || event == FileObserver.CLOSE_WRITE || event == FileObserver.MODIFY || event == FileObserver.MOVED_TO) {
                    Log.d("MediaListenerService", "File created [" + pathToWatch + file + "]");
                    Toast.makeText(MediaListenerService.this, "New File created: " + pathToWatch + file, Toast.LENGTH_LONG).show();


                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            handleNotification();
                            //showNotification();
                            //Toast.makeText(getBaseContext(), file + " was saved!", Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        };
        observer.startWatching();
    }

    private void handleNotification() {

        Intent pushNotification = new Intent(Common.STR_PUSH);
        pushNotification.putExtra("New WhatsApp Status Added, Refresh now!", 42);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }

    private void showNotification(){

        Intent detailsIntent = new Intent(getBaseContext(), MainActivity.class);
        PendingIntent detailsPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, detailsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_status);
                            builder.setContentTitle("Status Downloader");
                            builder.setContentText("New WhatsApp Status Added, Refresh now!");
                            builder.setContentIntent(detailsPendingIntent);
                            builder.setAutoCancel(true);
                            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.notify(notificationId, builder.build());

    }


}
