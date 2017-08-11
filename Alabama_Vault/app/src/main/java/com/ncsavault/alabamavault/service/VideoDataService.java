package com.ncsavault.alabamavault.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.VideoDTO;

import com.ncsavault.alabamavault.fragments.views.CoachesEraFragment;
import com.ncsavault.alabamavault.fragments.views.FeaturedFragment;
import com.ncsavault.alabamavault.fragments.views.GamesFragment;
import com.ncsavault.alabamavault.fragments.views.OpponentsFragment;
import com.ncsavault.alabamavault.fragments.views.PlayerFragment;
import com.ncsavault.alabamavault.utils.Utils;

import java.util.ArrayList;

/**
 * Created by aqeeb.pathan on 24-06-2015.
 */
public class VideoDataService extends Service {

    public static boolean isServiceRunning = false;
    ArrayList<VideoDTO> arrayListVideos = new ArrayList<VideoDTO>();

    ArrayList<String> lstUrls = new ArrayList<>();

    boolean status = true;

    @Override
    public void onCreate() {
        super.onCreate();
        lstUrls.addAll(AppController.getInstance().getModelFacade().getLocalModel().getAPI_URLS());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
        AppController.getInstance().getModelFacade().getLocalModel().getAPI_URLS().clear();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isServiceRunning = true;
        try {

            System.out.println("tabBannerDTO service " + lstUrls.size());
            if (Utils.isInternetAvailable(AppController.getInstance().getApplicationContext())) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {

                        for (String apiUrl : lstUrls) {
                            status = true;
                            System.out.println("tabBannerDTO thread " + isServiceRunning);
                            if (Utils.isInternetAvailable(AppController.getInstance().getApplicationContext())) {
                                if (isServiceRunning) {
                                    String url = "";
                                    if (apiUrl.contains("TrendingPlayList")) {
                                        url = apiUrl + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId() + "&Trending List";
                                    } else {
                                        url = apiUrl + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                                    }
                                    try {
                                        arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                        System.out.println("Size of list after calling " + apiUrl + " : " + arrayListVideos.size());

                                    } catch (Exception e) {

                                        status = false;
                                        e.printStackTrace();
                                    }

                                    if (status)
                                        VaultDatabaseHelper.getInstance(getApplicationContext()).insertVideosInDatabase(arrayListVideos);

                                    Intent broadCastIntent = new Intent();
                                    if (url.toLowerCase().contains("featured") || url.toLowerCase().contains("TrendingPlayList")) {
                                        broadCastIntent.setAction(FeaturedFragment.FeaturedResponseReceiver.ACTION_RESP);
                                    }

//                                    else if (url.toLowerCase().contains("games")) {
//                                         broadCastIntent.setAction(GamesFragment.GamesResponseReceiver.ACTION_RESP);
//                                    } else if (url.toLowerCase().contains("player")) {
//                                         broadCastIntent.setAction(PlayerFragment.PlayerResponseReceiver.ACTION_RESP);
//                                    } else if (url.toLowerCase().contains("coach")) {
//                                         broadCastIntent.setAction(CoachesEraFragment.CoachesResponseReceiver.ACTION_RESP);
//                                    } else if (url.toLowerCase().contains("opponent"))
//                                    {
//                                         broadCastIntent.setAction(OpponentsFragment.OpponentsResponseReceiver.ACTION_RESP);
//                                    }

                                    broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                    sendBroadcast(broadCastIntent);
                                    arrayListVideos.clear();
                                    System.out.println("tabBannerDTO thread end ");
                                }
                            } else {
                                isServiceRunning = false;
                                AppController.getInstance().getModelFacade().getLocalModel().getAPI_URLS().clear();
                                stopSelf();
                            }
                        }
                        isServiceRunning = false;
                        AppController.getInstance().getModelFacade().getLocalModel().getAPI_URLS().clear();
                        stopSelf();
                    }
                };
                thread.start();

            }

        } catch (Exception e) {
            System.out.println("sevice exception : " + e.getMessage());
            e.printStackTrace();
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
