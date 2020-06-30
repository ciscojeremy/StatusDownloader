package com.appdev.statusdownloader.Fragments;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.appdev.statusdownloader.Adapter.VideoStatusAdapter;
import com.appdev.statusdownloader.Common.Common;
import com.appdev.statusdownloader.R;

import java.io.File;
import java.util.ArrayList;


public class VideoStatusFragment extends Fragment {

    View myFragment;
    RecyclerView recycler_video_status;
    SwipeRefreshLayout videoSwipeRefresh;

    //private static VideoStatusFragment videoStatusFragment = null;

    public VideoStatusFragment() {
        //Require empty public constructor
    }

    public static VideoStatusFragment newInstance(){
        VideoStatusFragment videoStatusFragment = new VideoStatusFragment();
        return videoStatusFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_video_status, container, false);


        // Setup SwipeRefreshLayout
        videoSwipeRefresh = (SwipeRefreshLayout) myFragment.findViewById(R.id.videoSwipeRefresh);
        videoSwipeRefresh.setColorSchemeResources(R.color.colorAccent);


        // Setup RecyclerView with LayoutManager
        recycler_video_status = (RecyclerView) myFragment.findViewById(R.id.recycler_video_status);
        recycler_video_status.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_video_status.setHasFixedSize(true);


        // RecyclerView Item Decorator
        //recycler_video_status.addItemDecoration(new SpacesItemDecorationGrid(4, 4));


        // Set Animation to RecyclerView
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_video_status.getContext(), R.anim.layout_fall_down);
        recycler_video_status.setLayoutAnimation(controller);


        // Get WhatsApp Status Files from Device
        File file = new File(Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_DIR_LOCATION);


        // Set Adapter on RecyclerView
        VideoStatusAdapter adapter = new VideoStatusAdapter(this.getListFiles(file), getActivity());
        adapter.notifyDataSetChanged();
        recycler_video_status.setClickable(true);
        recycler_video_status.setAdapter(adapter);

        videoSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ReloadVideoFile();

                    }
                }, 500);

            }
        });

        return myFragment;
    }

    private void ReloadVideoFile() {

        File file = new File(Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_DIR_LOCATION);
        VideoStatusAdapter adapter = new VideoStatusAdapter(this.getListFiles(file), getActivity());
        recycler_video_status.setAdapter(adapter);
        videoSwipeRefresh.setRefreshing(false);


        recycler_video_status.getAdapter().notifyDataSetChanged();
        recycler_video_status.scheduleLayoutAnimation();

    }

    private ArrayList<File> getListFiles(File parentDir)
    {

        ArrayList<File> inFiles = new ArrayList<>();
        File[] files;

        files = parentDir.listFiles();

        if (files != null)
        {
            for (File file : files)
            {
                if (file.getName().endsWith(".mp4")
                        || file.getName().endsWith(".jpeg"))
                {
                    if (!inFiles.contains(file))
                        inFiles.add(file);

                }
            }
        }

        return inFiles;

    }

}
