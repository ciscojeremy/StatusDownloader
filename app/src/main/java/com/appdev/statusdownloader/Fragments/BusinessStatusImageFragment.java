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
import android.widget.TextView;
import android.widget.Toast;

import com.appdev.statusdownloader.Adapter.BusinessImageStatusAdapter;
import com.appdev.statusdownloader.Adapter.ImageStatusAdapter;
import com.appdev.statusdownloader.Common.Common;
import com.appdev.statusdownloader.FileObserver.RVEmptyObserver;
import com.appdev.statusdownloader.R;

import java.io.File;
import java.util.ArrayList;


public class BusinessStatusImageFragment extends Fragment {

    View myBusinessStatusFragment;
    RecyclerView recycler_business_status_image;
    //TextView emptyView;
    SwipeRefreshLayout businessStatusSwipeRefresh;

    public BusinessStatusImageFragment() {
        // Required empty public constructor
    }

    public static BusinessStatusImageFragment newInstance(){
        BusinessStatusImageFragment IMAGE_INSTANCE = new BusinessStatusImageFragment();
        return IMAGE_INSTANCE;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myBusinessStatusFragment =  inflater.inflate(R.layout.fragment_business_status_image, container, false);

        // Setup SwipeRefreshLayout
        businessStatusSwipeRefresh = (SwipeRefreshLayout) myBusinessStatusFragment.findViewById(R.id.businessStatusSwipeRefresh);
        businessStatusSwipeRefresh.setColorSchemeResources(R.color.colorAccent);


        // Setup RecyclerView with LayoutManager
        recycler_business_status_image = (RecyclerView) myBusinessStatusFragment.findViewById(R.id.recycler_business_status_image);
        recycler_business_status_image.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_business_status_image.setHasFixedSize(true);


        // RecyclerView Item Decorator
        //recycler_image_status.addItemDecoration(new SpacesItemDecorationGrid(4, 4));


        // Set Animation to RecyclerView
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_business_status_image.getContext(), R.anim.layout_fall_down);
        recycler_business_status_image.setLayoutAnimation(controller);


        // Get WhatsApp Status Files from Device
        File file = new File(Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_BUSINESS_DIR_LOCATION);


        // Set Adapter on RecyclerView
        BusinessImageStatusAdapter businessStatusAdapter = new BusinessImageStatusAdapter(this.getListFiles(file), getActivity());//remember to change the status adapter here
        businessStatusAdapter.notifyDataSetChanged();
        recycler_business_status_image.setClickable(true);
        recycler_business_status_image.setAdapter(businessStatusAdapter);

        //Check for empty view
        /*emptyView = (TextView) myBusinessStatusFragment.findViewById(R.id.emptyView);
        RVEmptyObserver observer = new RVEmptyObserver(recycler_business_status_image, emptyView);
        businessStatusAdapter.registerAdapterDataObserver(observer);*/

        businessStatusSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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

        return myBusinessStatusFragment;
    }

    private void ReloadImageFile() {

        File file = new File(Environment.getExternalStorageDirectory().toString() + Common.WHATSAPP_BUSINESS_DIR_LOCATION);
        BusinessImageStatusAdapter adapter = new BusinessImageStatusAdapter(this.getListFiles(file), getActivity());//remember to change the status adapter here
        recycler_business_status_image.setAdapter(adapter);
        businessStatusSwipeRefresh.setRefreshing(false);

        //Check for empty view
        /*emptyView = (TextView) myBusinessStatusFragment.findViewById(R.id.emptyView);
        RVEmptyObserver observer = new RVEmptyObserver(recycler_business_status_image, emptyView);
        adapter.registerAdapterDataObserver(observer);*/


        recycler_business_status_image.getAdapter().notifyDataSetChanged();
        recycler_business_status_image.scheduleLayoutAnimation();

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
                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png"))
                {
                    if (!inFiles.contains(file))
                        inFiles.add(file);

                }
            }
        }

        return inFiles;

    }

}
