package com.ncsavault.alabamavault.models;

import android.app.Activity;
import android.os.AsyncTask;

import com.ncsavault.alabamavault.adapters.VideoContentHeaderListAdapter;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by gauravkumar.singh on 12/28/2016.
 */

public class FragmentDataTaskModel extends BaseModel {

    private Activity mActivity;
    public ArrayList<VideoDTO> gameVideoDTOArrayList;
    public ArrayList<VideoDTO> playerVideoDTOArrayList;
    private VideoContentHeaderListAdapter videoContentHeaderListAdapter;
    private String mTabName;
    private boolean isGameTab;
    private StickyListHeadersListView mStickyListHeadersListView;

    public void loadFragmentData(Activity activity, ArrayList<VideoDTO> videoListData,
                                 VideoContentHeaderListAdapter headerListAdapter, String tabName, Boolean isGame,
                                 StickyListHeadersListView stickyListHeadersListView) {
        this.mActivity = activity;
        if(tabName.equalsIgnoreCase(GlobalConstants.OKF_GAMES))
        {
            this.gameVideoDTOArrayList = videoListData;
        }else if(tabName.equalsIgnoreCase(GlobalConstants.OKF_PLAYERS))
        {
            this.playerVideoDTOArrayList = videoListData;
        }
        this.videoContentHeaderListAdapter = headerListAdapter;
        this.mTabName = tabName;
        this.isGameTab = isGame;
        this.mStickyListHeadersListView = stickyListHeadersListView;

        FragmentTaskData fragmentTaskData = new FragmentTaskData();
        fragmentTaskData.execute();
    }


    private class FragmentTaskData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {

                switch (GlobalConstants.CURRENT_TAB) {
                    case 1: {
                        gameVideoDTOArrayList.clear();
                        gameVideoDTOArrayList.addAll(VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).
                                getVideoListForGame(GlobalConstants.OKF_GAMES));
                        sortData(gameVideoDTOArrayList);
                        break;
                    }

                    case 2: {
                        playerVideoDTOArrayList.clear();
                        playerVideoDTOArrayList.addAll(VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).
                                getVideoListForGame(GlobalConstants.OKF_PLAYERS));
                        sortData(playerVideoDTOArrayList);
                        break;
                    }
                }

                state = STATE_SUCCESS_FRAGMENT_DATA;

                informViews();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    private void sortData(ArrayList<VideoDTO> videoDTOArrayList)
    {
        Collections.sort(videoDTOArrayList, new Comparator<VideoDTO>() {

            @Override
            public int compare(VideoDTO lhs, VideoDTO rhs) {
                // TODO Auto-generated method stub
                return lhs.getPlaylistName().toLowerCase()
                        .compareTo(rhs.getPlaylistName().toLowerCase());
            }
        });
    }

    public ArrayList<VideoDTO> getVideoDTOArrayList() {

        ArrayList<VideoDTO> videoDTOArrayList = new ArrayList<>();

        switch (GlobalConstants.CURRENT_TAB) {
            case 1: {
                videoDTOArrayList.addAll(gameVideoDTOArrayList);
                break;
            }
            case 2: {
                videoDTOArrayList.addAll(playerVideoDTOArrayList);
                break;
            }
        }

        return videoDTOArrayList;
    }
}
