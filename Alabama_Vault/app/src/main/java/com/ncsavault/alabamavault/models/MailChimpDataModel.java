package com.ncsavault.alabamavault.models;

import android.os.AsyncTask;
import android.util.Log;

import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.MailChimpData;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.mailchimp.org.xmlrpc.android.XMLRPCException;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.MailChimpApiException;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.lists.ListMethods;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.lists.MergeFieldListUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by gauravkumar.singh on 12/22/2016.
 */

public class MailChimpDataModel extends BaseModel {

    private MailChimpData mMailChimpData;
    private boolean mIsSignUpSuccessfull;
    private String mEmail;
    private String mFName;
    private String mLName;


    public void loadMailChimpData(MailChimpData mailChimpData, String email, String fName, String lName) {
        this.mMailChimpData = mailChimpData;
        this.mEmail = email;
        this.mFName = fName;
        this.mLName = lName;
        MailChimpTask mailChimpTask = new MailChimpTask();
        mailChimpTask.execute();
    }

    private class MailChimpTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            state = STATE_SUCCESS_MAIL_CHIMP;
            if (mMailChimpData != null && mMailChimpData.getIsRegisteredUser() == "Y" || mMailChimpData == null) {
                return addToList(mEmail, mFName, mLName);
            } else {
                 AppController.getInstance().getServiceManager().getVaultService()
                        .postMailChimpData(mMailChimpData);
            }
            informViews();
            return false;

        }
    }

    private boolean addToList(String emailId, String firstName, String lastName) {

        MergeFieldListUtil mergeFields = new MergeFieldListUtil();
        mergeFields.addEmail(emailId);
        try {
            mergeFields.addDateField("BIRFDAY", (new SimpleDateFormat("MM/dd/yyyy")).parse("07/30/2007"));
        } catch (ParseException e1) {
        }
        mergeFields.addField("FNAME", firstName);
        mergeFields.addField("LNAME", lastName);
        mergeFields.addField("PLATFORM", GlobalConstants.DEVICE_TYPE);
        mergeFields.addField("SCHOOL", GlobalConstants.APP_SCHOOL_NAME);

        // ListMethods listMethods = new ListMethods(getResources().getText(R.string.mc_api_key));
        ListMethods listMethods = new ListMethods(GlobalConstants.MAIL_CHIMP_API_KEY);

        try {
            try {
                mIsSignUpSuccessfull = listMethods.listSubscribe(GlobalConstants.MAIL_CHIMP_LIST_ID, emailId, mergeFields);
            } catch (XMLRPCException e) {
                e.printStackTrace();
                mIsSignUpSuccessfull = false;

                return mIsSignUpSuccessfull;
            }
        } catch (MailChimpApiException e) {
            Log.e("MailChimp", "Exception subscribing person: " + e.getMessage());
            e.getMessage();

            mIsSignUpSuccessfull = false;
            return mIsSignUpSuccessfull;
        }
        informViews();
        return mIsSignUpSuccessfull;

    }
}
