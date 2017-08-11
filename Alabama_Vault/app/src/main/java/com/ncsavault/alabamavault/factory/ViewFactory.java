package com.ncsavault.alabamavault.factory;


import com.ncsavault.alabamavault.views.ChangePasswordActivity;
import com.ncsavault.alabamavault.views.ContactActivity;
import com.ncsavault.alabamavault.views.ForgotPasswordActivity;
import com.ncsavault.alabamavault.views.HomeScreen;
import com.ncsavault.alabamavault.views.LoginEmailActivity;
import com.ncsavault.alabamavault.views.LoginPasswordActivity;
import com.ncsavault.alabamavault.views.MainActivity;
import com.ncsavault.alabamavault.views.RegistrationActivity;
import com.ncsavault.alabamavault.views.SplashActivity;
import com.ncsavault.alabamavault.views.UploadPhotoActivity;
import com.ncsavault.alabamavault.views.UserProfileActivity;
import com.ncsavault.alabamavault.views.VideoInfoActivity;

/**
 * Created by gauravkumar.singh on 7/19/2016.
 * Class using factory pattern to deliver the activity class reference to launch new screen
 */
public class ViewFactory {

    //instance of singleton task
    private static ViewFactory instance;

    // screen id to launch splash screen
    public static final int SPLASH_SCREEN = 0;
    public static final int LOGIN_SCREEN = 1;
    public static final int LOGIN_PASSWORD_SCREEN = 2;
    public static final int MAIN_SCREEN = 3;
    public static final int UPLOAD_PHOTO_SCREEN = 4;
    public static final int USER_PROFILE_SCREEN = 5;
    public static final int CHANGE_PASSWORD_SCREEN = 6;
    public static final int CONTACT_SCREEN = 7;
    public static final int VIDEO_INFO_SCREEN = 8;
    public static final int FORGOT_PASSWORD_SCREEN = 9;
    public static final int REGISTRATION_SCREEN = 10;

    //NEW UI SCREEN
    public static final int HOME_SCREEN = 11;


    /**
     * To get reference of ViewFactory
     *
     * @return
     */
    public static ViewFactory getInstance() {
        if (instance == null) {
            synchronized (ViewFactory.class) {
                if (instance == null) {
                    instance = new ViewFactory();
                }
            }
        }
        return instance;
    }

    /**
     * To return activity class name.
     *
     * @param id
     * @return
     */
    public Class getActivityClass(int id) {
        switch (id) {
            case LOGIN_SCREEN: {
                return LoginEmailActivity.class;
            }
            case LOGIN_PASSWORD_SCREEN: {
                return LoginPasswordActivity.class;
            }
            case MAIN_SCREEN: {
                return MainActivity.class;
            }
            case UPLOAD_PHOTO_SCREEN: {
                return UploadPhotoActivity.class;
            }
            case USER_PROFILE_SCREEN: {
                return UserProfileActivity.class;
            }
            case CHANGE_PASSWORD_SCREEN: {
                return ChangePasswordActivity.class;
            }
            case CONTACT_SCREEN: {
                return ContactActivity.class;
            }
            case VIDEO_INFO_SCREEN: {
                return VideoInfoActivity.class;
            }
            case FORGOT_PASSWORD_SCREEN: {
                return ForgotPasswordActivity.class;
            }
            case REGISTRATION_SCREEN: {
                return RegistrationActivity.class;
            }
            case HOME_SCREEN: {
                return HomeScreen.class;
            }

        }
        return null;
    }
}
