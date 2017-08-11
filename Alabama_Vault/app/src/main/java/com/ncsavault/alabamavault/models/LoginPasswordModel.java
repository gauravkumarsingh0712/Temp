package com.ncsavault.alabamavault.models;

import android.os.AsyncTask;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.network.GETJsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by gauravkumar.singh on 12/21/2016.
 */

public class LoginPasswordModel extends BaseModel {

    private String mEmailPasswordResult;
    private String mUserName;
    private String mUserNameResult;


//    public void loadEmailAndPassData1(String email, String password) {
//        ArrayList<String> stringArrayList = new ArrayList<>();
//        stringArrayList.add(email);
//        stringArrayList.add(password);
//        mEmailPassList = stringArrayList;
//        ValidateTask validateTask = new ValidateTask();
//        validateTask.execute();
//    }

    public void loadEmailAndPassData(String email, String password) {

        String url = GlobalConstants.VALIDATE_USER_CREDENTIALS_URL + "?emailID=" + email + "&pass=" + password;

        GETJsonObjectRequest jsonRequest = new GETJsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        state = STATE_SUCCESS_EMAIL_PASSWORD_DATA;
                        mEmailPasswordResult = response.toString();
                        informViews();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();

                    }
                });
        Volley.newRequestQueue(AppController.getInstance().getApplicationContext()).add(jsonRequest);
    }

    public void loadUserNameData1(String username) {
//        this.mUserName = username;
//        UserNameTask userNameTask = new UserNameTask();
//        userNameTask.execute();
    }

    public void loadUserNameData(String username) {

        String url = null;
        try {
            url = GlobalConstants.VALIDATE_USERNAME_URL + URLEncoder.encode(username, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        GETJsonObjectRequest jsonRequest = new GETJsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        state = STATE_SUCCESS_USERNAME_DATA;
                        mUserNameResult = response.toString();
                        informViews();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();

                    }
                });
        Volley.newRequestQueue(AppController.getInstance().getApplicationContext()).add(jsonRequest);
    }


//    public class ValidateTask extends AsyncTask<Void, Void, String> {
//        @Override
//        protected String doInBackground(Void... params) {
//            String result = AppController.getInstance().getServiceManager().getVaultService().
//                    validateUserCredentials(mEmailPassList.get(0), mEmailPassList.get(1));
//            state = STATE_SUCCESS_EMAIL_PASSWORD_DATA;
//            mEmailPasswordResult = result;
//            informViews();
//            mEmailPassList.clear();
//            return result;
//        }
//    }

//    private class UserNameTask extends AsyncTask<Void, Void, String> {
//        @Override
//        protected String doInBackground(Void... params) {
//
//            String userName = AppController.getInstance().getServiceManager().getVaultService().validateUsername(mUserName);
//            state = STATE_SUCCESS_USERNAME_DATA;
//            mUserNameResult = userName;
//            informViews();
//            return userName;
//        }
//    }

    public String getmEmailPasswordResult() {
        return mEmailPasswordResult;
    }

    public String getmUserNameResult() {
        return mUserNameResult;
    }

    public int getState() {
        return state;
    }
}
