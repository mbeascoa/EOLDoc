package com.beastek.eol.adapter;

/**
 * A [MainFragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.beastek.eol.ui.patient.MainActivityFragment;


public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

/*
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position + 1);
    }
    */
    @NonNull
    @Override
    public Fragment getItem(int position) {

        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment frag = MainActivityFragment.newInstance(0);
        switch (position) {
            case 0: // Fragment - Pending
                 frag = MainActivityFragment.newInstance(0);
                 break;
            case 1: // Fragment - Done
                frag= MainActivityFragment.newInstance(1);
                break;
            case 2: // Fragment - All
                frag = MainActivityFragment.newInstance(2);
                break;
            default:
                frag = MainActivityFragment.newInstance(0);
                break;
        } return frag;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return NUM_ITEMS;
    }



    // Returns the page title for the top indicator
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        /* public CharSequence getPageTitle(int position) {
            return mContext.getResources().getString(TAB_TITLES[position]);
        } */
        switch (position) {
            case 0: // Fragment - Pending
                return "Pending";
            case 1: // Fragment - Done
                return "Done";
            case 2: // Fragment - All
                return "ALL";
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
