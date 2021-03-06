package com.appdev.statusdownloader.Adapter;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.appdev.statusdownloader.Common.Common;
import com.appdev.statusdownloader.FullVideoActivity;
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

public class BusinessVideoStatusAdapter extends RecyclerView.Adapter<BusinessVideoStatusAdapter.BusinessVideoStatusHolder>{

    private ArrayList<File> businessStatusVideoFilesList;
    private Activity mActivity;

    public BusinessVideoStatusAdapter(ArrayList<File> businessStatusVideoFilesList, Activity activity) {
        this.businessStatusVideoFilesList = businessStatusVideoFilesList;
        mActivity = activity;
    }

    @Override
    public BusinessVideoStatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_status_video_row_item, parent, false);
        return new BusinessVideoStatusHolder(view);
    }

    @Override
    public void onBindViewHolder(BusinessVideoStatusHolder holder, int position) {

        // Get preference settings
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean isDeleteChecked = sharedPrefs.getBoolean("switch_delete_option", false);

        File currentFile = businessStatusVideoFilesList.get(position);
        holder.businessStatusVideoDownloadButton.setOnClickListener(downloadMediaItem(currentFile));


        if (isDeleteChecked){

            holder.btnBusinessStatusDeleteVideo.setVisibility(View.VISIBLE);
            holder.btnBusinessStatusDeleteVideo.setOnClickListener(deleteMediaItem(currentFile));

        } else {

            holder.btnBusinessStatusDeleteVideo.setVisibility(View.GONE);

        }


        if (currentFile.getAbsolutePath().endsWith(".mp4") || currentFile.getAbsolutePath().endsWith(".jpeg"))
        {

            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(currentFile.getAbsolutePath(), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
            holder.businessStatusVideoThumbNail.setBackgroundDrawable(bitmapDrawable);


            final Uri videoUri = Uri.parse(currentFile.getAbsolutePath());
            holder.businessStatusVideoFile.setVideoURI(videoUri);
            holder.businessStatusVideoFile.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    //mediaPlayer.setLooping(true);
                    //holder.statusVideoFile.start();
                    //int time = mediaPlayer.getDuration();
                    //holder.statusVideoFile.seekTo(100);

                }
            });

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {

                    //Pass video file to another activity
                    Intent videoIntent = new Intent(mActivity.getBaseContext(), FullVideoActivity.class);
                    videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    videoIntent.putExtra("videoUri", videoUri);
                    mActivity.startActivity(videoIntent);

                }
            });

        }

    }

    private View.OnClickListener deleteMediaItem(final File currentFile) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setMessage("Are you sure you want to delete this Video?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                new Runnable(){
                                    @Override
                                    public void run() {

                                        try {

                                            File file = new File(Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_BUSINESS_DIR_LOCATION + currentFile.getName());
                                            deleteFile(file);
                                            int position = businessStatusVideoFilesList.indexOf(currentFile);
                                            businessStatusVideoFilesList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, businessStatusVideoFilesList.size());
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

    private View.OnClickListener downloadMediaItem(final File sourceFile) {

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
                                //String videoName = edtRenameFile.getText().toString();
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
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.layout_rename_file, null);
                    dialog.setView(dialogView);

                    TextView txtHint = dialogView.findViewById(R.id.txtHint);

                    final EditText edtRenameFile = dialogView.findViewById(R.id.edtRenameFile);
                    edtRenameFile.setText(fileName);

                    final CheckBox ckb_default_name = dialogView.findViewById(R.id.ckb_default_name);
                    String isDefault = Paper.book().read("default_name");
                    if (isDefault == null || TextUtils.isEmpty(isDefault) || isDefault.equals("false")){

                        ckb_default_name.setChecked(false);
                        edtRenameFile.setVisibility(View.VISIBLE);
                        txtHint.setVisibility(View.VISIBLE);
                        ckb_default_name.setText(R.string.use_default_file_name);

                    } else {

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
                                            //String videoName = edtRenameFile.getText().toString();
                                            File destFile = new File(Environment.getExternalStorageDirectory().toString() + Common.DIR_SAVE + sourceFile.getName());
                                            copyFile(sourceFile, destFile, 0);
                                            Snacky.builder().setActivty(mActivity).setText(sourceFile.getName() + " saved to gallery").success().show();

                                        } catch (Exception e){
                                            e.printStackTrace();
                                            Snacky.builder().setActivty(mActivity).setText("Oops: " + e.getMessage()).error().show();
                                        }
                                        Paper.book().write("default_name", "true");

                                    } else {

                                        try {//sourceFile.getName()
                                            String videoName = edtRenameFile.getText().toString();
                                            File destFile = new File(Environment.getExternalStorageDirectory().toString() + Common.DIR_SAVE + videoName);
                                            copyFile(sourceFile, destFile, 0);
                                            Snacky.builder().setActivty(mActivity).setText(videoName + " saved to gallery").success().show();

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

    private void copyFile(File sourceFile, File destFile, long WSDownloader) throws IOException {

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
        return businessStatusVideoFilesList.size();
    }

    public class BusinessVideoStatusHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        VideoView businessStatusVideoFile;
        TextView businessStatusVideoDownloadButton;
        ImageView businessStatusVideoThumbNail;
        ImageView btnBusinessStatusDeleteVideo;

        ItemClickListener mItemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            mItemClickListener = itemClickListener;
        }

        public BusinessVideoStatusHolder(View itemView) {
            super(itemView);

            businessStatusVideoFile = itemView.findViewById(R.id.businessStatusVideoFile);
            businessStatusVideoDownloadButton = itemView.findViewById(R.id.businessStatusVideoDownloadButton);
            businessStatusVideoThumbNail = itemView.findViewById(R.id.businessStatusVideoThumbNail);
            btnBusinessStatusDeleteVideo = itemView.findViewById(R.id.btnBusinessStatusDeleteVideo);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            mItemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }

}
