package com.appdev.statusdownloader.Adapter;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appdev.statusdownloader.Fragments.BusinessStatusImageFragment;
import com.appdev.statusdownloader.Fragments.BusinessStatusVideoFragment;
import com.appdev.statusdownloader.Fragments.BusinessVideo;
import com.appdev.statusdownloader.Fragments.StatusImageFragment;
import com.appdev.statusdownloader.Fragments.VideoStatusFragment;

public class BusinessStatusFragmentAdapter extends FragmentPagerAdapter {

    private Context context;

    public BusinessStatusFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return BusinessStatusImageFragment.newInstance();
        else if (position == 1)
            return BusinessStatusVideoFragment.newInstance();
        else
            return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {

            case 0:
                return "Images";
            case 1:
                return "Videos";

        }
        return "";
    }

}
