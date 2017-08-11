package com.ncsavault.alabamavault.fragments.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.ncsavault.alabamavault.AsyncTask.PullRefreshTask;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.VideoContentHeaderListAdapter;
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
import com.baoyz.widget.PullRefreshLayout;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * @author gauravkumar.singh
 */
public class GamesFragment extends BaseFragment implements PullRefreshTask.UpdateTabDtoInfo, AbstractView {

    public StickyListHeadersListView stickyListHeadersListView;
    public ImageView bannerCacheableImageView;
    public VideoContentHeaderListAdapter videoHeaderListAdapter;
    public ArrayList<VideoDTO> gamesVideoList = new ArrayList<VideoDTO>();
    public TextView tvsearchRecordsNotAvailable;
    public ProgressBar progressBar;
    public SearchView searchView;

    public boolean isLastPageLoaded = false;
    private GamesResponseReceiver receiver;
    PullRefreshLayout refreshLayout;
    public boolean isBannerUpdated = false;

    String url = "";
    Activity mActivity;
    private TabBannerDTO tabBannerDTO;
    private ProgressDialog pDialog;
    private ProgressBar mBannerProgressBar;
    private LinearLayout bannerLayout;
    private String tabId;
    CountDownTimer countDownTimer;
    private AdView mAdView;
    private RelativeLayout relativeLayout;
    VideoDataTaskModel mVideoDataTaskModel;

    @Override
    public void onPause() {
        super.onPause();
        try {
          /*  if (responseReceiver != null && mActivity != null) {
                //    mActivity.unregisterReceiver(receiver);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        try {
            //yp
//            if (gamesVideoList.size() == 0) {
//                progressBar.setVisibility(View.GONE);
//                tvsearchRecordsNotAvailable.setText("No records found");
//                tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
//            } else {
//                progressBar.setVisibility(View.GONE);
//                tvsearchRecordsNotAvailable.setVisibility(View.INVISIBLE);
//            }

            if (videoHeaderListAdapter != null)
                videoHeaderListAdapter.notifyDataSetChanged();
            if (progressBar != null && refreshLayout != null) {
                if (progressBar.getVisibility() == View.GONE || progressBar.getVisibility() == View.INVISIBLE) {
                    refreshLayout.setEnabled(true);
                    refreshLayout.setOnRefreshListener(refreshListener);
                }
            }

            //yp

            if (gamesVideoList != null && gamesVideoList.size() == 0) {
                if (GlobalConstants.SEARCH_VIEW_QUERY.isEmpty()) {

                    progressBar.setVisibility(View.VISIBLE);
                }
            } else {
                progressBar.setVisibility(View.GONE);
            }

            if (gamesVideoList.size() > 0) {
                progressBar.setVisibility(View.GONE);
            }

            Utils.getInstance().gethideKeyboard(mActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle = getArguments();
        tabId = bundle.getString("tabId");

        // tabBannerDTO = (TabBannerDTO) bundle.getSerializable("tabObject");
        tabBannerDTO = VaultDatabaseHelper.getInstance(getActivity()).getLocalTabBannerDataByTabId(Long.valueOf(tabId));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.opponets_coaches_playersfragment_layout, container, false);

        mActivity = getActivity();


        isLastPageLoaded = false;
//        isFreshDataLoading = true;
        // --------Intializing Views---------
        initComponents(v);
        System.out.println("Games Video List Count : " + gamesVideoList.size());
        Utils.getInstance().adMobBannerAdvertising(mActivity, v, relativeLayout, mAdView);
        setHasOptionsMenu(true);

        // ------registerevents---------
        registerEvents();
        getGameDataFromDataBase();

        return v;
    }


    private void getGameDataFromDataBase() {

        AsyncTask<Void, Void, Void> mDbTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (gamesVideoList.size() == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    IntentFilter filter = new IntentFilter(GamesResponseReceiver.ACTION_RESP);
                    filter.addCategory(Intent.CATEGORY_DEFAULT);
                    receiver = new GamesResponseReceiver();
                    mActivity.registerReceiver(receiver, filter);
/*
                    IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
                    filter.addCategory(Intent.CATEGORY_DEFAULT);
                    responseReceiver = new ResponseReceiver(mActivity,gamesVideoList,null,null,
                            progressBar,refreshLayout,tabBannerDTO,bannerCacheableImageView,
                            bannerLayout,mBannerProgressBar,tabId,refreshListener,GlobalConstants.OKF_GAMES,
                            stickyListHeadersListView, videoHeaderListAdapter,true);
                    mActivity.registerReceiver(responseReceiver,filter);*/

                    gamesVideoList.clear();
                    gamesVideoList.addAll(VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).getVideoListForGame(GlobalConstants.OKF_GAMES));

                    Collections.sort(gamesVideoList, new Comparator<VideoDTO>() {

                        @Override
                        public int compare(VideoDTO lhs, VideoDTO rhs) {
                            // TODO Auto-generated method stub
                            return lhs.getPlaylistName().toLowerCase()
                                    .compareTo(rhs.getPlaylistName().toLowerCase());
                        }
                    });
                    videoHeaderListAdapter = new VideoContentHeaderListAdapter(gamesVideoList, mActivity, 1, false, true);
                    if (!GlobalConstants.SEARCH_VIEW_QUERY.isEmpty()) {
                        videoHeaderListAdapter.filter(GlobalConstants.SEARCH_VIEW_QUERY.toLowerCase(Locale
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
                stickyListHeadersListView.setAdapter(videoHeaderListAdapter);
               // stickyListHeadersListView.setFastScrollAlwaysVisible(false);

                if (progressBar != null) {
                    if (gamesVideoList.size() == 0 /*&& VideoDataService.isServiceRunning*/) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
//                if (progressBar != null) {
//                    if (gamesVideoList.size() == 0 && !VideoDataService.isServiceRunning) {
//                        progressBar.setVisibility(View.GONE);
//                        tvsearchRecordsNotAvailable.setText("No records found");
//                        tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
//                    } else {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                }
                // ---- add banner---------
                if (tabBannerDTO != null)
                    Utils.addBannerImage(bannerCacheableImageView, bannerLayout, tabBannerDTO, mActivity);

                if (progressBar.getVisibility() == View.GONE || progressBar.getVisibility() == View.INVISIBLE) {
                    refreshLayout.setEnabled(true);
                    refreshLayout.setOnRefreshListener(refreshListener);
                }
                //visibility of scroll bar set dynamically list height
                Utils.setVisibilityOfScrollBarHeightForHeader(GlobalConstants.SEARCH_VIEW_QUERY, stickyListHeadersListView);
            }
        };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        updateBannerImage();
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
                   /* pullTask = new PullRefreshTask();
                    pullTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
                    PullRefreshTask pullRefreshTask = new PullRefreshTask(mActivity, GlobalConstants.GAMES_API_URL, GlobalConstants.OKF_GAMES,
                            isBannerUpdated, stickyListHeadersListView, refreshLayout, videoHeaderListAdapter,
                            tabBannerDTO, gamesVideoList, progressBar, tvsearchRecordsNotAvailable,
                            bannerCacheableImageView, bannerLayout, mBannerProgressBar, true, GamesFragment.this);
                    pullRefreshTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

            } else {
                ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                refreshLayout.setRefreshing(false);
            }
        }
    };

    private void registerEvents() {
        stickyListHeadersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                ((MainActivity) mActivity).makeShareDialog(gamesVideoList.get(position).getVideoLongUrl(), gamesVideoList.get(position).getVideoShortUrl(), gamesVideoList.get(position).getVideoStillUrl(), gamesVideoList.get(position).getVideoLongDescription(), gamesVideoList.get(position).getVideoName(), getActivity());
                return true;
            }
        });


        // TODO Auto-generated method stub
        stickyListHeadersListView
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int pos, long arg3) {

                        if (!videoHeaderListAdapter.isPullRefreshInProgress) {
                            // TODO Auto-generated method stub
                           Utils.getInstance().gethideKeyboard(mActivity);
                            if (Utils.isInternetAvailable(mActivity)) {
                                if (gamesVideoList.get(pos).getVideoLongUrl() != null) {
                                    if (gamesVideoList.get(pos).getVideoLongUrl().length() > 0 && !gamesVideoList.get(pos).getVideoLongUrl().toLowerCase().equals("none")) {
                                        String videoCategory = GlobalConstants.GAMES;
                                        Intent intent = new Intent(mActivity,
                                                VideoInfoActivity.class);
                                        intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                                        intent.putExtra(GlobalConstants.VIDEO_OBJ, gamesVideoList.get(pos));
                                        GlobalConstants.LIST_FRAGMENT = new GamesFragment();
                                        GlobalConstants.LIST_ITEM_POSITION = pos;
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
                    }
                });

        stickyListHeadersListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
               Utils.getInstance().gethideKeyboard(mActivity);
                return false;
            }
        });

        stickyListHeadersListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                view.setScrollBarSize(0);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
               /* if (((MainActivity) mActivity).progressDialog != null)
                    if (((MainActivity) mActivity).progressDialog.isShowing())
                        ((MainActivity) mActivity).progressDialog.dismiss();*/
            }

        });


        bannerCacheableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bannerImageView();
            }
        });

        stickyListHeadersListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (stickyListHeadersListView != null) {
//                            stickyListHeadersListView.setFastScrollAlwaysVisible(false);
//                        }
//                    }
//                }, 2000);
//
//                if(gamesVideoList.size()>5)
//                {
//                    stickyListHeadersListView.setFastScrollAlwaysVisible(true);
//                }
                return false;
            }
        });
    }

    private void bannerImageView() {
        if (tabBannerDTO != null) {
            if (tabBannerDTO.isBannerActive()) {
                if (tabBannerDTO.isHyperlinkActive() && tabBannerDTO.getBannerActionURL().length() > 0) {
                    //Start the ActionUrl in Browser
                    Uri uri = Uri.parse(tabBannerDTO.getBannerActionURL());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(tabBannerDTO.getBannerActionURL())));

                    }
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
                                            String videoCategory = GlobalConstants.GAMES;
                                            Intent intent = new Intent(mActivity,
                                                    VideoInfoActivity.class);
                                            intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                                            intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, videoDTO.getPlaylistReferenceId());
                                            intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDTO);
                                            startActivity(intent);
                                            mActivity.overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                                        } /*else {
                                                        ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                                    }*/
                                    } else {
                                        ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                    }
                                } else {
                                    ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                                }

                            } else {
                                //Make an API call to get video data
                                System.out.println("Video not available in the local database. Making server call for video.");

                                pDialog = new ProgressDialog(mActivity, R.style.CustomDialogTheme);
                                pDialog.show();
                                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(mActivity));
                                pDialog.setCanceledOnTouchOutside(false);
                                pDialog.setCancelable(false);

                                mVideoDataTaskModel = AppController.getInstance().getModelFacade().getRemoteModel()
                                        .getVideoDataTaskModel();
                                mVideoDataTaskModel.registerView(GamesFragment.this);

                                mVideoDataTaskModel.setProgressDialog(pDialog);
                                mVideoDataTaskModel.loadVideoData(videoMap);
                            }
                        }
                    }
                }
            }
        }
    }

    private void initComponents(View v) {
        // TODO Auto-generated method stub
        stickyListHeadersListView = (StickyListHeadersListView) v
                .findViewById(R.id.lv_stickyheader);
        stickyListHeadersListView.setFastScrollEnabled(true);


        mBannerProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mBannerProgressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            mBannerProgressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.progress_large_material, null));
        }
        bannerCacheableImageView = (ImageView) v
                .findViewById(R.id.imv_opponents_coaches_playe_banner);
        bannerLayout = (LinearLayout) v
                .findViewById(R.id.ll_banner_block);

        progressBar = (ProgressBar) v.findViewById(R.id.progressbar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progress_large_material, null));
        }
//        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#CC0000"), android.graphics.PorterDuff.Mode.MULTIPLY);
        tvsearchRecordsNotAvailable = (TextView) v.findViewById(R.id.tvSearchStatus);
        refreshLayout = (PullRefreshLayout) v.findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        relativeLayout = (RelativeLayout) v.findViewById(R.id.admob_banner_frame_layout);
        mAdView = (AdView) v.findViewById(R.id.adView_games);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final MenuItem menuItem = menu.findItem(R.id.action_search);

        // progressBarItem = menu.findItem(R.id.miActionProgress);

        searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconified(true);

        searchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                boolean isRecordsAvailableInDb = false;
                ArrayList<VideoDTO> videoList = VaultDatabaseHelper.getInstance(mActivity).getVideoListForGame(GlobalConstants.OKF_GAMES);
                if (videoList.size() > 0) {
                    isRecordsAvailableInDb = true;
                }


                GlobalConstants.SEARCH_VIEW_QUERY = newText;

                if (videoHeaderListAdapter != null) {
                    videoHeaderListAdapter.filter(newText.toLowerCase(Locale
                            .getDefault()));
                    videoHeaderListAdapter.notifyDataSetChanged();
                }

                if (progressBar.isShown() && gamesVideoList.size() > 0) {
                    progressBar.setVisibility(View.GONE);
                }

                //visibility of scroll bar set dynamically list height
                if (stickyListHeadersListView != null) {
                    Utils.setVisibilityOfScrollBarHeightForHeader(newText, stickyListHeadersListView);
                }

                if (!newText.isEmpty()) {

                    if ((gamesVideoList.size() == 0 && isRecordsAvailableInDb) || (gamesVideoList.size() == 0 && !VideoDataService.isServiceRunning)) {
                        tvsearchRecordsNotAvailable.setText(GlobalConstants.NO_RECORDS_FOUND);
                        tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        stickyListHeadersListView.setFastScrollAlwaysVisible(false);
                    } else {
                        tvsearchRecordsNotAvailable.setVisibility(View.INVISIBLE);
                        stickyListHeadersListView.setFastScrollAlwaysVisible(true);
                    }
                } else {
                    //yp        //tvsearchRecordsNotAvailable.setVisibility(View.INVISIBLE);
                    if (gamesVideoList.size() > 0)
                        tvsearchRecordsNotAvailable.setVisibility(View.INVISIBLE);
                    stickyListHeadersListView.setFastScrollAlwaysVisible(true);
                }
                return false;
            }


        });

//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//
//                if (gamesVideoList.size() > 0) {
//                    if (progressBar.isShown()) {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                } else if (gamesVideoList.size() == 0) {
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            /*case R.id.action_sync_now:
                syncDialog.showDatabaseConfirmationDialog();
                break;*/
            default:
                break;
        }
        return true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // it is used to track the ecent of opponennts fragment
            FlurryAgent.onEvent(GlobalConstants.GAMES);
//            if (progressBar != null) {
//                if (gamesVideoList != null && gamesVideoList.size() == 0) {
//                    progressBar.setVisibility(View.GONE);
//                    tvsearchRecordsNotAvailable.setText("No records found");
//                    tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
//                } else {
//                    progressBar.setVisibility(View.GONE);
//                    tvsearchRecordsNotAvailable.setVisibility(View.INVISIBLE);
//                }
//            }
            if (progressBar != null) {
                if (gamesVideoList != null && gamesVideoList.size() == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }


        }
    }

    @Override
    public void updateObject(TabBannerDTO tabBannerDTOobj) {
        tabBannerDTO = tabBannerDTOobj;
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


    public class GamesResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP =
                "Message Processed";

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                gamesVideoList.clear();
                gamesVideoList.addAll(VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).getVideoListForGame(GlobalConstants.OKF_GAMES));

                Collections.sort(gamesVideoList, new Comparator<VideoDTO>() {

                    @Override
                    public int compare(VideoDTO lhs, VideoDTO rhs) {
                        // TODO Auto-generated method stub
                        return lhs.getPlaylistName().toLowerCase()
                                .compareTo(rhs.getPlaylistName().toLowerCase());
                    }
                });

                videoHeaderListAdapter = new VideoContentHeaderListAdapter(gamesVideoList, mActivity, 1, false, true);
                if (!GlobalConstants.SEARCH_VIEW_QUERY.isEmpty()) {
                    videoHeaderListAdapter.filter(GlobalConstants.SEARCH_VIEW_QUERY.toLowerCase(Locale
                            .getDefault()));
                }
                stickyListHeadersListView.setAdapter(videoHeaderListAdapter);
                System.out.println("tabBannerDTO gamesVideoList " + gamesVideoList.size() + " isServiceRunning " + VideoDataService.isServiceRunning);
                if (gamesVideoList.size() == 0 /*&& VideoDataService.isServiceRunning*/) {
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
                System.out.println("tabBannerDTO game " + tabBannerDTO);
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

    private void updateBannerImage() {
        countDownTimer = new CountDownTimer(GlobalConstants.AUTO_REFRESH_INTERVAL, GlobalConstants.AUTO_REFRESH_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                tabBannerDTO = VaultDatabaseHelper.getInstance(getActivity()).getLocalTabBannerDataByTabId(Long.valueOf(tabId));
                if (tabBannerDTO != null) {
                    tabBannerDTO = VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).getLocalTabBannerDataByTabId(tabBannerDTO.getTabId());
                    if (tabBannerDTO != null) {
                        Utils.addBannerImagePullToRefresh(bannerCacheableImageView, bannerLayout, tabBannerDTO, mActivity, mBannerProgressBar);
                    }
                }
            }

            @Override
            public void onFinish() {

                if (countDownTimer != null) {
                    countDownTimer.start();
                }

            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mActivity != null && receiver != null)
        {
            mActivity.unregisterReceiver(receiver);
        }
    }
}