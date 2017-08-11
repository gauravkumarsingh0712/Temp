package com.ncsavault.alabamavault.fragments.views;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.ncsavault.alabamavault.R;

/**
 * Created by gauravkumar.singh on 6/12/2017.
 */

public class ProfileFragment extends BaseFragment {

    private static Context mContext;
    private SwitchCompat mSwitchCompat;
    OnFragmentTouched listener;
    private ImageView mPlayerBackgroundImage;

    public static Fragment newInstance(Context context, int centerX, int centerY) {
        mContext = context;
        Bundle args = new Bundle();
        args.putInt("cx", centerX);
        args.putInt("cy", centerY);
        Fragment frag = new ProfileFragment();
        frag.setArguments(args);
        return frag;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.user_profile_screen_layout, container, false);
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//////            int randomColor =
//////                    Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
//////            rootView.setBackgroundColor((randomColor));
////
////            // To run the animation as soon as the view is layout in the view hierarchy we add this
////            // listener and remove it
////            // as soon as it runs to prevent multiple animations if the view changes bounds
////            rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
////                @Override
////                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
////                                           int oldRight, int oldBottom) {
////                    v.removeOnLayoutChangeListener(this);
////                    int cx = getArguments().getInt("cx");
////                    int cy = getArguments().getInt("cy");
////
////                    // get the hypothenuse so the radius is from one corner to the other
////                    int radius = (int) Math.hypot(right, bottom);
////
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                        Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
////                        reveal.setInterpolator(new DecelerateInterpolator(2f));
////                        reveal.setDuration(1000);
////                        reveal.start();
////                    }
////                }
////            });
////
////            // attach a touch listener
////            rootView.setOnTouchListener(new View.OnTouchListener() {
////                @Override
////                public boolean onTouch(View v, MotionEvent event) {
////                    if (listener != null) {
////                        listener.onFragmentTouched(ProfileFragment.this, event.getX(), event.getY());
////                    }
////                    return true;
////                }
////            });
//        }

        return rootView;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwitchCompat = (SwitchCompat) view.findViewById(R.id.toggle_button);
        mPlayerBackgroundImage = (ImageView) view.findViewById(R.id.profile_image);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                enterReveal(mPlayerBackgroundImage);
//            }
//        },500);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentTouched) {
            listener = (OnFragmentTouched) activity;
        }
    }

    void enterReveal(ImageView imageView) {
        // previously invisible view
        // get the center for the clipping circle
//        int cx = imageView.getMeasuredWidth() / 2;
//        int cy = imageView.getMeasuredHeight() / 2;
//
//        // get the final radius for the clipping circle
//        int finalRadius = Math.max(imageView.getWidth(), imageView.getHeight()) / 2;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            // create the animator for this view (the start radius is zero)
//            Animator anim =
//                    ViewAnimationUtils.createCircularReveal(imageView, cx, cy, 0, finalRadius);
//
//            // make the view visible and start the animation
//            imageView.setVisibility(View.VISIBLE);
//            anim.start();
//        }

//        int centerX = (imageView.getLeft() + imageView.getRight()) / 2;
//        int centerY = (imageView.getTop() + imageView.getBottom()) / 2;
        // finding X and Y co-ordinates
        int centerX = (imageView.getLeft() + imageView.getRight());
        int centerY = (imageView.getLeft());

        int startRadius = 0;
// get the final radius for the clipping circle
        int endRadius = Math.max(imageView.getWidth(), imageView.getHeight());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
// create the animator for this view (the start radius is zero)
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(imageView, centerX, centerY, startRadius, endRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(1000);

// make the view visible and start the animation
            imageView.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

}
