package com.example.william.harusem.utils;

import com.example.william.harusem.R;
import com.example.william.harusem.fcm.NotificationHelper;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;

import org.json.JSONObject;

/**
 * Created by tereha on 13.05.16.
 */
public class PushNotificationSender {

    public static void sendPushMessage(int recipient, String senderName) {
        String outMessage = String.format(String.valueOf(R.string.text_push_notification_message), senderName);

        // Send Push: create QuickBlox Push Notification Event
        QBEvent qbEvent = new QBEvent();
        qbEvent.setNotificationType(QBNotificationType.PUSH);
        qbEvent.setEnvironment(QBEnvironment.PRODUCTION);
        // Generic push - will be delivered to all platforms (Android, iOS, WP, Blackberry..)

        StringifyArrayList<Integer> userIds = new StringifyArrayList<>();
        userIds.add(recipient);
        qbEvent.setUserIds(userIds);

        JSONObject json = new JSONObject();
        try {
            json.put(NotificationHelper.CALL_MESSAGE, outMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        qbEvent.setMessage(json.toString());
        QBPushNotifications.createEvent(qbEvent).performAsync(null);
    }
}