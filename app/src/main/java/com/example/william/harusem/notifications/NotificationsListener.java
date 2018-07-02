package com.example.william.harusem.notifications;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.william.harusem.R;
import com.example.william.harusem.ui.activities.ChatActivity;
import com.example.william.harusem.ui.activities.MainActivity;
import com.example.william.harusem.util.consts.GcmConsts;
import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;

import org.jivesoftware.smack.chat.Chat;
import org.json.JSONObject;

import java.util.Map;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class NotificationsListener extends QBFcmPushListenerService {
    private final int NOTIFICATION_ID2 = 237;
    private static int value = 0;
    private static int id =0;
    private static int unread_notif = 0;
    final static String GROUP_KEY_GUEST = "group_key_guest";
    private QBChatDialog dialogId;
    private String userName;
    private String message;
    private String friendRequestNotifierName;
    private static final int NOTIFICATION_ID = 4;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        //Custom params receiver from Push notifications
        userName = data.get("user_name");
        message = data.get("message");
        friendRequestNotifierName = data.get("notifier_name");
        //dialogId = data.get("dialogID");
        Intent result = new Intent();
        result.putExtra(ChatActivity.EXTRA_DIALOG_ID,dialogId);


        Log.e("taggy","tagi" +dialogId);
        //Toast.makeText(this, "Message: " + message + "Username: " + userName, Toast.LENGTH_SHORT).show();
        sendNotification();
        //friendRequestNotifications();

    }

    public void sendNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //Intent intent = new Intent(this, NotificationsListener.class);
        //PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);



        // Create an Intent for the activity you want to start
        //Intent resultIntent = new Intent(this, ChatActivity.class);

        Intent resultIntent = new Intent(this, ChatActivity.class);
        resultIntent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);
        //resultIntent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);

// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);



        String notificationContent = userName + " :" + message;
        String notificationTitle = "HARUSEM";

        NotificationCompat.Builder note = new NotificationCompat.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.harusem_logo)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("channel_01", "Chats",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Chats");
            manager.createNotificationChannel(channel);
            note.setChannelId("channel_01");
        }

        manager.notify(NOTIFICATION_ID, note.build());
    }


    public void friendRequestNotifications() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, NotificationsListener.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        String notificationContent = friendRequestNotifierName + " " + "Wants to be your friend!";
        String notificationTitle = "HARUSEM";

        NotificationCompat.Builder note = new NotificationCompat.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.harusem_logo)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("channel_02", "Chats2",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Chats2");
            manager.createNotificationChannel(channel);
            note.setChannelId("channel_02");
        }

        manager.notify(NOTIFICATION_ID, note.build());
    }


    private void generateNotification() {


    }
}
