package com.ncsavault.alabamavault.AsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.MainActivity;
import com.ncsavault.alabamavault.views.VideoInfoActivity;

import java.util.ArrayList;

/**
 * Created by yogita.panpaliya on 9/23/2016.
 */

public class VideoPlayTask  extends AsyncTask<String, Void, VideoDTO> {

    private Activity mActivity;
    private ProgressDialog pDialog;
    String videoCategory;

    public VideoPlayTask(Activity mActivity, ProgressDialog pDialog, String videoCategory) {
        this.mActivity = mActivity;
        this.pDialog = pDialog;
        this.videoCategory = videoCategory;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(mActivity, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(mActivity));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
    }

    @Override
    protected VideoDTO doInBackground(String... params) {

        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
        VideoDTO videoData = null;
        try {
            videoData = AppController.getInstance().getServiceManager().getVaultService().
                    getVideosDataFromServer(GlobalConstants.GET_VIDEO_DATA + "?videoid=" + params[0] + "&userid=" + userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return videoData;
    }


    @Override
    protected void onPostExecute(VideoDTO videoDTOs) {
        super.onPostExecute(videoDTOs);
        if (videoDTOs != null) {
            ArrayList<VideoDTO> videoDTOArrayList = new ArrayList<>();
            videoDTOArrayList.add(videoDTOs);
            VaultDatabaseHelper.getInstance(mActivity.getApplicationContext()).insertVideosInDatabase(videoDTOArrayList);
            if (Utils.isInternetAvailable(mActivity)) {
                if (videoDTOs.getVideoLongUrl() != null) {
                    if (videoDTOs.getVideoLongUrl().length() > 0 && !videoDTOs.getVideoLongUrl().toLowerCase().equals("none")) {
                        String videoCategories = videoCategory;
                        Intent intent = new Intent(mActivity,
                                VideoInfoActivity.class);
                        intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategories);
                        intent.putExtra(GlobalConstants.PLAYLIST_REF_ID, videoDTOs.getPlaylistReferenceId());
                        intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDTOs);
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

        } else {
            ((MainActivity) mActivity).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
        }
        pDialog.dismiss();
    }
}
