package com.beastek.eol.adapter;

//import android.support.v4.app.Fragment;//import android.support.v4.app.FragmentManager;
// import android.support.v4.app.FragmentPagerAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.beastek.eol.ui.patient.MainActivityFragment;


public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment frag = MainActivityFragment.newInstance(0);
        switch (position) {
            case 0: // Fragment - Pending
                 frag = MainActivityFragment.newInstance(0);
            case 1: // Fragment - Done
                frag= MainActivityFragment.newInstance(1);
            case 2: // Fragment - All
                frag = MainActivityFragment.newInstance(2);
        } return frag;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: // Fragment - Pending
                return "Pendientes";
            case 1: // Fragment - Done
                return "Realizadas";
            case 2: // Fragment - All
                return "TODAS";
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        MainActivityFragment f = (MainActivityFragment) object;
        if (f != null) {
            f.update();
        }
        return super.getItemPosition(object);
    }

}
