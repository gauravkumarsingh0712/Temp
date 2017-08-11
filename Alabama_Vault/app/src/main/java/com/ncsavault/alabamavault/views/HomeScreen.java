package com.ncsavault.alabamavault.views;

import android.animation.Animator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.bottomnavigation.BottomNavigationBar;
import com.ncsavault.alabamavault.bottomnavigation.NavigationPage;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.fragments.views.BaseFragment;
import com.ncsavault.alabamavault.fragments.views.CatagoriesFragment;
import com.ncsavault.alabamavault.fragments.views.HomeFragment;
import com.ncsavault.alabamavault.fragments.views.PlaylistFragment;
import com.ncsavault.alabamavault.fragments.views.ProfileFragment;
import com.ncsavault.alabamavault.fragments.views.SavedVideoFragment;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gauravkumar.singh on 5/16/2017.
 */

public class HomeScreen extends AppCompatActivity implements  BottomNavigationBar.BottomNavigationMenuClickListener
        ,OnFragmentToucheded {
    private static final String SELECTED_ITEM = "arg_selected_item";

    private int mSelectedItem;
    public static int count = 10; //ViewPager items size
    // helper class for handling UI and click events of bottom-nav-bar
    private BottomNavigationBar mBottomNav;

    // list of Navigation pages to be shown
    private List<NavigationPage> mNavigationPageList = new ArrayList<>();
    /**
     * You shouldn't define first page = 0.
     * Let define firstpage = 'number viewpager size' to make endless carousel
     */
    public static int FIRST_PAGE = 10;
    public final static int LOOPS = 1000;

    public static Activity activity;
    public static int[] listItems = {R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4,
            R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4,
            R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4, R.drawable.vault_4};

    private String navigationPageArray[] = {"Home", "Catagories", "Saved", "Settings"};

    private Fragment navigationPageFragment[] = {HomeFragment.newInstance(this), CatagoriesFragment.newInstance(this),
            SavedVideoFragment.newInstance(this), ProfileFragment.newInstance(this,20,20)};
    public SearchView searchView;

    private int[] bottomTabIcons = {R.drawable.home_icon, R.drawable.categories, R.drawable.video_save,
            R.drawable.user_profile};


    public static Toolbar mToolbar;

    ImageView imageViewSearch;
    EditText editTextSearch;
    ImageView imageViewLogo;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_layout);

        activity = this;
        loadUniversalImageLoader();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appBar);
        appBarLayout.setExpanded(false, true);

        // mToolbar.setTitle("UGAVAULT");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");

        imageViewSearch= (ImageView) mToolbar.findViewById(R.id.imageview_search);
        imageViewSearch.setTag(R.drawable.search);
        editTextSearch= (EditText) mToolbar.findViewById(R.id.editText_search);
        imageViewLogo= (ImageView) mToolbar.findViewById(R.id.imageview_logo);

        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Integer) imageViewSearch.getTag()).intValue() == R.drawable.search){
                    editTextSearch.setVisibility(View.VISIBLE);
                    imageViewSearch.setImageResource(R.drawable.close);
                    imageViewLogo.setVisibility(View.GONE);
                }else{
                    editTextSearch.setVisibility(View.GONE);
                    imageViewSearch.setImageResource(R.drawable.search);
                    imageViewSearch.setTag(R.drawable.close);
                    imageViewLogo.setVisibility(View.VISIBLE);
                }

            }
        });

        loadBottomNavigationItems();

        AppController.getInstance().setCurrentActivity(activity);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onFragmentTouched(Fragment fragment, float x, float y) {


            if (fragment instanceof BaseFragment) {

             final BaseFragment theFragment = (BaseFragment) fragment;

                Animator unreveal = theFragment.prepareUnrevealAnimator(x, y);

                unreveal.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // remove the fragment only when the animation finishes
                        //  getFragmentManager().beginTransaction().remove(theFragment).commit();
                        //to prevent flashing the fragment before removing it, execute pending transactions inmediately
                        getFragmentManager().executePendingTransactions();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                unreveal.start();
            }


    }


    private void loadUniversalImageLoader()
    {
        File cacheDir = StorageUtils.getCacheDirectory(HomeScreen.this);
        ImageLoaderConfiguration config;
        config = new ImageLoaderConfiguration.Builder(HomeScreen.this)
                .threadPoolSize(3) // default
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiscCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }


    /**
     * Method used to load all the bottom navigation pages..
     */
    private void loadBottomNavigationItems() {
        List<NavigationPage> navigationPages = new ArrayList<>();
        for (int i = 0; i < navigationPageArray.length; i++) {
            NavigationPage bottomNavagationPage = new NavigationPage(navigationPageArray[i],
                    ContextCompat.getDrawable(this,bottomTabIcons[i]),
                    navigationPageFragment[i]);
            navigationPages.add(bottomNavagationPage);
        }
        setupBottomBarHolderActivity(navigationPages);
    }

    /**
     * initializes the BottomBarHolderActivity with sent list of Navigation pages
     *
     * @param pages
     */
    public void setupBottomBarHolderActivity(List<NavigationPage> pages) {
        // throw error if pages does not have 4 elements
        if (pages.size() != 4) {
            throw new RuntimeException("List of NavigationPage must contain 5 members.");
        } else {
            mNavigationPageList = pages;
            mBottomNav = new BottomNavigationBar(this, pages, this);
            setupFragments();
        }

    }

    /**
     * sets up the fragments with initial view
     */
    private void setupFragments() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, mNavigationPageList.get(0).getFragment());
        fragmentTransaction.commit();
    }

    /**
     * handling onclick events of bar items
     *
     * @param menuType
     */
    @Override
    public void onClickedOnBottomNavigationMenu(int menuType) {

        // finding the selected fragment
        Fragment fragment = null;
        switch (menuType) {
            case BottomNavigationBar.MENU_BAR_1:
                fragment = mNavigationPageList.get(0).getFragment();
                break;
            case BottomNavigationBar.MENU_BAR_2:
                fragment = mNavigationPageList.get(1).getFragment();

                break;
            case BottomNavigationBar.MENU_BAR_3:
                fragment = mNavigationPageList.get(2).getFragment();

                break;
            case BottomNavigationBar.MENU_BAR_4:
                fragment = mNavigationPageList.get(3).getFragment();
                break;

        }

        // replacing fragment with the current one
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed()
    {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }




}
