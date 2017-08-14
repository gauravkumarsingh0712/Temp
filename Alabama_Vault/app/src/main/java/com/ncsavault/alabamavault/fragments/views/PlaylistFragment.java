package com.ncsavault.alabamavault.fragments.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.baoyz.widget.PullRefreshLayout;
import com.ncsavault.alabamavault.AsyncTask.PullRefreshTask;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.CatagoriesAdapter;
import com.ncsavault.alabamavault.adapters.FilterSubtypesAdapter;
import com.ncsavault.alabamavault.adapters.PlaylistDataAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.PlaylistDto;
import com.ncsavault.alabamavault.dto.TabBannerDTO;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.VideoDataTaskModel;
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

/**
 * Created by gauravkumar.singh on 8/10/2017.
 */

public class PlaylistFragment extends Fragment implements PlaylistDataAdapter.PlaylistDataClickListener,AbstractView {

    private static Context mContext;
    RecyclerView mRecyclerView;
    private PlaylistDataAdapter mAlbumsAdapter;
    private static int TOTAL_CELLS_PER_ROW = 1;
    private ProgressBar progressBar;
    private ArrayList<PlaylistDto> playlistDtoDataList = new ArrayList<>();
    long tabId = 0;
    private TabBannerDTO tabBannerDTO = null;
    private ImageView bannerImageView;
    private LinearLayout bannerLayout;
    private VideoDataTaskModel mVideoDataTaskModel;
    private ProgressDialog pDialog = null;
    private PullRefreshLayout refreshLayout;
    PullRefreshTask pullTask;

    public static Fragment newInstance(Context context,long tabId) {
        Fragment playlistFragment = new PlaylistFragment();
        mContext = context;
        Bundle args = new Bundle();
        args.putLong("tab_id", tabId);
        playlistFragment.setArguments(args);
        return playlistFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();

        if (refreshLayout != null) {
            refreshLayout.setEnabled(true);
            refreshLayout.setOnRefreshListener(refreshListener);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playlist_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initListener();

        tabBannerDTO = VaultDatabaseHelper.getInstance(getActivity()).getLocalTabBannerDataByTabId(Long.valueOf(tabId));

        if(tabBannerDTO != null) {
            showBannerImage(bannerImageView,tabBannerDTO);
        }

        if(playlistDtoDataList.size()==0) {
            getPlaylistData(tabId);
        }else {
            getPlaylistDateFromDatabase();
        }
    }

    private void initViews(View view)
    {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        bannerImageView = (ImageView) view.findViewById(R.id.img_banner);
        bannerLayout = (LinearLayout) view.findViewById(R.id.syncro_banner_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else {
            System.out.println("progress bar not showing ");
            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.progress_large_material, null));
        }

        refreshLayout = (PullRefreshLayout) view.findViewById(R.id.refresh_layout);

        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setEnabled(false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tabId  = bundle.getLong("tab_id", 0);
        }
    }

    private void initListener()
    {
        bannerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick();
            }
        });
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
                                mVideoDataTaskModel.registerView(PlaylistFragment.this);

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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(PlaylistDataAdapter.MyViewHolder viewHolder, final long playlistId) {

        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDetailFragment videoFragment = (VideoDetailFragment) VideoDetailFragment.newInstance(mContext,playlistId);
                FragmentManager manager =  ((HomeScreen)mContext).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container,videoFragment);
                transaction.addToBackStack(videoFragment.getClass().getName());
                transaction.commit();
            }
        });

    }

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

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void getPlaylistData(final long tabId) {

        final AsyncTask<Void, Void, ArrayList<PlaylistDto>> mDbTask = new AsyncTask<Void, Void, ArrayList<PlaylistDto>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressBar != null) {
                    if (playlistDtoDataList.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            protected ArrayList<PlaylistDto> doInBackground(Void... params) {

                try {
                    int userId = 1110;
                    String url = GlobalConstants.CATEGORIES_PLAYLIST_URL + "userid=" + userId + "&nav_tab_id=" + tabId;

                    playlistDtoDataList.clear();
                    playlistDtoDataList.addAll(AppController.getInstance().getServiceManager().getVaultService().getPlaylistData(url));

                    if(playlistDtoDataList.size() >0) {
                        VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllPlaylistTabData();
                        VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                insertPlaylistTabData(playlistDtoDataList);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return playlistDtoDataList;
            }

            @Override
            protected void onPostExecute(ArrayList<PlaylistDto> result) {
                super.onPostExecute(result);

                if (progressBar != null) {
                    if (result.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }


                for(int j = 0;j<playlistDtoDataList.size();j++)
                {
                    if((j+1) % 5 ==0)
                    {
                        playlistDtoDataList.add(j,new PlaylistDto());
                    }
                }

                mAlbumsAdapter = new PlaylistDataAdapter(mContext,PlaylistFragment.this,result);
                GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAlbumsAdapter);
                mLayoutManager.setSpanSizeLookup( new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        switch(mAlbumsAdapter.getItemViewType(position) ) {
                            case PlaylistDataAdapter.TYPE_LIST_DATA:
                                return TOTAL_CELLS_PER_ROW;
                            case PlaylistDataAdapter.TYPE_AD:
                                return 2;
                            default:
                                return 2;
                        }
                    }
                });

            }
        };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getPlaylistDateFromDatabase()
    {
        if(playlistDtoDataList.size()>0) {
            playlistDtoDataList.clear();
            playlistDtoDataList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                    .getAllLocalPlaylistTabData());

//        if(mCatagoriesAdapter != null)
//        {
//            mCatagoriesAdapter.notifyDataSetChanged();
//        }else
//        {
            for(int j = 0;j<playlistDtoDataList.size();j++)
            {
                if((j+1) % 5 ==0)
                {
                    playlistDtoDataList.add(j,new PlaylistDto());
                }
            }

            mAlbumsAdapter = new PlaylistDataAdapter(mContext,PlaylistFragment.this,playlistDtoDataList);
            GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setAdapter(mAlbumsAdapter);
            mLayoutManager.setSpanSizeLookup( new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch(mAlbumsAdapter.getItemViewType(position) ) {
                        case PlaylistDataAdapter.TYPE_LIST_DATA:
                            return TOTAL_CELLS_PER_ROW;
                        case PlaylistDataAdapter.TYPE_AD:
                            return 2;
                        default:
                            return 2;
                    }
                }
            });
//        }
        }
    }

    public void showBannerImage(final ImageView bannerCacheableImageView, TabBannerDTO tabBannerDTO) {
        if (tabBannerDTO != null)
            if (tabBannerDTO.isBannerActive()) {
                bannerLayout.setVisibility(View.VISIBLE);
                DisplayImageOptions imgLoadingOptions = new DisplayImageOptions.Builder()
                        .cacheOnDisk(true).resetViewBeforeLoading(true)
                        .cacheInMemory(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .build();
                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(tabBannerDTO.getBannerURL(),
                        bannerCacheableImageView, imgLoadingOptions, new ImageLoadingListener() {

                            @Override
                            public void onLoadingStarted(String s, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                                bannerCacheableImageView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                                bannerCacheableImageView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {
                                //bannerCacheableImageView.setVisibility(View.GONE);
                            }
                        });
            } else {
                bannerCacheableImageView.setVisibility(View.GONE);
                bannerLayout.setVisibility(View.GONE);
            }
    }

    public class PullRefreshTask extends AsyncTask<Void, Void, ArrayList<PlaylistDto>> {

        public boolean isBannerUpdated = false;

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
        protected ArrayList<PlaylistDto> doInBackground(Void... params) {
            try {
                int userId = 1110;
                String url = GlobalConstants.CATEGORIES_PLAYLIST_URL + "userid=" + userId + "&nav_tab_id=" + tabId;

                playlistDtoDataList.clear();
                playlistDtoDataList.addAll(AppController.getInstance().getServiceManager().getVaultService().getPlaylistData(url));

                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllPlaylistTabData();
                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).insertPlaylistTabData
                        (playlistDtoDataList);

                //Update Banner Data
                if (tabBannerDTO != null) {
                    TabBannerDTO serverObj = AppController.getInstance().getServiceManager().
                            getVaultService().getTabBannerDataById(tabBannerDTO.getTabBannerId(),
                            tabBannerDTO.getTabKeyword(), tabBannerDTO.getTabId());

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
            return playlistDtoDataList;
        }

        @Override
        protected void onPostExecute(final ArrayList<PlaylistDto> result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    if (result.size() > 0) {
                        for(int j = 0;j<playlistDtoDataList.size();j++)
                        {
                            if((j+1) % 5 ==0)
                            {
                                playlistDtoDataList.add(j,new PlaylistDto());
                            }
                        }

                        mAlbumsAdapter = new PlaylistDataAdapter(mContext,PlaylistFragment.this,result);
                        GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        mRecyclerView.setAdapter(mAlbumsAdapter);
                        mLayoutManager.setSpanSizeLookup( new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                switch(mAlbumsAdapter.getItemViewType(position) ) {
                                    case PlaylistDataAdapter.TYPE_LIST_DATA:
                                        return TOTAL_CELLS_PER_ROW;
                                    case PlaylistDataAdapter.TYPE_AD:
                                        return 2;
                                    default:
                                        return 2;
                                }
                            }
                        });
                        refreshLayout.setRefreshing(false);
                    }
                    // ------- update BannerImage---------------------
                    if (isBannerUpdated)
                        if (tabBannerDTO != null) {
                            tabBannerDTO = VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                                    .getLocalTabBannerDataByTabId(tabBannerDTO.getTabId());
                            if (tabBannerDTO != null)
                                showBannerImage(bannerImageView,tabBannerDTO);
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
}
