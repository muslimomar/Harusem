package com.example.william.harusem.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.william.harusem.R;
import com.example.william.harusem.services.CallService;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;
import com.quickblox.users.model.QBUser;

public class NotificationsListener extends QBFcmPushListenerService {
    private static final int NOTIFICATION_ID = 4;
    private static final String TAG = NotificationsListener.class.getSimpleName();
    private String userName;
    private String message;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        if (sharedPrefsHelper.hasQbUser()) {
            Log.d(TAG, "App have logined user");
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            startLoginService(qbUser);
        }



        /*
        //Even if you get the message (see logcat) you have NPE!
        //How you get NPE while you already got the message data!
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();


        //Custom params receiver from Push notifications
        userName = data.get("user_name");
        message = data.get("message");
        sendNotification();

        */

    }

    private void startLoginService(QBUser qbUser){
        CallService.start(this, qbUser);
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
