package com.ncsavault.alabamavault.fragments.views;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.SavedNotificationScreenAdapter;


/**
 * Created by gauravkumar.singh on 6/12/2017.
 */

public class NotificationFragment extends Fragment {

    private static Context mContext;
    RecyclerView mRecyclerView;
    OnFragmentTouched listener;
    public static Fragment newInstance(Context context, int centerX, int centerY) {

        mContext = context;
        Bundle args = new Bundle();
        args.putInt("cx", centerX);
        args.putInt("cy", centerY);
        Fragment frag = new NotificationFragment();
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.saved_push_notification_layout, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            int randomColor =
//                    Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
//            rootView.setBackgroundColor((randomColor));

            // To run the animation as soon as the view is layout in the view hierarchy we add this
            // listener and remove it
            // as soon as it runs to prevent multiple animations if the view changes bounds
            rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                           int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    int cx = getArguments().getInt("cx");
                    int cy = getArguments().getInt("cy");

                    // get the hypothenuse so the radius is from one corner to the other
                    int radius = (int) Math.hypot(right, bottom);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                        reveal.setInterpolator(new DecelerateInterpolator(2f));
                        reveal.setDuration(1000);
                        reveal.start();
                    }
                }
            });

            // attach a touch listener
            rootView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (listener != null) {
                        listener.onFragmentTouched(NotificationFragment.this, event.getX(), event.getY());
                    }
                    return true;
                }
            });
        }

        return rootView;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.saved_notification_recycler_view);

        SavedNotificationScreenAdapter savedNotificationScreenAdapter = new SavedNotificationScreenAdapter(mContext);

        mRecyclerView.setHasFixedSize(true);
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if(screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL)
        {

            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(3), true));
            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(llm);

        }else
        {
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(savedNotificationScreenAdapter);
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentTouched) {
            listener = (OnFragmentTouched) activity;
        }
    }



}
