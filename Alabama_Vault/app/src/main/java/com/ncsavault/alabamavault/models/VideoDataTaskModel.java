package com.ncsavault.alabamavault.models;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gauravkumar.singh on 12/27/2016.
 */

public class VideoDataTaskModel extends BaseModel {

    private ArrayList<VideoDTO> videoDTO;

    public void loadVideoData(HashMap<String, String> hashMap) {
        VideoDataTask videoDataTask = new VideoDataTask();
        videoDataTask.execute(hashMap);
    }

    private class VideoDataTask extends AsyncTask<HashMap, Void, ArrayList<VideoDTO>> {
        @Override
        protected ArrayList<VideoDTO> doInBackground(HashMap... params) {
            ArrayList<VideoDTO> videoList = null;
            try {
                videoList = AppController.getInstance().getServiceManager().getVaultService().
                        getVideosListFromServer(GlobalConstants.GET_VIDEO_DATA_FROM_BANNER + "?navTabId=" +
                                params[0].get("TabId").toString() + "&videoId=" + params[0].get("VideoId").toString()
                                + "&userId=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId());
                System.out.println("Video List Size from server : " + videoList.size());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            videoDTO = videoList;
            state = STATE_SUCCESS;
            informViews();
            return videoList;
        }
    }

    public ArrayList<VideoDTO> getVideoDTO() {
        return videoDTO;
    }
}
