package com.example.william.harusem.fcm;

import android.app.Activity;
import android.util.Log;

import com.example.william.harusem.R;
import com.example.william.harusem.SplashActivity;
import com.example.william.harusem.services.CallService;
import com.example.william.harusem.ui.activities.ChatActivity;
import com.example.william.harusem.ui.activities.FriendRequestsActivity;
import com.example.william.harusem.ui.activities.MainActivity;
import com.example.william.harusem.ui.activities.ProfileActivity;
import com.example.william.harusem.util.ActivityLifecycle;
import com.example.william.harusem.util.NotificationUtils;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.chat.QBChatService;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;


public class FcmPushListenerService extends QBFcmPushListenerService {
    private static final int NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_ID2 = 2;
    String friendRequestName;
    private static final String TAG = FcmPushListenerService.class.getSimpleName();


    private void startLoginService(QBUser qbUser){
        CallService.start(this, qbUser);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (ActivityLifecycle.getInstance().isBackground()) {
            Map<String, String> data = remoteMessage.getData();

            String sender = data.get(NotificationHelper.MESSAGE_SENDER);
            String message = data.get(NotificationHelper.MESSAGE_CONTENT);
            String dialogId = data.get(NotificationHelper.MESSAGE_DIALOG_ID);
            friendRequestName = data.get(NotificationHelper.FRIEND_REQUEST_SENDER_FULL_NAME);
            String friendAcceptedOponentName = data.get(NotificationHelper.FRIEND_REQUEST_ACCEPTED_FULL_NAME);
            String userID = data.get(NotificationHelper.USER_ID_PUSH);


            String callMessage = data.get(NotificationHelper.CALL_MESSAGE);
            if(callMessage != null && !callMessage.isEmpty()) {
                SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
                if (sharedPrefsHelper.hasQbUser()) {
                    QBUser qbUser = sharedPrefsHelper.getQbUser();
                    startLoginService(qbUser);
                }
            }else {

                Log.v("tatyhaha", " " + friendAcceptedOponentName + getString(R.string.has_acccepted_friend_request));
                SharedPrefsHelper prefsHelper = SharedPrefsHelper.getInstance();
                prefsHelper.savePushDialogId(dialogId);
                prefsHelper.saveFriendRequests(userID);
                String title = sender;
                String text = message;

                Log.v("onReceive Notify", "name is: " + friendRequestName);


                //Push friendrequests array

                ArrayList<String> pushFriendRequests = prefsHelper.getRequestsArray();


                // generate the title for one friend request
                // Push messages array
                ArrayList<String> pushMessages = prefsHelper.getMessagesArray();


                // generate title (one conversation)
                if (pushMessages == null) {
                    pushMessages = new ArrayList<>();
                    pushMessages.add(message);
                } else {
                    pushMessages.add(message);
                    int messagesSize = pushMessages.size();
                    if (messagesSize > 1) {
                        title += " " + getString(R.string.message_size, messagesSize);
                    }
                }
                // generate title (multiple conversations)
                int dialogsSize = prefsHelper.getPushDialogIds().size();
                if (dialogsSize > 1) {
                    title = getString(R.string.dialogs_size, dialogsSize);
                    text = "";
                }
                prefsHelper.saveMessagesArray(pushMessages);

                // showFriendRequestNotification("aaaa");

//            showNotification(text, title, dialogId);
                if (friendRequestName != null) {
                    showFriendRequestNotification(friendRequestName);
                } else if (text != null) {

                    showNotification(text, title, dialogId);
                } else if (friendAcceptedOponentName != null) {
                    showAcceptedFriendNotification(friendAcceptedOponentName, userID);
                }
            }
            Log.v("onReceive Notify", "receiveddddddddddd");
        }
    }

    protected void showAcceptedFriendNotification(String acceptedFriendName, String userId) {
        Class<? extends Activity> activity = ProfileActivity.class;
        if (QBChatService.getInstance().isLoggedIn()) {
            //activity = getProfileActivity();
        }
        NotificationUtils.showAcceptedFriendNotification(this, activity, acceptedFriendName, R.drawable.harusem_logo, NOTIFICATION_ID, userId);
    }

    protected void showNotification(String message, String sender, String dialogId) {
        Class<? extends Activity> activity = SplashActivity.class;
        if (QBChatService.getInstance().isLoggedIn()) {
            activity = getRightActivity();
        }

        NotificationUtils.showNotification(this, activity,
                sender, message, dialogId,
                R.drawable.harusem_logo, NOTIFICATION_ID);
    }

    protected void showFriendRequestNotification(String friendRequestName) {
        Class<? extends Activity> activity = SplashActivity.class;
        if (QBChatService.getInstance().isLoggedIn()) {
            //activity = getFriendRequestsActivity();
        }
        NotificationUtils.showFriendRequestNotification(this, activity,
                friendRequestName,
                R.drawable.harusem_logo, NOTIFICATION_ID);
    }

    private Class<? extends Activity> getRightActivity() {
        Class<? extends Activity> activity = ChatActivity.class;
        Set<String> dialogs = SharedPrefsHelper.getInstance().getPushDialogIds();
        if (dialogs != null && dialogs.size() > 1) {
            activity = MainActivity.class;
        }
        return activity;
    }

    private Class<? extends Activity> getFriendRequestsActivity() {
        Class<? extends Activity> activity = FriendRequestsActivity.class;
        Set<String> friendRequests = SharedPrefsHelper.getInstance().getFriendRequests();
        activity = FriendRequestsActivity.class;
        return activity;
    }

    private Class<? extends Activity> getProfileActivity() {
        Class<? extends Activity> activity = ProfileActivity.class;
        activity = ProfileActivity.class;
        return activity;
    }
}