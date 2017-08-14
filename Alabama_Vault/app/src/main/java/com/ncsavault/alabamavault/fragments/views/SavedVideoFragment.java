package com.ncsavault.alabamavault.fragments.views;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.ncsavault.alabamavault.AsyncTask.PullRefreshTask;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.SavedVideoAdapter;
import com.ncsavault.alabamavault.adapters.VideoContentListAdapter;
import com.ncsavault.alabamavault.adapters.VideoDetailAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by gauravkumar.singh on 6/12/2017.
 */

public class SavedVideoFragment extends Fragment {

    private static Context mContext;
    RecyclerView mRecyclerView;
    private ArrayList<VideoDTO> favoriteVideoList = new ArrayList<>();
    private SavedVideoAdapter savedVideoAdapter;
    private PullRefreshLayout refreshLayout;
    PullRefreshTask pullTask;
    private ProgressBar progressBar;
    private TextView tvNoRecoredFound;

    public static Fragment newInstance(Context context) {
        Fragment frag = new SavedVideoFragment();

        mContext = context;
        Bundle args = new Bundle();
        return frag;
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

        getFavoriteDataFromDataBase();
    }

    /**
     * Get the favorite video from database
     */
    private void getFavoriteDataFromDataBase() {
        try {

            AsyncTask<Void, Void, ArrayList<VideoDTO>> mDbTask = new AsyncTask<Void, Void, ArrayList<VideoDTO>>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    if(progressBar != null)
                    {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    if (savedVideoAdapter != null) {
                        savedVideoAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                protected ArrayList<VideoDTO> doInBackground(Void... params) {
                    try {
                        favoriteVideoList.clear();
                        favoriteVideoList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                getFavouriteVideosArrayList());
                        System.out.println("favoriteVideoList size getFavoriteDataFromDataBase : " +
                                favoriteVideoList.size());
                        Collections.sort(favoriteVideoList, new Comparator<VideoDTO>() {

                            @Override
                            public int compare(VideoDTO lhs, VideoDTO rhs) {
                                // TODO Auto-generated method stub
                                return lhs.getVideoName().toLowerCase()
                                        .compareTo(rhs.getVideoName().toLowerCase());
                            }
                        });

                        savedVideoAdapter  = new SavedVideoAdapter(mContext,favoriteVideoList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return favoriteVideoList;
                }

                @Override
                protected void onPostExecute(ArrayList<VideoDTO> list) {
                    super.onPostExecute(list);

                    if(progressBar != null)
                    {
                        progressBar.setVisibility(View.GONE);
                    }

                    if(list.size() == 0)
                    {
                        tvNoRecoredFound.setVisibility(View.VISIBLE);
                        tvNoRecoredFound.setText(GlobalConstants.NO_RECORDS_FOUND);
                    }else
                    {
                        tvNoRecoredFound.setVisibility(View.GONE);
                    }


                    mRecyclerView.setHasFixedSize(true);
                    LinearLayoutManager llm = new LinearLayoutManager(mContext);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(llm);
                    mRecyclerView.setAdapter(savedVideoAdapter);
                    savedVideoAdapter.notifyDataSetChanged();

                }
            };

            mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////


        } catch (Exception e) {
            e.printStackTrace();
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
                try {
                    String url = GlobalConstants.FAVORITE_API_URL + "userId=" + userId;

                    favoriteVideoList.clear();
                    favoriteVideoList.addAll(AppController.getInstance().getServiceManager().getVaultService()
                            .getVideosListFromServer(url));
                } catch (Exception e) {
                    e.printStackTrace();
                }





            } catch (Exception e) {
                e.printStackTrace();
            }
            return favoriteVideoList;
        }

        @Override
        protected void onPostExecute(final ArrayList<VideoDTO> result) {
            super.onPostExecute(result);

              try {

                for (VideoDTO vidDto : result) {
                    if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                            checkVideoAvailability(vidDto.getVideoId())) {
                        VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                                setFavoriteFlag(1, vidDto.getVideoId());
                    }
                }
                favoriteVideoList.clear();
                favoriteVideoList.addAll(VaultDatabaseHelper.getInstance(mContext.getApplicationContext())
                        .getFavouriteVideosArrayList());

                Collections.sort(favoriteVideoList, new Comparator<VideoDTO>() {

                    @Override
                    public int compare(VideoDTO lhs, VideoDTO rhs) {
                        // TODO Auto-generated method stub
                        return lhs.getVideoName().toLowerCase()
                                .compareTo(rhs.getVideoName().toLowerCase());
                    }
                });


                savedVideoAdapter  = new SavedVideoAdapter(mContext,favoriteVideoList);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(savedVideoAdapter);
                refreshLayout.setRefreshing(false);

                if(favoriteVideoList.size() == 0)
                {
                    tvNoRecoredFound.setVisibility(View.VISIBLE);
                    tvNoRecoredFound.setText(GlobalConstants.NO_RECORDS_FOUND);
                }else
                {
                    tvNoRecoredFound.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}

