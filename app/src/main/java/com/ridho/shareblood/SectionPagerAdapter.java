package com.ridho.shareblood;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Ridho on 3/23/2018.
 */

class SectionPagerAdapter extends FragmentPagerAdapter {
    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Home home = new Home();
                return home;
            case 1:
                Event event = new Event();
                return event;
            case 2:
                Info info = new Info();
                return info;
            case 3:
                Profile profile = new Profile();
                return profile;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Home";
            case 1:
                return "Events";
            case 2:
                return "Info";
            case 3:
                return "Profile";
            default:
                return null;
        }
    }
}
