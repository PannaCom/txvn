package com.quickcar.thuexe.Controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.quickcar.thuexe.UI.CarListFragment;
import com.quickcar.thuexe.UI.MapCarActiveFragment;

/**
 * Created by DatNT on 10/7/2016.
 */
public class TabsApdater extends FragmentPagerAdapter {

    public TabsApdater(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new MapCarActiveFragment();
            case 1:
                return new MapCarActiveFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}