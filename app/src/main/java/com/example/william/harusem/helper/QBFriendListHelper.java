package com.example.william.harusem.helper;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.william.harusem.R;
import com.example.william.harusem.holder.QBFriendRequestsHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.listeners.QBSubscriptionListener;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.chat.model.QBRosterEntry;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.roster.packet.RosterPacket;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by william on 6/8/2018.
 */

public class QBFriendListHelper {

    private static final String TAG = QBFriendListHelper.class.getSimpleName();
    private Context context;
    private QBRoster roster;
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
            roster.subscribe(user.getId(), callback);
        } else {
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

    public QBPresence getUserPresence(int userId) {
        QBPresence presence = roster.getPresence(userId);
        if (presence != null) {
            return presence;
        }
        return null;
    }

}
