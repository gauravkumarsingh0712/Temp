package com.ncsavault.alabamavault.firebase;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.views.MainActivity;
import com.ncsavault.alabamavault.views.SplashActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by yogita.panpaliya on 7/25/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private FirebaseAnalytics mFirebaseAnalytics;
    Bundle params = new Bundle();

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//
//              // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//        }
//
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        try {
            if (remoteMessage.getData() != null) {
                String message = remoteMessage.getData().get("message");
                String title = remoteMessage.getData().get("title");
                String videoId = remoteMessage.getData().get("tickerText");

// Check if message contains a notification payload.
//        String message = remoteMessage.getNotification().getBody();
//        String title = remoteMessage.getNotification().getTitle();
//        LocalModel.getInstance().setVideoId(remoteMessage.getNotification().getTag());
//        Log.d(TAG, "Message data payload getTag  : " + remoteMessage.getNotification().getTag());
//       // sendNotification(remoteMessage.getNotification().getBody());


                sendNotificationCustomView(message, title,videoId);
            }
            boolean isBackground = isAppIsInBackground(this);
            if(isBackground)
            {
                params.putString(GlobalConstants.NOTIFICATION_RECEIVE, GlobalConstants.NOTIFICATION_RECEIVE);
                mFirebaseAnalytics.logEvent(GlobalConstants.NOTIFICATION_RECEIVE, params);
                mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
            }else {
                params.putString(GlobalConstants.NOTIFICATION_FOREGROUND, GlobalConstants.NOTIFICATION_FOREGROUND);
                mFirebaseAnalytics.logEvent(GlobalConstants.NOTIFICATION_FOREGROUND, params);
                mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
            }
            System.out.println("app is background : "+isBackground);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // sendNotificationCustomView();
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.logo)
                .setContentTitle(getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setContentText(messageBody).setLights(Color.GREEN, 300, 300)
                .setVibrate(new long[] { 100, 250 })
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true);

        mBuilder.setContentIntent(pendingIntent);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(m, mBuilder.build());
    }

    private void sendNotificationCustomView(String message, String title, String video){
        try {
            Bitmap remote_picture = BitmapFactory.decodeResource(
                    getResources(), R.drawable.logo);
            Calendar c = Calendar.getInstance();
            String currTime = "";
            if (c.get(Calendar.HOUR_OF_DAY) < 10)
                currTime += "0" + c.get(Calendar.HOUR_OF_DAY) + ":";
            else
                currTime += c.get(Calendar.HOUR_OF_DAY) + ":";

            if (c.get(Calendar.MINUTE) < 10)
                currTime += "0" + c.get(Calendar.MINUTE);
            else
                currTime += c.get(Calendar.MINUTE);

            Intent resultIntent = new Intent(this, SplashActivity.class);
            resultIntent.putExtra("key", video);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(iUniqueId,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            // The custom view
            RemoteViews expandedView = new RemoteViews(getPackageName(),
                    R.layout.notification_custom_view);
            expandedView.setTextViewText(R.id.notification_title, title);
            expandedView.setTextViewText(R.id.notification_subtitle, message);
            expandedView.setTextViewText(R.id.tv_notification_time, currTime);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        this).setAutoCancel(true).setContentTitle(title)
                        .setSmallIcon(R.drawable.logo).setLargeIcon(remote_picture)
                        .setContentIntent(resultPendingIntent)
                        .setContentText(message);
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
                mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);

                NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                bigText.bigText(message);
                bigText.setBigContentTitle(title);
                mBuilder.setStyle(bigText);

                Random random = new Random();
                int m = random.nextInt(9999 - 1000) + 1000;
                NotificationManager mNotificationManager = (NotificationManager) this
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(m, mBuilder.build());
            } else {
                // this notification appears properly below 4.4.4
                Notification notificationBuilder = new NotificationCompat.Builder(
                        this)
                        .setSmallIcon(R.drawable.logo)
                        .setLargeIcon(remote_picture)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(message.toString()))
                        .setContent(expandedView).build();
                notificationBuilder.defaults |= Notification.DEFAULT_LIGHTS;
                notificationBuilder.defaults |= Notification.DEFAULT_VIBRATE;
                notificationBuilder.defaults |= Notification.DEFAULT_SOUND;

                Random random = new Random();
                int m = random.nextInt(9999 - 1000) + 1000;

                NotificationManager mNotificationManager = (NotificationManager)this
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(m, notificationBuilder);
            }
        } catch (Exception e) {
            Log.i("GCMIntentService", "Exception generateNotificationCustomView : = " + e.getMessage());
        }
    }
}
