package com.melodygram.adapter;

/**
 * Created by FuGenX-01 on 27-09-2016.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;



//Extending FragmentStatePagerAdapter
public class HomeFragmentPagerAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    Fragment[] fragmentArray;
    String[] titleArray;

    int tabCount;

    //Constructor to the class
    public HomeFragmentPagerAdapter(FragmentManager fm, String[] titleArray, Fragment[] fragmentArray) {
        super(fm);
        this.fragmentArray = fragmentArray;
        this.titleArray = titleArray;

    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();

        Fragment f = fragmentArray[position];
        f.setArguments(bundle);
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleArray[position];
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return fragmentArray.length;
    }
}
