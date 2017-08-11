package com.ncsavault.alabamavault.views;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.flurry.android.FlurryAgent;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.PagerAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.defines.AppDefines;
import com.ncsavault.alabamavault.dto.TabBannerDTO;
import com.ncsavault.alabamavault.fragments.views.CoachesEraFragment;
import com.ncsavault.alabamavault.fragments.views.FavoritesFragment;
import com.ncsavault.alabamavault.fragments.views.FeaturedFragment;
import com.ncsavault.alabamavault.fragments.views.GamesFragment;
import com.ncsavault.alabamavault.fragments.views.OpponentsFragment;
import com.ncsavault.alabamavault.fragments.views.PlayerFragment;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BannerDataModel;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.service.VideoDataService;
import com.ncsavault.alabamavault.utils.Utils;
import com.viewpagerindicator.TitlePageIndicator;

import java.io.File;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import io.fabric.sdk.android.Fabric;

@SuppressWarnings("serial")
public class MainActivity extends FragmentActivity implements Serializable, AbstractView {

    public PagerAdapter mPagerAdapter;
    public static ViewPager mPager;
    public static TitlePageIndicator mIndicator;
    private ActionBar actionBar;
    public static Activity context;
    private String gryColor = "#999999";
    public static List<Fragment> fragments;

    Animation animation;

    SharedPreferences prefs;
    public SearchView searchView;

    public ProgressDialog progressDialog;

    String videoUrl;

    public static ProgressBar autoRefreshProgressBar;
    private BannerDataModel mBannerDataModel;
    Handler autoRefreshHandler = new Handler();


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.main_activity);
        System.out.println("push notification MainActivity");
        context = MainActivity.this;
        getSoftButtonsBarSizePort(this);

        initlizeData();

    }

    private void initlizeData() {

        //gk CrashManager.initialize(this, GlobalConstants.HOCKEY_APP_ID, null);

//        if (VaultDatabaseHelper.getInstance(getApplicationContext()).getVideoCount() == 0 && !VideoDataService.isServiceRunning) {
//            Toast.makeText(this, "No videos in local database, starting background service", Toast.LENGTH_LONG).show();
//            startService(new Intent(this, VideoDataService.class));
//        }

        File cacheDir = StorageUtils.getCacheDirectory(context);
        ImageLoaderConfiguration config;
        config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(3) // default
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .build();
        ImageLoader.getInstance().init(config);

        prefs = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);

        // --------forcing to show overflow menu---------
        Utils.forcingToShowOverflowMenu(context);

        // ----- start flurry session-------------
        FlurryAgent.onStartSession(context, GlobalConstants.FLURRY_KEY);


        TwitterAuthConfig authConfig = new TwitterAuthConfig(GlobalConstants.TWITTER_CONSUMER_KEY, GlobalConstants.TWITTER_CONSUMER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new TweetUi());
        Fabric.with(this, new TweetComposer());

        initViews();

        //get Wifi strength
        int numberOfLevels = 5;
       /* WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Utils.addPagerIndicatorBelowActionBar(context, mIndicator);
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //calculating runtime device width and height
        setDimensions();

        // -------- initializing adapter and indicator-----------------
        initialisePaging();

        mIndicator.setViewPager(mPager);

        setUpPullOptionHeader();

        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
//                mIndicator.setCurrentItem(position);
//                mPager.setCurrentItem(position, true);
                GlobalConstants.CURRENT_TAB = position;
                android.support.v4.app.Fragment fragment = ((FragmentStatePagerAdapter) mPager.getAdapter()).getItem(position);
                fragment.onAttach(MainActivity.this);
                fragment.onResume();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });

        //  getIntentData();
        autoRefresh();
    }

    public static int getSoftButtonsBarSizePort(Activity activity) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri UriValue = AppController.getInstance().getModelFacade().getLocalModel().getUriUrl();
        String pushVideoId = AppController.getInstance().getModelFacade().getLocalModel().getVideoId();
        if (UriValue != null
                || pushVideoId != null) {
            System.out.println("on new inteant call");
            initlizeData();

        }
    }

    /**
     * Initialise the fragments to be paged
     */
    private void initialisePaging() {


        fragments = new Vector<Fragment>();

        ArrayList<TabBannerDTO> lstTabBannerData = VaultDatabaseHelper.getInstance(getApplicationContext()).getAllLocalTabBannerData();
        Collections.sort(lstTabBannerData, new Comparator<TabBannerDTO>() {

            @Override
            public int compare(TabBannerDTO lhs, TabBannerDTO rhs) {
                // TODO Auto-generated method stub
                return Long.valueOf(lhs.getTabIndexPosition())
                        .compareTo(Long.valueOf(rhs.getTabIndexPosition()));
            }
        });
        for (TabBannerDTO tabBannerDTO : lstTabBannerData) {
            Bundle bundle = new Bundle();
            bundle.putString("tabId", String.valueOf(tabBannerDTO.getTabId()));
            bundle.putString("tabName", tabBannerDTO.getTabName());

            if (tabBannerDTO.getTabName().toLowerCase().contains("featured")) {
                fragments.add(Fragment.instantiate(this, FeaturedFragment.class.getName(), bundle));
            } else if (tabBannerDTO.getTabName().toLowerCase().contains("games"))
                fragments.add(Fragment.instantiate(this, GamesFragment.class.getName(), bundle));
            else if (tabBannerDTO.getTabName().toLowerCase().contains("players"))
                fragments.add(Fragment.instantiate(this, PlayerFragment.class.getName(), bundle));
            else if (tabBannerDTO.getTabName().toLowerCase().contains("coach"))
                fragments.add(Fragment.instantiate(this, CoachesEraFragment.class.getName(), bundle));
            else if (tabBannerDTO.getTabName().toLowerCase().contains("opponent"))
                fragments.add(Fragment.instantiate(this, OpponentsFragment.class.getName(), bundle));
        }
        fragments.add(Fragment.instantiate(this, FavoritesFragment.class.getName()));

        this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        this.mPager.setAdapter(this.mPagerAdapter);

    }

    public void setUpPullOptionHeader() {
        final View pullView = findViewById(R.id.rl_pull_option);

        final SharedPreferences prefs = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        boolean isPullHeaderSeen = prefs.getBoolean(GlobalConstants.PREF_PULL_OPTION_HEADER, false);

        Button btnGotIt = (Button) pullView.findViewById(R.id.btn_got_it);

        btnGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean(GlobalConstants.PREF_PULL_OPTION_HEADER, true).commit();

                Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.abc_fade_out);
                pullView.setVisibility(View.GONE);
                pullView.setAnimation(anim);
            }
        });

        if (isPullHeaderSeen) {
            pullView.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //gk CrashManager.execute(this, null);
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        GlobalConstants.SEARCH_VIEW_QUERY = "";
        finishAffinity();
    }

    //public static ProgressBar auto_refresh_progress_bar;
    private void initViews() {
        mPager = (ViewPager) findViewById(R.id.pager);
        // auto_refresh_progress_bar = (ProgressBar) findViewById(R.id.auto_refresh_progress_bar);
        mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);

        View autoRefreshView = findViewById(R.id.auto_refresh_progress_main);
        autoRefreshProgressBar = (ProgressBar) autoRefreshView.findViewById(R.id.auto_refresh_progress_bar);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            autoRefreshProgressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            autoRefreshProgressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.progress_large_material, null));
        }

        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor(gryColor)));
//        actionBar.setLogo(R.drawable.actionbaricon);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        boolean isSkipLogin = pref.getBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false);
        if (isSkipLogin || AppController.getInstance().getModelFacade().getLocalModel().getUserId() == GlobalConstants.DEFAULT_USER_ID) {
            // try to see if already exists
            MenuItem item = menu.findItem(R.id.action_profile);
            if (item != null) {
                menu.removeItem(R.id.action_profile);
            }

            MenuItem editItem = menu.findItem(1001);
            if (editItem == null) {
                menu.add(0, 1001, 0,
                        "Log In");
            }
        } else if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() > 0 ||
                Profile.getCurrentProfile() != null) {
            MenuItem item = menu.findItem(1001);
            if (item != null)
                menu.removeItem(1001);
        }

        // ---------- intializing searchview in actionbar------------
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setIconified(true);
        searchView.clearFocus();


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActionBar().setDisplayUseLogoEnabled(false);
                getActionBar().setIcon(R.drawable.actionbaricon);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getActionBar().setDisplayUseLogoEnabled(true);
                return false;
            }
        });

        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate",
                null, null);
        // Getting the 'search_plate' LinearLayout.
        View searchPlate = searchView.findViewById(searchPlateId);
        searchPlate.setBackgroundResource(R.drawable.searchview_selector);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//         end the flurry session
        FlurryAgent.onEndSession(context);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {

            case R.id.action_toggle_notifications:
                Utils.getInstance().showNotificationToggleSetting(MainActivity.this);
                break;
            case R.id.action_profile:
                GlobalConstants.SEARCH_VIEW_QUERY = "";
                AppController.getInstance().handleEvent(AppDefines.EVENT_ID_USER_PROFILE_SCREEN);
                overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);

                return true;
            case R.id.action_contact:
                long userId = AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                if (userId == GlobalConstants.DEFAULT_USER_ID) {
                    AppController.getInstance().handleEvent(AppDefines.EVENT_ID_CONTACT_SCREEN);
                } else {
                    CharSequence supportOptions[] = new CharSequence[]{"Somethings Wrong", "I have a suggestion", "Clip Request"};

                    TextView title = new TextView(this);
                    title.setText("Support");
                    title.setPadding(10, 10, 10, 10);
                    title.setGravity(Gravity.CENTER);
//                title.setTextColor(Color.RED);
                    title.setTextSize(20);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCustomTitle(title);
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setItems(supportOptions, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent supportIntent = new Intent(MainActivity.this, ContactActivity.class);
                            if (which == 0) {
                                supportIntent.putExtra("title", getResources().getString(R.string.support_title));
                                supportIntent.putExtra("subtitle", getResources().getString(R.string.support_sub_title));
                                supportIntent.putExtra("tagId", GlobalConstants.SUPPORT_TAG_ID);
                                startActivity(supportIntent);
                            } else if (which == 1) {
                                supportIntent.putExtra("title", getResources().getString(R.string.feedback_title));
                                supportIntent.putExtra("subtitle", getResources().getString(R.string.feedback_sub_title));
                                supportIntent.putExtra("tagId", GlobalConstants.FEEDBACK_TAG_ID);
                                startActivity(supportIntent);
                            } else if (which == 2) {
                                supportIntent.putExtra("title", getResources().getString(R.string.clip_request_title));
                                supportIntent.putExtra("subtitle", getResources().getString(R.string.clip_request_sub_title));
                                supportIntent.putExtra("tagId", GlobalConstants.CLIP_REQUEST_TAG_ID);
                                startActivity(supportIntent);
                            }
                            overridePendingTransition(R.anim.slideup, R.anim.nochange);
                        }
                    });
                    builder.show();
                }
                break;
            case 1001:
                GlobalConstants.SEARCH_VIEW_QUERY = "";
                stopService(new Intent(MainActivity.this, VideoDataService.class));

                VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();

                SharedPreferences prefs = context.getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).commit();
                prefs.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).commit();

                AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_SCREEN);
                overridePendingTransition(R.anim.slideup, R.anim.nochange);
                finish();
            default:
                break;
        }
        return false;
    }

    public void showToastMessage(String message) {
        View includedLayout = findViewById(R.id.llToast);

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
                animation = AnimationUtils.loadAnimation(MainActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    public void setDimensions() {

        Point size = new Point();
        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            AppController.getInstance().getModelFacade().getLocalModel().setmDisplayHeight(size.y);
            AppController.getInstance().getModelFacade().getLocalModel().setmDisplayWidth(size.x);
        } else {
            Display d = w.getDefaultDisplay();
            AppController.getInstance().getModelFacade().getLocalModel().setmDisplayHeight(d.getHeight());
            AppController.getInstance().getModelFacade().getLocalModel().setmDisplayWidth(d.getWidth());
        }

    }


    public void autoRefresh() {
        autoRefreshHandler.postDelayed(autoRefreshRunnable, GlobalConstants.AUTO_REFRESH_INTERVAL);

    }


    private Runnable autoRefreshRunnable = new Runnable() {
        @Override
        public void run() {

            System.out.println("auto refresh time : " + Calendar.getInstance().getTime());
            loadAutoRefreshData();
        }
    };


    public void loadAutoRefreshData() {
        try {
            if (autoRefreshProgressBar != null) {

                if (autoRefreshProgressBar.isShown()) {

                    return;
                }
                autoRefreshProgressBar.setVisibility(View.VISIBLE);
            }

            if (VideoDataService.isServiceRunning) {
                VideoDataService.isServiceRunning = false;
                stopService(new Intent(MainActivity.this, VideoDataService.class));
            }

            if (mBannerDataModel != null) {
                mBannerDataModel.unRegisterView(this);
            }
            mBannerDataModel = AppController.getInstance().getModelFacade().getRemoteModel().
                    getBannerDataModel();
            mBannerDataModel.registerView(this);
            mBannerDataModel.loadTabData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void update() {

        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mBannerDataModel != null && mBannerDataModel.getState() == BaseModel.STATE_SUCCESS) {
                        mBannerDataModel.unRegisterView(MainActivity.this);
                        if (autoRefreshProgressBar != null) {
                            autoRefreshProgressBar.setVisibility(View.GONE);
                        }
                        autoRefreshHandler.postDelayed(autoRefreshRunnable, GlobalConstants.AUTO_REFRESH_INTERVAL);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void hashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.ncsavault.alabamavault",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                // Log.d("My key Hash : ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.println("My key Hash : " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}