package com.ncsavault.alabamavault.models;

import android.os.AsyncTask;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.APIResponse;
import com.ncsavault.alabamavault.dto.User;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.network.GETJsonObjectRequest;
import com.ncsavault.alabamavault.network.GETJsonRequest;
import com.ncsavault.alabamavault.network.POSTJsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gauravkumar.singh on 12/21/2016.
 */

public class FBLoginModel extends BaseModel {

    private User mSocailUser;
    private long mUserId;
    private String mResultData;
    private String returnPostData;

    public FBLoginModel() {

    }

    public void fetchData(User user) {
        this.mSocailUser = user;
        FBLoginTask fbLoginModel = new FBLoginTask();
        fbLoginModel.execute();
    }

//    public void fetchData(User user) {
//        this.mSocailUser = user;
//        String emailId = mSocailUser.getEmailID();
//        String flagStatus = "fb";
//        String url = GlobalConstants.VALIDATE_SOCIAL_LOGIN_URL + "?emailID=" + emailId + "&flagStatus=" + flagStatus;
//
//        GETJsonObjectRequest jsonRequest = new GETJsonObjectRequest(url,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            String result = "";
//                            Gson gson = new Gson();
//                            Type classType = new TypeToken<APIResponse>() {
//                            }.getType();
//                            APIResponse responseData = gson.fromJson(response.toString().trim(), classType);
//                           // if (responseData.getReturnStatus().toLowerCase().contains("fail")) {
//                                result = postUserData(mSocailUser);
//                          //  }
////                            else if (responseData.getReturnStatus().toLowerCase().contains("success")) {
////                                result = "success";
////                                mUserId = responseData.getUserID();
////                            }
//
//                            mResultData = result;
//                            state = STATE_SUCCESS_FETCH_FB_DATA;
//                            informViews();
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
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
//        Volley.newRequestQueue(AppController.getInstance().getApplicationContext()).add(jsonRequest);
//
//    }
//
//    private String postUserData(User user) {
//        String url = GlobalConstants.POST_USER_DATA_URL;
//        String postStr = new Gson().toJson(user);
//        POSTJsonRequest jsonRequest = new POSTJsonRequest(url, postStr,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//
//                            returnPostData = response.toString();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
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
//        Volley.newRequestQueue(AppController.getInstance().getApplicationContext()).add(jsonRequest);
//        return returnPostData;
//    }


    private class FBLoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            try {
//            String validationResult = AppController.getInstance().getServiceManager().getVaultService().
//                    validateSocialLogin(mSocailUser.getEmailID(), "fb");
//
//                Gson gson = new Gson();
//                Type classType = new TypeToken<APIResponse>() {
//                }.getType();
//                APIResponse response = gson.fromJson(validationResult.trim(), classType);
               // if (response.getReturnStatus().toLowerCase().contains("fail")) {
                    result = AppController.getInstance().getServiceManager().getVaultService().
                            postUserData(mSocailUser);
              //  }
//                else if (response.getReturnStatus().toLowerCase().contains("success")) {
//                    result = "success";
//                    mUserId = response.getUserID();
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mResultData = result;
            state = STATE_SUCCESS_FETCH_FB_DATA;
            informViews();
            return result;
        }
    }

    public long getUserId() {
        return mUserId;
    }

    public String getResultData() {
        return mResultData;
    }
}
