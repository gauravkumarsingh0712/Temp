package com.ncsavault.alabamavault.AsyncTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.ncsavault.alabamavault.adapters.VideoContentHeaderListAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.TabBannerDTO;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by yogita.panpaliya on 9/22/2016.
 */

/*This class is called when user Pull to get the Refresh Data.*/

public class PullRefreshTask extends AsyncTask<Void, Void, ArrayList<VideoDTO>> {

    // Initialise Variables
    private ArrayList<VideoDTO> videoList;
    public boolean isBannerUpdated = false;
    public TabBannerDTO serverObj;
    StickyListHeadersListView stickyListHeadersListView;
    PullRefreshLayout refreshLayout;
    VideoContentHeaderListAdapter videoHeaderListAdapter;
    private TabBannerDTO tabBannerDTO;
    private ProgressBar progressBar;
    private TextView tvsearchRecordsNotAvailable;
    private ImageView bannerCacheableImageView;
    private LinearLayout bannerLayout;
    private Activity activity;
    private ProgressBar mBannerProgressBar;
    private String videoUrl;
    private String tabName;
    private Boolean isGames = true;
    private UpdateTabDtoInfo mUpdateTabDtoInfo;

    //empty Constructor
    public PullRefreshTask() {
    }

    public interface UpdateTabDtoInfo {
        public void updateObject(TabBannerDTO tabBannerDTOobj);
    }

    public PullRefreshTask(Activity activity, String videoUrl, String tabName, boolean isBannerUpdated, StickyListHeadersListView stickyHeader,
                           PullRefreshLayout pullRefreshLayout, VideoContentHeaderListAdapter contentHeader,
                           TabBannerDTO tabBanner, ArrayList<VideoDTO> videoDtoList, ProgressBar progressBar,
                           TextView tvsearchRecordsNotAvailable, ImageView bannerCacheableImageView,
                           LinearLayout bannerLayout, ProgressBar mBannerProgressBar, Boolean isGames, UpdateTabDtoInfo updateTabDtoInfo) {
        this.activity = activity;
        this.videoUrl = videoUrl;
        this.isBannerUpdated = isBannerUpdated;
        this.stickyListHeadersListView = stickyHeader;
        this.refreshLayout = pullRefreshLayout;
        this.videoHeaderListAdapter = contentHeader;
        this.tabBannerDTO = tabBanner;
        this.videoList = videoDtoList;
        this.progressBar = progressBar;
        this.tvsearchRecordsNotAvailable = tvsearchRecordsNotAvailable;
        this.bannerCacheableImageView = bannerCacheableImageView;
        this.bannerLayout = bannerLayout;
        this.mBannerProgressBar = mBannerProgressBar;
        this.tabName = tabName;
        this.isGames = isGames;
        mUpdateTabDtoInfo = updateTabDtoInfo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (stickyListHeadersListView != null) {
            //gk Utils.setDisabledStickyListHeadersListViewScrolling(stickyListHeadersListView);
        }

        refreshLayout.setRefreshing(true);
        if (videoHeaderListAdapter != null) {
            videoHeaderListAdapter.isPullRefreshInProgress = true;
            videoHeaderListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected ArrayList<VideoDTO> doInBackground(Void... params) {
        final ArrayList<VideoDTO> arrList = new ArrayList<VideoDTO>();
        try {
            String url = videoUrl + "userId=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
            arrList.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
            if (arrList.size() > 0) {
                VaultDatabaseHelper.getInstance(activity.getApplicationContext()).removeRecordsByTab(tabName);
                VaultDatabaseHelper.getInstance(activity.getApplicationContext()).insertVideosInDatabase(arrList);
            }


            if (tabBannerDTO != null) {
                TabBannerDTO serverObj = AppController.getInstance().getServiceManager().getVaultService().getTabBannerDataById(tabBannerDTO.getTabBannerId(), tabBannerDTO.getTabKeyword(), tabBannerDTO.getTabId());
                if (serverObj != null) {
                    System.out.println("TabBannerModifiedValue " + tabBannerDTO.getBannerModified() + "TabBannerModified Server " + serverObj.getBannerModified() +
                            "TabBanner Created " + tabBannerDTO.getBannerCreated() + "TabBanner Created Server " + serverObj.getBannerCreated());
                    if ((tabBannerDTO.getBannerModified() != serverObj.getBannerModified()) || (tabBannerDTO.getBannerCreated() != serverObj.getBannerCreated())) {
                        File imageFile = ImageLoader.getInstance().getDiscCache().get(tabBannerDTO.getBannerURL());
                        if (imageFile.exists()) {
                            imageFile.delete();
                        }
                        MemoryCacheUtils.removeFromCache(tabBannerDTO.getBannerURL(), ImageLoader.getInstance().getMemoryCache());

                        VaultDatabaseHelper.getInstance(activity.getApplicationContext()).updateTabBannerData(serverObj);
                        isBannerUpdated = true;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return arrList;
    }

    @Override
    protected void onPostExecute(final ArrayList<VideoDTO> result) {
        super.onPostExecute(result);

        try {
            if (result != null) {
                if (result.size() > 0) {
                    videoList.clear();

                    if (isGames) {
                        videoList.addAll(VaultDatabaseHelper.getInstance(activity.getApplicationContext()).getVideoListForGame(GlobalConstants.OKF_GAMES));
                        Collections.sort(videoList, new Comparator<VideoDTO>() {

                            @Override
                            public int compare(VideoDTO lhs, VideoDTO rhs) {
                                // TODO Auto-generated method stub
                                return lhs.getPlaylistName().toLowerCase()
                                        .compareTo(rhs.getPlaylistName().toLowerCase());
                            }
                        });
                    } else {
                        videoList.addAll(VaultDatabaseHelper.getInstance(activity).getVideoList(tabName));
                        Collections.sort(videoList, new Comparator<VideoDTO>() {

                            @Override
                            public int compare(VideoDTO lhs, VideoDTO rhs) {
                                // TODO Auto-generated method stub
                                return lhs.getPlaylistName().toLowerCase()
                                        .compareTo(rhs.getPlaylistName().toLowerCase());

                            }
                        });
                        try {
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    if (result.size() > 0) {
                                        VaultDatabaseHelper.getInstance(activity.getApplicationContext()).removeRecordsByTab(tabName);
                                        VaultDatabaseHelper.getInstance(activity.getApplicationContext()).insertVideosInDatabase(result);
                                    }
                                }
                            };
                            thread.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    if (videoHeaderListAdapter != null) {
                        videoHeaderListAdapter.listSearch.clear();
                        videoHeaderListAdapter.listSearch.addAll(videoList);

                        videoHeaderListAdapter.notifyDataSetChanged();
//                    videoHeaderListAdapter.updateIndexer();
                    } else {
                        if (isGames) {
                            videoHeaderListAdapter = new VideoContentHeaderListAdapter(videoList, activity, 1, false, true);
                        } else {
                            videoHeaderListAdapter = new VideoContentHeaderListAdapter(videoList, activity, 2, true, false);
                        }
                        stickyListHeadersListView.setAdapter(videoHeaderListAdapter);
                    }
                    if (!GlobalConstants.SEARCH_VIEW_QUERY.isEmpty()) {
                        videoHeaderListAdapter.filter(GlobalConstants.SEARCH_VIEW_QUERY.toLowerCase(Locale
                                .getDefault()));
                        videoHeaderListAdapter.notifyDataSetChanged();
                    }
                    if (videoHeaderListAdapter.getCount() == 0) {

                        tvsearchRecordsNotAvailable.setText(GlobalConstants.NO_RECORDS_FOUND);
                        tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        stickyListHeadersListView.setFastScrollAlwaysVisible(false);
                    } else {
                        tvsearchRecordsNotAvailable.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                } else if (result.size() == 0) {
                    videoList.clear();
                    try {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                    VaultDatabaseHelper.getInstance(activity.getApplicationContext()).removeRecordsByTab(tabName);
                                    VaultDatabaseHelper.getInstance(activity.getApplicationContext()).insertVideosInDatabase(result);
                            }
                        };
                        thread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tvsearchRecordsNotAvailable.setText(GlobalConstants.NO_RECORDS_FOUND);
                    tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    stickyListHeadersListView.setFastScrollAlwaysVisible(false);
                    if (videoHeaderListAdapter != null) {
                        videoHeaderListAdapter.listSearch.clear();
                        videoHeaderListAdapter.listSearch.addAll(videoList);

                        videoHeaderListAdapter.notifyDataSetChanged();
                    }

                } else {
                    tvsearchRecordsNotAvailable.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
                if (isBannerUpdated)
                    if (tabBannerDTO != null) {
                        tabBannerDTO = VaultDatabaseHelper.getInstance(activity.getApplicationContext()).getLocalTabBannerDataByTabId(tabBannerDTO.getTabId());
                        Utils.addBannerImagePullToRefresh(bannerCacheableImageView, bannerLayout, tabBannerDTO, activity, mBannerProgressBar);
                        mUpdateTabDtoInfo.updateObject(tabBannerDTO);
                    }
                videoHeaderListAdapter.isPullRefreshInProgress = false;
                refreshLayout.setRefreshing(false);

                if (stickyListHeadersListView != null) {
                    //gk Utils.setEnabledStickyListHeadersListViewScrolling(stickyListHeadersListView);
                }
            } else {

                //if (videoList.size()>= 0) {
//                    videoList.clear();
//                    tvsearchRecordsNotAvailable.setText("No Records Found");
//                    tvsearchRecordsNotAvailable.setVisibility(View.VISIBLE);
//                    progressBar.setVisibility(View.GONE);
//                    stickyListHeadersListView.setFastScrollAlwaysVisible(false);
//
//                    if(videoHeaderListAdapter != null) {
//                        videoHeaderListAdapter.listSearch.clear();
//                        videoHeaderListAdapter.listSearch.addAll(videoList);
//
//                        videoHeaderListAdapter.notifyDataSetChanged();
//                    }

                //  }
//                else
//                {
                ((MainActivity) activity).showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
//                }
                refreshLayout.setRefreshing(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            refreshLayout.setRefreshing(false);
        }

    }
}