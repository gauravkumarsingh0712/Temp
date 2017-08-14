package com.ncsavault.alabamavault.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.fragments.views.FeaturedFragment;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.utils.Utils;

import java.util.ArrayList;

/**
 * Created by krunal.boxey on 8/11/2017.
 */

public class TrendingFeaturedVideoService extends Service {

    ArrayList<VideoDTO> arrayListVideos = new ArrayList<VideoDTO>();
    ArrayList<String> apiUrls = new ArrayList<>();

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        //apiUrls = intent.getStringArrayListExtra("apiUrls");
        String url = "";
        apiUrls.clear();
        apiUrls.add(GlobalConstants.GET_TRENDING_PLAYLIST_URL);
        apiUrls.add(GlobalConstants.FEATURED_API_URL);

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "";
                for (String apiUrl : apiUrls) {

                    if (Utils.isInternetAvailable(AppController.getInstance().getApplicationContext())) {

                        url = apiUrl + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();

                        try {
                            System.out.println("Size of list after calling " + apiUrl + " : " + arrayListVideos.size());
                            if(url.contains("Featured")) {
                                arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                VaultDatabaseHelper.getInstance(getApplicationContext()).insertVideosInDatabase(arrayListVideos);
                            }else if(url.contains("TrendingPlayList")){
                                arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                VaultDatabaseHelper.getInstance(getApplicationContext()).insertTrendingVideosInDatabase(arrayListVideos);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        arrayListVideos.clear();
                        System.out.println("tabBannerDTO thread end ");

                    }
                }

                Intent broadCastIntent = new Intent();
                broadCastIntent.setAction(FeaturedFragment.FeaturedResponseReceiver.ACTION_RESP);
                broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                sendBroadcast(broadCastIntent);

                stopSelf();
            }

        }).start();

        return Service.START_NOT_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
