package com.ncsavault.alabamavault.models;

import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.APIResponse;
import com.ncsavault.alabamavault.dto.User;
import java.lang.reflect.Type;

/**
 * Created by gauravkumar.singh on 12/23/2016.
 */

public class UserProfileModel extends BaseModel {

    private User mResponeUser;
    private String mEmail;
    private long mUserId;
    private Boolean userProfileResult;
    private String fetchingResult;


    public void loadUserProfileData(User user, String email, long userId) {
        this.mResponeUser = user;
        this.mEmail = email;
        this.mUserId = userId;

        UpdateUserProfileTask updateUserProfileTask = new UpdateUserProfileTask();
        updateUserProfileTask.execute();
    }

    public void loadFetchData(String email, long userId) {
        this.mEmail = email;
        this.mUserId = userId;

        FetchingTask fetchingTask = new FetchingTask();
        fetchingTask.execute();

    }

    private class UpdateUserProfileTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String result = AppController.getInstance().getServiceManager().getVaultService().updateUserData(mResponeUser);
                System.out.println("Result of user data updating : " + result);
                Gson gson = new Gson();
                Type classType = new TypeToken<APIResponse>() {
                }.getType();
                APIResponse response = gson.fromJson(result.trim(), classType);
                if (response != null) {
                    if (response.getReturnStatus().toLowerCase().equals("success")) {
                        String userJsonData = AppController.getInstance().getServiceManager().getVaultService().getUserData(mUserId, mEmail);
                        if (!userJsonData.isEmpty()) {
                            Type classUserType = new TypeToken<User>() {
                            }.getType();
                            System.out.println("User Data : " + userJsonData);
                            User responseUser = gson.fromJson(userJsonData.trim(), classUserType);
                            if (responseUser != null) {
                                userProfileResult = true;
                                state = STATE_SUCCESS;
                                informViews();
                                if (responseUser.getUserID() > 0) {
                                    AppController.getInstance().getModelFacade().getLocalModel().storeUserDataInPreferences(responseUser);
                                    return true;
                                }

                            } else
                                return false;
                        } else
                            return false;
                    } else
                        return false;
                } else {
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    private class FetchingTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            String result = AppController.getInstance().getServiceManager().getVaultService().getUserData(mUserId, mEmail);
            fetchingResult = result;
            state = STATE_SUCCESS_FETCH_ALL_DATA;
            informViews();
            return result;
        }
    }

    public Boolean getUserProfileResult() {
        return userProfileResult;
    }

    public String getFetchingResult() {
        return fetchingResult;
    }

}
