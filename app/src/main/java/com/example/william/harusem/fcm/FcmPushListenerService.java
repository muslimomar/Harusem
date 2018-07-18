package com.example.william.harusem.fcm;

import android.app.Activity;
import android.util.Log;

import com.example.william.harusem.R;
import com.example.william.harusem.SplashActivity;
import com.example.william.harusem.services.CallService;
import com.example.william.harusem.ui.activities.CallActivity;
import com.example.william.harusem.ui.activities.ChatActivity;
import com.example.william.harusem.ui.activities.FriendRequestsActivity;
import com.example.william.harusem.ui.activities.MainActivity;
import com.example.william.harusem.util.ActivityLifecycle;
import com.example.william.harusem.util.NotificationUtils;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.example.william.harusem.utils.PushNotificationSender;
import com.example.william.harusem.utils.WebRtcSessionManager;
import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.chat.QBChatService;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class FcmPushListenerService extends QBFcmPushListenerService {
    private static final int NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_ID2 = 2;
    String friendRequestName;
    private static final String TAG = FcmPushListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (ActivityLifecycle.getInstance().isBackground()) {
            Map<String, String> data = remoteMessage.getData();

            String callMessage = data.get(NotificationHelper.CALL_MESSAGE);
            if(callMessage != null && !callMessage.isEmpty()) {
                SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
                if (sharedPrefsHelper.hasQbUser()) {
                    QBUser qbUser = sharedPrefsHelper.getQbUser();
                    startLoginService(qbUser);
                }

            }else{
                getPushData(data);
            }

            Log.v("onReceive Notify", "receiveddddddddddd");
        }
    }


    private void startLoginService(QBUser qbUser){
        CallService.start(this, qbUser);
    }
    private void getPushData(Map<String, String> data) {

        String sender = data.get(NotificationHelper.MESSAGE_SENDER);
        String message = data.get(NotificationHelper.MESSAGE_CONTENT);
        String dialogId = data.get(NotificationHelper.MESSAGE_DIALOG_ID);
        friendRequestName = data.get(NotificationHelper.FRIEND_REQUEST_SENDER_FULL_NAME);
        String friendAcceptedOponentName = data.get(NotificationHelper.FRIEND_REQUEST_ACCEPTED_FULL_NAME);

        Log.v("tatyhaha", "" + friendAcceptedOponentName + " Has accepted your friend request!");
        SharedPrefsHelper prefsHelper = SharedPrefsHelper.getInstance();
        prefsHelper.savePushDialogId(dialogId);
        String title = sender;
        String text = message;

        Log.v("onReceive Notify", "name is: " + friendRequestName);
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
            showAcceptedFriendNotification(friendAcceptedOponentName);
        }

    }

    protected void showAcceptedFriendNotification(String acceptedFriendName) {
        NotificationUtils.showAcceptedFriendNotification(this, acceptedFriendName, R.drawable.harusem_logo, NOTIFICATION_ID);
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
            activity = getFriendRequestsActivity();
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
        activity = FriendRequestsActivity.class;
        return activity;
    }

}