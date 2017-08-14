package com.ncsavault.alabamavault.models;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.defines.AppDefines;
import com.ncsavault.alabamavault.dto.TabBannerDTO;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.network.GETJsonRequest;

import org.json.JSONArray;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by gauravkumar.singh on 12/22/2016.
 */

public class BannerDataModel extends BaseModel {

    private ArrayList<VideoDTO> arrayListVideos;
    private Context context;
    private ArrayList<TabBannerDTO> tabBannerDTOs = new ArrayList<>();

    private String[] tabNameArray = {GlobalConstants.FEATURED, GlobalConstants.GAMES, GlobalConstants.PLAYERS,
            GlobalConstants.OPPONENTS, GlobalConstants.COACHES_ERA};

    private String[] tabUrl = {GlobalConstants.FEATURED_API_URL, GlobalConstants.GAMES_API_URL, GlobalConstants.PLAYER_API_URL,
            GlobalConstants.OPPONENT_API_URL, GlobalConstants.COACH_API_URL};

    private String[] tabOKFName = {GlobalConstants.OKF_FEATURED,GlobalConstants.OKF_GAMES,GlobalConstants.OKF_PLAYERS,
            GlobalConstants.OKF_OPPONENT,GlobalConstants.OKF_COACH};


    public BannerDataModel() {
        //arrayListVideos = new ArrayList<>();
        context = AppController.getInstance().getApplicationContext();
    }

    public void loadTabData()
    {
        BannerData bannerData = new BannerData();
        bannerData.execute();
    }

//    public void loadTabData1() {
//        String url = GlobalConstants.GET_ALL_TAB_BANNER_DATA_URL;
//
//        GETJsonRequest jsonRequest = new GETJsonRequest(url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        System.out.println("loadTabData");
//                        Gson gson = new Gson();
//
//                        Type classType = new TypeToken<ArrayList<TabBannerDTO>>() {
//                        }.getType();
//
//                        tabBannerDTOs = gson.fromJson(response.toString(), classType);
//
//                        loadBannerData();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        error.printStackTrace();
//
//                    }
//                });
//
//
//        Volley.newRequestQueue(AppController.getInstance().getApplicationContext()).add(jsonRequest);
//
//    }
//
//    public void loadBannerData() {
//
//        Intent broadCastIntent = new Intent();
//        try {
//            ArrayList<String> lstUrls = new ArrayList<>();
//            File imageFile;
//            System.out.println("loadTabData size : "+tabBannerDTOs.size());
//            for (TabBannerDTO bDTO : tabBannerDTOs) {
//                TabBannerDTO localBannerData = VaultDatabaseHelper.getInstance(AppController.getInstance().getApplicationContext()).getLocalTabBannerDataByTabId(bDTO.getTabId());
//                if (localBannerData != null) {
//                    if ((localBannerData.getBannerModified() != bDTO.getBannerModified()) || (localBannerData.getBannerCreated() != bDTO.getBannerCreated())) {
//                        VaultDatabaseHelper.getInstance(context.getApplicationContext()).updateBannerData(bDTO);
//                    }
//                    if (localBannerData.getTabDataModified() != bDTO.getTabDataModified()) {
//                        System.out.println("tab name : "+localBannerData.getTabName());
//                        VaultDatabaseHelper.getInstance(context.getApplicationContext()).updateTabData(bDTO);
//                        for (int i = 0; i < tabNameArray.length; i++) {
//                            System.out.println("tab name 123 : "+localBannerData.getTabName() + " and "+tabNameArray[i]);
//                            if (localBannerData.getTabName().toLowerCase().contains((tabNameArray[i]).toLowerCase())) {
//                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).removeRecordsByTab(tabOKFName[i]);
//                                lstUrls.add(tabUrl[i]);
//                                String url = tabUrl[i] + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
//                                try {
//
//                                    GETJsonRequest jsonRequest = new GETJsonRequest(url,
//                                            new Response.Listener<JSONArray>() {
//                                                @Override
//                                                public void onResponse(JSONArray response) {
//
//                                                    Gson gson = new Gson();
//
//                                                    Type classType = new TypeToken<ArrayList<VideoDTO>>() {
//                                                    }.getType();
//
//                                                    ArrayList<VideoDTO> videoList = gson.fromJson(response.toString(), classType);
//
//                                                    System.out.println("Size of video list : " + videoList.size());
//                                                    VideoDTO vidObj;
//                                                    for (Iterator<VideoDTO> it = videoList.iterator(); it.hasNext(); ) {
//                                                        vidObj = it.next();
//                                                        if (vidObj.getVideoName().equals("") && vidObj.getVideoShortDescription() == "")
//                                                            it.remove();
//                                                    }
//                                                    VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertVideosInDatabase(videoList);
//                                                }
//                                            },
//                                            new Response.ErrorListener() {
//                                                @Override
//                                                public void onErrorResponse(VolleyError error) {
//
//                                                    error.printStackTrace();
//
//                                                }
//                                            });
//                                    Volley.newRequestQueue(AppController.getInstance().getApplicationContext()).add(jsonRequest);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                        imageFile = ImageLoader.getInstance().getDiscCache().get(localBannerData.getBannerURL());
//                        if (imageFile.exists()) {
//                            imageFile.delete();
//                        }
//                        MemoryCacheUtils.removeFromCache(localBannerData.getBannerURL(), ImageLoader.getInstance().getMemoryCache());
//                        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
//                        context.sendBroadcast(broadCastIntent);
//                    }
//                } else {
//                    VaultDatabaseHelper.getInstance(AppController.getInstance().getApplicationContext()).insertTabBannerData(bDTO);
//                }
//
//            }
//            if (lstUrls.size() == 0) {
//                int count = VaultDatabaseHelper.getInstance(context.getApplicationContext()).getTabBannerCount();
//                if (count > 0) {
//                    lstUrls.add(GlobalConstants.FEATURED_API_URL);
//                    lstUrls.add(GlobalConstants.GAMES_API_URL);
//                    lstUrls.add(GlobalConstants.PLAYER_API_URL);
//                    lstUrls.add(GlobalConstants.OPPONENT_API_URL);
//                    lstUrls.add(GlobalConstants.COACH_API_URL);
//
//                }
//            }
//            AppController.getInstance().getModelFacade().getLocalModel().setAPI_URLS(lstUrls);
//            state = STATE_SUCCESS;
//            informViews();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    private class BannerData extends AsyncTask<Void, Void, ArrayList<TabBannerDTO>> {

        @Override
        protected ArrayList<TabBannerDTO> doInBackground(Void... params) {
            ArrayList<TabBannerDTO> arrayListBanner = new ArrayList<TabBannerDTO>();
            Intent broadCastIntent = new Intent();
            try {
                arrayListBanner.addAll(AppController.getInstance().getServiceManager().getVaultService().getAllTabBannerData());

                state = STATE_SUCCESS;
                ArrayList<String> lstUrls = new ArrayList<>();

                File imageFile;
                for (TabBannerDTO bDTO : arrayListBanner) {
                     TabBannerDTO localBannerData = VaultDatabaseHelper.getInstance(context.getApplicationContext()).getLocalTabBannerDataByTabId(bDTO.getTabId());
                    if (bDTO.getTabName().toLowerCase().contains((GlobalConstants.FEATURED).toLowerCase())) {
                        AppController.getInstance().getModelFacade().getLocalModel().setTabId(bDTO.getTabId());
                    }
                    if (localBannerData != null) {
                        if ((localBannerData.getBannerModified() != bDTO.getBannerModified()) || (localBannerData.getBannerCreated() != bDTO.getBannerCreated())) {
                            VaultDatabaseHelper.getInstance(context.getApplicationContext()).updateBannerData(bDTO);
                        }
                        if (localBannerData.getTabDataModified() != bDTO.getTabDataModified()) {
                            VaultDatabaseHelper.getInstance(context.getApplicationContext()).updateTabData(bDTO);
                            if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.FEATURED).toLowerCase())) {

                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).removeRecordsByTab(GlobalConstants.OKF_FEATURED);
                                lstUrls.add(GlobalConstants.FEATURED_API_URL);
                                String url = GlobalConstants.FEATURED_API_URL + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                                try {
                                    arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertVideosInDatabase(arrayListVideos);
                                //gk broadCastIntent.setAction(FeaturedFragment.FeaturedResponseReceiver.ACTION_RESP);
                            } else if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.GAMES).toLowerCase())) {

                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).removeRecordsByTab(GlobalConstants.OKF_GAMES);
                                lstUrls.add(GlobalConstants.GAMES_API_URL);
                                String url = GlobalConstants.GAMES_API_URL + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                                try {
                                    arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertVideosInDatabase(arrayListVideos);
                                //gk broadCastIntent.setAction(GamesFragment.GamesResponseReceiver.ACTION_RESP);
                            } else if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.PLAYERS).toLowerCase())) {

                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).removeRecordsByTab(GlobalConstants.OKF_PLAYERS);

                                lstUrls.add(GlobalConstants.PLAYER_API_URL);
                                String url = GlobalConstants.PLAYER_API_URL + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                                try {
                                    arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertVideosInDatabase(arrayListVideos);
                                //gk broadCastIntent.setAction(PlayerFragment.PlayerResponseReceiver.ACTION_RESP);
                            } else if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.OPPONENTS).toLowerCase())) {

                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).removeRecordsByTab(GlobalConstants.OKF_OPPONENT);
                                lstUrls.add(GlobalConstants.OPPONENT_API_URL);
                                String url = GlobalConstants.OPPONENT_API_URL + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                                try {
                                    arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertVideosInDatabase(arrayListVideos);
                                //gk broadCastIntent.setAction(OpponentsFragment.OpponentsResponseReceiver.ACTION_RESP);
                            } else if (localBannerData.getTabName().toLowerCase().contains((GlobalConstants.COACHES_ERA).toLowerCase())) {

                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).removeRecordsByTab(GlobalConstants.OKF_COACH);
                                lstUrls.add(GlobalConstants.COACH_API_URL);
                                String url = GlobalConstants.COACH_API_URL + "userid=" + AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                                try {
                                    arrayListVideos.addAll(AppController.getInstance().getServiceManager().getVaultService().getVideosListFromServer(url));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertVideosInDatabase(arrayListVideos);
                                //gk broadCastIntent.setAction(CoachesEraFragment.CoachesResponseReceiver.ACTION_RESP);
                            }
                            imageFile = ImageLoader.getInstance().getDiscCache().get(localBannerData.getBannerURL());
                            if (imageFile.exists()) {
                                imageFile.delete();
                            }
                            MemoryCacheUtils.removeFromCache(localBannerData.getBannerURL(), ImageLoader.getInstance().getMemoryCache());
                            broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            context.sendBroadcast(broadCastIntent);
                            arrayListVideos.clear();
                        }
                    } else {
                        VaultDatabaseHelper.getInstance(context.getApplicationContext()).insertTabBannerData(bDTO);
                    }

                }
                if (lstUrls.size() == 0) {
                    int count = VaultDatabaseHelper.getInstance(context.getApplicationContext()).getTabBannerCount();
                    if (count > 0) {
                        lstUrls.add(GlobalConstants.FEATURED_API_URL);
                        lstUrls.add(GlobalConstants.GAMES_API_URL);
                        lstUrls.add(GlobalConstants.PLAYER_API_URL);
                        lstUrls.add(GlobalConstants.OPPONENT_API_URL);
                        lstUrls.add(GlobalConstants.COACH_API_URL);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (arrayListBanner.size() == 0) {
                state = STATE_RESULT_NOT_FOUND;
            }
            informViews();
            return arrayListBanner;
        }

    }

}
