package com.ncsavault.alabamavault.defines;

/**
 * Created by gauravkumar.singh on 7/19/2016.
 */
public interface AppDefines {

   //events for launching different screen
   int EVENT_ID_SPLASH_SCREEN = 0;
   int EVENT_ID_LOGIN_SCREEN = 1;
   int EVENT_ID_LOGIN_PASSWORD_SCREEN = 2;
   int EVENT_ID_MAIN_SCREEN = 3;
   int EVENT_ID_UPLOAD_PHOTO_SCREEN = 4;
   int EVENT_ID_USER_PROFILE_SCREEN = 5;
   int EVENT_ID_CHANGE_PASSWORD_SCREEN = 6;
   int EVENT_ID_CONTACT_SCREEN = 7;
   int EVENT_ID_VIDEO_INFO_SCREEN = 8;
   int EVENT_ID_FORGOT_PASSWORD_SCREEN = 9;
   int EVENT_ID_REGISTRATION_SCREEN = 10;


   //new UI screen:-
   int EVENT_ID_HOME_SCREEN = 11;

   //local url
   String SERVER_URL_API = "http://10.10.10.233/";

   String HOME_URL = SERVER_URL_API+"First";
}
