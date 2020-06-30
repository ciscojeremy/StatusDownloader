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

import com.appdev.statusdownloader.Adapter.ImageStatusAdapter;
import com.appdev.statusdownloader.Common.Common;
import com.appdev.statusdownloader.R;

import java.io.File;
import java.util.ArrayList;


public class StatusImageFragment extends Fragment {

    View myFragment;
    RecyclerView recycler_image_status;
    SwipeRefreshLayout imageSwipeRefresh;

    //private static StatusImageFragment INSTANCE = null;

    public StatusImageFragment() {
        //Require empty public constructor
    }

    public static StatusImageFragment newInstance(){
        StatusImageFragment INSTANCE = new StatusImageFragment();
        return INSTANCE;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myFragment = inflater.inflate(R.layout.fragment_status_image, container, false);
        //myFragment.setSaveFromParentEnabled(false);


        // Setup SwipeRefreshLayout
        imageSwipeRefresh = (SwipeRefreshLayout) myFragment.findViewById(R.id.imageSwipeRefresh);
        imageSwipeRefresh.setColorSchemeResources(R.color.colorAccent);


        // Setup RecyclerView with LayoutManager
        recycler_image_status = (RecyclerView) myFragment.findViewById(R.id.recycler_image_status);
        recycler_image_status.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_image_status.setHasFixedSize(true);


        // RecyclerView Item Decorator
        //recycler_image_status.addItemDecoration(new SpacesItemDecorationGrid(4, 4));


        // Set Animation to RecyclerView
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_image_status.getContext(), R.anim.layout_fall_down);
        recycler_image_status.setLayoutAnimation(controller);


        // Get WhatsApp Status Files from Device
        File file = new File(Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_DIR_LOCATION);


        // Set Adapter on RecyclerView
        ImageStatusAdapter adapter = new ImageStatusAdapter(this.getListFiles(file), getActivity());
        adapter.notifyDataSetChanged();
        recycler_image_status.setClickable(true);
        recycler_image_status.setAdapter(adapter);

        imageSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ReloadImageFile();

                    }
                }, 500);

            }
        });

        return myFragment;
    }

    private void ReloadImageFile() {

        File file = new File(Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_DIR_LOCATION);
        ImageStatusAdapter adapter = new ImageStatusAdapter(this.getListFiles(file), getActivity());
        recycler_image_status.setAdapter(adapter);
        imageSwipeRefresh.setRefreshing(false);


        recycler_image_status.getAdapter().notifyDataSetChanged();
        recycler_image_status.scheduleLayoutAnimation();

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
                if (file.getName().endsWith(".jpg")
                        || file.getName().endsWith(".png"))
                {
                    if (!inFiles.contains(file))
                        inFiles.add(file);

                }
            }
        }

        return inFiles;

    }


}
