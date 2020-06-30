package com.appdev.statusdownloader.Adapter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.appdev.statusdownloader.Common.Common;
import com.appdev.statusdownloader.FullImageActivity;
import com.appdev.statusdownloader.Interface.ItemClickListener;
import com.appdev.statusdownloader.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import de.mateware.snacky.Snacky;
import io.paperdb.Paper;

public class ImageStatusAdapter extends RecyclerView.Adapter<ImageStatusAdapter.ImageStatusHolder>{

    private ArrayList<File> filesList;
    private Activity mActivity;

    private int lastPosition = -1;

    public ImageStatusAdapter(ArrayList<File> filesList, Activity activity) {
        this.filesList = filesList;
        mActivity = activity;
    }

    @Override
    public ImageStatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_status_row_item, parent, false);
        return new ImageStatusHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageStatusHolder fileHolder, int position) {

        // Get preference settings
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean isDeleteChecked = sharedPrefs.getBoolean("switch_delete_option", false);

        final File currentFile = filesList.get(position);
        fileHolder.imageDownloadButton.setOnClickListener(downloadMediaItem(currentFile));

        //setAnimation(fileHolder.itemView, position);

        if (isDeleteChecked){

            fileHolder.btnDelete.setVisibility(View.VISIBLE);
            fileHolder.btnDelete.setOnClickListener(deleteMediaItem(currentFile));

        } else {

            fileHolder.btnDelete.setVisibility(View.GONE);

        }

        if (currentFile.getAbsolutePath().endsWith(".jpg") || currentFile.getAbsolutePath().endsWith(".png"))
        {
            final Bitmap imageThumbNail = BitmapFactory.decodeFile(currentFile.getAbsolutePath());
            fileHolder.statusImageFile.setImageBitmap(imageThumbNail);

            fileHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {

                    //Bitmap convertedImage = getResizedBitmap(imageThumbNail, 350);
                    Intent imageIntent = new Intent(mActivity, FullImageActivity.class);
                    imageIntent.putExtra("bitmap", currentFile.getAbsolutePath());
                    imageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(imageIntent);

                }
            });

        }

    }

    private void setAnimation(View itemView, int i) {

        if (i > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mActivity, android.R.anim.slide_in_left);
            itemView.startAnimation(animation);
            lastPosition = i;
        }

    }

    private View.OnClickListener deleteMediaItem(final File currentFile) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setMessage("Are you sure you want to delete this Image?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                new Runnable(){
                                    @Override
                                    public void run() {

                                        try {

                                            File file = new File(Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_DIR_LOCATION + currentFile.getName());
                                            deleteFile(file);
                                            int position = filesList.indexOf(currentFile);
                                            filesList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, filesList.size());
                                            Snacky.builder().setActivty(mActivity).setText(currentFile.getName() + " deleted").success().show();

                                        } catch (Exception e){
                                            e.printStackTrace();
                                            Snacky.builder().setActivty(mActivity).setText("Oops: " + e.getMessage()).error().show();

                                        }

                                    }
                                }.run();

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
        };
    }

    private void deleteFile(File file) {

        if (file.exists()){

            file.delete();

        }

    }

    private Bitmap getResizedBitmap(Bitmap imageThumbNail, int maxSize) {

        int width = imageThumbNail.getWidth();
        int height = imageThumbNail.getHeight();

        float bitmapRatio = (float) width /(float) height;
        if (bitmapRatio > 1){

            width = maxSize;
            height = (int) (width /bitmapRatio);

        } else {

            height = maxSize;
            width = (int) (height * bitmapRatio);

        }
        return Bitmap.createScaledBitmap(imageThumbNail, width, height, true);
    }

    private View.OnClickListener downloadMediaItem(final File sourceFile)
    {
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        final boolean isDownloadChecked = sharedPrefs.getBoolean("switch_rename_file", false);

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isDownloadChecked){

                    new Runnable(){
                        @Override
                        public void run() {

                            try {

                                //String imageName = edtRenameFile.getText().toString();
                                File destFile = new File(Environment.getExternalStorageDirectory().toString() + Common.DIR_SAVE + sourceFile.getName());
                                copyFile(sourceFile, destFile, 0);
                                Snacky.builder().setActivty(mActivity).setText(sourceFile.getName() + " saved to gallery").success().show();

                            } catch (Exception e){
                                e.printStackTrace();
                                Snacky.builder().setActivty(mActivity).setText("Oops: " + e.getMessage()).error().show();

                            }

                        }
                    }.run();

                } else {

                    //Paper
                    Paper.init(mActivity);

                    String fileName = sourceFile.getName();
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.layout_rename_file, null);
                    dialog.setView(dialogView);

                    TextView txtHint = dialogView.findViewById(R.id.txtHint);

                    final EditText edtRenameFile = dialogView.findViewById(R.id.edtRenameFile);
                    edtRenameFile.setText(fileName);

                    final CheckBox ckb_default_name = dialogView.findViewById(R.id.ckb_default_name);
                    String isDefault = Paper.book().read("default_name");
                    if (isDefault == null || TextUtils.isEmpty(isDefault) || isDefault.equals("false"))
                    {
                        ckb_default_name.setChecked(false);
                        edtRenameFile.setVisibility(View.VISIBLE);
                        txtHint.setVisibility(View.VISIBLE);
                        ckb_default_name.setText(R.string.use_default_file_name);
                    }
                    else
                    {
                        ckb_default_name.setChecked(true);
                        edtRenameFile.setVisibility(View.GONE);
                        txtHint.setVisibility(View.GONE);
                        ckb_default_name.setText(R.string.file_name_rename);
                    }

                    dialog.setPositiveButton("DOWNLOAD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            new Runnable() {
                                @Override
                                public void run() {

                                    if (ckb_default_name.isChecked()){

                                        try {

                                            //String imageName = edtRenameFile.getText().toString();
                                            File destFile = new File(Environment.getExternalStorageDirectory().toString() + Common.DIR_SAVE + sourceFile.getName());
                                            copyFile(sourceFile, destFile, 0);
                                            Snacky.builder().setActivty(mActivity).setText(sourceFile.getName() + " saved to gallery").success().show();

                                        } catch (Exception e){
                                            e.printStackTrace();
                                            Snacky.builder().setActivty(mActivity).setText("Oops: " + e.getMessage()).error().show();

                                        }
                                        Paper.book().write("default_name", "true");

                                    }else {

                                        try {
                                            //sourceFile.getName()
                                            String imageName = edtRenameFile.getText().toString();
                                            File destFile = new File(Environment.getExternalStorageDirectory().toString() + Common.DIR_SAVE + imageName);
                                            copyFile(sourceFile, destFile, 0);
                                            Snacky.builder().setActivty(mActivity).setText(imageName + " saved to gallery").success().show();

                                        } catch (Exception e){
                                            e.printStackTrace();
                                            Snacky.builder().setActivty(mActivity).setText("Oops: " + e.getMessage()).error().show();

                                        }
                                        Paper.book().write("default_name", "false");

                                    }

                                }
                            }.run();

                        }
                    });
                    dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.create();
                    dialog.show();

                }



            }
        };
    }

    private void copyFile(File sourceFile, File destFile, long WSDownloader) throws IOException
    {

        if (!destFile.getParentFile().exists())
        {
            destFile.getParentFile().mkdirs();
        }

        if (!destFile.exists())
            destFile.createNewFile();

        FileChannel source = null;
        FileChannel destination = null;

        source = new FileInputStream(sourceFile).getChannel();
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

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public class ImageStatusHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView statusImageFile;
        ImageView btnDelete;
        TextView imageDownloadButton;

        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public ImageStatusHolder(View itemView) {
            super(itemView);

            statusImageFile = itemView.findViewById(R.id.statusImageFile);
            imageDownloadButton = itemView.findViewById(R.id.imageDownloadButton);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
