package com.chalmers.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Chalmers on 2016-05-08 21:58.
 * email:qxinhai@yeah.net
 */
public class MainAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = null;

    public MainAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
