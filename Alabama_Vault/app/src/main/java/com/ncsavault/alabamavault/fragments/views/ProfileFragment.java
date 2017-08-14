package com.ncsavault.alabamavault.fragments.views;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.UserProfileModel;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.AbstractView;
import com.ncsavault.alabamavault.views.HomeScreen;
import com.ncsavault.alabamavault.views.UserProfileActivity;

import java.io.File;

/**
 * Created by gauravkumar.singh on 6/12/2017.
 */

public class ProfileFragment extends BaseFragment implements AbstractView {

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

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwitchCompat = (SwitchCompat) view.findViewById(R.id.toggle_button);
        mPlayerBackgroundImage = (ImageView) view.findViewById(R.id.profile_image);

    //    if (Utils.isInternetAvailable(mContext))
     //       loadUserDataFromServer();

    }
    ProgressDialog pDialog;
    private UserProfileModel mUserProfileModel;
    public void loadUserDataFromServer() {

        pDialog = new ProgressDialog(mContext, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog((HomeScreen)mContext));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);

        SharedPreferences pref = mContext.getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        final long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
        final String email = pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, "");


        mUserProfileModel = AppController.getInstance().getModelFacade().getRemoteModel().getUserProfileModel();
        mUserProfileModel.registerView(this);
        mUserProfileModel.setProgressDialog(pDialog);
        mUserProfileModel.loadFetchData(email, userId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentTouched) {
            listener = (OnFragmentTouched) activity;
        }
    }

    @Override
    public void update() {
        ((HomeScreen)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mUserProfileModel != null && mUserProfileModel.getState() == BaseModel.STATE_SUCCESS) {
                    try {
                        pDialog.dismiss();
                        if (mUserProfileModel.getUserProfileResult()) {
                            ((HomeScreen )mContext).showToastMessage("Profile updated successfully");
                        } else {
                            ((HomeScreen )mContext).showToastMessage("Error updating information");
                           //gk loadUserDataFromLocal();
                        }
                        final File root = new File(Environment.getExternalStorageDirectory() +
                                File.separator + GlobalConstants.PROFILE_PIC_DIRECTORY + File.separator);
                        if (root != null) {
                            if (root.listFiles() != null) {
                                for (File childFile : root.listFiles()) {
                                    if (childFile != null) {
                                        if (childFile.exists())
                                            childFile.delete();
                                    }
                                }
                                if (root.exists())
                                    root.delete();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (mUserProfileModel != null && mUserProfileModel.getState() == BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {
                 //   loadData();
                }
            }
        });
    }



}
