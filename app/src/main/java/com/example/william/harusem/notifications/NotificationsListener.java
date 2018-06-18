package com.example.william.harusem.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.example.william.harusem.R;
import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;

import java.util.Map;

public class NotificationsListener extends QBFcmPushListenerService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //TODO FIX NULL POINT EXCEPTION in onReceive
        //Even if you get the message (see logcat) you have NPE!
        //How you get NPE while you already got the message data!
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();


        //Custom params receiver from Push notifications
        String userName = data.get("user_name");
        String message = data.get("message");

        displayNotifcation(userName,message);

    }
    public void displayNotifcation(String title, String contentText){

        // I changed the notifications I know it's not working always but it's not important now I can fix later the important is why we have NPE? in onReceive
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(14, notification);
    }
}
