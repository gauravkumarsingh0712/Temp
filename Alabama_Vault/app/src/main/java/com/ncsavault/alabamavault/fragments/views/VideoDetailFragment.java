package com.ncsavault.alabamavault.fragments.views;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.VideoDetailAdapter;
import com.ncsavault.alabamavault.controllers.AppController;

import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.HomeScreen;
import com.ncsavault.alabamavault.views.VideoInfoActivity;

import java.util.ArrayList;

/**
 * Created by gauravkumar.singh on 14/08/17.
 */

public class VideoDetailFragment extends Fragment implements VideoDetailAdapter.VideoClickListener {

    private static Context mContext;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private VideoDetailAdapter videoDetailAdapter;
    private ArrayList<VideoDTO> videoDtoArrayList = new ArrayList<>();
    AsyncTask<Void, Void, Void> mPostTask;
    private boolean isFavoriteChecked;
    private String postResult;
    private PullRefreshLayout refreshLayout;
    PullRefreshTask pullTask;
    long playlistId = 0;
    private TextView tvNoRecoredFound;

    public static Fragment newInstance(Context context,long playlistId) {
        Fragment videoDetailFragment = new VideoDetailFragment();
        mContext = context;
        Bundle args = new Bundle();
        args.putLong("playlist_id", playlistId);
        videoDetailFragment.setArguments(args);
        return videoDetailFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.saved_video_fragment_layout, container, false);


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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.saved_video_recycler_view);
        tvNoRecoredFound = (TextView) view.findViewById(R.id.tv_no_recored_found);
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
            playlistId  = bundle.getLong("playlist_id", 0);
        }
        getVideoData(playlistId);
    }

    private void getVideoData(final long playlistId) {
        final AsyncTask<Void, Void, ArrayList<VideoDTO>> mDbTask = new AsyncTask<Void, Void, ArrayList<VideoDTO>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressBar != null) {
                    if (videoDtoArrayList.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            protected ArrayList<VideoDTO> doInBackground(Void... params) {

                try {
                    int userId = 1110;
                    String url = GlobalConstants.PLAYLIST_VIDEO_URL + "userid=" + userId + "&playlistid=" + playlistId;
                    videoDtoArrayList.clear();
                    videoDtoArrayList.addAll(AppController.getInstance().getServiceManager().getVaultService().getNewVideoData(url));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return videoDtoArrayList;
            }

            @Override
            protected void onPostExecute(ArrayList<VideoDTO> result) {
                super.onPostExecute(result);

                if (progressBar != null) {
                    if (result.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }

                if(result.size() == 0)
                {
                    tvNoRecoredFound.setVisibility(View.VISIBLE);
                    tvNoRecoredFound.setText(GlobalConstants.NO_RECORDS_FOUND);
                }else
                {
                    tvNoRecoredFound.setVisibility(View.GONE);
                }


                videoDetailAdapter = new VideoDetailAdapter(mContext,result,VideoDetailFragment.this);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(videoDetailAdapter);
                // ------- addBannerImage---------------------
            }
        };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClick(final VideoDetailAdapter.VideoViewHolder videoViewHolder,final int pos) {

        videoViewHolder.videoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("position : " + pos);
                if (Utils.isInternetAvailable(mContext)) {
                    if (videoDtoArrayList.get(pos).getVideoLongUrl() != null) {
                        if (videoDtoArrayList.get(pos).getVideoLongUrl().length() > 0
                                && !videoDtoArrayList.get(pos).getVideoLongUrl().toLowerCase().equals("none")) {
                            String videoCategory = GlobalConstants.FEATURED;
                            Intent intent = new Intent(mContext, VideoInfoActivity.class);
                            intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                            intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDtoArrayList.get(pos));
                            GlobalConstants.LIST_FRAGMENT = new VideoDetailFragment();
                            GlobalConstants.LIST_ITEM_POSITION = pos;
                            startActivity(intent);
                            ((HomeScreen)mContext).overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
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
        });


        videoViewHolder.savedVideoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (videoDtoArrayList.get(pos).isVideoIsFavorite() && ((videoDtoArrayList.get(pos)
                        .getVideoLongUrl().length() == 0 || videoDtoArrayList.get(pos).getVideoLongUrl()
                        .toLowerCase().equals("none")))) {
                      markFavoriteStatus(videoViewHolder,pos);
                } else {
                    if (videoDtoArrayList.get(pos).getVideoLongUrl().length() > 0 && !videoDtoArrayList
                            .get(pos).getVideoLongUrl().toLowerCase().equals("none")) {
                        markFavoriteStatus(videoViewHolder,pos);
                    } else {
                       //gk ((MainActivity) context).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                        videoViewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                    }
                }

                videoDetailAdapter.notifyDataSetChanged();
            }
        });
    }

    public void markFavoriteStatus(final VideoDetailAdapter.VideoViewHolder viewHolder, final int pos) {
        if (Utils.isInternetAvailable(mContext)) {
//gk            if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() ==
//                    GlobalConstants.DEFAULT_USER_ID) {
//                viewHolder.savedVideoImageView.setBackgroundResource(R.drawable.video_save);
//               //gk showConfirmLoginDialog(GlobalConstants.LOGIN_MESSAGE);
//            } else {
                System.out.println("favorite position : " + pos);
                if (videoDtoArrayList.get(pos).isVideoIsFavorite()) {
                    isFavoriteChecked = false;
                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                            (0, videoDtoArrayList.get(pos).getVideoId());
                    videoDtoArrayList.get(pos).setVideoIsFavorite(false);
                    viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
                } else {
                    isFavoriteChecked = true;
                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                            (1, videoDtoArrayList.get(pos).getVideoId());
                    videoDtoArrayList.get(pos).setVideoIsFavorite(true);
                    viewHolder.savedVideoImageView.setImageResource(R.drawable.saved_video_img);
                }

                mPostTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            postResult = AppController.getInstance().getServiceManager().getVaultService().
                                    postFavoriteStatus(AppController.getInstance().getModelFacade().getLocalModel()
                                            .getUserId(), videoDtoArrayList.get(pos).getVideoId(),
                                            videoDtoArrayList.get(pos).getPlaylistId(),
                                            isFavoriteChecked);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        try {
                            System.out.println("favorite position 111 : " + pos);
                            if (isFavoriteChecked) {
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag(1,
                                        videoDtoArrayList.get(pos).getVideoId());
                                // firebase analytics favoride video
//                                params.putString(FirebaseAnalytics.Param.ITEM_ID, arrayListVideoDTOs.get(pos).getVideoName());
//                                params.putString(FirebaseAnalytics.Param.ITEM_NAME, arrayListVideoDTOs.get(pos).getVideoName());
//                                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video_favorite");
//                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

                            }else{
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                                        (0, videoDtoArrayList.get(pos).getVideoId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
         //gk   }
        } else {
          //gk  ((MainActivity) context).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            viewHolder.savedVideoImageView.setBackgroundResource(R.drawable.video_save);
        }
    }

    public class PullRefreshTask extends AsyncTask<Void, Void, ArrayList<VideoDTO>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mRecyclerView != null) {
                mRecyclerView.setEnabled(false);
            }

            refreshLayout.setRefreshing(true);

        }

        @Override
        protected ArrayList<VideoDTO> doInBackground(Void... params) {
            try {
                    int userId = 1110;
                    String url = GlobalConstants.PLAYLIST_VIDEO_URL + "userid=" + userId + "&playlistid=" + playlistId;
                    videoDtoArrayList.clear();
                    videoDtoArrayList.addAll(AppController.getInstance().getServiceManager().
                            getVaultService().getNewVideoData(url));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return videoDtoArrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<VideoDTO> result) {
            super.onPostExecute(result);
            try {

                if(result.size() == 0)
                {
                    tvNoRecoredFound.setVisibility(View.VISIBLE);
                    tvNoRecoredFound.setText(GlobalConstants.NO_RECORDS_FOUND);
                }else
                {
                    tvNoRecoredFound.setVisibility(View.GONE);
                }
                videoDetailAdapter = new VideoDetailAdapter(mContext,result,VideoDetailFragment.this);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(videoDetailAdapter);
                refreshLayout.setRefreshing(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}