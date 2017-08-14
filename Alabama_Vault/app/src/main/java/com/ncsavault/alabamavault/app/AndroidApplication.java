package com.ncsavault.alabamavault.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.utils.FontsOverride;

import io.fabric.sdk.android.Fabric;


/**
 * Created by gauravkumar.singh on 7/19/2016.
 * Application Class which is starting part of any android application
 */
public class AndroidApplication extends Application {

    //handler will be used as global variable in the application. wherever
    //it is required
    private Handler handler = new Handler();


    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        //MultiDex.install(this);
        AppController.getInstance().setApplication(this);
        AppController.getInstance().setHandler(handler);

        AppController.getInstance().initialize();

       //gk CrashManager.initialize(this, GlobalConstants.HOCKEY_APP_ID, null);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(GlobalConstants.TWITTER_CONSUMER_KEY, GlobalConstants.TWITTER_CONSUMER_SECRET);
        Fabric.with(this, new Twitter(authConfig),new Crashlytics());

        FlurryAgent.init(this, GlobalConstants.FLURRY_KEY);

        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Roboto-Regular.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Roboto-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Roboto-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Roboto-Regular.ttf");
        FontsOverride.overrideFont(this, "SERIF", "fonts/OpenSans-Regular.ttf");
    }

    @Override
    public void onLowMemory() {

        super.onLowMemory();
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {

        super.onTrimMemory(level);
    }


}
