package com.ncsavault.alabamavault.controllers;

import android.content.Intent;

import com.ncsavault.alabamavault.defines.AppDefines;

import java.io.Serializable;

/**
 * Created by gauravkumar.singh on 7/19/2016.
 * Singleton Class
 */
public class ActivityUIController {


    //singleton instance
    private static ActivityUIController instance;

    //private contructor to achieve Singleton design patterns
    private ActivityUIController() {

    }

    /**
     * Get the instance of ActivityUIController
     * @return
     */
    public static ActivityUIController getInstance() {
        if (instance == null) {
            synchronized (ActivityUIController.class) {
                if (instance == null) {
                    instance = new ActivityUIController();
                }
            }
        }

        return instance;
    }

    /**
     * To launch the activity
     * @param className The Activity class object to launch the required activity
     * @param eventId the event id transaction for future purpose
     * @param eventObject Object reference to be passed to newly created activity
     */
    public void launchActivity(Class className, int eventId, Object eventObject) {
        Intent intent = new Intent(AppController.getInstance().getApplicationContext(), className);
        intent.putExtra("eventId", eventId);
        if(eventObject!=null && eventObject instanceof Serializable)
            intent.putExtra("eventObject",(Serializable)eventObject);
//        if (eventId == AppDefines.EVENT_ID_MAIN_SCREEN) {
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        } else
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        AppController.getInstance().getApplicationContext().startActivity(intent);
    }
}
