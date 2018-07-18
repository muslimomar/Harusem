package com.example.william.harusem.fcm;

import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class NotificationHelper {
    public static final String MESSAGE_CONTENT = "data.message";
    public static final String MESSAGE_SENDER = "data.sender";
    public static final String MESSAGE_DIALOG_ID = "data.dialog";
    public static final String FRIEND_REQUEST_SENDER_FULL_NAME = "data.friendname";
    public static final String FRIEND_REQUEST_ACCEPTED_FULL_NAME = "data.opponent_name";
    public static final String MESSAGE_QB_CHAT_MESSAGE = "data.qb.chat.message";
    public static final String CALL_MESSAGE = "data.call.message";
    public static final String CALL_LIST_OCCUPANT = "data.call.occupants";

    public static QBEvent createPushEvent(List<Integer> userIdsList, String message, String fullName, String dialogId) {
        StringifyArrayList<Integer> userIds = new StringifyArrayList<>();
        userIds.addAll(userIdsList);
        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.PRODUCTION);
        event.setNotificationType(QBNotificationType.PUSH);


        JSONObject json = new JSONObject();
        try {
            json.put(MESSAGE_SENDER, fullName);
            json.put(MESSAGE_DIALOG_ID, dialogId);
            json.put(MESSAGE_CONTENT, message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        event.setMessage(json.toString());

        return event;
    }

    public static QBEvent friendRequestPushEvent(List<Integer> userIdsList, String fullName) {
        StringifyArrayList<Integer> userIds = new StringifyArrayList<>();
        userIds.addAll(userIdsList);
        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.PRODUCTION);
        event.setNotificationType(QBNotificationType.PUSH);


        JSONObject json = new JSONObject();
        try {
            json.put(FRIEND_REQUEST_SENDER_FULL_NAME, fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        event.setMessage(json.toString());

        return event;
    }

    public static QBEvent acceptedYourRequestPushEvent(List<Integer> userIdsList, String fullName) {
        StringifyArrayList<Integer> userIds = new StringifyArrayList<>();
        userIds.addAll(userIdsList);
        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.PRODUCTION);
        event.setNotificationType(QBNotificationType.PUSH);

        JSONObject json = new JSONObject();
        try {
            json.put(FRIEND_REQUEST_ACCEPTED_FULL_NAME, fullName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        event.setMessage(json.toString());
        return event;
    }
}
