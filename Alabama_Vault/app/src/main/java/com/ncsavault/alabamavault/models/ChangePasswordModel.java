package com.ncsavault.alabamavault.models;

import android.os.AsyncTask;

import com.ncsavault.alabamavault.controllers.AppController;

/**
 * Created by gauravkumar.singh on 12/23/2016.
 */

public class ChangePasswordModel extends BaseModel {

    private String resultData;
    private String mOldPass;
    private String mNewPass;

    public void loadChnagePasswordData(String oldPass,String newPass)
    {
        mOldPass = oldPass;
        mNewPass = newPass;
        ChangePasswordTask changePasswordTask = new ChangePasswordTask();
        changePasswordTask.execute();
    }


    private class ChangePasswordTask extends AsyncTask<Void,Void,String>
    {
        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                result = AppController.getInstance().getServiceManager().getVaultService().changeUserPassword(AppController.getInstance().
                        getModelFacade().getLocalModel().getUserId(), mOldPass, mNewPass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            resultData = result;
            informViews();
            state = STATE_SUCCESS;
            return result;
        }
    }

    public String getResult()
    {
        return resultData;
    }
}
