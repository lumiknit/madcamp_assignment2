package io.madcamp.jh.madcamp_assignment2;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    final int PAGE_COUNT = 3;
    private Context context;
    private Fragment[] fragments;

    public class SharedData {
        public ArrayList<Image> imageList;
        public int clickedInfoWindow;
    }

    public SharedData shared;

    public TabPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

        shared = new SharedData();
        shared.imageList = new ArrayList<>();
        shared.clickedInfoWindow = -1;

        fragments = new Fragment[3];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("Test@TabPager.getItem", "" + position);
        if(fragments[position] == null) {
            switch(position) {
                case 0:
                    fragments[position] = Tab1Fragment.newInstance(0);
                    break;
                case 1:
                    fragments[position] = Tab2Fragment.newInstance(1, shared);
                    break;
                case 2:
                    fragments[position] = Tab3Fragment.newInstance(2, shared);
                    break;
            }
        }
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Resources res = context.getResources();
        switch(position) {
            case 0: return res.getString(R.string.tab1_label);
            case 1: return res.getString(R.string.tab2_label);
            case 2: return res.getString(R.string.tab3_label);
            default: return null;
        }
    }
}


