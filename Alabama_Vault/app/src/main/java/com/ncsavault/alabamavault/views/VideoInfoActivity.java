package com.ncsavault.alabamavault.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.transition.Transition;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.PagerAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.fragments.views.VideoInfoPagerFragment;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.jwplayer.KeepScreenOnHandler;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.ncsavault.alabamavault.service.VideoDataService;
import com.ncsavault.alabamavault.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.configuration.PlayerConfig;
import com.longtailvideo.jwplayer.events.listeners.AdvertisingEvents;
import com.longtailvideo.jwplayer.events.listeners.VideoPlayerEvents;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;


/**
 * Created by Gauravkumar.singh on 09-09-2015.
 */
public class VideoInfoActivity extends PermissionActivity implements VideoPlayerEvents.OnFullscreenListener {

    //Declare fields required
    private VideoDTO videoObject;
    private Map<String, String> articleParams;
    private Handler mVideoControlHandler = new Handler();
    //gk private CustomMediaController mController;
    private String videoCategory;
    private boolean isFavoriteChecked;
    private AsyncTask<Void, Void, Void> mPostTask;
    private String postResult;
    private Activity context;
    int displayHeight = 0, displayWidth = 0;

    //Declare UI elements
    LinearLayout ll_header;
    private RelativeLayout rlVideoNameStrip, rlActionStrip, rlParentLayout;
    private FrameLayout rlVideoLayout;
    //gk private CustomVideoView videoView;
    private JWPlayerView videoView;
    private TextView tvVideoName;
    private ImageView imgToggleButton;
    private ViewPager viewPager;
    private CirclePageIndicator circleIndicator;
    private ImageView imgVideoClose, imgVideoShare, imgVideoStillUrl;
    public static LinearLayout llVideoLoader, bufferLinearLayout;

    //UI Elements and fields for Social Sharing
    private static CallbackManager callbackManager;
    private static ShareDialog shareDialog;
    AlertDialog alertDialog = null;
    private static boolean canPresentShareDialog;
    TwitterLoginButton twitterLoginButton;
    public ProgressDialog progressDialog;
    ProfileTracker profileTracker;
    private Animation animation;
    private Vector<Fragment> fragments;
    private PagerAdapter mPagerAdapter;
    private RelativeLayout viewPagerRelativeView;
    private LinearLayout shareVideoLayout;
    private boolean askAgainForMustPermissions = false;
    private boolean goToSettingsScreen = false;
    private MusicIntentReceiver myReceiver;
    private boolean isBackToSplashScreen = false;
    long videoCurrentPostion = 0;
    boolean isFirstTimeEntry = false;
    private AdView mAdView;
    private ProgressDialog pDialog;
    private View view;
    private static Uri imageUri;
    public static View linearLayout;
    private ImageView facebookShareView;
    private ImageView twitterShareView;
    private ImageView flatButtonFacebook;
    private ImageView flatButtonTwitter;
    private String longDescription;
    private String videoName;
    private FirebaseAnalytics mFirebaseAnalytics;
    Bundle params = new Bundle();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.video_info_layout);
        context = VideoInfoActivity.this;

        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config;
        config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(3) // default
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .build();
        ImageLoader.getInstance().init(config);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        getIntentData();
        try {
            initlizeAllVideoInfoActivityData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void adMobBannerAdvertising() {
        int navBarHeight = Utils.getNavBarStatusAndHeight(this);

        mAdView = (AdView) findViewById(R.id.adView);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        if (Utils.hasNavBar(this)) {
            layoutParams.bottomMargin = navBarHeight;
            //  mAdView.setLayoutParams(layoutParams);
//        mAdView.setPadding(0,0,0,navBarHeight);
        }
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
    }

    private void initlizeAllVideoInfoActivityData() {
        try {
            initializeFacebookUtil();
            initViews();
            setDimensions();
            adMobBannerAdvertising();
            //The reason to put this thread, to make screen aware of what orientation it is using

            initData();
            initListener();

            Thread thread = new Thread();
            thread.sleep(500);

            if (getScreenOrientation() == 1) {
                performAnimations();
            } else {
                moveToFullscreen();
            }

            AppController.getInstance().getModelFacade().getLocalModel().setUriUrl(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(myReceiver != null)
//        {
//            unregisterReceiver(myReceiver);
//        }

    }


    @Override
    public void onPermissionResult(int requestCode, boolean isGranted, Object extras) {

        try {
            switch (requestCode) {
                case PERMISSION_REQUEST_MUST:
                    if (isGranted) {
                        //perform action here
                        //gk initlizeAllVideoInfoActivityData();
                        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                            linearLayout.setVisibility(View.GONE);
                        }
                        if (videoObject != null && imageUri == null) {
                            new ShareTwitter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                       //gk twitterSharingData();
                    } else {
                        if (!askAgainForMustPermissions) {
                            askAgainForMustPermissions = true;
                            haveAllMustPermissions(writeExternalStorage, PERMISSION_REQUEST_MUST);
                        } else if (!goToSettingsScreen) {
                            goToSettingsScreen = true;

                            showPermissionsConfirmationDialog(GlobalConstants.VAULT_WRITE_PERMISSION);

                        } else {
                            showPermissionsConfirmationDialog(GlobalConstants.VAULT_WRITE_PERMISSION);
                        }
                        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                            linearLayout.setVisibility(View.GONE);
                        }
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (isBackToSplashScreen) {
                isBackToSplashScreen = false;
                if (haveAllMustPermissions()) {
                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        linearLayout.setVisibility(View.GONE);
                    }
//GK                    if (videoObject != null && imageUri == null) {
//                        new ShareTwitter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//GK                    }
                 //gk   twitterSharingData();
                    //gk initlizeAllVideoInfoActivityData();
                }
            }
        }
    }

    public void showPermissionsConfirmationDialog(String message) {
        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.GONE);
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Permission Denied");
        alertDialogBuilder
                .setMessage(message);


        alertDialogBuilder.setPositiveButton("Go to Settings",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Utils.getInstance().registerWithGCM(mActivity);
                        goToSettings();

                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        //gk showPermissionsConfirmationDialog(GlobalConstants.UGA_VAULT_RWAD_PHONE_STATE_PERMISSION);
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(getResources().getColor(R.color.green));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(getResources().getColor(R.color.green));
    }


    public void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, 400);
    }

    public int getScreenOrientation() {
        Display getOrient = getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        Point outSize = new Point();
        getOrient.getSize(outSize);

        if (outSize.x == outSize.y) {
            orientation = Configuration.ORIENTATION_UNDEFINED;
        } else {
            if (outSize.x < outSize.y) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (videoView != null) {
                videoView.onPause();
            }
            if (myReceiver != null) {
                //gk    unregisterReceiver(myReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (videoView != null) {
                videoView.onResume();
            }

            boolean installedFbApp = checkIfAppInstalled("com.facebook.katana");
            boolean installedTwitterApp = checkIfAppInstalled("com.twitter.android");

            if (facebookShareView != null && facebookShareView.getVisibility() == View.VISIBLE && installedFbApp) {
                facebookShareView.setVisibility(View.GONE);
                if (flatButtonFacebook != null && flatButtonFacebook.getVisibility() == View.GONE) {
                    flatButtonFacebook.setVisibility(View.VISIBLE);
                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        makeShareDialog();
                    }

                }
            }
            if (facebookShareView != null && facebookShareView.getVisibility() == View.VISIBLE && !installedFbApp) {
                if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                    makeShareDialog();
                }

            }
            if (twitterShareView != null && twitterShareView.getVisibility() == View.VISIBLE && !installedTwitterApp) {
                if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                    makeShareDialog();
                }
            }

            if (twitterShareView != null && twitterShareView.getVisibility() == View.VISIBLE && installedTwitterApp) {
                twitterShareView.setVisibility(View.GONE);
                if (flatButtonTwitter != null && flatButtonTwitter.getVisibility() == View.GONE) {
                    flatButtonTwitter.setVisibility(View.VISIBLE);
                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        makeShareDialog();
                    }
                }
            }

            if (flatButtonFacebook != null && flatButtonFacebook.getVisibility() == View.VISIBLE && !installedFbApp) {
                flatButtonFacebook.setVisibility(View.GONE);
                if (facebookShareView != null && facebookShareView.getVisibility() == View.GONE) {
                    facebookShareView.setVisibility(View.VISIBLE);
                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        makeShareDialog();
                    }
                }
            }

            if (flatButtonTwitter != null && flatButtonTwitter.getVisibility() == View.VISIBLE && !installedTwitterApp) {
                flatButtonTwitter.setVisibility(View.GONE);
                if (twitterShareView != null && twitterShareView.getVisibility() == View.GONE) {
                    twitterShareView.setVisibility(View.VISIBLE);
                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        linearLayout.setVisibility(View.GONE);
                        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                            makeShareDialog();
                        }
                    }
                }
            }

            if (flatButtonFacebook != null && flatButtonFacebook.getVisibility() == View.VISIBLE && installedFbApp) {
                if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                    makeShareDialog();
                }
            }
            if (flatButtonTwitter != null && flatButtonTwitter.getVisibility() == View.VISIBLE && installedTwitterApp) {
                if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                    makeShareDialog();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (MainActivity.mIndicator != null && MainActivity.mPager != null) {
            MainActivity.mIndicator.setCurrentItem(GlobalConstants.CURRENT_TAB);
            MainActivity.mPager.setCurrentItem(GlobalConstants.CURRENT_TAB);
        }
        if (imageUri != null && path != null) {
            deleteFileFromSDcard();
        }

        params.putString(FirebaseAnalytics.Param.ITEM_ID,  videoObject.getVideoName());
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, videoObject.getVideoName());
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video_info");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
        //gk mVideoControlHandler.removeCallbacks(videoRunning);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoCategory != null) {
            // -----stopping the flurry event of video-----------
            try {
                FlurryAgent.endTimedEvent(videoCategory);

//gk                if (videoView != null && mVideoControlHandler != null && videoRunning != null) {
//                    mVideoControlHandler.removeCallbacks(videoRunning);
//                }
//                unregisterReceiver(myReceiver);
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (twitterLoginButton != null) {
            twitterLoginButton.onActivityResult(requestCode, resultCode,
                    data);
        }

        if (requestCode == 400) {
            isBackToSplashScreen = true;
        }

        if (requestCode == 100) {
            // Toast.makeText(VideoInfoActivity.this, "Request Code : ", Toast.LENGTH_SHORT).show();
            boolean installedFbApp = checkIfAppInstalled("com.facebook.katana");
            boolean installedTwitterApp = checkIfAppInstalled("com.twitter.android");

            if (facebookShareView != null && facebookShareView.getVisibility() == View.VISIBLE && installedFbApp) {
                facebookShareView.setVisibility(View.GONE);
            }
            if (twitterShareView != null && twitterShareView.getVisibility() == View.VISIBLE && installedTwitterApp) {
                twitterShareView.setVisibility(View.GONE);
            }

            if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                linearLayout.setVisibility(View.GONE);
                makeShareDialog();
            }
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getScreenDimensions();
//        if (videoView != null) {
//            videoView.setFullscreen(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE, true);
//        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            // rlActionStrip.setVisibility(View.GONE);

            if (ll_header != null && rlVideoNameStrip != null ) {
                ll_header.setVisibility(View.GONE);
                rlVideoNameStrip.setVisibility(View.GONE);
           }
            if (mAdView != null) {
                mAdView.setVisibility(View.GONE);
            }
            if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                linearLayout.setVisibility(View.GONE);
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


            if (imgVideoStillUrl != null && imgVideoStillUrl.isShown()) {
                Animation anim = AnimationUtils.loadAnimation(VideoInfoActivity.this, R.anim.fadein);
                imgVideoStillUrl.setAnimation(anim);
                imgVideoStillUrl.setVisibility(View.GONE);
            }


            if (llVideoLoader != null && llVideoLoader.isShown()
                        /*&& videoView.getCurrentPosition() > 500*/) {
                llVideoLoader.setVisibility(View.GONE);
            }

            System.out.println("displayWidth land : " + displayWidth + " " + displayHeight);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(displayWidth, displayHeight);
            if (rlVideoLayout != null) {
                rlVideoLayout.setLayoutParams(lp);
            }


            //gk videoView.setDimensions(displayWidth, displayHeight);
            //gk videoView.getHolder().setFixedSize(displayWidth, displayHeight);
        } else {

            // rlActionStrip.setVisibility(View.VISIBLE);
            ll_header.setVisibility(View.VISIBLE);
            rlVideoNameStrip.setVisibility(View.VISIBLE);
            if (mAdView != null) {
                mAdView.setVisibility(View.VISIBLE);
            }

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(displayWidth, (int) (displayHeight * 0.35));
            lp.addRule(RelativeLayout.BELOW, R.id.view_line);
            if (rlVideoLayout != null) {
                rlVideoLayout.setLayoutParams(lp);
            }


            if (imgVideoStillUrl.isShown()) {
                Animation anim = AnimationUtils.loadAnimation(VideoInfoActivity.this, R.anim.fadein);
                imgVideoStillUrl.setAnimation(anim);
                imgVideoStillUrl.setVisibility(View.GONE);
            }


            if (llVideoLoader.isShown()
                        /*&& videoView.getCurrentPosition() > 500*/) {
                llVideoLoader.setVisibility(View.GONE);
            }

            //gk videoView.setDimensions(displayWidth, (int) (displayHeight * 0.35));
            //gk videoView.getHolder().setFixedSize(displayWidth, (int) (displayHeight * 0.35));
        }
    }


    public void moveToFullscreen() {
        getScreenDimensions();

        // rlActionStrip.setVisibility(View.GONE);
        if (ll_header != null && rlVideoNameStrip != null) {
            ll_header.setVisibility(View.GONE);
            rlVideoNameStrip.setVisibility(View.GONE);

        }
        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.GONE);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(displayWidth, displayHeight);
        if (rlVideoLayout != null) {
            rlVideoLayout.setLayoutParams(lp1);
        }

        if (mAdView != null) {
            mAdView.setVisibility(View.GONE);
        }

        //gk videoView.setDimensions(displayWidth, displayHeight);
        //gk videoView.getHolder().setFixedSize(displayWidth, displayHeight);
    }


    public void performAnimations() {

        /*animation = AnimationUtils.loadAnimation(this, R.anim.slideup);
        rlParentLayout.setAnimation(animation);

        rlParentLayout.setVisibility(View.VISIBLE);*/

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(VideoInfoActivity.this, R.anim.slidedown_header);
                if (rlVideoNameStrip != null && animation != null) {
                    rlVideoNameStrip.setAnimation(animation);
                    rlVideoNameStrip.setVisibility(View.VISIBLE);
                }
            }
        }, 300);

        /*imgVideoClose.setVisibility(View.VISIBLE);
        imgVideoShare.setVisibility(View.VISIBLE);*/
    }

    void initViews() {

        ll_header = (LinearLayout) findViewById(R.id.ll_header);
        rlVideoNameStrip = (RelativeLayout) findViewById(R.id.rl_header);
        // rlActionStrip = (RelativeLayout) findViewById(R.id.rl_header);
        rlParentLayout = (RelativeLayout) findViewById(R.id.rl_parent_layout);
        rlVideoLayout = (FrameLayout) findViewById(R.id.rl_video_layout);
        //gk videoView = (CustomVideoView) findViewById(R.id.video_view);
        videoView = (JWPlayerView) findViewById(R.id.jwplayer);
        tvVideoName = (TextView) findViewById(R.id.tv_video_name);
        imgToggleButton = (ImageView) findViewById(R.id.imgToggleButton);
//        viewPager = (ViewPager) findViewById(R.id.pager);
//        viewPagerRelativeView = (RelativeLayout) findViewById(R.id.relative_view_pager);
//        circleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
//        circleIndicator.setPageColor(getResources().getColor(R.color.app_dark_grey));
//        circleIndicator.setStrokeColor(Color.parseColor("#999999"));
//        circleIndicator.setFillColor(Color.parseColor("#999999"));
        llVideoLoader = (LinearLayout) findViewById(R.id.ll_video_loader);
        bufferLinearLayout = (LinearLayout) findViewById(R.id.buffer_layout);
        bufferLinearLayout.setVisibility(View.GONE);
//gk        bufferProgressBar = (ProgressBar) findViewById(R.id.progressbar);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            bufferProgressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.circle_progress_bar_lower));
//        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
//            bufferProgressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progress_large_material, null));
//        }

        shareVideoLayout = (LinearLayout) findViewById(R.id.share_video_layout);
        imgVideoClose = (ImageView) findViewById(R.id.img_video_close);
        imgVideoShare = (ImageView) findViewById(R.id.img_video_share);
        imgVideoStillUrl = (ImageView) findViewById(R.id.image_video_still);

    }


    void initData() {

        myReceiver = new MusicIntentReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);

        if (videoObject != null) {
            if (VaultDatabaseHelper.getInstance(VideoInfoActivity.this).isFavorite(videoObject.getVideoId()))
                imgToggleButton.setBackgroundResource(R.drawable.saved_video_img);
            else
                imgToggleButton.setBackgroundResource(R.drawable.video_save);
            if (imgVideoStillUrl != null)
                Utils.addImageByCaching(imgVideoStillUrl, videoObject.getVideoStillUrl());
            tvVideoName.setText(videoObject.getVideoName().toString());
        }

        llVideoLoader.addView(Utils.getInstance().setViewToProgressDialog(this));

        // -------- starting the flurry event of video------
        articleParams = new HashMap<String, String>();
        articleParams.put(GlobalConstants.KEY_VIDEONAME, videoObject.getVideoName());
        FlurryAgent.logEvent(videoCategory, articleParams, true);

        //Set Video to videoview
        if (Utils.isInternetAvailable(this)) {
            String encodedVideoUrl = videoObject.getVideoLongUrl();
            // http://testingmobile.streaming.mediaservices.windows.net/1093cec3-b555-4184-bd8c-4242fa1e3bee/394.ism/Manifest(format=m3u8-aapl)
//            http://testingmobile.streaming.mediaservices.windows.net/1093cec3-b555-4184-bd8c-4242fa1e3bee/394.ism/Manifest(format=m3u8-aapl-v3)
            // https://www.youtube.com/watch?v=EY0vwK7a2yg+"format=m3u8-aapl-v3";
            llVideoLoader.setVisibility(View.VISIBLE);
            encodedVideoUrl = encodedVideoUrl.replace("(format=m3u8-aapl)", "(format=m3u8-aapl-v3)");

            System.out.println("Media Url : " + encodedVideoUrl);
            Uri videoUri = Uri.parse(encodedVideoUrl);
            //gk mController = new CustomMediaController(this);
            //////////////////////////////////////////////////////////////////////////////
            videoView.setKeepScreenOn(true);
            videoView.addOnFullscreenListener(this);

            // Keep the screen on during playback
            new KeepScreenOnHandler(videoView, getWindow());

            //videoView.setSkin("http://0b78b111a9d0410784caa8a634aa3b90.cloudapp.net/JWPlayerCss/UGA/UgaPlayer.css");
            videoView.setSkin(GlobalConstants.JW_PLAYER_CSS_FILE_URL);

            PlayerConfig playerConfig = new PlayerConfig.Builder()
                    .autostart(false)
                    .captionsEdgeStyle("ec_seek")
                    .stretching(PlayerConfig.STRETCHING_FILL) //"exactfit"
                    .build();

            videoView.setup(playerConfig);

            // Load a media source
            PlaylistItem pi = new PlaylistItem.Builder()
                    .file(videoObject.getVideoLongUrl())
                    .image(videoObject.getVideoStillUrl())
                    .build();

            videoView.load(pi);



            //gkimgVideoStillUrl.setVisibility(View.GONE);
            //gk llVideoLoader.setVisibility(View.GONE);
            //////////////////////////////////////////////////////////////////////////////

            System.out.println("Video Length : " + videoView.getDuration());
        } else {
            Utils.showNoConnectionMessage(this);
            finish();
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (imgVideoStillUrl.isShown()) {
                    Animation anim = AnimationUtils.loadAnimation(VideoInfoActivity.this, R.anim.fadein);
                    imgVideoStillUrl.setAnimation(anim);
                    imgVideoStillUrl.setVisibility(View.GONE);
                }


                if (llVideoLoader.isShown()
                        /*&& videoView.getCurrentPosition() > 500*/) {
                    llVideoLoader.setVisibility(View.GONE);
                }

            }
        }, 2000);

    }


    void initListener() {


        videoView.addOnErrorListener(new VideoPlayerEvents.OnErrorListener() {
            @Override
            public void onError(String s) {
                if (videoView != null) {
                    videoView.stop();
                }
                if (Utils.isInternetAvailable(VideoInfoActivity.this)) {
                    //gk  showToastMessageForBanner(GlobalConstants.MSG_NO_INFO_AVAILABLE);

                } else {

                    showToastMessageForBanner(GlobalConstants.MSG_NO_CONNECTION);

                }
                if (videoView != null) {
                    videoCurrentPostion = videoView.getPosition();
                }
                isFirstTimeEntry = true;
            }
        });


        videoView.addOnSetupErrorListener(new VideoPlayerEvents.OnSetupErrorListener() {
            @Override
            public void onSetupError(String s) {
                System.out.println("here come then 123456 : " + s);

                if (videoView != null) {
                    videoView.stop();

                    videoView.clearFocus();
                    videoView.setSkin(GlobalConstants.JW_PLAYER_CSS_FILE_URL);
                    PlayerConfig playerConfig = new PlayerConfig.Builder()
                            .autostart(false)
                            .skinName("glow")
                            .build();

                    videoView.setup(playerConfig);
                    // Load a media source
                    PlaylistItem pi = new PlaylistItem.Builder()
                            .file(videoObject.getVideoLongUrl())
                            .image(videoObject.getVideoStillUrl())
                            .build();

                    videoView.load(pi);
                }
                if (Utils.isInternetAvailable(VideoInfoActivity.this)) {
                    //gk showToastMessageForBanner(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                } else {
                    showToastMessageForBanner(GlobalConstants.MSG_NO_CONNECTION);
                }
//                videoCurrentPostion = videoView.getPosition();
//                isFirstTimeEntry = true;

            }


        });


        videoView.addOnBeforePlayListener(new AdvertisingEvents.OnBeforePlayListener() {
            @Override
            public void onBeforePlay() {

                if (isFirstTimeEntry) {

                    System.out.println("videoCurrentPostion");
                    if (videoView != null) {
                        videoView.seek(videoCurrentPostion);
                    }
                    isFirstTimeEntry = false;
                }


            }
        });

        videoView.addOnCompleteListener(new VideoPlayerEvents.OnCompleteListener() {
            @Override
            public void onComplete() {
                //gk imgVideoStillUrl.setVisibility(View.VISIBLE);
                //gk  mVideoControlHandler.removeCallbacks(videoRunning);

                //  mp.reset();
                //gk videoView.stop();
                //gk videoView.clearFocus();
                // Keep the screen on during playback
                new KeepScreenOnHandler(videoView, getWindow());

                // videoView.setSkin("http://0b78b111a9d0410784caa8a634aa3b90.cloudapp.net/JWPlayerCss/Auburn/AuburnPlayer.css");
                videoView.setSkin(GlobalConstants.JW_PLAYER_CSS_FILE_URL);
                // Load a media source
                String encodedVideoUrl = videoObject.getVideoLongUrl();
                encodedVideoUrl = encodedVideoUrl.replace("(format=m3u8-aapl)", "(format=m3u8-aapl-v3)");
                PlaylistItem pi = new PlaylistItem.Builder()
                        .file(encodedVideoUrl)
                        .image(videoObject.getVideoStillUrl())
                        .build();

                videoView.load(pi);

                //videoView.requestFocus();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //llVideoLoader.setVisibility(View.VISIBLE);
                    }
                }, 500);
            }

        });


        imgVideoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (!VideoInfoActivity.llVideoLoader.isShown()) {
                        if (MainActivity.mIndicator != null && MainActivity.mPager != null) {
                            MainActivity.mIndicator.setCurrentItem(GlobalConstants.CURRENT_TAB);
                            MainActivity.mPager.setCurrentItem(GlobalConstants.CURRENT_TAB);
                        }

                        if (imageUri != null) {
                            deleteFileFromSDcard();
                        }

                        if (videoView != null) {
                            videoView.pause();
                            videoView.stop();

                            videoView = null;
                        }

                        params.putString(FirebaseAnalytics.Param.ITEM_ID, videoObject.getVideoName());
                        params.putString(FirebaseAnalytics.Param.ITEM_NAME, videoObject.getVideoName());
                        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video_info");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
                        finish();
//gk                        if (mVideoControlHandler != null && videoRunning != null) {
//                            mVideoControlHandler.removeCallbacks(videoRunning);
//
//
//                            // showConfirmCloseButton("Do you want stop video?");
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

        shareVideoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(VideoInfoActivity.this, "Share Button Clicked", Toast.LENGTH_LONG).show();

//                if (!installedFbApp && !installedTwitterApp) {
//
//                    if (videoView != null) {
//                        {
//                            videoView.pause();
//                        }
//                    }
//                    String PlayStoreUrl = "https://play.google.com/store?hl=en";
//                    showConfirmSharingDialog("Please installed facebook App and Twitter App for sharing with your friends.", PlayStoreUrl);
//                } else {
                //  new ShareTwitter().execute();
                makeShareDialog();
                //    }


            }
        });

        imgToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // firebase analytics favoride video
                params.putString(FirebaseAnalytics.Param.ITEM_ID, videoObject.getVideoName());
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, videoObject.getVideoName());
                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video_favorite");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

                if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                    linearLayout.setVisibility(View.GONE);
                }
                if (Utils.isInternetAvailable(context)) {
                    if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() == GlobalConstants.DEFAULT_USER_ID) {
                        imgToggleButton.setBackgroundResource(R.drawable.video_save);
                        showConfirmLoginDialog(GlobalConstants.LOGIN_MESSAGE);
                    } else {
                        if (VaultDatabaseHelper.getInstance(VideoInfoActivity.this).isFavorite(videoObject.getVideoId())) {
                            isFavoriteChecked = false;
                            VaultDatabaseHelper.getInstance(context.getApplicationContext()).setFavoriteFlag(0, videoObject.getVideoId());
                            videoObject.setVideoIsFavorite(false);
                            imgToggleButton.setBackgroundResource(R.drawable.video_save);
                        } else {
                            isFavoriteChecked = true;
                            VaultDatabaseHelper.getInstance(context.getApplicationContext()).setFavoriteFlag(1, videoObject.getVideoId());
                            videoObject.setVideoIsFavorite(true);
                            imgToggleButton.setBackgroundResource(R.drawable.saved_video_img);
                        }

//                        mPostTask = new AsyncTask<Void, Void, Void>() {
//                            @Override
//                            protected void onPreExecute() {
//                                super.onPreExecute();
//                            }
//
//                            @Override
//                            protected Void doInBackground(Void... params) {
//                                try {
//                                    postResult = AppController.getInstance().getServiceManager()
//                                            .getVaultService().postFavoriteStatus(AppController.getInstance()
//                                                    .getModelFacade().getLocalModel().
//                                            getUserId(), videoObject.getVideoId(), videoObject.getPlaylistId(), isFavoriteChecked);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                return null;
//                            }
//
//                            @Override
//                            protected void onPostExecute(Void result) {
//                                System.out.println("Result of POST request : " + postResult);
//                                if (isFavoriteChecked)
//                                    VaultDatabaseHelper.getInstance(context.getApplicationContext()).setFavoriteFlag(1, videoObject.getVideoId());
//                                else
//                                    VaultDatabaseHelper.getInstance(context.getApplicationContext()).setFavoriteFlag(0, videoObject.getVideoId());
//                            }
//                        };
//
////                        mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                        mPostTask.execute();
                    }
                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                    imgToggleButton.setBackgroundResource(R.drawable.video_save);
                }
            }
        });

//        viewPagerRelativeView.setOnClickListener(new View.OnClickListener()
//
//                                                 {
//                                                     @Override
//                                                     public void onClick(View v) {
//                                                         if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
//                                                             linearLayout.setVisibility(View.GONE);
//                                                             // exitReveal1(linearLayout);
//                                                         }
//                                                     }
//                                                 }
//
//        );
    }


    public void showToastMessage(String message) {
        View includedLayout = findViewById(R.id.llToast);
        //includedLayout.setPadding(0,20,0,0);

        final TextView text = (TextView) includedLayout.findViewById(R.id.tv_toast_message);
        text.setText(message);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.abc_fade_in);

        text.setAnimation(animation);
        text.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(VideoInfoActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    public void showToastMessageForBanner(String message) {
        View includedLayout = findViewById(R.id.llToast);
        // Handler handler = new Handler();
        final TextView text = (TextView) includedLayout.findViewById(R.id.tv_toast_message);
        text.setText(message);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            animation = AnimationUtils.loadAnimation(this,
                    R.anim.abc_fade_in);

            text.setAnimation(animation);
            text.setVisibility(View.VISIBLE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    text.setVisibility(View.VISIBLE);
                }
            }, 50);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(VideoInfoActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    public void getScreenDimensions() {

        Point size = new Point();
        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            displayHeight = size.y;
            displayWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            displayHeight = d.getHeight();
            displayWidth = d.getWidth();
        }
    }

    public void setDimensions() {
        Point size = new Point();
        WindowManager w = getWindowManager();
        int measuredHeight = 0;
        int measuredWidth = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            measuredHeight = size.y;
            measuredWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            measuredHeight = d.getHeight();
            measuredWidth = d.getWidth();
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (measuredHeight * 0.35));
        lp.addRule(RelativeLayout.BELOW, R.id.view_line);
        if (rlVideoLayout != null) {
            rlVideoLayout.setLayoutParams(lp);
        }

        //gk videoView.setDimensions(measuredWidth, (int) (measuredHeight * 0.35));
    }

    public void getIntentData() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                videoObject = (VideoDTO) intent
                        .getSerializableExtra(GlobalConstants.VIDEO_OBJ);
                videoCategory = intent
                        .getStringExtra(GlobalConstants.KEY_CATEGORY);

                Uri videoUri = AppController.getInstance().getModelFacade().getLocalModel().getUriUrl();
                String pushNotification = AppController.getInstance().getModelFacade().getLocalModel().getNotificationVideoId();
                if(pushNotification != null)
                {
                    params.putString(GlobalConstants.NOTIFICATION_OPEN, GlobalConstants.NOTIFICATION_OPEN);
                    mFirebaseAnalytics.logEvent(GlobalConstants.NOTIFICATION_OPEN, params);
                    mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
                }
                if (videoObject != null && videoUri == null) {
                    if (haveAllMustPermissions(writeExternalStorage, PERMISSION_REQUEST_MUST)) {
                        new ShareTwitter().execute();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void makeShareDialog() {

        boolean installedFbApp = checkIfAppInstalled("com.facebook.katana");
        boolean installedTwitterApp = checkIfAppInstalled("com.twitter.android");
        view = findViewById(R.id.sharinglayout);

        if (videoObject.getVideoShortDescription() != null && videoObject.getVideoName() != null) {
            longDescription = videoObject.getVideoShortDescription();
            videoName = videoObject.getVideoName();
            try {
                if (longDescription.length() > 40) {
                    longDescription = longDescription.substring(0, 40);
                }

                if (videoName.length() > 60) {
                    longDescription = longDescription.substring(0, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        linearLayout = (View) view.findViewById(R.id.social_sharing_linear_layout);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(VideoInfoActivity.this, R.anim.sliding_up_dialog);
                linearLayout.setAnimation(animation);
                linearLayout.setVisibility(View.VISIBLE);
            }
        }, 500);
        //enterReveal(linearLayout);
        //exitReveal1(view,linearLayout);


        int Measuredwidth = 0;
        try {
            Point size = new Point();
            WindowManager w = getWindowManager();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                w.getDefaultDisplay().getSize(size);
                Measuredwidth = size.x;
            } else {
                Display d = w.getDefaultDisplay();
                Measuredwidth = d.getWidth();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        flatButtonFacebook = (ImageView) view.findViewById(R.id.facebookShare);
        flatButtonTwitter = (ImageView) view.findViewById(R.id.twitterShare);
        facebookShareView = (ImageView) view.findViewById(R.id.facebookShareView);
        twitterShareView = (ImageView) view.findViewById(R.id.twitterShareView);

        twitterLoginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button_share);

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                try {
                    if (imageUri == null) {
//                        pDialog = new ProgressDialog(VideoInfoActivity.this, R.style.CustomDialogTheme);
//                        pDialog.show();
//                        pDialog.setContentView(Utils.getInstance().showRelatedVideoLoader(VideoInfoActivity.this, false));
//                        pDialog.setCanceledOnTouchOutside(false);
//                        pDialog.setCancelable(false);

                    } else {
                        if (videoObject.getVideoName() != null && videoObject.getVideoShortDescription() != null) {

                            Intent intent = new TweetComposer.Builder(VideoInfoActivity.this)
                                    .text(videoName + "\n" + longDescription + "\n\n")
                                    .url(new URL(videoObject.getVideoSocialUrl()))
                                    .image(imageUri)
                                    .createIntent();
                            startActivityForResult(intent, 100);
                        } else if (videoObject.getVideoName() != null) {

                            Intent intent = new TweetComposer.Builder(VideoInfoActivity.this)
                                    .text(videoName + "\n" + longDescription + "\n\n")
                                    .url(new URL(videoObject.getVideoSocialUrl()))
                                    .image(imageUri)
                                    .createIntent();

                            startActivityForResult(intent, 100);

                        }

                        if (imageUri != null) {
                            deleteFileFromSDcard();
                        }
                    }

                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        linearLayout.setVisibility(View.GONE);
                        // exitReveal1(linearLayout);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void failure(TwitterException e) {
                showToastMessage(GlobalConstants.TWITTER_LOGIN_CANCEL);
            }

        });

        if (!installedFbApp) {

            facebookShareView.setVisibility(View.VISIBLE);
            flatButtonFacebook.setVisibility(View.GONE);
            facebookShareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView != null) {
                        videoView.pause();
                    }
                    String facebookPlayStoreUrl = "https://play.google.com/store/apps/details?id=com.facebook.katana&hl=en";
                    showConfirmSharingDialog("Facebook app is not installed would you like to install it now?", facebookPlayStoreUrl);
                }
            });


        } else {
            facebookShareView.setVisibility(View.GONE);
            flatButtonFacebook.setVisibility(View.VISIBLE);
            flatButtonFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //   progressDialog.dismiss();
                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        linearLayout.setVisibility(View.GONE);
                        //exitReveal1(linearLayout);
                    }
                    if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() == GlobalConstants.DEFAULT_USER_ID) {
                        showConfirmLoginDialog(GlobalConstants.SHARE_MESSAGE);
                    } else if (Utils.isInternetAvailable(VideoInfoActivity.this)) {
                        if (videoObject.getVideoSocialUrl() != null) {
                            if (videoObject.getVideoSocialUrl().length() == 0) {
                                showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE + " to share");
                            } else {
                                //  videoView.pause();
                                shareVideoUrlFacebook(videoObject.getVideoId(), videoObject.getVideoSocialUrl(), videoObject.getVideoStillUrl(), videoObject.getVideoShortDescription(), videoObject.getVideoName(), context);
                            }
                        } else {
                            showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE + " to share");
                        }
                    } else {
                        showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                    }
                }
            });
        }
        if (!installedTwitterApp) {

            twitterShareView.setVisibility(View.VISIBLE);
            flatButtonTwitter.setVisibility(View.GONE);
            twitterShareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView != null) {
                        videoView.pause();
                    }
                    String twitterPlayStoreUrl = "https://play.google.com/store/apps/details?id=com.twitter.android&hl=en";
                    showConfirmSharingDialog("Twitter app is not installed would you like to install it now?", twitterPlayStoreUrl);
                }
            });


        } else {

            twitterShareView.setVisibility(View.GONE);
            flatButtonTwitter.setVisibility(View.VISIBLE);
            flatButtonTwitter.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    //  progressDialog.dismiss();
//                        if (videoObject != null) {
//                            new ShareTwitter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                        }


                        twitterSharingData();
                }
            });
        }

//        progressDialog = new ProgressDialog(context);
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setCancelable(true);
//        progressDialog.setCanceledOnTouchOutside(true);
//        progressDialog.show();
//        progressDialog.setContentView(view);
    }


    private void twitterSharingData() {
        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.GONE);
            //exitReveal1(linearLayout);
        }

        System.out.println("twitter sharing");
        if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() == GlobalConstants.DEFAULT_USER_ID) {
            showConfirmLoginDialog(GlobalConstants.SHARE_MESSAGE);
        } else if (Utils.isInternetAvailable(VideoInfoActivity.this)) {
            if (videoObject.getVideoSocialUrl() != null) {
                if (videoObject.getVideoSocialUrl().length() == 0) {
                    showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE + " to share");
                } else {

                    // videoView.pause();
                    TwitterSession session = Twitter.getSessionManager().getActiveSession();

                    if (session == null) {
                        twitterLoginButton.performClick();
                    } else {
                        try {
                            if (imageUri == null) {
//                                pDialog = new ProgressDialog(VideoInfoActivity.this, R.style.CustomDialogTheme);
//                                pDialog.show();
//                                pDialog.setContentView(Utils.getInstance().showRelatedVideoLoader(VideoInfoActivity.this, false));
//                                pDialog.setCanceledOnTouchOutside(false);
//                                pDialog.setCancelable(false);

                            } else {

                                if (videoObject.getVideoName() != null && videoObject.getVideoShortDescription() != null) {
                                    try {

                                        TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                                .text(videoName + "\n" + longDescription + "\n\n")
                                                .url(new URL(videoObject.getVideoSocialUrl()))
                                                .image(imageUri);

                                        builder.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else if (videoObject.getVideoName() != null) {

                                    try {

                                        TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                                .text(videoName + "\n" + longDescription + "\n\n")
                                                .url(new URL(videoObject.getVideoSocialUrl()))
                                                .image(imageUri);

                                        builder.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (imageUri != null) {
                                    deleteFileFromSDcard();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE + " to share");
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }


    public void stopVideoEvents() {
        try {
            if (videoView != null) {
                videoView.stop();
            }
            //gk mVideoControlHandler.removeCallbacks(videoRunning);
            llVideoLoader.setVisibility(View.GONE);
            bufferLinearLayout.setVisibility(View.GONE);
            //gk bufferProgressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startRelatedVideo(VideoDTO videoObj) {
        videoObject = videoObj;

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (rlVideoNameStrip != null) {
//                    rlVideoNameStrip.setVisibility(View.GONE);
//                }
//            }
//        });

        if (videoObject != null) {
            new ShareTwitter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    public void setRelatedVideoData(final VideoDTO videoObject) {
        //Set Video to videoview
        if (videoCategory != null) {
            // -----stopping the flurry event of video-----------
            FlurryAgent.endTimedEvent(videoCategory);
        }
        videoCategory = GlobalConstants.RELATED_VIDEO_CATEGORY;
        if (videoObject != null) {
            if (Utils.isInternetAvailable(this)) {
                String encodedVideoUrl = videoObject.getVideoLongUrl();
                //gk llVideoLoader.setVisibility(View.VISIBLE);
                encodedVideoUrl = encodedVideoUrl.replace("(format=m3u8-aapl)", "(format=m3u8-aapl-v3)");

                System.out.println("Media Url : " + encodedVideoUrl);
                Uri videoUri = Uri.parse(encodedVideoUrl);
            /*mController = new MediaController(this);
            videoView.setMediaController(mController);*/
                //gk videoView.stop();
                //gk videoView.clearFocus();
                // Keep the screen on during playback
                new KeepScreenOnHandler(videoView, getWindow());
                // videoView.setSkin("http://0b78b111a9d0410784caa8a634aa3b90.cloudapp.net/JWPlayerCss/Auburn/AuburnPlayer.css");
                //videoView.setSkin("http://0b78b111a9d0410784caa8a634aa3b90.cloudapp.net/JWPlayerCss/UGA/UgaPlayer.css");
                videoView.setSkin(GlobalConstants.JW_PLAYER_CSS_FILE_URL);
                // Load a media source

                PlaylistItem pi = new PlaylistItem.Builder()
                        .file(encodedVideoUrl)
                        .image(videoObject.getVideoStillUrl())
                        .build();

                videoView.load(pi);

//gk                videoView.requestFocus();
//gk                videoView.play();
            } else {
                Utils.showNoConnectionMessage(this);
            }
        }
        if (videoObject != null) {
            if (VaultDatabaseHelper.getInstance(VideoInfoActivity.this).isFavorite(videoObject.getVideoId()))
                imgToggleButton.setBackgroundResource(R.drawable.saved_video_img);
            else
                imgToggleButton.setBackgroundResource(R.drawable.video_save);
            Utils.addImageByCaching(imgVideoStillUrl, videoObject.getVideoStillUrl());
            //gk imgVideoStillUrl.setVisibility(View.VISIBLE);
            tvVideoName.setText(videoObject.getVideoName().toString());
        }
        // -------- starting the flurry event of video------
        articleParams = new HashMap<String, String>();
        articleParams.put(GlobalConstants.KEY_VIDEONAME, videoObject.getVideoName());
        FlurryAgent.logEvent(videoCategory, articleParams, true);

        if (mPagerAdapter.getCount() > 0) {
            VideoInfoPagerFragment descriptionFragment = (VideoInfoPagerFragment) mPagerAdapter.getItem(0);
            View fragmentView = descriptionFragment.getView();
            TextView tvLongDescription = (TextView) fragmentView.findViewById(R.id.tv_video_long_description);
            tvLongDescription.setText(videoObject.getVideoLongDescription());

            mPagerAdapter.notifyDataSetChanged();
        }
    }

    public void showConfirmLoginDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(message);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            stopService(new Intent(VideoInfoActivity.this, TrendingFeaturedVideoService.class));

                            VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();

                            SharedPreferences prefs = context.getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                            prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).commit();
//                        prefs.edit().putBoolean(GlobalConstants.PREF_PULL_OPTION_HEADER, false).commit();

                            Intent intent = new Intent(context, LoginEmailActivity.class);
                            context.startActivity(intent);
                            context.finish();

                            MainActivity.context.finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        nbutton.setAllCaps(false);
        nbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        pbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
        pbutton.setAllCaps(false);
    }

    public void showConfirmSharingDialog(String message, final String playStoreUrl) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(message);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Install",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                            linearLayout.setVisibility(View.GONE);
                            //  exitReveal1(linearLayout);
                        }

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(playStoreUrl));
                        startActivityForResult(intent, 100);

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        nbutton.setAllCaps(false);
        nbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        pbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
        pbutton.setAllCaps(false);
    }


    public void showConfirmDialogBox(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(message);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        alertDialog.dismiss();

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        nbutton.setAllCaps(false);
        nbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        pbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
        pbutton.setAllCaps(false);
    }

    public void shareVideoUrlFacebook(final long videoId, String videourl, String imageurl, String description, String name, final Activity context) {
        try {
            final FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
                @Override
                public void onCancel() {
                   //gk showToastMessage(GlobalConstants.FACEBOOK_SHARING_CANCEL);
                    GlobalConstants.IS_SHARING_ON_FACEBOOK = false;
                }

                @Override
                public void onError(FacebookException error) {
                    String title = "Error";
                    String alertMessage = error.getMessage();
                    showToastMessage(GlobalConstants.FACEBOOK_SHARING_CANCEL);
                    GlobalConstants.IS_SHARING_ON_FACEBOOK = false;
                }

                @Override
                public void onSuccess(Sharer.Result result) {
                    boolean installed = checkIfFacebookAppInstalled("com.facebook.android");
                    if (!installed)
                        installed = checkIfFacebookAppInstalled("com.facebook.katana");
                    if (!installed)
                        showToastMessage(GlobalConstants.FACEBOOK_POST_SUCCESS_MESSAGE);
                    GlobalConstants.IS_SHARING_ON_FACEBOOK = false;

                    if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
                        linearLayout.setVisibility(View.GONE);
                        // exitReveal1(linearLayout);
                    }

                    String videoIdData = String.valueOf(videoId);

//                    shareInfoTask = new shareInfoTask();
//                    shareInfoTask.execute(videoIdData);
                }
            };

            GlobalConstants.IS_SHARING_ON_FACEBOOK = true;
            FacebookSdk.sdkInitialize(context.getApplicationContext());

            callbackManager = CallbackManager.Factory.create();

            shareDialog = new ShareDialog(context);
            shareDialog.registerCallback(
                    callbackManager,
                    shareCallback);

            canPresentShareDialog = ShareDialog.canShow(
                    ShareLinkContent.class);

            Profile profile = Profile.getCurrentProfile();

            ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                    .setContentTitle(name)
//                    .setContentDescription(description)
//                    .setImageUrl(Uri.parse(imageurl))
                    .setContentUrl(Uri.parse(videourl))
                    .build();

            if (profile != null) {
                if (canPresentShareDialog) {
                    shareDialog.show(linkContent);
                } else if (profile != null && hasPublishPermission()) {
                    ShareApi.share(linkContent, shareCallback);
                }
            } else {
                loginWithFacebook();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String postResultData;
    AsyncTask<String, Void, String> shareInfoTask;

    @Override
    public void onFullscreen(boolean fullscreen) {
        android.app.ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (fullscreen) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }
    }

    public class shareInfoTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                postResultData = AppController.getInstance().getServiceManager().getVaultService().postSharingInfo(params[0].toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return postResultData;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

    public static boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    public void loginWithFacebook() {
//        Toast.makeText(this, "Your are not logged in, please login", Toast.LENGTH_LONG).show();
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(GlobalConstants.FACEBOOK_PERMISSION));
    }

    private boolean checkIfFacebookAppInstalled(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
            //Check if the Facebook app is disabled
            ApplicationInfo ai = getPackageManager().getApplicationInfo(uri, 0);
            app_installed = ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }

        return app_installed;
    }

    private boolean checkIfAppInstalled(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
            //Check if the Facebook app is disabled
            ApplicationInfo ai = getPackageManager().getApplicationInfo(uri, 0);
            app_installed = ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }

        return app_installed;
    }

    public void initializeFacebookUtil() {
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        System.out.println("Facebook login successful");
                    }

                    @Override
                    public void onCancel() {
                        //gk showAlert();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showAlert();
                    }

                    private void showAlert() {
                        showToastMessage(GlobalConstants.FACEBOOK_LOGIN_CANCEL);
                        /*new AlertDialog.Builder(context)
                                .setTitle("Cancelled")
                                .setMessage("Permission not granted")
                                .setPositiveButton("Ok", null)
                                .show();*/
                    }
                });

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null && GlobalConstants.IS_SHARING_ON_FACEBOOK) {
                    shareVideoUrlFacebook(videoObject.getVideoId(), videoObject.getVideoSocialUrl(), videoObject.getVideoStillUrl(), videoObject.getVideoShortDescription(), videoObject.getVideoName(), context);
                }
            }
        };
    }

    String path = null;
    File file;
    String STORE_DIRECTORY;

    private class ShareTwitter extends AsyncTask<Void, Void, Uri> {

        protected Uri doInBackground(Void... arg0) {
            try {
                if (videoObject.getVideoStillUrl() != null) {
                    InputStream is = new URL(videoObject.getVideoStillUrl().trim()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

//                    path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
//                    imageUri = Uri.parse(path);

                    File externalFilesDir = getExternalFilesDir(null);
                    if (externalFilesDir != null) {
                        STORE_DIRECTORY = externalFilesDir.getAbsolutePath() + "/abc/";
                        File storeDirectory = new File(STORE_DIRECTORY);
                        Log.e("Storage Dir", "" + storeDirectory);
                        if (!storeDirectory.exists()) {
                            boolean success = storeDirectory.mkdirs();
                            if (!success) {
                                Log.e(TAG, "failed to create file storage directory.");
                                return null;
                            }
                        }
                        try {
                            Random generator = new Random();
                            int n = 10000;
                            n = generator.nextInt(n);
                            String fname = "Image-" + n + ".jpg";
                            file = new File(storeDirectory, fname);
                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();
                            imageUri = Uri.parse(file.getAbsolutePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");

                    }


                } else {
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return imageUri;
        }

        protected void onPostExecute(Uri result) {
            try {
                imageUri = result;
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                    sharingImageOnTwitter();
                }
                System.out.println("imageUri value ");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteAllContent(File file, String... filename) {
        if (file.isDirectory())
            for (File child : file.listFiles())
                deleteAllContent(child);
        if (filename == null)
            file.delete();
        else
            for (String fn : filename)
                if (file.getName().equals(filename))
                    file.delete();
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }


    private void sharingImageOnTwitter() {
        if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.GONE);
        }
        if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() == GlobalConstants.DEFAULT_USER_ID) {
            if (videoView != null) {
                videoView.pause();
            }
            showConfirmLoginDialog(GlobalConstants.SHARE_MESSAGE);

        } else if (Utils.isInternetAvailable(VideoInfoActivity.this)) {
            if (videoObject.getVideoSocialUrl() != null) {
                if (videoObject.getVideoSocialUrl().length() == 0) {
                    showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE + " to share");
                } else {
                    //gk videoView.pause();
                    TwitterSession session = Twitter.getSessionManager().getActiveSession();
                    if (session == null) {
                        twitterLoginButton.performClick();
                    } else {
                        try {
                            if (imageUri == null) {
//                                pDialog = new ProgressDialog(VideoInfoActivity.this, R.style.CustomDialogTheme);
//                                pDialog.show();
//                                pDialog.setContentView(Utils.getInstance().showRelatedVideoLoader(VideoInfoActivity.this, false));
//                                pDialog.setCanceledOnTouchOutside(false);
//                                pDialog.setCancelable(false);

                            } else {

                                if (videoObject.getVideoName() != null && videoObject.getVideoShortDescription() != null) {
                                    try {

                                        TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                                .text(videoName + "\n" + longDescription + "\n\n")
                                                .url(new URL(videoObject.getVideoSocialUrl()))
                                                .image(imageUri);

                                        builder.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else if (videoObject.getVideoName() != null) {

                                    try {

                                        TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                                .text(videoName + "\n" + longDescription + "\n\n")
                                                .url(new URL(videoObject.getVideoSocialUrl()))
                                                .image(imageUri);

                                        builder.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (imageUri != null) {
                                    deleteFileFromSDcard();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE + " to share");
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    private void deleteFileFromSDcard() {

//        File root = new File(file.getAbsolutePath());
//        if (root != null) {
//            if (root.listFiles() != null) {
//                for (File childFile : root.listFiles()) {
//                    if (childFile != null) {
//                        if (childFile.exists())
//                            childFile.delete();
//                    }
//
//                }
//                if (root.exists())
//                    root.delete();
//            }
//        }
//    deleteAllContent(getExternalFilesDir(null), getPath(imageUri));


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                File path = new File(file.getAbsolutePath());
//
//                if (path.exists()) {
//                    path.delete();
//                }
//            }
//        }, 5000);


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (imageUri != null) {
//                        if (getPath(imageUri) != null) {
//                            System.out.println("deleted file from  sd card..");
//                            File file = new File(getPath(imageUri));
//                            if (file.exists()) {
//                                file.delete();
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 3000);

    }

//    private void deleteFile()
//    {
//       new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Set up the projection (we only need the ID)
//                String[] projection = { MediaStore.Images.Media._ID };
//                File file = new File(getPath(imageUri));
//// Match on the file path
//                String selection = MediaStore.Images.Media.DATA + " = ?";
//                String[] selectionArgs = new String[] { file.getAbsolutePath() };
//
//                // Query for the ID of the media matching the file path
//                Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                ContentResolver contentResolver = VideoInfoActivity.this.getContentResolver();
//                Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
//                if (c.moveToFirst()) {
//                    // We found the ID. Deleting the item via the content provider will also remove the file
//                    long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
//                    Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
//                    contentResolver.delete(deleteUri, null, null);
//                } else {
//                    // File not found in media store DB
//                }
//                c.close();
//            }
//        }, 1000);
//    }

    private static final String TAG = "VideoInfoActivity";

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d(TAG, "Headset is unplugged");
                        videoView.pause(true);
                        break;
                    case 1:
                        Log.d(TAG, "Headset is plugged");

                        break;
                    default:
                        Log.d(TAG, "I have no idea what the headset state is");
                }
            }
        }
    }
}