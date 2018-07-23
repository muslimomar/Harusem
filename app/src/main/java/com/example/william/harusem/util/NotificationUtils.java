package com.example.william.harusem.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.example.william.harusem.R;
import com.example.william.harusem.ui.activities.ChatActivity;
import com.example.william.harusem.ui.activities.ProfileActivity;


public class NotificationUtils {
    private static final String CHANNEL_ONE_ID = "com.quickblox.samples.ONE";// The id of the channel.
    private static final String CHANNEL_ONE_NAME = "Messages";
    private static final String CHANNEL_TWO_ID = "com.quickblox.sample.TWO";
    private static final String CHANNEL_TWO_NAME = "Friends Requests";

    public static void showNotification(Context context, Class<? extends Activity> activityClass,
                                        String title, String message, String dialogId, @DrawableRes int icon,
                                        int notificationId) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelIfNotExist(notificationManager);
        }

        Notification notification = buildNotification(context, activityClass, title, message, icon, dialogId);

        notificationManager.notify(notificationId, notification);
    }

    public static void showFriendRequestNotification(Context context, Class<? extends Activity> activityClass, String friendName, int icon, int notificationId) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelIfNotExist(notificationManager);
        }

        Notification notification = buildFriendRequestNotification(context, activityClass, friendName, icon);

        notificationManager.notify(notificationId, notification);
    }

    public static void showAcceptedFriendNotification(Context context, Class<? extends Activity> activityClass, String friendName, int icon, int notificationId, String userId) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelIfNotExist(notificationManager);
        }
        Notification notification = buildAcceptedFriendRequestNotification(context, activityClass, friendName, icon, userId);
        notificationManager.notify(notificationId, notification);
    }

    private static Notification buildAcceptedFriendRequestNotification(Context context, Class<? extends Activity> activityClass, String friendName, @DrawableRes int icon, String userId) {
        Uri defaultSoundsUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String newline = System.getProperty("line.separator");

        return new NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                .setSmallIcon(icon)
                .setContentTitle("HARUSEM")
                .setContentText(friendName + " " + context.getString(R.string.push_accepted))
                .setAutoCancel(true)
                .setSound(defaultSoundsUri)
                //.setContentIntent(buildContentIntentForAcceptedFriend(context, activityClass,userId))
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createChannelIfNotExist(NotificationManager notificationManager) {
        if (notificationManager.getNotificationChannel(CHANNEL_ONE_ID) == null) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private static Notification buildNotification(Context context, Class<? extends Activity> activityClass,
                                                  String title, String message, @DrawableRes int icon, String dialogId) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String newline = System.getProperty("line.separator");

        return new NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setContentIntent(buildContentIntent(context, activityClass, dialogId))
                .build();
    }


    private static Notification buildFriendRequestNotification(Context context, Class<? extends Activity> activityClass, String requestFriendName, @DrawableRes int icon) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String newline = System.getProperty("line.separator");

        String message = context.getString(R.string.push_wants_friend);

        return new NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                .setSmallIcon(icon)
                .setContentTitle("HARUSEM")
                .setContentText(requestFriendName + " " + message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setContentIntent(buildContentIntentForFriendRequest(context, activityClass))
                .build();
    }

    private static PendingIntent buildContentIntent(Context context, Class<? extends Activity> activityClass, String dialogId) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private static PendingIntent buildContentIntentForFriendRequest(Context context, Class<? extends Activity> activityClass) {
        Intent intent = new Intent(context, activityClass);
        //intent.putExtra(ChatActivity.EXTRA_DIALOG_ID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

//    private static PendingIntent buildContentIntentForAcceptedFriend(Context context, Class<? extends Activity> activityClass, String profileId) {
//        //Intent intent = new Intent(context, activityClass);
//        //intent.putExtra("user_id", profileId);
//        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
}