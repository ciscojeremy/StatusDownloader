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

import com.appdev.statusdownloader.Adapter.BusinessVideoStatusAdapter;
import com.appdev.statusdownloader.Adapter.VideoStatusAdapter;
import com.appdev.statusdownloader.Common.Common;
import com.appdev.statusdownloader.R;

import java.io.File;
import java.util.ArrayList;

public class BusinessStatusVideoFragment extends Fragment {

    View myBusinessStatusVideoFragment;
    RecyclerView recycler_business_status_video;
    SwipeRefreshLayout businessStatusVideoSwipeRefresh;

    public BusinessStatusVideoFragment() {
        // Required empty public constructor
    }

    public static BusinessStatusVideoFragment newInstance(){
        BusinessStatusVideoFragment VIDEO_INSTANCE = new BusinessStatusVideoFragment();
        return VIDEO_INSTANCE;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myBusinessStatusVideoFragment =  inflater.inflate(R.layout.fragment_business_status_video, container, false);

        // Setup SwipeRefreshLayout
        businessStatusVideoSwipeRefresh = (SwipeRefreshLayout) myBusinessStatusVideoFragment.findViewById(R.id.businessStatusVideoSwipeRefresh);
        businessStatusVideoSwipeRefresh.setColorSchemeResources(R.color.colorAccent);


        // Setup RecyclerView with LayoutManager
        recycler_business_status_video = (RecyclerView) myBusinessStatusVideoFragment.findViewById(R.id.recycler_business_status_video);
        recycler_business_status_video.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_business_status_video.setHasFixedSize(true);


        // RecyclerView Item Decorator
        //recycler_video_status.addItemDecoration(new SpacesItemDecorationGrid(4, 4));


        // Set Animation to RecyclerView
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_business_status_video.getContext(), R.anim.layout_fall_down);
        recycler_business_status_video.setLayoutAnimation(controller);


        // Get WhatsApp Status Files from Device
        File file = new File(Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_BUSINESS_DIR_LOCATION);


        // Set Adapter on RecyclerView
        BusinessVideoStatusAdapter businessAdapter = new BusinessVideoStatusAdapter(this.getListFiles(file), getActivity());// remember to make changes here
        businessAdapter.notifyDataSetChanged();
        recycler_business_status_video.setClickable(true);
        recycler_business_status_video.setAdapter(businessAdapter);

        businessStatusVideoSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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

        return myBusinessStatusVideoFragment;

    }

    private void ReloadVideoFile() {

        //BusinessVideoStatusAdapter
        File file = new File(Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_BUSINESS_DIR_LOCATION);
        BusinessVideoStatusAdapter adapter = new BusinessVideoStatusAdapter(this.getListFiles(file), getActivity());//make changes here
        recycler_business_status_video.setAdapter(adapter);
        businessStatusVideoSwipeRefresh.setRefreshing(false);


        recycler_business_status_video.getAdapter().notifyDataSetChanged();
        recycler_business_status_video.scheduleLayoutAnimation();

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
                if (file.getName().endsWith(".mp4") || file.getName().endsWith(".jpeg"))
                {
                    if (!inFiles.contains(file))
                        inFiles.add(file);

                }
            }
        }

        return inFiles;

    }

}
