package com.ncsavault.alabamavault.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.app.AndroidApplication;
import com.ncsavault.alabamavault.defines.AppDefines;
import com.ncsavault.alabamavault.factory.ViewFactory;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.imagecache.BitmapLruCache;
import com.ncsavault.alabamavault.models.ModelFacade;
import com.ncsavault.alabamavault.service.AndroidServiceContext;
import com.ncsavault.alabamavault.service.ServiceManager;
import com.ncsavault.alabamavault.serviceimpl.AbstractServiceManagerImpl;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

/**
 * Created by gauravkumar.singh on 7/19/2016.
 * Singleton Class
 */
public class AppController {

    //singleton instance
    private static AppController instance;

    //ModelFacade Reference
    private ModelFacade modelFacade;

    //Reference to android Application class object
    private AndroidApplication application;
    //Handler object reference
    private Handler handler;

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 10;
    private static Bitmap.CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided


    private AndroidServiceContext serviceContext;
    private ServiceManager serviceManager;
    /**
     * set it as true only when all the screens are refreshed once app came to
     * foreground after 2 hours.
     */
    private boolean isAllScreenRefreshed = true;
    private boolean isDataRefreshed = false;
    private long cacheClearTime = 999999999999999L;


    private ArrayList<String> API_URLS = new ArrayList<>();

    private Activity currentActivity;

    /**
     * Private constructor to achieve singleton design patterns
     */
    private AppController() {
        modelFacade = new ModelFacade();

        init();
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }


    /**
     * To get singleton reference of AppController class
     *
     * @return
     */
    public static AppController getInstance() {
        if (instance == null) {
            synchronized (AppController.class) {
                if (instance == null) {
                    instance = new AppController();
                }
            }

        }
        return instance;
    }

    /**
     * This function should called only once.
     * Initialize the required objects
     */
    public void initialize() {

        modelFacade.initialize();
    }

    /**
     * Destory all the required object
     */
    public void destroy() {
        modelFacade.destroy();
    }

    /**
     * Set Android application reference
     *
     * @param application
     */
    public void setApplication(AndroidApplication application) {
        this.application = application;
    }

    /**
     * Get the Android application reference
     *
     * @return
     */
    public AndroidApplication getApplication() {
        return application;
    }

    /**
     * To get the application context refernce to be used in different posstion
     *
     * @return
     */
    public Context getApplicationContext() {

        return application.getApplicationContext();
    }

    /**
     * To get the reference of the ModelFacade Class
     *
     * @return
     */
    public ModelFacade getModelFacade() {
        return modelFacade;
    }

    /**
     * Function to set the Handler reference
     *
     * @param handler
     */
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * Function to get the Handler Reference
     *
     * @return
     */
    public Handler getHandler() {
        return handler;
    }

    /**
     * HandleEvent function to manage events inside the appliction.
     * this functions should be used for various background events and activity launch activity
     *
     * @param eventID
     */
    public void handleEvent(int eventID) {
        handleEvent(eventID, null);
    }

    /**
     * HandleEvent function to manage events inside the appliction.
     * this functions should be used for various background events and activity launch activity
     *
     * @param eventID      eventID to process the particular events
     * @param eventObjects eventObjects to be passed to next activyt
     */
    public void handleEvent(int eventID, Object eventObjects) {

        Class className;
        switch (eventID) {

            case AppDefines.EVENT_ID_LOGIN_SCREEN: {
                //to launch login screen.
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.LOGIN_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_LOGIN_PASSWORD_SCREEN: {
                //to launch password screen.
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.LOGIN_PASSWORD_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_MAIN_SCREEN: {
                // to launch main screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.MAIN_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_UPLOAD_PHOTO_SCREEN: {
                // to launch upload photo screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.UPLOAD_PHOTO_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_USER_PROFILE_SCREEN: {
                // to launch user profile screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.USER_PROFILE_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_CHANGE_PASSWORD_SCREEN: {
                // to launch change password screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.CHANGE_PASSWORD_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_CONTACT_SCREEN: {
                // to launch change contact screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.CONTACT_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_VIDEO_INFO_SCREEN: {
                // to launch video info screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.VIDEO_INFO_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_FORGOT_PASSWORD_SCREEN: {
                // to launch forgot password screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.FORGOT_PASSWORD_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            case AppDefines.EVENT_ID_REGISTRATION_SCREEN: {
                // to launch registration screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.REGISTRATION_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;

            //NEW UI SCREEN

            case AppDefines.EVENT_ID_HOME_SCREEN: {
                // to launch registration screen
                className = ViewFactory.getInstance().getActivityClass(ViewFactory.HOME_SCREEN);
                ActivityUIController.getInstance().launchActivity(className, eventID, eventObjects);
            }
            break;


        }

    }

    public ServiceManager getServiceManager() {
        while (serviceManager == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        synchronized (this) {
            return serviceManager;
        }
    }

    /**
     * Intialize the request manager and the image cache
     */
    private void init() {
        //gk RequestManager.init(application);
        //gk createImageCache();

        new AsyncTask<Void, Void, Void>() {

            protected void onPreExecute() {
            }

            ;

            @Override
            protected Void doInBackground(Void... params) {
                synchronized (AppController.this) {
                    try {
                        serviceManager = new AbstractServiceManagerImpl(
                                serviceContext);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }

        }.execute(null, null, null);
    }

    /**
     * Create the image cache.
     */
    private void createImageCache() {
//        ImageCacheManager.getInstance().init(application,
//                application.getPackageCodePath()
//                , DISK_IMAGECACHE_SIZE
//                , DISK_IMAGECACHE_COMPRESS_FORMAT
//                , DISK_IMAGECACHE_QUALITY);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new BitmapLruCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    private PowerManager.WakeLock wakeLock;

    @SuppressWarnings("deprecation")
    public void acquireWakeLock(Context context) {
        if (wakeLock != null)
            wakeLock.release();

        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "WakeLock");

        wakeLock.acquire();
    }

    public void releaseWakeLock() {
        if (wakeLock != null)
            wakeLock.release();
        wakeLock = null;
    }


    public boolean isAllScreenRefreshed() {
        return isAllScreenRefreshed;
    }

    public void setAllScreenRefreshed(boolean isAllScreenRefreshed) {
        this.isAllScreenRefreshed = isAllScreenRefreshed;
    }

    public long getCacheClearTime() {
        return cacheClearTime;
    }

    public void setCacheClearTime(long cacheClearTime) {
        this.cacheClearTime = cacheClearTime;
    }

    public boolean isDataRefreshed() {
        return isDataRefreshed;
    }

    public void setDataRefreshed(boolean isDataRefreshed) {
        this.isDataRefreshed = isDataRefreshed;
    }


}
