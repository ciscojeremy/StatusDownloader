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
import android.widget.Button;
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

public class VideoStatusAdapter extends RecyclerView.Adapter<VideoStatusAdapter.VideoStatusHolder>{

    private ArrayList<File> filesList;
    private Activity activity;

    public VideoStatusAdapter(ArrayList<File> filesList, Activity activity) {
        this.filesList = filesList;
        this.activity = activity;
    }

    @Override
    public VideoStatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_status_row_item, parent, false);
        return new VideoStatusHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoStatusHolder holder, int position) {

        // Get preference settings
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean isDeleteChecked = sharedPrefs.getBoolean("switch_delete_option", false);

        File currentFile = filesList.get(position);
        holder.videoDownloadButton.setOnClickListener(downloadMediaItem(currentFile));


        if (isDeleteChecked){

            holder.btnDeleteVideo.setVisibility(View.VISIBLE);
            holder.btnDeleteVideo.setOnClickListener(deleteMediaItem(currentFile));

        } else {

            holder.btnDeleteVideo.setVisibility(View.GONE);

        }


        if (currentFile.getAbsolutePath().endsWith(".mp4") || currentFile.getAbsolutePath().endsWith(".jpeg"))
        {

            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(currentFile.getAbsolutePath(), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
            holder.videoThumbNail.setBackgroundDrawable(bitmapDrawable);


            final Uri videoUri = Uri.parse(currentFile.getAbsolutePath());
            holder.statusVideoFile.setVideoURI(videoUri);
            holder.statusVideoFile.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
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
                    Intent videoIntent = new Intent(activity.getBaseContext(), FullVideoActivity.class);
                    videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    videoIntent.putExtra("videoUri", videoUri);
                    activity.startActivity(videoIntent);

                }
            });

        }

    }

    private View.OnClickListener deleteMediaItem(final File currentFile) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setMessage("Are you sure you want to delete this Video?")
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
                                            Snacky.builder().setActivty(activity).setText(currentFile.getName() + " deleted").success().show();

                                        } catch (Exception e){
                                            e.printStackTrace();
                                            Snacky.builder().setActivty(activity).setText("Oops: " + e.getMessage()).error().show();

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

    private View.OnClickListener downloadMediaItem(final File sourceFile)
    {

        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
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
                                Snacky.builder().setActivty(activity).setText(sourceFile.getName() + " saved to gallery").success().show();

                            } catch (Exception e){
                                e.printStackTrace();
                                Snacky.builder().setActivty(activity).setText("Oops: " + e.getMessage()).error().show();
                            }

                        }
                    }.run();

                } else {

                    //Paper
                    Paper.init(activity);

                    String fileName = sourceFile.getName();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = activity.getLayoutInflater();
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
                                            Snacky.builder().setActivty(activity).setText(sourceFile.getName() + " saved to gallery").success().show();

                                        } catch (Exception e){
                                            e.printStackTrace();
                                            Snacky.builder().setActivty(activity).setText("Oops: " + e.getMessage()).error().show();
                                        }
                                        Paper.book().write("default_name", "true");

                                    } else {

                                        try {//sourceFile.getName()
                                            String videoName = edtRenameFile.getText().toString();
                                            File destFile = new File(Environment.getExternalStorageDirectory().toString() + Common.DIR_SAVE + videoName);
                                            copyFile(sourceFile, destFile, 0);
                                            Snacky.builder().setActivty(activity).setText(videoName + " saved to gallery").success().show();

                                        } catch (Exception e){
                                            e.printStackTrace();
                                            Snacky.builder().setActivty(activity).setText("Oops: " + e.getMessage()).error().show();
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

    public class VideoStatusHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        VideoView statusVideoFile;
        TextView videoDownloadButton;
        ImageView videoThumbNail;
        ImageView btnDeleteVideo;

        ItemClickListener mItemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            mItemClickListener = itemClickListener;
        }

        public VideoStatusHolder(View itemView) {
            super(itemView);

            statusVideoFile = itemView.findViewById(R.id.statusVideoFile);
            videoDownloadButton = itemView.findViewById(R.id.videoDownloadButton);
            videoThumbNail = itemView.findViewById(R.id.videoThumbNail);
            btnDeleteVideo = itemView.findViewById(R.id.btnDeleteVideo);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            mItemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
