package com.ncsavault.alabamavault.fragments.views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.CarouselPagerAdapter;
import com.ncsavault.alabamavault.adapters.FilterSubtypesAdapter;
import com.ncsavault.alabamavault.adapters.VideoContentListAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.MenuItem;
import com.ncsavault.alabamavault.dto.TabBannerDTO;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.HomeScreen;
import com.ncsavault.alabamavault.views.MainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by gauravkumar.singh on 6/12/2017.
 */

public class HomeFragment extends BaseFragment implements AbsListView.OnScrollListener {
    private static final String ARG_TEXT = "arg_text";
    private static final String ARG_COLOR = "arg_color";

    private String mText;
    private int mColor;

    private View mContent;
    private TextView mTextView;

    public CarouselPagerAdapter adapter1;
    public ViewPager pager;
    static Activity mContext;


    // A Native Express ad is placed in every nth position in the RecyclerView.
    public static final int ITEMS_PER_AD = 4;

    // The Native Express ad height.
    private static final int NATIVE_EXPRESS_AD_HEIGHT = 100;

    private static final int NATIVE_EXPRESS_AD_WIDTH = 340;

    // The Native Express ad unit ID.
    private static final String AD_UNIT_ID = "ca-app-pub-6624530735885630/3536691501";

    // The RecyclerView that holds and displays Native Express ads and menu items.
    private RecyclerView mRecyclerView;
    private PullRefreshLayout refreshLayout;
    PullRefreshTask pullTask;

    // List of Native Express ads and MenuItems that populate the RecyclerView.
    private List<VideoDTO> mRecyclerViewItems = new ArrayList<>();


    private static int TOTAL_CELLS_PER_ROW = 1;
    public static final int TYPE_LOW = 0;
    public static final int TYPE_HIGH = 1;

    public static Fragment newInstance(Activity context) {
        Fragment frag = new HomeFragment();
        mContext = context;
        Bundle args = new Bundle();
//        args.putString(ARG_TEXT, text);
//        args.putInt(ARG_COLOR, color);
//        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents(view);
        setPagerAdapter(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (refreshLayout != null) {
            refreshLayout.setEnabled(true);
            refreshLayout.setOnRefreshListener(refreshListener);
        }
    }

    private void initComponents(View view) {
        refreshLayout = (PullRefreshLayout) view.findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);
    }

    PullRefreshLayout.OnRefreshListener refreshListener = new PullRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!Utils.isInternetAvailable(mContext.getApplicationContext())) {
                refreshLayout.setEnabled(false);
                refreshLayout.setRefreshing(false);
            } else {
                refreshLayout.setEnabled(true);
                refreshLayout.setRefreshing(true);
                pullTask = new PullRefreshTask();
                pullTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    };


    public class PullRefreshTask extends AsyncTask<Void, Void, ArrayList<VideoDTO>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mRecyclerView != null) {
                mRecyclerView.setEnabled(false);
            }

            refreshLayout.setRefreshing(true);
//            if (adapter != null) {
//                adapter.notifyDataSetChanged();
//            }
        }

        @Override
        protected ArrayList<VideoDTO> doInBackground(Void... params) {
            ArrayList<VideoDTO> arrList = new ArrayList<VideoDTO>();
            try {
                String url = GlobalConstants.FEATURED_API_URL + "userId=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                arrList.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));


            } catch (Exception e) {
                e.printStackTrace();
            }

            return arrList;
        }

        @Override
        protected void onPostExecute(final ArrayList<VideoDTO> result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    if (result.size() > 0) {
                        mRecyclerViewItems.clear();
                        mRecyclerViewItems.addAll(result);

                        Collections.sort(mRecyclerViewItems, new Comparator<VideoDTO>() {

                            @Override
                            public int compare(VideoDTO lhs, VideoDTO rhs) {
                                // TODO Auto-generated method stub
                                return Integer.valueOf(lhs.getVideoIndex())
                                        .compareTo(Integer.valueOf(rhs.getVideoIndex()));
                            }
                        });

                        mRecyclerViewItems.add(0,new VideoDTO());
                        mRecyclerViewItems.add(1, new VideoDTO());
                        for(int i =0;i<mRecyclerViewItems.size();i++)
                        {
                            if((i+1) % 3==0)
                            {
                                mRecyclerViewItems.add(i,new VideoDTO());
                            }
                        }


                        if (adapter != null) {
                            adapter = new FilterSubtypesAdapter(mContext, mRecyclerViewItems);
                            mRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            refreshLayout.setRefreshing(false);
                        }

                    }

                } else {
                    ((MainActivity) mContext).showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
                    refreshLayout.setRefreshing(false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putString(ARG_TEXT, mText);
        //outState.putInt(ARG_COLOR, mColor);
        super.onSaveInstanceState(outState);
    }

    ListView listView;
    float headerHeight = 250;
    float minHeaderHeight = 50;
    RelativeLayout relativeLayout;
    View layoutView;
    FilterSubtypesAdapter adapter;
    private int mResId;
    private int mNativeAdsResId;

    private void setPagerAdapter(View view) {
        try {
            int screenSize = getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;

            mRecyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view);

            getFeatureDataFromDataBase();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        float scrollY = getScrollY(view);

        float headerBarOffsetY = headerHeight - minHeaderHeight;
        float offset = 1 - Math.max((headerBarOffsetY - scrollY) / headerBarOffsetY, 0f);

        relativeLayout.setTranslationY(scrollY / 2);

    }

    /**
     *
     */
    public float getScrollY(AbsListView view) {
        View c = view.getChildAt(0);

        if (c == null)
            return 0;

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();

        float headerHeight = 0;
        if (firstVisiblePosition >= 1)
            headerHeight = this.headerHeight;

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    public class HomeResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP =
                "Message Processed";

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                mRecyclerViewItems.clear();
                mRecyclerViewItems.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getVideoList(GlobalConstants.OKF_FEATURED));

                Collections.sort(mRecyclerViewItems, new Comparator<VideoDTO>() {

                    @Override
                    public int compare(VideoDTO lhs, VideoDTO rhs) {
                        // TODO Auto-generated method stub
                        return Integer.valueOf(lhs.getVideoIndex())
                                .compareTo(Integer.valueOf(rhs.getVideoIndex()));
                    }
                });

                mRecyclerViewItems.add(0,new VideoDTO());
                mRecyclerViewItems.add(1, new VideoDTO());
                for(int i =0;i<mRecyclerViewItems.size();i++)
                {
                    if((i+1) % 3==0)
                    {
                        mRecyclerViewItems.add(i,new VideoDTO());
                    }
                }

                adapter = new FilterSubtypesAdapter(mContext, mRecyclerViewItems);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    HomeResponseReceiver receiver;
    private void getFeatureDataFromDataBase() {
        final AsyncTask<Void, Void, Void> mDbTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    IntentFilter filter = new IntentFilter(HomeFragment.HomeResponseReceiver.ACTION_RESP);
                    filter.addCategory(Intent.CATEGORY_DEFAULT);
                    receiver = new HomeFragment.HomeResponseReceiver();
                    mContext.registerReceiver(receiver, filter);

                    mRecyclerViewItems.clear();
                    mRecyclerViewItems.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getVideoList(GlobalConstants.OKF_FEATURED));

                    Collections.sort(mRecyclerViewItems, new Comparator<VideoDTO>() {

                        @Override
                        public int compare(VideoDTO lhs, VideoDTO rhs) {
                            // TODO Auto-generated method stub

                            return Integer.valueOf(lhs.getVideoIndex())
                                    .compareTo(Integer.valueOf(rhs.getVideoIndex()));
                        }
                    });
                    System.out.println("featuredVideoList doInBackground : " + mRecyclerViewItems.size());
                    mRecyclerViewItems.add(0,new VideoDTO());
                    mRecyclerViewItems.add(1, new VideoDTO());
                    for(int i =0;i<mRecyclerViewItems.size();i++)
                    {
                        if((i+1) % 3==0)
                        {
                            mRecyclerViewItems.add(i,new VideoDTO());
                        }
                    }
                    adapter = new FilterSubtypesAdapter(mContext, mRecyclerViewItems);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);
                // ------- addBannerImage---------------------

            }
        };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

}
