package com.ncsavault.alabamavault.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;


/**
 * Created by yogita.panpaliya on 7/25/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    AsyncTask<Void, Void, Void> mRegisterTask;
    String refreshedToken;
    SharedPreferences prefs;
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        prefs = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        mRegisterTask = new RegisterTask();
        // execute AsyncTask
        mRegisterTask.execute();
    }
    // String tokenId = "dXbLimyXDhU:APA91bHldDn1Yewcx8rIabc9YH7fbf8pFMT6xykHZj8ZAO8R2Zo4KxnLolJo43fkOJXBdm6k2bJvt52UQmL98d8YBJMs7gxnTXJUqbcZst574ngZ1_Q2Qcz5jjiOMDF4ScF3iQeuUq5Y";
    private class RegisterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.i("MainActivity", "Device tokenId : = "
                        + refreshedToken);
                String deviceId = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                String result = AppController.getInstance().getServiceManager().getVaultService().sendPushNotificationRegistration(GlobalConstants.PUSH_REGISTER_URL,
                        refreshedToken, deviceId, true);
                if (result != null) {
                    Log.i("MainActivity", "Response from server after registration : = "
                            + result);
                }

                if (result.toLowerCase().contains("success")) {
                    prefs.edit().putBoolean(GlobalConstants.PREF_IS_NOTIFICATION_ALLOW, true).commit();
                    prefs.edit().putBoolean(GlobalConstants.PREF_IS_DEVICE_REGISTERED, true).commit();

                }
            } catch (Exception e) {
                Log.i("GCMIntentService", "Exception onRegistered : = " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            refreshedToken = null;

        }
    }

    ;
}
