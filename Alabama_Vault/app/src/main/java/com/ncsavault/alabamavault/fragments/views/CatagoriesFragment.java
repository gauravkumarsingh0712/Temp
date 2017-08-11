package com.ncsavault.alabamavault.fragments.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.CatagoriesAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.CatagoriesTabDao;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.views.HomeScreen;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by gauravkumar.singh on 8/4/2017.
 */

public class CatagoriesFragment extends Fragment implements CatagoriesAdapter.OnClickInterface {

    private static Context mContext;
    private RecyclerView mRecyclerView;
    public PlaylistFragment fragment = null;
    private ProgressBar progressBar;
    ArrayList<CatagoriesTabDao> catagoriesTabList = new ArrayList<>();

    public static Fragment newInstance(Context context) {
        Fragment frag = new CatagoriesFragment();
        mContext = context;
        Bundle args = new Bundle();
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.catagaroies_fragmnet_layout, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        getCatagoriesData();
    }

    private void initViews(View view)
    {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.catagories_recycler_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else {
            System.out.println("progress bar not showing ");
            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.progress_large_material, null));
        }
    }


    @Override
    public void onClick(CatagoriesAdapter.CatagoriesAdapterViewHolder v,final long tabPosition) {
        v.playlistImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistFragment playlistFragment = (PlaylistFragment) PlaylistFragment.newInstance(mContext,tabPosition);
                FragmentManager manager =  ((HomeScreen)mContext).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container,playlistFragment);
                transaction.addToBackStack(playlistFragment.getClass().getName());
                transaction.commit();
            }
        });

    }

    private void getCatagoriesData() {
        final AsyncTask<Void, Void, ArrayList<CatagoriesTabDao>> mDbTask = new AsyncTask<Void, Void, ArrayList<CatagoriesTabDao>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressBar != null) {
                    if (catagoriesTabList.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            protected ArrayList<CatagoriesTabDao> doInBackground(Void... params) {

                try {
                    int userId = 1110;
                    String url = GlobalConstants.CATEGORIES_TAB_URL + "userid=" + userId;
                    catagoriesTabList.clear();
                    catagoriesTabList.addAll(AppController.getInstance().getServiceManager().getVaultService().getCategoriesData(url));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return catagoriesTabList;
            }

            @Override
            protected void onPostExecute(ArrayList<CatagoriesTabDao> result) {
                super.onPostExecute(result);

                if (progressBar != null) {
                    if (result.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
                CatagoriesAdapter savedVideoAdapter = new CatagoriesAdapter(mContext,CatagoriesFragment.this,result);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setAdapter(savedVideoAdapter);
                // ------- addBannerImage---------------------
            }
        };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}


