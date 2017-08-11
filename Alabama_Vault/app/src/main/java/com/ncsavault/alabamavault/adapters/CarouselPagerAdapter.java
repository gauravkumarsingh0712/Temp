package com.ncsavault.alabamavault.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.fragments.views.ItemFragment;
import com.ncsavault.alabamavault.utils.CarouselLinearLayout;
import com.ncsavault.alabamavault.views.HomeScreen;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    public final static float BIG_SCALE = 1.4f;
    public final static float SMALL_SCALE = 0.5f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    private HomeScreen context;
    private FragmentManager fragmentManager;
    private float scale;
    ViewPager pager;


    public CarouselPagerAdapter(HomeScreen context, FragmentManager fm, ViewPager viewPager) {
        super(fm);
        this.pager = viewPager;
        this.fragmentManager = fm;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        try {
            if (position == HomeScreen.FIRST_PAGE)
                scale = BIG_SCALE;
            else
                scale = SMALL_SCALE;

            position = position % HomeScreen.count;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ItemFragment.newInstance(context, position, scale);
    }

    @Override
    public int getCount() {
        int count = 0;
        try {
            count = HomeScreen.count * HomeScreen.LOOPS;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        try {
            if (positionOffset >= 0f && positionOffset <= 1f) {
                CarouselLinearLayout cur = getRootView(position);
                CarouselLinearLayout next = getRootView(position + 1);

                cur.setScaleBoth(BIG_SCALE - DIFF_SCALE * positionOffset);
                next.setScaleBoth(SMALL_SCALE + DIFF_SCALE * positionOffset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @SuppressWarnings("ConstantConditions")
    private CarouselLinearLayout getRootView(int position) {
        return (CarouselLinearLayout) fragmentManager.findFragmentByTag(this.getFragmentTag(position))
                .getView().findViewById(R.id.root_container);
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + pager.getId() + ":" + position;
    }
}