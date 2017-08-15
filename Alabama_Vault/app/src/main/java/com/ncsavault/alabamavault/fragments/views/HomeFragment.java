package com.ncsavault.alabamavault.fragments.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.ncsavault.alabamavault.AsyncTask.VideoPlayTask;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.CarouselPagerAdapter;
import com.ncsavault.alabamavault.adapters.FilterSubtypesAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.TabBannerDTO;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BannerDataModel;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.VideoDataTaskModel;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.AbstractView;
import com.ncsavault.alabamavault.views.HomeScreen;
import com.ncsavault.alabamavault.views.MainActivity;
import com.ncsavault.alabamavault.views.VideoInfoActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.ncsavault.alabamavault.views.HomeScreen.mToolbar;

/**
 * Created by gauravkumar.singh on 6/12/2017.
 */

public class HomeFragment extends BaseFragment implements AbsListView.OnScrollListener, AbstractView, FilterSubtypesAdapter.BannerClickListener {
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

    ProgressBar progressBar;
    ProgressDialog pDialog;
    private TabBannerDTO tabBannerDTO = null;
    ArrayList<VideoDTO> trendingArraylist = new ArrayList<>();
    ArrayList<TabBannerDTO> bannerList = new ArrayList<>();
    private VideoDataTaskModel mVideoDataTaskModel;

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

        String videoUrl = AppController.getInstance().getModelFacade().getLocalModel().getVideoUrl();
        String videoId = AppController.getInstance().getModelFacade().getLocalModel().getVideoId();

        long tabId = AppController.getInstance().getModelFacade().getLocalModel().getTabId();
        tabBannerDTO = VaultDatabaseHelper.getInstance(getActivity()).getLocalTabBannerDataByTabId(Long.valueOf(tabId));

        if (videoUrl != null || (videoId != null && videoId != "0")) {
            if (videoUrl == null) {
                videoUrl = videoId;
            }
            playFacbookVideo(videoUrl);

            AppController.getInstance().getModelFacade().getLocalModel().setVideoUrl(null);
            // bundle.putString("eventObject",null);
        }

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

        ArrayList<String> apiUrls = new ArrayList<>();
        apiUrls.add(GlobalConstants.FEATURED_API_URL);
        apiUrls.add(GlobalConstants.GET_TRENDING_PLAYLIST_URL);
        Intent intent = new Intent(context.getApplicationContext(), TrendingFeaturedVideoService.class);
        intent.putStringArrayListExtra("apiUrls", apiUrls);
// Create the bundle to pass to the service.


    }

    private void initComponents(View view) {
        refreshLayout = (PullRefreshLayout) view.findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.
                    circle_progress_bar_lower));
        } else {
            System.out.println("progress bar not showing ");
            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(),
                    R.drawable.progress_large_material, null));
        }
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

    @Override
    public void update() {

        try {
            ((HomeScreen) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mVideoDataTaskModel != null && mVideoDataTaskModel.getState() == BaseModel.STATE_SUCCESS) {
                        if (mVideoDataTaskModel.getVideoDTO().size() > 0) {
                            VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                    insertVideosInDatabase(mVideoDataTaskModel.getVideoDTO());
                            if (Utils.isInternetAvailable(mContext)) {
                                if (mVideoDataTaskModel.getVideoDTO().get(0).getVideoLongUrl() != null) {
                                    if (mVideoDataTaskModel.getVideoDTO().get(0).getVideoLongUrl().length() > 0
                                            && !mVideoDataTaskModel.getVideoDTO()
                                            .get(0).getVideoLongUrl().toLowerCase().equals("none")) {
                                        String videoCategories = GlobalConstants.FEATURED;
                                        Intent intent = new Intent(mContext,
                                                VideoInfoActivity.class);
                                        intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategories);
                                        intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, mVideoDataTaskModel.getVideoDTO()
                                                .get(0).getPlaylistReferenceId());
                                        intent.putExtra(GlobalConstants.VIDEO_OBJ, mVideoDataTaskModel.getVideoDTO().get(0));
                                        mContext.startActivity(intent);
                                        ((HomeScreen) mContext).overridePendingTransition(R.anim.slide_up_video_info,
                                                R.anim.nochange);
                                    } else {
                                        ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                    }
                                } else {
                                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                }
                            } else {
                                ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                            }
                        }
                        pDialog.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bannerClick() {
        if (tabBannerDTO != null) {
            if (tabBannerDTO.isBannerActive()) {
                if (tabBannerDTO.isHyperlinkActive() && tabBannerDTO.getBannerActionURL().length() > 0) {
                    //Start the ActionUrl in Browser
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(tabBannerDTO.getBannerActionURL()));
                    startActivity(intent);
                } else if (!tabBannerDTO.isHyperlinkActive() && tabBannerDTO.getBannerActionURL().length() > 0) {
                    //The ActionUrl has DeepLink associated with it
                    HashMap videoMap = Utils.getInstance().getVideoInfoFromBanner(tabBannerDTO.getBannerActionURL());
                    if (videoMap != null) {
                        if (videoMap.get("VideoId") != null) {
                            if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                    isVideoAvailableInDB(videoMap.get("VideoId").toString())) {
                                VideoDTO videoDTO = VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                                        .getVideoDataByVideoId(videoMap.get("VideoId").toString());
                                if (Utils.isInternetAvailable(mContext)) {
                                    if (videoDTO != null) {
                                        if (videoDTO.getVideoLongUrl() != null) {
                                            String videoCategory = GlobalConstants.FEATURED;
                                            Intent intent = new Intent(mContext,
                                                    VideoInfoActivity.class);
                                            intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                                            intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, videoDTO.
                                                    getPlaylistReferenceId());
                                            intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDTO);
                                            startActivity(intent);
                                            ((HomeScreen) mContext).overridePendingTransition(R.anim.slide_up_video_info,
                                                    R.anim.nochange);
                                        }
                                    } else {
                                        ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                    }
                                } else {
                                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                                }
                            } else {
                                //Make an API call to get video data
                                pDialog = new ProgressDialog(mContext, R.style.CustomDialogTheme);
                                pDialog.show();
                                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(getActivity()));
                                pDialog.setCanceledOnTouchOutside(false);
                                pDialog.setCancelable(false);

                                mVideoDataTaskModel = AppController.getInstance().getModelFacade().getRemoteModel()
                                        .getVideoDataTaskModel();
                                mVideoDataTaskModel.registerView(HomeFragment.this);

                                mVideoDataTaskModel.setProgressDialog(pDialog);
                                mVideoDataTaskModel.loadVideoData(videoMap);

                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(FilterSubtypesAdapter.BannerViewHolder viewHolder, int position) {

        viewHolder.imageviewBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick();
            }
        });

    }


    public class PullRefreshTask extends AsyncTask<Void, Void, ArrayList<VideoDTO>> {

        public boolean isBannerUpdated = false;
        public boolean isTabDataUpdated = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mRecyclerView != null) {
                mRecyclerView.setEnabled(false);
            }

            if(progressBar != null)
            {
                progressBar.setVisibility(View.VISIBLE);
            }

            refreshLayout.setRefreshing(true);
//            if (adapter != null) {
//                adapter.notifyDataSetChanged();
//            }
        }

        @Override
        protected ArrayList<VideoDTO> doInBackground(Void... params) {
            ArrayList<VideoDTO> arrList = new ArrayList<VideoDTO>();
            String url = "";
            try {
                //Update Banner Data
                if (tabBannerDTO != null) {
                    TabBannerDTO serverObj = AppController.getInstance().getServiceManager().
                            getVaultService().getTabBannerDataById(tabBannerDTO.getTabBannerId(),
                            tabBannerDTO.getTabKeyword(), tabBannerDTO.getTabId());
                    if (tabBannerDTO.getTabDataModified() != serverObj.getTabDataModified()) {
                        VaultDatabaseHelper.getInstance(context.getApplicationContext()).updateTabData(serverObj);

                        url = GlobalConstants.FEATURED_API_URL + "userId=" + AppController.getInstance().
                                getModelFacade().getLocalModel().getUserId();
                        arrList.clear();
                        arrList.addAll(AppController.getInstance().getServiceManager().getVaultService().
                                getVideosListFromServer(url));
                        VaultDatabaseHelper.getInstance(getActivity().getApplicationContext()).insertVideosInDatabase(arrList);

                        url = GlobalConstants.GET_TRENDING_PLAYLIST_URL + "userId=" + AppController.getInstance().
                                getModelFacade().getLocalModel().getUserId();
                        trendingArraylist.clear();
                        trendingArraylist.addAll(AppController.getInstance().getServiceManager().getVaultService().
                                getVideosListFromServer(url));
                        VaultDatabaseHelper.getInstance(getActivity().getApplicationContext()).
                                insertTrendingVideosInDatabase(trendingArraylist);
                        isTabDataUpdated = true;
                    }

                    if (serverObj != null) {
                        if ((tabBannerDTO.getBannerModified() != serverObj.getBannerModified()) ||
                                (tabBannerDTO.getBannerCreated() != serverObj.getBannerCreated())) {
                            File imageFile = ImageLoader.getInstance().getDiscCache().
                                    get(tabBannerDTO.getBannerURL());
                            if (imageFile.exists()) {
                                imageFile.delete();
                            }
                            MemoryCacheUtils.removeFromCache(tabBannerDTO.getBannerURL(),
                                    ImageLoader.getInstance().getMemoryCache());

                            VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                    updateTabBannerData(serverObj);
                            isBannerUpdated = true;
                        }
                    }
                }

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

                    if(progressBar != null)
                    {
                        progressBar.setVisibility(View.GONE);
                    }

                    // ------- update BannerImage---------------------


                    mRecyclerViewItems.add(0, new VideoDTO());
                    if (tabBannerDTO != null) {
                        tabBannerDTO = VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                                .getLocalTabBannerDataByTabId(tabBannerDTO.getTabId());
                        VideoDTO videoDTOBanner = new VideoDTO();
                        videoDTOBanner.setVideoStillUrl(tabBannerDTO.getBannerURL());
                        if (tabBannerDTO.isBannerActive()) {
                            mRecyclerViewItems.add(1, videoDTOBanner);
                            AppController.getInstance().getModelFacade().getLocalModel().setBannerActivated(true);
                        } else {
                            mRecyclerViewItems.remove(1);
                            AppController.getInstance().getModelFacade().getLocalModel().setBannerActivated(false);
                        }
                    }

                    for (int i = 0; i < mRecyclerViewItems.size(); i++) {
                        if ((i + 1) % 3 == 0) {
                            mRecyclerViewItems.add(i, new VideoDTO());
                        }
                    }
                    if (adapter != null) {
                        if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getTrendingVideoCount() > 0) {
                            trendingArraylist.clear();
                            trendingArraylist.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getAllTrendingVideoList());
                        }
                        adapter = new FilterSubtypesAdapter(mContext, mRecyclerViewItems, trendingArraylist, HomeFragment.this);
                        mRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }


                } else {
                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
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

            mRecyclerViewItems.clear();
            mRecyclerViewItems.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getVideoList(GlobalConstants.OKF_FEATURED));

            if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getTrendingVideoCount() > 0) {
                trendingArraylist.clear();
                trendingArraylist.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getAllTrendingVideoList());

            }

            if(progressBar != null)
            {
                progressBar.setVisibility(View.GONE);
            }

            Collections.sort(mRecyclerViewItems, new Comparator<VideoDTO>() {

                @Override
                public int compare(VideoDTO lhs, VideoDTO rhs) {
                    // TODO Auto-generated method stub
                    return Integer.valueOf(lhs.getVideoIndex())
                            .compareTo(Integer.valueOf(rhs.getVideoIndex()));
                }
            });
            long tabId = AppController.getInstance().getModelFacade().getLocalModel().getTabId();
            tabBannerDTO = VaultDatabaseHelper.getInstance(getActivity()).getLocalTabBannerDataByTabId(Long.valueOf(tabId));

            mRecyclerViewItems.add(0, new VideoDTO());
            VideoDTO videoDTOBanner = new VideoDTO();
            videoDTOBanner.setVideoStillUrl(tabBannerDTO.getBannerURL());
            if (tabBannerDTO.isBannerActive()) {
                mRecyclerViewItems.add(1, videoDTOBanner);
                AppController.getInstance().getModelFacade().getLocalModel().setBannerActivated(true);
            } else {
                mRecyclerViewItems.remove(1);
                AppController.getInstance().getModelFacade().getLocalModel().setBannerActivated(false);
            }
            for (int i = 0; i < mRecyclerViewItems.size(); i++) {
                if ((i + 1) % 3 == 0) {
                    mRecyclerViewItems.add(i, new VideoDTO());
                }
            }
            adapter = new FilterSubtypesAdapter(mContext, mRecyclerViewItems, trendingArraylist, HomeFragment.this);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layoutManager);

        }
    }

    HomeResponseReceiver receiver;

    private void getFeatureDataFromDataBase() {
        final AsyncTask<Void, Void, Void> mDbTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if(progressBar != null)
                {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    IntentFilter filter = new IntentFilter(HomeFragment.HomeResponseReceiver.ACTION_RESP);
                    filter.addCategory(Intent.CATEGORY_DEFAULT);
                    receiver = new HomeFragment.HomeResponseReceiver();
                    mContext.registerReceiver(receiver, filter);


                    if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getVideoCount() > 0) {
                        mRecyclerViewItems.clear();
                        mRecyclerViewItems.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getVideoList(GlobalConstants.OKF_FEATURED));

                    }
                    if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getTrendingVideoCount() > 0) {
                        trendingArraylist.clear();
                        trendingArraylist.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).getAllTrendingVideoList());
                    }


                    Collections.sort(mRecyclerViewItems, new Comparator<VideoDTO>() {

                        @Override
                        public int compare(VideoDTO lhs, VideoDTO rhs) {
                            // TODO Auto-generated method stub

                            return Integer.valueOf(lhs.getVideoIndex())
                                    .compareTo(Integer.valueOf(rhs.getVideoIndex()));
                        }
                    });

                    System.out.println("featuredVideoList doInBackground : " + mRecyclerViewItems.size());
                    mRecyclerViewItems.add(0, new VideoDTO());
                    VideoDTO videoDTOBanner = new VideoDTO();
                    videoDTOBanner.setVideoStillUrl(tabBannerDTO.getBannerURL());
                    if(tabBannerDTO.isBannerActive()) {
                        mRecyclerViewItems.add(1, videoDTOBanner);
                        AppController.getInstance().getModelFacade().getLocalModel().setBannerActivated(true);
                    }else
                    {
                        mRecyclerViewItems.remove(1);
                        AppController.getInstance().getModelFacade().getLocalModel().setBannerActivated(false);
                    }
                    for (int i = 0; i < mRecyclerViewItems.size(); i++) {
                        if ((i + 1) % 3 == 0) {
                            mRecyclerViewItems.add(i, new VideoDTO());
                        }
                    }
                    adapter = new FilterSubtypesAdapter(mContext, mRecyclerViewItems, trendingArraylist, HomeFragment.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if(progressBar != null)
                {
                    progressBar.setVisibility(View.GONE);
                }

                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);
                // ------- addBannerImage---------------------

            }
        };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void playFacbookVideo(String videoId) {

        try {
            if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).isVideoAvailableInDB(videoId)) {
                VideoDTO videoDTO = VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                        .getVideoDataByVideoId(videoId);
                AppController.getInstance().getModelFacade().getLocalModel().setVideoId(null);
                if (Utils.isInternetAvailable(mContext)) {
                    if (videoDTO != null) {
                        if (videoDTO.getVideoLongUrl() != null) {
                            //  if (videoDTO.getVideoLongUrl().length() > 0 && !videoDTO.getVideoLongUrl().toLowerCase().equals("none")) {
                            String videoCategory = GlobalConstants.FEATURED;
                            Intent intent = new Intent(mContext,
                                    VideoInfoActivity.class);
                            intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                            intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, videoDTO.getPlaylistReferenceId());
                            intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDTO);
                            startActivity(intent);
                            mContext.overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                        }
                    } else {
                        ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                    }
                }
            } else {
                System.out.println("isvideo available: " + VaultDatabaseHelper.getInstance(mContext
                        .getApplicationContext()).isVideoAvailableInDB(videoId));
                try {
                    /*VideoPlayTask videoPlayTask = new VideoPlayTask();
                    videoPlayTask.execute(videoId);*/
                    VideoPlayTask videoPlayTask = new VideoPlayTask(mContext, pDialog, GlobalConstants.FEATURED);
                    videoPlayTask.execute(videoId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            AppController.getInstance().getModelFacade().getLocalModel().setVideoId(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
