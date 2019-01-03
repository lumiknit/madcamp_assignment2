package io.madcamp.jh.madcamp_assignment2;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    final int PAGE_COUNT = 3;
    private Context context;

    public TabPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return Tab1Fragment.newInstance(0);
            case 1: return Tab2Fragment.newInstance(1);
            case 2: return Tab3Fragment.newInstance(2);
            default: return null;
        }
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


