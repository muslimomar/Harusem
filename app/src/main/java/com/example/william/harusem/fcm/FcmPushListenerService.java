package com.example.william.harusem.fcm;

import android.app.Activity;

import com.example.william.harusem.R;
import com.example.william.harusem.SplashActivity;
import com.example.william.harusem.ui.activities.ChatActivity;
import com.example.william.harusem.ui.activities.MainActivity;
import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.chat.QBChatService;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;
import com.example.william.harusem.util.ActivityLifecycle;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.example.william.harusem.util.NotificationUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


public class FcmPushListenerService extends QBFcmPushListenerService {
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = FcmPushListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (ActivityLifecycle.getInstance().isBackground()) {

        Map<String, String> data = remoteMessage.getData();

        String sender = data.get(NotificationHelper.MESSAGE_SENDER);
        String message = data.get(NotificationHelper.MESSAGE_CONTENT);
        String dialogId = data.get(NotificationHelper.MESSAGE_DIALOG_ID);

        SharedPrefsHelper prefsHelper = SharedPrefsHelper.getInstance();

        prefsHelper.savePushDialogId(dialogId);

        String title = sender;
        String text = message;


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

        showNotification(text, title, dialogId);
        }

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

    private Class<? extends Activity> getRightActivity() {
        Class<? extends Activity> activity = ChatActivity.class;
        Set<String> dialogs = SharedPrefsHelper.getInstance().getPushDialogIds();
        if (dialogs != null && dialogs.size() > 1) {
            activity = MainActivity.class;
        }
        return activity;
    }

}