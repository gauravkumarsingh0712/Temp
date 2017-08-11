package com.ncsavault.alabamavault.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.ncsavault.alabamavault.fragments.views.VideoListFragment;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.views.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class TabsFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private static final String[] CONTENT = { GlobalConstants.FEATURED,
            GlobalConstants.PLAYERS, GlobalConstants.COACHES_ERA, GlobalConstants.OPPONENTS,
            GlobalConstants.FAVORITES};
    private int mCount = GlobalConstants.tabsList.length;
    private MainActivity context;
    private FragmentManager mFragmentManager;
    private Map<Integer, String> mFragmentTags;

    public TabsFragmentPagerAdapter(FragmentManager fm, Activity context) {
        super(fm);
        this.context = (MainActivity)context;
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, String>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub
        Object obj = super.instantiateItem(container, position);
        if(obj instanceof Fragment){
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    public Fragment getFragment(int position){
        String tag = mFragmentTags.get(position);
        if(tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("tabIdentifier", GlobalConstants.tabsDbIdentifierList[position].toString());
        if(GlobalConstants.tabType[position].toLowerCase().equals("edgetoedge")) {
            bundle.putInt("tabType", 1);
        }else if(GlobalConstants.tabType[position].toLowerCase().equals("wide")) {
            bundle.putInt("tabType", 2);
        }
        VideoListFragment listFragment = new VideoListFragment();
        listFragment.setArguments(bundle);
        return listFragment;

    }


    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return GlobalConstants.tabsList[position].toUpperCase();
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }

}