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
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.network.GETJsonObjectRequest;
import com.ncsavault.alabamavault.views.LoginEmailActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gauravkumar.singh on 12/21/2016.
 */

public class LoginEmailModel extends BaseModel {

    private String mLoginResult;
    private String mEmail;
    LoginTask loginTask;

    public LoginEmailModel() {

    }

    public void loadLoginData(String email) {
        this.mEmail = email;
        loginTask = new LoginTask();
        loginTask.execute();
    }

//    public void loadLoginData(String email) {
//        this.mEmail = email;
//        String url = null;
//        try {
//            url = GlobalConstants.VALIDATE_EMAIL_URL + URLEncoder.encode(mEmail, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        GETJsonObjectRequest jsonRequest = new GETJsonObjectRequest(url,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            mLoginResult = response.toString();
//                            state = STATE_SUCCESS;
//                            informViews();
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
//    }


    public class LoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            String validateValue = AppController.getInstance().getServiceManager().
                    getVaultService().validateEmail(mEmail);
            mLoginResult = validateValue;
            state = STATE_SUCCESS;
            informViews();
            return validateValue;
        }
    }


    public String getLoginResult() {
        return mLoginResult;
    }

    public int getState() {
        return state;
    }
}
