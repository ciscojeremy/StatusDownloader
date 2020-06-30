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

import com.appdev.statusdownloader.Adapter.BusinessVideoStatusAdapter;
import com.appdev.statusdownloader.Adapter.VideoStatusAdapter;
import com.appdev.statusdownloader.Common.Common;
import com.appdev.statusdownloader.R;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessVideo extends Fragment {

    View my_Fragment;
    RecyclerView recycler;
    SwipeRefreshLayout Refresh;

    public BusinessVideo() {
        // Required empty public constructor
    }

    public static BusinessVideo newInstance(){
        BusinessVideo videoStatusFragment = new BusinessVideo();
        return videoStatusFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        my_Fragment =  inflater.inflate(R.layout.fragment_business_video, container, false);

        // Setup SwipeRefreshLayout
        Refresh = (SwipeRefreshLayout) my_Fragment.findViewById(R.id.Refresh);
        Refresh.setColorSchemeResources(R.color.colorAccent);


        // Setup RecyclerView with LayoutManager
        recycler = (RecyclerView) my_Fragment.findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler.setHasFixedSize(true);


        // RecyclerView Item Decorator
        //recycler_video_status.addItemDecoration(new SpacesItemDecorationGrid(4, 4));


        // Set Animation to RecyclerView
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler.getContext(), R.anim.layout_fall_down);
        recycler.setLayoutAnimation(controller);


        // Get WhatsApp Status Files from Device
        File file = new File(Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_BUSINESS_DIR_LOCATION);


        // Set Adapter on RecyclerView
        BusinessVideoStatusAdapter adapter = new BusinessVideoStatusAdapter(this.getListFiles(file), getActivity());
        adapter.notifyDataSetChanged();
        recycler.setClickable(true);
        recycler.setAdapter(adapter);

        Refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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

        return my_Fragment;

    }

    private void ReloadVideoFile() {

        File file = new File(Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_DIR_LOCATION);
        BusinessVideoStatusAdapter adapter = new BusinessVideoStatusAdapter(this.getListFiles(file), getActivity());
        recycler.setAdapter(adapter);
        Refresh.setRefreshing(false);


        recycler.getAdapter().notifyDataSetChanged();
        recycler.scheduleLayoutAnimation();

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
