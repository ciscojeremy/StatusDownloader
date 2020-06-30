package com.appdev.statusdownloader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.appdev.statusdownloader.Common.Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import de.mateware.snacky.Snacky;

public class FullVideoActivity extends AppCompatActivity implements View.OnClickListener{

    VideoView statusVideoView;
    //FloatingActionButton btnDownload;
    LinearLayout layoutSave, layoutShare, layoutRePostVideo,layoutDelete;
    BottomSheetDialog bottomSheetDialog;
    Uri videoUri;

    SharedPreferences sharedPrefs, sharedPrefsDarkTheme;
    boolean isDarkThemeChecked, isVideoControlChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPrefsDarkTheme = PreferenceManager.getDefaultSharedPreferences(this);
        isDarkThemeChecked =  sharedPrefsDarkTheme.getBoolean("switch_dark_theme", false);

        if (isDarkThemeChecked){
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_video);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        isVideoControlChecked = sharedPrefs.getBoolean("switch_video_control", false);

        Intent videoIntent = getIntent();
        videoUri = Uri.parse(videoIntent.getExtras().get("videoUri").toString());

        statusVideoView = (VideoView)findViewById(R.id.statusVideoView);
        if (videoUri != null)
        {
            statusVideoView.setVideoURI(videoUri);
        }
        else
        {
            return;
        }

        statusVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {

                            if (isVideoControlChecked){

                                android.widget.MediaController mc = new android.widget.MediaController(FullVideoActivity.this);
                                statusVideoView.setMediaController(mc);
                                statusVideoView.requestFocus();
                                mc.setAnchorView(statusVideoView);

                            }


                        }
                    });

                statusVideoView.start();

            }
        });

        statusVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                finish();
            }
        });

        //btnDownload = (FloatingActionButton) findViewById(R.id.btnDownload);
        /*btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.show();

            }
        });*/

        createBottomSheetDialog();

        //btnDownload.setOnClickListener(downloadMediaItem(videoUri));

    }

    private void createBottomSheetDialog() {

        if (bottomSheetDialog == null) {

            View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_video_layout, null);

            layoutSave = view.findViewById(R.id.layoutSave);
            layoutShare = view.findViewById(R.id.layoutShare);
            layoutDelete = view.findViewById(R.id.layoutDelete);
            layoutRePostVideo = view.findViewById(R.id.layoutRePostVideo);

            layoutSave.setOnClickListener(this);
            layoutShare.setOnClickListener(this);
            layoutDelete.setOnClickListener(this);
            layoutRePostVideo.setOnClickListener(this);

            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);

        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.layoutSave:
                downloadMediaItem(videoUri);
                bottomSheetDialog.dismiss();
                break;

            case R.id.layoutShare:
                ShareVideo();
                bottomSheetDialog.dismiss();
                break;

            case R.id.layoutDelete:
                Toast.makeText(this, "Deleting file....", Toast.LENGTH_LONG).show();
                bottomSheetDialog.dismiss();
                break;

            case R.id.layoutRePostVideo:
                RePostVideo();
                bottomSheetDialog.dismiss();
                break;

        }

    }

    private void ShareVideo() {

        Intent sharing = new Intent(Intent.ACTION_SEND);
        sharing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharing.setType("image/*");
        sharing.putExtra(Intent.EXTRA_TEXT, "Shared from Status Downloader Get it here https://play.google.com/store/apps/details?id=com.appdev.statusdownloader");
        sharing.putExtra(Intent.EXTRA_STREAM, videoUri);
        startActivity(Intent.createChooser(sharing, "Share Video:"));

    }

    private void RePostVideo() {

        Intent sharing = new Intent(Intent.ACTION_SEND);
        sharing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharing.setType("image/*");
        sharing.setPackage("com.whatsapp");
        sharing.putExtra(Intent.EXTRA_TEXT, "Shared from Status Downloader Get it here https://play.google.com/store/apps/details?id=com.appdev.statusdownloader");
        sharing.putExtra(Intent.EXTRA_STREAM, videoUri);
        startActivity(sharing);

    }

    private void downloadMediaItem(final Uri videoUri) {

        new Runnable() {
            @Override
            public void run() {

                try {

                    File destFile = new File(Environment.getExternalStorageDirectory().toString() + Common.DIR_SAVE + videoUri);
                    copyFile(videoUri, destFile, 0);
                    Toast.makeText(FullVideoActivity.this, "Saved to gallery", Toast.LENGTH_LONG).show();

                } catch (Exception e){

                    e.printStackTrace();
                    Toast.makeText(FullVideoActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        }.run();

    }

    private void copyFile(Uri videoUri, File destFile, long WSDownloader) throws IOException
    {

        if (!destFile.getParentFile().exists())
        {
            destFile.getParentFile().mkdirs();
        }

        if (!destFile.exists())
            destFile.createNewFile();

        FileChannel source = null;
        FileChannel destination = null;

        source = new FileInputStream(String.valueOf(videoUri)).getChannel();
        destination = new FileOutputStream(destFile).getChannel();

        destination.transferFrom(source, WSDownloader, source.size());

        if (source != null)
        {
            source.close();
        }

        if (destination != null)
        {
            destination.close();
        }

    }


}
