package com.example.william.harusem.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.example.william.harusem.R;
import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;

import java.util.Map;

public class NotificationsListener extends QBFcmPushListenerService {
    private String userName;
    private String message;
    private static final int NOTIFICATION_ID = 4;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //Even if you get the message (see logcat) you have NPE!
        //How you get NPE while you already got the message data!
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();


        //Custom params receiver from Push notifications
        userName = data.get("user_name");
        message = data.get("message");
        sendNotification();
    }

    public void sendNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, NotificationsListener.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        String notificationContent = userName + ":" + message;
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

            NotificationChannel channel = new NotificationChannel("channel_01", "Chats",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Chats");
            manager.createNotificationChannel(channel);
            note.setChannelId("channel_01");
        }

        manager.notify(NOTIFICATION_ID, note.build());
    }
}
