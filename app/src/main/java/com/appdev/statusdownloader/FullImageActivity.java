package com.appdev.statusdownloader;

import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appdev.statusdownloader.Common.Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import de.mateware.snacky.Snacky;

public class FullImageActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView statusImage;
    FloatingActionButton btnDialog;
    LinearLayout layoutSave, layoutShare, layoutSetAs, layoutRePost,layoutDelete;
    BottomSheetDialog bottomSheetDialog;
    Bitmap bitmap;
    Uri bitmapUri;

    SharedPreferences sharedPrefsDarkTheme;
    boolean isDarkThemeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPrefsDarkTheme = PreferenceManager.getDefaultSharedPreferences(this);
        isDarkThemeEnabled = sharedPrefsDarkTheme.getBoolean("switch_dark_theme", false);

        if (isDarkThemeEnabled){
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        Intent intent = getIntent();
        String image = intent.getStringExtra("bitmap");

        File imageFile = new File(image);
        if (imageFile.exists()){
            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            statusImage = findViewById(R.id.statusImage);
            statusImage.setImageBitmap(bitmap);
        }

        btnDialog = findViewById(R.id.btnDialog);
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.show();

            }
        });

        createBottomSheetDialog();

    }

    private void createBottomSheetDialog() {

        if (bottomSheetDialog == null) {

            View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet, null);

            layoutSave = view.findViewById(R.id.layoutSave);
            layoutShare = view.findViewById(R.id.layoutShare);
            layoutSetAs = view.findViewById(R.id.layoutSetAs);
            layoutDelete = view.findViewById(R.id.layoutDelete);
            layoutRePost = view.findViewById(R.id.layoutRePost);

            layoutSave.setOnClickListener(this);
            layoutShare.setOnClickListener(this);
            layoutSetAs.setOnClickListener(this);
            layoutDelete.setOnClickListener(this);
            layoutRePost.setOnClickListener(this);

            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);

        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.layoutSave:
                downloadMediaItem(bitmap);
                bottomSheetDialog.dismiss();
                break;

            case R.id.layoutShare:
                ShareImage();
                bottomSheetDialog.dismiss();
                break;

            case R.id.layoutSetAs:
                setImageAsWallpaper();
                bottomSheetDialog.dismiss();
                break;

            case R.id.layoutDelete:
                Toast.makeText(this, "Deleting file....", Toast.LENGTH_LONG).show();
                bottomSheetDialog.dismiss();
                break;

            case R.id.layoutRePost:
                RePostImage();
                bottomSheetDialog.dismiss();
                break;

        }

    }

    private void RePostImage() {

        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "status image", null);
        bitmapUri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.setPackage("com.whatsapp");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared from Status Downloader Get it here https://play.google.com/store/apps/details?id=com.appdev.statusdownloader");
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(FullImageActivity.this, "Kindly install whatsApp first", Toast.LENGTH_LONG).show();
        }

    }

    private void ShareImage() {

        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "status image", null);
        bitmapUri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared from Status Downloader Get it here https://play.google.com/store/apps/details?id=com.appdev.statusdownloader");
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent.createChooser(shareIntent, "Share Image via:"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(FullImageActivity.this, "Kindly install whatsApp first", Toast.LENGTH_LONG).show();
        }

    }

    private void setImageAsWallpaper() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("You're about to set this image as your device wallpaper")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                        try {
                            wallpaperManager.setBitmap(bitmap);
                            Toast.makeText(FullImageActivity.this, "Wallpaper was set", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();

    }

    private void downloadMediaItem(final Bitmap bitmap) {

        new Runnable() {
            @Override
            public void run() {

                try {
                    //sourceFile.getName()
                    String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "status image", null);
                    bitmapUri = Uri.parse(bitmapPath);
                    Snacky.builder().setActivty(FullImageActivity.this).setText("Saved to gallery").success().show();

                } catch (Exception e){
                    e.printStackTrace();
                    Snacky.builder().setActivty(FullImageActivity.this).setText("Oops: " + e.getMessage()).error().show();

                }

            }
        }.run();

    }

    /*File destFile = new File(Environment.getExternalStorageDirectory().toString() + Common.DIR_SAVE + bitmapUri);
    copyFile(bitmap, destFile, 0);*/
    private void copyFile(Bitmap bitMap, File destFile, long WSDownloader) throws IOException {

        if (!destFile.getParentFile().exists())
        {
            destFile.getParentFile().mkdirs();
        }

        if (!destFile.exists())
            destFile.createNewFile();

        FileChannel source = null;
        FileChannel destination = null;

        source = new FileInputStream(String.valueOf(bitMap)).getChannel();
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
