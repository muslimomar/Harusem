package com.example.william.harusem.manager;

import android.os.Bundle;

import com.example.william.harusem.holder.QBChatDialogHolder;
import com.example.william.harusem.util.ChatHelper;
import com.example.william.harusem.util.qb.QbDialogUtils;
import com.example.william.harusem.util.qb.callback.QbEntityCallbackImpl;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class DialogsManager {

    public static final String PROPERTY_OCCUPANTS_IDS = "occupants_ids";
    public static final String PROPERTY_DIALOG_TYPE = "dialog_type";
    public static final String PROPERTY_DIALOG_NAME = "dialog_name";
    public static final String PROPERTY_NOTIFICATION_TYPE = "notification_type";
    public static final String CREATING_DIALOG = "creating_dialog";

    private Set<ManagingDialogsCallbacks> managingDialogsCallbackListener = new CopyOnWriteArraySet<>();

    private boolean isMessageCreatingDialog(QBChatMessage systemMessage) {
        return CREATING_DIALOG.equals(systemMessage.getProperty(PROPERTY_NOTIFICATION_TYPE));
    }

    private QBChatMessage buildSystemMessageAboutCreatingGroupDialog(QBChatDialog dialog) {
        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setDialogId(dialog.getDialogId());
        qbChatMessage.setProperty(PROPERTY_OCCUPANTS_IDS, QbDialogUtils.getOccupantsIdsStringFromList(dialog.getOccupants()));
        qbChatMessage.setProperty(PROPERTY_DIALOG_TYPE, String.valueOf(dialog.getType().getCode()));
        qbChatMessage.setProperty(PROPERTY_DIALOG_NAME, String.valueOf(dialog.getName()));
        qbChatMessage.setProperty(PROPERTY_NOTIFICATION_TYPE, CREATING_DIALOG);

        return qbChatMessage;
    }

    private QBChatDialog buildChatDialogFromSystemMessage(QBChatMessage qbChatMessage) {
        QBChatDialog chatDialog = new QBChatDialog();
        chatDialog.setDialogId(qbChatMessage.getDialogId());
        chatDialog.setOccupantsIds(QbDialogUtils.getOccupantsIdsListFromString((String) qbChatMessage.getProperty(PROPERTY_OCCUPANTS_IDS)));
        chatDialog.setType(QBDialogType.parseByCode(Integer.parseInt(qbChatMessage.getProperty(PROPERTY_DIALOG_TYPE).toString())));
        chatDialog.setName(qbChatMessage.getProperty(PROPERTY_DIALOG_NAME).toString());
        chatDialog.setUnreadMessageCount(0);

        return chatDialog;
    }

    public void sendSystemMessageAboutCreatingDialog(QBSystemMessagesManager systemMessagesManager, QBChatDialog dialog) {
        QBChatMessage systemMessageCreatingDialog = buildSystemMessageAboutCreatingGroupDialog(dialog);

        try {
            for (Integer recipientId : dialog.getOccupants()) {
                if (!recipientId.equals(QBChatService.getInstance().getUser().getId())) {
                    systemMessageCreatingDialog.setRecipientId(recipientId);
                    systemMessagesManager.sendSystemMessage(systemMessageCreatingDialog);
                }
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void loadUsersFromDialog(QBChatDialog chatDialog) {
        ChatHelper.getInstance().getUsersFromDialog(chatDialog, new QbEntityCallbackImpl<ArrayList<QBUser>>());
    }

    public void onGlobalMessageReceived(String dialogId, QBChatMessage chatMessage) {
        if ((chatMessage.getBody() != null && chatMessage.isMarkable()) || (chatMessage.getAttachments() != null && chatMessage.getAttachments().size() > 0)) { //for excluding status messages until will be released v.3.1
            if (QBChatDialogHolder.getInstance().hasDialogWithId(dialogId)) {
                QBChatDialogHolder.getInstance().updateDialog(dialogId, chatMessage);
                notifyListenersDialogUpdated(dialogId);
            } else {
                ChatHelper.getInstance().getDialogById(dialogId, new QbEntityCallbackImpl<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog chatDialog, Bundle bundle) {
                        loadUsersFromDialog(chatDialog);
                        QBChatDialogHolder.getInstance().putDialog(chatDialog);
                        notifyListenersNewDialogLoaded(chatDialog);
                    }
                });
            }
        }

    }

    public void onSystemMessageReceived(QBChatMessage systemMessage) {
        if (isMessageCreatingDialog(systemMessage)) {
            QBChatDialog chatDialog = buildChatDialogFromSystemMessage(systemMessage);
            chatDialog.initForChat(QBChatService.getInstance());
            QBChatDialogHolder.getInstance().putDialog(chatDialog);
            notifyListenersDialogCreated(chatDialog);
        }
    }

    private void notifyListenersDialogCreated(final QBChatDialog chatDialog) {
        for (ManagingDialogsCallbacks listener : getManagingDialogsCallbackListeners()) {
            listener.onDialogCreated(chatDialog);
        }
    }

    private void notifyListenersDialogUpdated(final String dialogId) {
        for (ManagingDialogsCallbacks listener : getManagingDialogsCallbackListeners()) {
            listener.onDialogUpdated(dialogId);
        }
    }

    private void notifyListenersNewDialogLoaded(final QBChatDialog chatDialog) {
        for (ManagingDialogsCallbacks listener : getManagingDialogsCallbackListeners()) {
            listener.onNewDialogLoaded(chatDialog);
        }
    }

    public void addManagingDialogsCallbackListener(ManagingDialogsCallbacks listener) {
        if (listener != null) {
            managingDialogsCallbackListener.add(listener);
        }
    }

    public void removeManagingDialogsCallbackListener(ManagingDialogsCallbacks listener) {
        managingDialogsCallbackListener.remove(listener);
    }

    public Collection<ManagingDialogsCallbacks> getManagingDialogsCallbackListeners() {
        return Collections.unmodifiableCollection(managingDialogsCallbackListener);
    }

    public interface ManagingDialogsCallbacks {

        void onDialogCreated(QBChatDialog chatDialog);

        void onDialogUpdated(String chatDialog);

        void onNewDialogLoaded(QBChatDialog chatDialog);
    }


}
