package com.example.william.harusem.helper;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.william.harusem.holder.QBFriendRequestsHolder;
import com.example.william.harusem.holder.QBUsersHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.listeners.QBSubscriptionListener;
import com.quickblox.chat.model.QBRosterEntry;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by william on 6/8/2018.
 */

public class QBFriendListHelper {

    private static final String TAG = QBFriendListHelper.class.getSimpleName();
    private Context context;
    private QBRoster roster;
    //String currentUserFullName;
    private QBFriendRequestsHolder qbFriendRequestsHolder;

    public QBFriendListHelper(Context context) {
        this.context = context;
        qbFriendRequestsHolder = QBFriendRequestsHolder.getInstance();
        roster = QBChatService.getInstance().getRoster(QBRoster.SubscriptionMode.mutual,
                new SubscriptionListener());
        roster.setSubscriptionMode(QBRoster.SubscriptionMode.mutual);
    }

    public void acceptFriendRequest(final QBUser user, QBEntityCallback<Void> callback) {
        roster.confirmSubscription(user.getId(), callback);
    }

    public void declineFriendRequest(final QBUser user, QBEntityCallback<Void> callback) {
        roster.reject(user.getId(), callback);
        // consider clearing roster entry as in quminicate
    }

    public boolean isFriendRequestAlreadySent(int userId) {
        QBRosterEntry entry = roster.getEntry(userId);
        if (entry == null) {
            return false;
        }

        return entry.getStatus() == RosterPacket.ItemStatus.SUBSCRIPTION_PENDING;
    }

    public void removeFriend(QBUser user, QBEntityCallback<Void> callback) {
        roster.removeEntry(roster.getEntry(user.getId()), callback);
    }

    public void cancelFriendRequest(int userId, QBEntityCallback<Void> callback) {
        QBRosterEntry entry = roster.getEntry(userId);
        if (entry != null && roster.contains(userId)) {
            roster.removeEntry(entry, callback);
            roster.unsubscribe(userId, callback);
        }
    }

    public void sendFriendRequest(QBUser user, QBEntityCallback<Void> callback) {

        if (roster.contains(user.getId())) {
            // Get current user fullname
            QBUser currentUser = QBUsersHolder.getInstance().getUserById(QBChatService.getInstance().getUser().getId());
            String currentt = currentUser.getFullName();

            StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
            userIds.add(user.getId());


            QBEvent event = new QBEvent();
            event.setUserIds(userIds);
            event.setEnvironment(QBEnvironment.PRODUCTION);
            event.setNotificationType(QBNotificationType.PUSH);
            event.setMessage("New Friend Request");
            JSONObject json = new JSONObject();
            try {
                // custom parameters
                json.put("notifier_name", currentt);

            } catch (Exception e) {
                e.printStackTrace();
            }

            event.setMessage(json.toString());


            QBPushNotifications.createEvent(event).performAsync(new QBEntityCallback<QBEvent>() {
                @Override
                public void onSuccess(QBEvent qbEvent, Bundle bundle) {
                    Log.v("friend_request", "Request Sent");
                    Toast.makeText(context, "Success Notification Sent", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.v("dede", e.getMessage());

                }
            });
            roster.subscribe(user.getId(), callback);
        } else {


            // Get current user fullname
            QBUser currentUser = QBUsersHolder.getInstance().getUserById(QBChatService.getInstance().getUser().getId());
            String currentt = currentUser.getFullName();

            StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
            userIds.add(user.getId());

            QBEvent event = new QBEvent();
            event.setUserIds(userIds);
            event.setEnvironment(QBEnvironment.PRODUCTION);
            event.setNotificationType(QBNotificationType.PUSH);
            event.setMessage("");
            JSONObject json = new JSONObject();
            try {
                // custom parameters
                json.put("notifier_name", currentt);
                //json.put("message", messageInputEt.getText().toString());
                //json.put("thread_id", "8343");

            } catch (Exception e) {
                e.printStackTrace();
            }

            event.setMessage(json.toString());

            QBPushNotifications.createEvent(event).performAsync(new QBEntityCallback<QBEvent>() {
                @Override
                public void onSuccess(QBEvent qbEvent, Bundle bundle) {
                    Log.v("friend_request", "Request Sent");
                    Toast.makeText(context, "Success Notification Sent", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.v("ERROR", e.getMessage());

                }
            });
            roster.createEntry(user.getId(), null, callback);
        }

    }

    public Collection<Integer> getAllFriends() {
        Collection<QBRosterEntry> entries = roster.getEntries();
        Collection<Integer> friendsIds = new ArrayList<>();

        for (QBRosterEntry entry : entries) {
            if (isFriend(entry)) {
                friendsIds.add(entry.getUserId());
            }
        }

        return friendsIds;
    }

    private boolean isFriend(QBRosterEntry entry) {
        return entry.getType().equals(RosterPacket.ItemType.both);
    }

    public boolean isFriend(int userId) {
        QBRosterEntry entry = roster.getEntry(userId);
        if (entry == null) {
            return false;
        }
        return entry.getType().equals(RosterPacket.ItemType.both);

    }

    private class SubscriptionListener implements QBSubscriptionListener {
        @Override
        public void subscriptionRequested(int userId) {

            QBUsers.getUser(userId).performAsync(new QBEntityCallback<QBUser>() {
                @Override
                public void onSuccess(QBUser qbUser, Bundle bundle) {

                    qbFriendRequestsHolder.putRequest(qbUser);

                    Log.d(TAG, "subscriptionListener onSuccess: " + qbUser);
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e(TAG, "subscriptionListener onError: ", e);
                }
            });
        }
    }


}
