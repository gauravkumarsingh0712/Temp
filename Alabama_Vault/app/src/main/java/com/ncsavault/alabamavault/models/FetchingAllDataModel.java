package com.ncsavault.alabamavault.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.User;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.network.GETJsonObjectRequest;
import com.ncsavault.alabamavault.utils.Utils;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;

/**
 * Created by gauravkumar.singh on 12/21/2016.
 */

public class FetchingAllDataModel extends BaseModel {

    private Context context;
    private boolean aBoolean;
    private User responeUserData;

    public FetchingAllDataModel() {

        context = AppController.getInstance().getApplicationContext();
    }

    public void fetchData()
    {
        FetchingDataTask fetchingDataTask =  new FetchingDataTask();
        fetchingDataTask.execute();
    }

    public int getState() {
        return state;
    }

    private class FetchingDataTask extends AsyncTask<Void,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean status = true;
            String userJsonData = "";
            try {
                SharedPreferences pref = context.getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                final long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
                final String email = pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, "");
                userJsonData = AppController.getInstance().getServiceManager().getVaultService().getUserData(userId, email);

                if (userJsonData != null) {
                    if (!userJsonData.isEmpty()) {
                        Gson gson = new Gson();
                        Type classType = new TypeToken<User>() {
                        }.getType();
                        System.out.println("User Data : " + userJsonData);
                        User responseUser = gson.fromJson(userJsonData.trim(), classType);
                        responeUserData = responseUser;
                        if (responseUser != null) {
                            if (responseUser.getUserID() > 0) {
                                AppController.getInstance().getModelFacade().getLocalModel().storeUserDataInPreferences(responseUser);
                            }
                        }
                    }
                }

                status =  Utils.loadDataFromServer(context);
                state =STATE_SUCCESS_FETCH_ALL_DATA;
            } catch (Exception e) {
                e.printStackTrace();
                status = false;
            }
            aBoolean = status;
            informViews();
            return status;
        }
    }

    public Boolean getABoolean()
    {
        return aBoolean;
    }

    public User getResponeUserData()
    {
        return responeUserData;
    }

  }
