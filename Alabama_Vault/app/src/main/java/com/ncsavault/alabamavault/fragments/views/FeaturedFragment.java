package com.ncsavault.alabamavault.fragments.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.ncsavault.alabamavault.AsyncTask.VideoPlayTask;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.VideoContentListAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.TabBannerDTO;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.VideoDataTaskModel;
import com.ncsavault.alabamavault.service.VideoDataService;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.AbstractView;
import com.ncsavault.alabamavault.views.MainActivity;
import com.ncsavault.alabamavault.views.VideoInfoActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author aqeeb.pathan
 */
public class FeaturedFragment extends BaseFragment implements AbstractView {
    public ListView listViewFeaturedVideo;
    public ImageView bannerCacheableImageView;
    public TextView tvsearchRecordsNotAvailable;
    public VideoContentListAdapter videoListAdapter;
    public ArrayList<VideoDTO> featuredVideoList = new ArrayList<>();
    public ProgressBar progressBar;
    SearchView searchView;
    public boolean isLastPageLoaded = false;
    public String url = "";
    Activity mActivity;
    private PullRefreshLayout refreshLayout;
    private TabBannerDTO tabBannerDTO = null;

    PullRefreshTask pullTask;
    FeaturedResponseReceiver receiver;
    private ProgressDialog pDialog = null;
    private ProgressBar mBannerProgressBar;
    private LinearLayout bannerLayout;
    private String tabId;
    private CountDownTimer countDownTimer;
    private AdView mAdView = null;
    private RelativeLayout relativeLayout = null;
    private boolean isInitilizeFirst = false;
    private VideoDataTaskModel mVideoDataTaskModel;

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (mActivity != null && receiver != null) {
            mActivity.unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        tabId = bundle.getString("tabId");
        String videoUrl = AppController.getInstance().getModelFacade().getLocalModel().getVideoUrl();
        String videoId = AppController.getInstance().getModelFacade().getLocalModel().getVideoId();

        // tabBannerDTO = (TabBannerDTO) bundle.getSerializable("tabObject");
        tabBannerDTO = VaultDatabaseHelper.getInstance(getActivity()).getLocalTabBannerDataByTabId(Long.valueOf(tabId));

        if (videoUrl != null || (videoId != null && videoId != "0")) {
            if (videoUrl == null) {
                videoUrl = videoId;
            }
            playFacbookVideo(videoUrl);

            AppController.getInstance().getModelFacade().getLocalModel().setVideoUrl(null);
            // bundle.putString("eventObject",null);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
//        Utils.hideSystemUI(getActivity());
        View view = inflater.inflate(R.layout.featured_video_layout, container,
                false);
        if (getActivity() != null)
            mActivity = getActivity();

        setHasOptionsMenu(true);
        isLastPageLoaded = false;
//        isFreshDataLoading = true;

        // -------initializing views-------------
        initComponents(view);

        Utils.getInstance().adMobBannerAdvertising(mActivity, view, relativeLayout, mAdView);
        System.out.println("Featured Video List Count : " + featuredVideoList.size());

        setHasOptionsMenu(true);

        getFeatureDataFromDataBase();

        return view;
    }


    private void getFeatureDataFromDataBase() {
        final AsyncTask<Void, Void, Void> mDbTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressBar != null) {
                    if (featuredVideoList.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    IntentFilter filter = new IntentFilter(FeaturedResponseReceiver.ACTION_RESP);
                    filter.addCategory(Intent.CATEGORY_DEFAULT);
                    receiver = new FeaturedResponseReceiver();
                    mActivity.registerReceiver(receiver, filter);

                    featuredVideoList.clear();
                    featuredVideoList.addAll(VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).getVideoList(GlobalConstants.OKF_FEATURED));

                    Collections.sort(featuredVideoList, new Comparator<VideoDTO>() {

                        @Override
                        public int compare(VideoDTO lhs, VideoDTO rhs) {
                            // TODO Auto-generated method stub

                            return Integer.valueOf(lhs.getVideoIndex())
                                    .compareTo(Integer.valueOf(rhs.getVideoIndex()));
                        }
                    });
                    System.out.println("featuredVideoList doInBackground : " + featuredVideoList.size());
                    videoListAdapter = new VideoContentListAdapter(featuredVideoList, mActivity, 1, false);

                    if (!GlobalConstants.SEARCH_VIEW_QUERY.isEmpty()) {
                        videoListAdapter.filter(GlobalConstants.SEARCH_VIEW_QUERY.toLowerCase(Locale
                                .getDefault()));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                listViewFeaturedVideo.setAdapter(videoListAdapter);
                isInitilizeFirst = true;

                if (progressBar != null) {
                    if (featuredVideoList.size() == 0 /*&& VideoDataService.isServiceRunning*/) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }

                // ------- addBannerImage---------------------
                if (tabBannerDTO != null)
                    Utils.addBannerImage(bannerCacheableImageView, bannerLayout, tabBannerDTO, mActivity);

                if (progressBar != null) {
                    if (progressBar.getVisibility() == View.GONE || progressBar.getVisibility() == View.INVISIBLE) {
                        refreshLayout.setEnabled(true);
                        refreshLayout.setOnRefreshListener(refreshListener);
                    }
                }

                //visibility of scroll bar set dynamically list height
                //GK Utils.setVisibilityOfScrollBarHeightForNormalList(mActivity, GlobalConstants.SEARCH_VIEW_QUERY, listViewFeaturedVideo);
            }
        };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        registerEvents();
        updateBannerImage();
    }

    private void updateBannerImage() {
        countDownTimer = new CountDownTimer(GlobalConstants.AUTO_REFRESH_INTERVAL, GlobalConstants.AUTO_REFRESH_INTERVAL) {

            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
                tabBannerDTO = VaultDatabaseHelper.getInstance(getActivity()).getLocalTabBannerDataByTabId(Long.valueOf(tabId));
                if (tabBannerDTO != null) {
                    tabBannerDTO = VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).getLocalTabBannerDataByTabId(tabBannerDTO.getTabId());
                    if (tabBannerDTO != null) {
                        Utils.addBannerImagePullToRefresh(bannerCacheableImageView, bannerLayout, tabBannerDTO, mActivity, mBannerProgressBar);
                    }
                }
            }

            public void onFinish() {
                if (countDownTimer != null) {
                    countDownTimer.start();
                }
            }

        }.start();
    }

    PullRefreshLayout.OnRefreshListener refreshListener = new PullRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (Utils.isInternetAvailable(mActivity.getApplicationContext())) {
                if (MainActivity.autoRefreshProgressBar.getVisibility() == View.VISIBLE) {
                    refreshLayout.setEnabled(false);
                    refreshLayout.setRefreshing(false);
                } else {

                    refreshLayout.setEnabled(true);
                    refreshLayout.setRefreshing(true);
                    pullTask = new PullRefreshTask();
                    pullTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

            } else {
                ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                refreshLayout.setRefreshing(false);
            }
        }
    };

    private void initComponents(View view) {
        // TODO Auto-generated method stub
        listViewFeaturedVideo = (ListView) view
                .findViewById(R.id.featured_list);

        listViewFeaturedVideo.setClickable(true);
        listViewFeaturedVideo.setFastScrollEnabled(true);
        mBannerProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mBannerProgressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            mBannerProgressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.progress_large_material, null));
        }
        bannerCacheableImageView = (ImageView) view
                .findViewById(R.id.img_featured_banner);
        bannerLayout = (LinearLayout) view
                .findViewById(R.id.ll_banner_block);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            System.out.println("progress bar not showing ");
            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.progress_large_material, null));
        }

        tvsearchRecordsNotAvailable = (TextView) view.findViewById(R.id.tvSearchStatus);

        refreshLayout = (PullRefreshLayout) view.findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        relativeLayout = (RelativeLayout) view.findViewById(R.id.admob_banner_frame_layout);
        mAdView = (AdView) view.findViewById(R.id.adView);
    }

    @Override
    public void onResume() {
        if (mAdView != null) {
            mAdView.resume();
        }
        super.onResume();
        try {
            //yp
            System.out.println("featuredVideoList onresume : " + featuredVideoList.size() + " isInitilizeFirst " + isInitilizeFirst);
//            if (featuredVideoList.size() == 0) {
//                progressBar.setVisibility(View.GONE);
//                if(isInitilizeFirst) {
//                    tvsearchRecordsNotAvailable.setText("No records found");
//                    tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
//                }
//            }else {
//                progressBar.setVisibility(View.GONE);
//                tvsearchRecordsNotAvailable.setVisibility(View.INVISIBLE);
//            }
//        if (videoListAdapter != null) {
//            videoListAdapter.notifyDataSetChanged();
//        }
//        if (progressBar != null && refreshLayout != null) {
//
//            if (progressBar.getVisibility() == View.GONE || progressBar.getVisibility() == View.INVISIBLE) {
//                refreshLayout.setEnabled(true);
//                refreshLayout.setOnRefreshListener(refreshListener);
//            }
//        }


            if (videoListAdapter != null) {
                videoListAdapter.notifyDataSetChanged();
            }
            if (progressBar != null && refreshLayout != null) {


                if (progressBar.getVisibility() == View.GONE || progressBar.getVisibility() == View.INVISIBLE) {
                    refreshLayout.setEnabled(true);
                    refreshLayout.setOnRefreshListener(refreshListener);
                }
            }

            if (featuredVideoList != null && featuredVideoList.size() == 0) {
                System.out.println("progress bar vbisible on resume");
                if(GlobalConstants.SEARCH_VIEW_QUERY.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            } else {
                progressBar.setVisibility(View.GONE);
            }

            if(featuredVideoList.size()>0)
            {
                progressBar.setVisibility(View.GONE);
            }

//yp
       /* if (featuredVideoList != null && featuredVideoList.size() == 0) {
            System.out.println("progress bar vbisible on resume");
            if(GlobalConstants.SEARCH_VIEW_QUERY.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
            }
        } else {
            progressBar.setVisibility(View.GONE);
        }*/

            Utils.getInstance().gethideKeyboard(mActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onPause() {

        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
        try {
            if (receiver != null && mActivity != null) {
                //mActivity.unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void registerEvents() {
        // TODO Auto-generated method stub
        listViewFeaturedVideo.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                Utils.getInstance().gethideKeyboard(mActivity);
                return false;
            }
        });

        listViewFeaturedVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view1, int pos, long id) {
                System.out.println("position : " + pos);
                Utils.getInstance().gethideKeyboard(mActivity);
                if (Utils.isInternetAvailable(mActivity)) {
                    if (featuredVideoList.get(pos).getVideoLongUrl() != null) {
                        if (featuredVideoList.get(pos).getVideoLongUrl().length() > 0 && !featuredVideoList.get(pos).getVideoLongUrl().toLowerCase().equals("none")) {
                            String videoCategory = GlobalConstants.FEATURED;
                            Intent intent = new Intent(mActivity,
                                    VideoInfoActivity.class);
                            intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                            intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, featuredVideoList.get(pos).getPlaylistReferenceId());
                            intent.putExtra(GlobalConstants.VIDEO_OBJ, featuredVideoList.get(pos));
                            GlobalConstants.LIST_FRAGMENT = new CoachesEraFragment();
                            GlobalConstants.LIST_ITEM_POSITION = pos;
                            startActivity(intent);
                            mActivity.overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                        } else {
                            ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                        }
                    } else {
                        ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                    }
                } else {
                    ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });

        listViewFeaturedVideo.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                /*if (((MainActivity) mActivity).progressDialog != null)
                    if (((MainActivity) mActivity).progressDialog.isShowing())
                        ((MainActivity) mActivity).progressDialog.dismiss();*/

            }
        });

        listViewFeaturedVideo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()

                                                         {
                                                             @Override
                                                             public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                                                                            long id) {
                                                                 //          ((MainActivity) mActivity).makeShareDialog(featuredVideoList.get(position).getVideoId(),featuredVideoList.get(position).getVideoLongUrl(), featuredVideoList.get(position).getVideoShortUrl(), featuredVideoList.get(position).getVideoStillUrl(), featuredVideoList.get(position).getVideoLongDescription(), featuredVideoList.get(position).getVideoName(), getActivity());
                                                                 return true;
                                                             }
                                                         }

        );

        listViewFeaturedVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (listViewFeaturedVideo != null) {
                            listViewFeaturedVideo.setFastScrollAlwaysVisible(false);
                        }
                    }
                }, 2000);
                return false;
            }
        });


        bannerCacheableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bannerImageView();
            }
        });

    }

    private void bannerImageView() {
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
                            if (VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).isVideoAvailableInDB(videoMap.get("VideoId").toString())) {
                                VideoDTO videoDTO = VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).getVideoDataByVideoId(videoMap.get("VideoId").toString());
                                if (Utils.isInternetAvailable(mActivity)) {
                                    if (videoDTO != null) {
                                        if (videoDTO.getVideoLongUrl() != null) {
                                            //  if (videoDTO.getVideoLongUrl().length() > 0 && !videoDTO.getVideoLongUrl().toLowerCase().equals("none")) {
                                            String videoCategory = GlobalConstants.FEATURED;
                                            Intent intent = new Intent(mActivity,
                                                    VideoInfoActivity.class);
                                            intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                                            intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, videoDTO.getPlaylistReferenceId());
                                            intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDTO);
                                            startActivity(intent);
                                            mActivity.overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                                        }
                                    } else {
                                        ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                    }
                                } else {
                                    ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                                }
                            } else {
                                //Make an API call to get video data
                                pDialog = new ProgressDialog(mActivity, R.style.CustomDialogTheme);
                                pDialog.show();
                                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(mActivity));
                                pDialog.setCanceledOnTouchOutside(false);
                                pDialog.setCancelable(false);

                                mVideoDataTaskModel = AppController.getInstance().getModelFacade().getRemoteModel()
                                        .getVideoDataTaskModel();
                                mVideoDataTaskModel.registerView(FeaturedFragment.this);

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
    public void onBackPress() {
        // TODO Auto-generated method stub
        super.onBackPress();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final MenuItem menuItem = menu.findItem(R.id.action_search);

        //  progressBarItem = menu.findItem(R.id.miActionProgress);

        searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconified(true);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                System.out.println("i am here");
                // TODO Auto-generated method stub
                boolean isRecordsAvailableInDb = false;
                ArrayList<VideoDTO> videoList = VaultDatabaseHelper.getInstance(mActivity).getVideoList(GlobalConstants.OKF_FEATURED);


                if (videoList.size() > 0) {
                    isRecordsAvailableInDb = true;
                }
//                    listViewFeaturedVideo.setFastScrollAlwaysVisible(true);
//                }else
//                    listViewFeaturedVideo.setFastScrollAlwaysVisible(false);


                GlobalConstants.SEARCH_VIEW_QUERY = newText;
                if (videoListAdapter != null /*&& !newText.equals("")*/) {
                    videoListAdapter.filter(newText.toLowerCase(Locale
                            .getDefault()));
                    Collections.sort(featuredVideoList, new Comparator<VideoDTO>() {

                        @Override
                        public int compare(VideoDTO lhs, VideoDTO rhs) {
                            // TODO Auto-generated method stub
                            return Integer.valueOf(lhs.getVideoIndex())
                                    .compareTo(Integer.valueOf(rhs.getVideoIndex()));
                        }
                    });

                    videoListAdapter.notifyDataSetChanged();
                }
                if (progressBar.isShown() && featuredVideoList.size() > 0) {
                    progressBar.setVisibility(View.GONE);
                }
                //set Visibility of scroll bar runtime
                if (listViewFeaturedVideo != null) {
                    //gk Utils.setVisibilityOfScrollBarHeightForNormalList(mActivity, GlobalConstants.SEARCH_VIEW_QUERY, listViewFeaturedVideo);
                }

                if (!newText.isEmpty()) {
                    if ((featuredVideoList.size() == 0 && isRecordsAvailableInDb) || (featuredVideoList.size() == 0 && !VideoDataService.isServiceRunning)) {
                        tvsearchRecordsNotAvailable.setText(GlobalConstants.NO_RECORDS_FOUND);
                        tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        listViewFeaturedVideo.setFastScrollAlwaysVisible(false);
                    } else {
//                        tvsearchRecordsNotAvailable.setVisibility(View.INVISIBLE);
                        if (AppController.getInstance().getModelFacade().getLocalModel().getmListViewHeight() >= AppController.getInstance().getModelFacade().getLocalModel().getmDisplayHeight()) {
                            tvsearchRecordsNotAvailable.setVisibility(View.INVISIBLE);
                            listViewFeaturedVideo.setFastScrollAlwaysVisible(true);
                        }
                    }
                } else {

                    if (featuredVideoList.size() > 0) {
                        tvsearchRecordsNotAvailable.setVisibility(View.INVISIBLE);
                        if (AppController.getInstance().getModelFacade().getLocalModel().getmListViewHeight() >= AppController.getInstance().getModelFacade().getLocalModel().getmDisplayHeight()) {
                            listViewFeaturedVideo.setFastScrollAlwaysVisible(true);
                        }
                    }
                }
                return false;
            }


        });

//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//
//                if (featuredVideoList.size() > 0) {
//                    if (progressBar.isShown()) {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                }
//                else if (featuredVideoList.size() == 0) {
////                    if (progressBar.isShown()) {
////                        progressBar.setVisibility(View.GONE);
////                        tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
////                        tvsearchRecordsNotAvailable.setText(GlobalConstants.NO_RECORDS_FOUND);
////                    }
//                }
//
//                return false;
//            }
//        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search: {
                System.out.println("dfdfnhfndknldn");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();


//        searchView.setSubmitButtonEnabled(true);
        String input = searchView.getQuery().toString();
        if (input != null && !input.equalsIgnoreCase("")) {
            searchView.setIconified(false);
        } else {
            searchView.setIconified(true);
        }
        searchView.clearFocus();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            /*if (bannerCacheableImageView != null && mActivity != null) {
                // ---- addBannerImage--------
                *//*Utils.addVolleyBanner(bannerCacheableImageView,
                        GlobalConstants.URL_FEATUREDBANNER, mActivity);*//*
                Utils.addBannerImage(bannerCacheableImageView, GlobalConstants.URL_FEATUREDBANNER);
            }*/
            // it is used to track the ecent of opponennts fragment
            System.out.println("featuredVideoList setUserVisibleHint : " + featuredVideoList.size() + " isInitilizeFirst " + isInitilizeFirst);
            FlurryAgent.onEvent(GlobalConstants.FEATURED);
//                 if (progressBar != null) {
//                if (featuredVideoList != null && featuredVideoList.size() == 0) {
//                    progressBar.setVisibility(View.GONE);
//                    if(isInitilizeFirst) {
//                        tvsearchRecordsNotAvailable.setText("No records found");
//                        tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    progressBar.setVisibility(View.GONE);
//                    tvsearchRecordsNotAvailable.setVisibility(View.INVISIBLE);
//                }
//            }

            if(progressBar != null) {
                if (featuredVideoList != null && featuredVideoList.size() == 0) {
                    System.out.println("progress bar vbisible on setUser");
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

        }
    }


    @Override
    public void update() {
        try {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mVideoDataTaskModel != null && mVideoDataTaskModel.getState() == BaseModel.STATE_SUCCESS) {
                        if (mVideoDataTaskModel.getVideoDTO().size() > 0) {
                            VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).insertVideosInDatabase(mVideoDataTaskModel.getVideoDTO());
                            if (Utils.isInternetAvailable(mActivity)) {
                                if (mVideoDataTaskModel.getVideoDTO().get(0).getVideoLongUrl() != null) {
                                    if (mVideoDataTaskModel.getVideoDTO().get(0).getVideoLongUrl().length() > 0 && !mVideoDataTaskModel.getVideoDTO()
                                            .get(0).getVideoLongUrl().toLowerCase().equals("none")) {
                                        String videoCategories = GlobalConstants.FEATURED;
                                        Intent intent = new Intent(mActivity,
                                                VideoInfoActivity.class);
                                        intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategories);
                                        intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, mVideoDataTaskModel.getVideoDTO().get(0).getPlaylistReferenceId());
                                        intent.putExtra(GlobalConstants.VIDEO_OBJ, mVideoDataTaskModel.getVideoDTO().get(0));
                                        mActivity.startActivity(intent);
                                        mActivity.overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                                    } else {
                                        ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                    }
                                } else {
                                    ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                }
                            } else {
                                ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
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


    public class PullRefreshTask extends AsyncTask<Void, Void, ArrayList<VideoDTO>> {

        public boolean isBannerUpdated = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (listViewFeaturedVideo != null) {
                listViewFeaturedVideo.setEnabled(false);
                listViewFeaturedVideo.setFastScrollAlwaysVisible(false);
                listViewFeaturedVideo.setVerticalScrollBarEnabled(false);
                listViewFeaturedVideo.setFastScrollEnabled(false);
            }

            refreshLayout.setRefreshing(true);
            if (videoListAdapter != null) {
                videoListAdapter.isPullRefreshInProgress = true;
                videoListAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected ArrayList<VideoDTO> doInBackground(Void... params) {
            ArrayList<VideoDTO> arrList = new ArrayList<VideoDTO>();
            try {
                String url = GlobalConstants.FEATURED_API_URL + "userId=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                arrList.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                if (arrList.size() > 0) {
                    VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).removeRecordsByTab(GlobalConstants.OKF_FEATURED);
                    VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).insertVideosInDatabase(arrList);
                }

                //Update Banner Data
                if (tabBannerDTO != null) {
                    TabBannerDTO serverObj = AppController.getInstance().getServiceManager().getVaultService().getTabBannerDataById(tabBannerDTO.getTabBannerId(), tabBannerDTO.getTabKeyword(), tabBannerDTO.getTabId());
                    if (serverObj != null) {
                        if ((tabBannerDTO.getBannerModified() != serverObj.getBannerModified()) || (tabBannerDTO.getBannerCreated() != serverObj.getBannerCreated())) {
                            File imageFile = ImageLoader.getInstance().getDiscCache().get(tabBannerDTO.getBannerURL());
                            if (imageFile.exists()) {
                                imageFile.delete();
                            }
                            MemoryCacheUtils.removeFromCache(tabBannerDTO.getBannerURL(), ImageLoader.getInstance().getMemoryCache());

                            VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).updateTabBannerData(serverObj);
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
                    if (result.size() > 0) {
                        featuredVideoList.clear();
                        featuredVideoList.addAll(VaultDatabaseHelper.getInstance(mActivity).getVideoList(GlobalConstants.OKF_FEATURED));

                        Collections.sort(featuredVideoList, new Comparator<VideoDTO>() {

                            @Override
                            public int compare(VideoDTO lhs, VideoDTO rhs) {
                                // TODO Auto-generated method stub
                                return Integer.valueOf(lhs.getVideoIndex())
                                        .compareTo(Integer.valueOf(rhs.getVideoIndex()));
                            }
                        });

                        if (videoListAdapter != null) {
                            videoListAdapter.listSearch.clear();
                            videoListAdapter.listSearch.addAll(result);
                            videoListAdapter.notifyDataSetChanged();
                        } else {
                            videoListAdapter = new VideoContentListAdapter(featuredVideoList, mActivity, 1, false);
                            listViewFeaturedVideo.setAdapter(videoListAdapter);
                        }
                        if (!GlobalConstants.SEARCH_VIEW_QUERY.isEmpty()) {
                            videoListAdapter.filter(GlobalConstants.SEARCH_VIEW_QUERY.toLowerCase(Locale
                                    .getDefault()));
                            videoListAdapter.notifyDataSetChanged();
                        }
                        if (videoListAdapter.getCount() == 0) {
                            tvsearchRecordsNotAvailable.setText(GlobalConstants.NO_RECORDS_FOUND);
                            tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            listViewFeaturedVideo.setFastScrollAlwaysVisible(false);
                        }
                    }
                    if (result.size() == 0) {
                        featuredVideoList.clear();
                        tvsearchRecordsNotAvailable.setText(GlobalConstants.NO_RECORDS_FOUND);
                        tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        listViewFeaturedVideo.setFastScrollAlwaysVisible(false);

                        if (videoListAdapter != null) {
                            videoListAdapter.listSearch.clear();
                            videoListAdapter.listSearch.addAll(featuredVideoList);
                            videoListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        tvsearchRecordsNotAvailable.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                    videoListAdapter.isPullRefreshInProgress = false;
                    if (isBannerUpdated)
                        if (tabBannerDTO != null) {
                            tabBannerDTO = VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).getLocalTabBannerDataByTabId(tabBannerDTO.getTabId());
                            if (tabBannerDTO != null)
                                Utils.addBannerImagePullToRefresh(bannerCacheableImageView, bannerLayout, tabBannerDTO, mActivity, mBannerProgressBar);
                        }
                    refreshLayout.setRefreshing(false);
                    if (listViewFeaturedVideo != null) {
                        listViewFeaturedVideo.setEnabled(true);
                        listViewFeaturedVideo.setFastScrollAlwaysVisible(true);
                        listViewFeaturedVideo.setVerticalScrollBarEnabled(true);
                        listViewFeaturedVideo.setFastScrollEnabled(true);
                    }
                } else {
                    ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
                    refreshLayout.setRefreshing(false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class FeaturedResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP =
                "Message Processed";

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                featuredVideoList.clear();
                featuredVideoList.addAll(VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).getVideoList(GlobalConstants.OKF_FEATURED));

                if (progressBar != null) {
                    if (featuredVideoList.size() == 0) {
                        progressBar.setVisibility(View.GONE);

                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }

                Collections.sort(featuredVideoList, new Comparator<VideoDTO>() {

                    @Override
                    public int compare(VideoDTO lhs, VideoDTO rhs) {
                        // TODO Auto-generated method stub
                        return Integer.valueOf(lhs.getVideoIndex())
                                .compareTo(Integer.valueOf(rhs.getVideoIndex()));
                    }
                });


                videoListAdapter = new VideoContentListAdapter(featuredVideoList, mActivity, 1, false);
                if (!GlobalConstants.SEARCH_VIEW_QUERY.isEmpty()) {
                    videoListAdapter.filter(GlobalConstants.SEARCH_VIEW_QUERY.toLowerCase(Locale
                            .getDefault()));
                }
                listViewFeaturedVideo.setAdapter(videoListAdapter);

                if (featuredVideoList.size() == 0 /*&& VideoDataService.isServiceRunning*/) {
                    if (!GlobalConstants.SEARCH_VIEW_QUERY.isEmpty()) {

                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                }

                if (progressBar != null) {
                    if (progressBar.getVisibility() == View.GONE || progressBar.getVisibility() == View.INVISIBLE) {
                        refreshLayout.setEnabled(true);
                        refreshLayout.setOnRefreshListener(refreshListener);
                    }
                }

                tabBannerDTO = VaultDatabaseHelper.getInstance(getActivity()).getLocalTabBannerDataByTabId(Long.valueOf(tabId));
                System.out.println("tabBannerDTO feature " + tabBannerDTO);
                if (tabBannerDTO != null) {
                    tabBannerDTO = VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).getLocalTabBannerDataByTabId(tabBannerDTO.getTabId());
                    if (tabBannerDTO != null)
                        Utils.addBannerImagePullToRefresh(bannerCacheableImageView, bannerLayout, tabBannerDTO, mActivity, mBannerProgressBar);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void playFacbookVideo(String videoId) {

        try {
            if (VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).isVideoAvailableInDB(videoId)) {
                VideoDTO videoDTO = VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).getVideoDataByVideoId(videoId);
                AppController.getInstance().getModelFacade().getLocalModel().setVideoId(null);
                if (Utils.isInternetAvailable(mActivity)) {
                    if (videoDTO != null) {
                        if (videoDTO.getVideoLongUrl() != null) {
                            //  if (videoDTO.getVideoLongUrl().length() > 0 && !videoDTO.getVideoLongUrl().toLowerCase().equals("none")) {
                            String videoCategory = GlobalConstants.FEATURED;
                            Intent intent = new Intent(mActivity,
                                    VideoInfoActivity.class);
                            intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                            intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, videoDTO.getPlaylistReferenceId());
                            intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDTO);
                            startActivity(intent);
                            mActivity.overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                        }
                    } else {
                        ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                    }
                }
            } else {
                System.out.println("isvideo available: " + VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).isVideoAvailableInDB(videoId));
                try {
                    /*VideoPlayTask videoPlayTask = new VideoPlayTask();
                    videoPlayTask.execute(videoId);*/
                    VideoPlayTask videoPlayTask = new VideoPlayTask(mActivity, pDialog, GlobalConstants.FEATURED);
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

