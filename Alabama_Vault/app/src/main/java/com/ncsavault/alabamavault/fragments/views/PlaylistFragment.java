package com.ncsavault.alabamavault.fragments.views;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.AlbumsAdapter;
import com.ncsavault.alabamavault.adapters.CatagoriesAdapter;
import com.ncsavault.alabamavault.adapters.FilterSubtypesAdapter;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.CatagoriesTabDao;
import com.ncsavault.alabamavault.dto.PlaylistDto;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.views.HomeScreen;

import java.util.ArrayList;

/**
 * Created by gauravkumar.singh on 8/10/2017.
 */

public class PlaylistFragment extends Fragment implements AlbumsAdapter.PlaylistDataClickListener {

    private static Context mContext;
    RecyclerView mRecyclerView;
    private AlbumsAdapter mAlbumsAdapter;
    private static int TOTAL_CELLS_PER_ROW = 1;
    private ProgressBar progressBar;
    private ArrayList<PlaylistDto> playlistDtoDataList = new ArrayList<>();

    public static Fragment newInstance(Context context,long tabPosition) {
        Fragment playlistFragment = new PlaylistFragment();
        mContext = context;
        Bundle args = new Bundle();
        args.putLong("tab_id", tabPosition);
        playlistFragment.setArguments(args);
        return playlistFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long tabId = 0;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tabId  = bundle.getLong("tab_id", 0);
        }
        getPlaylistData(tabId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playlist_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else {
            System.out.println("progress bar not showing ");
            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.progress_large_material, null));
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
    public void onClick(AlbumsAdapter.MyViewHolder viewHolder, long playlistId) {

        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedVideoFragment videoFragment = (SavedVideoFragment) SavedVideoFragment.newInstance(mContext);
                android.support.v4.app.FragmentManager manager =  ((HomeScreen)mContext).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container,videoFragment);
                transaction.addToBackStack(videoFragment.getClass().getName());
                transaction.commit();
            }
        });

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

                mAlbumsAdapter = new AlbumsAdapter(mContext,result, PlaylistFragment.this);
                GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAlbumsAdapter);
                mLayoutManager.setSpanSizeLookup( new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        switch(mAlbumsAdapter.getItemViewType(position) ) {
                            case AlbumsAdapter.TYPE_LIST_DATA:
                                return TOTAL_CELLS_PER_ROW;
                            case AlbumsAdapter.TYPE_AD:
                                return 2;
                            default:
                                return 2;
                        }
                    }
                });
                // ------- addBannerImage---------------------
            }
        };

        mDbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
