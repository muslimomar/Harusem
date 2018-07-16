package com.example.william.harusem.util;

import android.os.Bundle;
import android.util.Log;

import com.example.william.harusem.R;
import com.example.william.harusem.holder.QBChatDialogHolder;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.util.qb.QbDialogUtils;
import com.example.william.harusem.util.qb.callback.QbEntityCallbackTwoTypeWrapper;
import com.example.william.harusem.util.qb.callback.QbEntityCallbackWrapper;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.LogLevel;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by william on 6/10/2018.
 */

public class ChatHelper {
    public static final int DIALOG_ITEMS_PER_PAGE = 100;
    public static final int CHAT_HISTORY_ITEMS_PER_PAGE = 50;
    private static final String CHAT_HISTORY_ITEMS_SORT_FIELD = "date_sent";
    private static ChatHelper instance;
    String TAG = ChatHelper.class.getSimpleName();
    private QBChatService qbChatService;

    private ChatHelper() {
        qbChatService = QBChatService.getInstance();
        qbChatService.setUseStreamManagement(true);
    }


    public static synchronized ChatHelper getInstance() {
        if (instance == null) {
            QBSettings.getInstance().setLogLevel(LogLevel.DEBUG);
            QBChatService.setDebugEnabled(true);
            QBChatService.setConfigurationBuilder(buildChatConfigs());
            instance = new ChatHelper();
        }
        return instance;
    }

    private static QBChatService.ConfigurationBuilder buildChatConfigs() {
        QBChatService.ConfigurationBuilder configurationBuilder = new QBChatService.ConfigurationBuilder();
        configurationBuilder.setSocketTimeout(0);
        configurationBuilder.setKeepAlive(true);

        return configurationBuilder;
    }


    public static QBUser getCurrentUser() {
        return QBChatService.getInstance().getUser();
    }

    private static String buildDialogNameWithoutUser(String dialogName, String userName) {
        String regex = ", " + userName + "|" + userName + ", ";
        return dialogName.replaceAll(regex, "");
    }

    public boolean isLogged() {
        return QBChatService.getInstance().isLoggedIn();
    }

    public void loadFileAsAttachment(File file, QBEntityCallback<QBAttachment> callback,
                                     QBProgressCallback progressCallback) {
        QBContent.uploadFileTask(file, true, null, progressCallback).performAsync(
                new QbEntityCallbackTwoTypeWrapper<QBFile, QBAttachment>(callback) {
                    @Override
                    public void onSuccess(QBFile qbFile, Bundle bundle) {
                        QBAttachment attachment = new QBAttachment(QBAttachment.PHOTO_TYPE);
                        attachment.setId(qbFile.getId().toString());
                        attachment.setUrl(qbFile.getPublicUrl());
                        callback.onSuccess(attachment, bundle);
                    }
                });
    }

    public void join(QBChatDialog chatDialog, final QBEntityCallback<Void> callback) {
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);

        chatDialog.join(history, callback);
    }

    public void leaveChatDialog(QBChatDialog chatDialog) throws XMPPException, SmackException.NotConnectedException {
        chatDialog.leave();
    }

    public void updateDialog(QBChatDialog qbDialog, List<QBUser> newQbDialogUsersList
            , String name, String photo, QBEntityCallback<QBChatDialog> callback) {

        List<QBUser> addedUsers = QbDialogUtils.getAddedUsers(qbDialog, newQbDialogUsersList);
        List<QBUser> removedUsers = QbDialogUtils.getRemovedUsers(qbDialog, newQbDialogUsersList);

        QBDialogRequestBuilder qbRequestBuilder = new QBDialogRequestBuilder();
        if (!addedUsers.isEmpty()) {
            qbRequestBuilder.addUsers(addedUsers.toArray(new QBUser[addedUsers.size()]));
            QBUsersHolder.getInstance().putUsers(addedUsers);
        }
        if (!removedUsers.isEmpty()) {
            qbRequestBuilder.removeUsers(removedUsers.toArray(new QBUser[removedUsers.size()]));
        }


        qbDialog.setName(name);
        if (photo != null && !photo.isEmpty()) {
            qbDialog.setPhoto(photo);
        }

        QBRestChatService.updateGroupChatDialog(qbDialog, qbRequestBuilder).performAsync(
                new QbEntityCallbackWrapper<QBChatDialog>(callback) {
                    @Override
                    public void onSuccess(QBChatDialog qbDialog, Bundle bundle) {
                        QBUsersHolder.getInstance().putUsers(newQbDialogUsersList);
                        super.onSuccess(qbDialog, bundle);
                    }
                });

    }

    public void updateDialogUsers(QBChatDialog qbDialog,
                                  final List<QBUser> newQbDialogUsersList,
                                  QBEntityCallback<QBChatDialog> callback) {
        List<QBUser> addedUsers = QbDialogUtils.getAddedUsers(qbDialog, newQbDialogUsersList);
        List<QBUser> removedUsers = QbDialogUtils.getRemovedUsers(qbDialog, newQbDialogUsersList);

        QbDialogUtils.logDialogUsers(qbDialog);
        QbDialogUtils.logUsers(addedUsers);
        Log.w(TAG, "=======================");
        QbDialogUtils.logUsers(removedUsers);

        QBDialogRequestBuilder qbRequestBuilder = new QBDialogRequestBuilder();
        if (!addedUsers.isEmpty()) {
            qbRequestBuilder.addUsers(addedUsers.toArray(new QBUser[addedUsers.size()]));
        }
        if (!removedUsers.isEmpty()) {
            qbRequestBuilder.removeUsers(removedUsers.toArray(new QBUser[removedUsers.size()]));
        }
        qbDialog.setName(DialogUtils.createChatNameFromUserList(
                newQbDialogUsersList.toArray(new QBUser[newQbDialogUsersList.size()])));

        QBRestChatService.updateGroupChatDialog(qbDialog, qbRequestBuilder).performAsync(
                new QbEntityCallbackWrapper<QBChatDialog>(callback) {
                    @Override
                    public void onSuccess(QBChatDialog qbDialog, Bundle bundle) {
                        QBUsersHolder.getInstance().putUsers(newQbDialogUsersList);
                        QbDialogUtils.logDialogUsers(qbDialog);
                        super.onSuccess(qbDialog, bundle);
                    }
                });
    }

    public void getUsersFromDialog(QBChatDialog dialog,
                                   final QBEntityCallback<ArrayList<QBUser>> callback) {
        List<Integer> userIds = dialog.getOccupants();

        final ArrayList<QBUser> users = new ArrayList<>(userIds.size());
        for (Integer id : userIds) {
            users.add(QBUsersHolder.getInstance().getUserById(id));
        }

        // If we already have all users in memory
        // there is no need to make REST requests to QB
        if (userIds.size() == users.size()) {
            callback.onSuccess(users, null);
            return;
        }

        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder(userIds.size(), 1);
        QBUsers.getUsersByIDs(userIds, requestBuilder).performAsync(
                new QbEntityCallbackWrapper<ArrayList<QBUser>>(callback) {
                    @Override
                    public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                        QBUsersHolder.getInstance().putUsers(qbUsers);
                        callback.onSuccess(qbUsers, bundle);
                    }
                });
    }

    public void loadChatHistory(QBChatDialog dialog, int skipPagination,
                                final QBEntityCallback<ArrayList<QBChatMessage>> callback) {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setSkip(skipPagination);
        messageGetBuilder.setLimit(CHAT_HISTORY_ITEMS_PER_PAGE);
        messageGetBuilder.sortDesc(CHAT_HISTORY_ITEMS_SORT_FIELD);
        messageGetBuilder.markAsRead(false);

        QBRestChatService.getDialogMessages(dialog, messageGetBuilder).performAsync(
                new QbEntityCallbackWrapper<ArrayList<QBChatMessage>>(callback) {
                    @Override
                    public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {

                        Set<Integer> userIds = new HashSet<>();
                        for (QBChatMessage message : qbChatMessages) {
                            userIds.add(message.getSenderId());
                        }

                        if (!userIds.isEmpty()) {
                            getUsersFromMessages(qbChatMessages, userIds, callback);
                        } else {
                            callback.onSuccess(qbChatMessages, bundle);
                        }
                        // Not calling super.onSuccess() because
                        // we're want to load chat users before triggering the callback
                    }
                });
    }

    private void getUsersFromMessages(final ArrayList<QBChatMessage> messages,
                                      final Set<Integer> userIds,
                                      final QBEntityCallback<ArrayList<QBChatMessage>> callback) {

        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder(userIds.size(), 1);
        QBUsers.getUsersByIDs(userIds, requestBuilder).performAsync(
                new QbEntityCallbackTwoTypeWrapper<ArrayList<QBUser>, ArrayList<QBChatMessage>>(callback) {
                    @Override
                    public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                        QBUsersHolder.getInstance().putUsers(users);
                        callback.onSuccess(messages, params);
                    }
                });
    }

    public void deleteDialog(QBChatDialog qbDialog, QBEntityCallback<Void> callback) {
        if (qbDialog.getType() == QBDialogType.PUBLIC_GROUP) {
            Toaster.shortToast(R.string.public_group_chat_cannot_be_deleted);
        } else {
            QBRestChatService.deleteDialog(qbDialog.getDialogId(), false)
                    .performAsync(new QbEntityCallbackWrapper<Void>(callback));
        }
    }

    public void exitFromDialog(QBChatDialog qbDialog, QBEntityCallback<QBChatDialog> callback) {
        try {
            leaveChatDialog(qbDialog);
        } catch (XMPPException | SmackException.NotConnectedException e) {
            callback.onError(new QBResponseException(e.getMessage()));
        }

        QBUser currentUser = QBChatService.getInstance().getUser();
        QBDialogRequestBuilder qbRequestBuilder = new QBDialogRequestBuilder();
        qbRequestBuilder.removeUsers(currentUser.getId());

        qbDialog.setName(buildDialogNameWithoutUser(qbDialog.getName(), currentUser.getFullName()));

        QBRestChatService.updateGroupChatDialog(qbDialog, qbRequestBuilder).performAsync(callback);
    }

    public void addConnectionListener(ConnectionListener listener) {
        qbChatService.addConnectionListener(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        qbChatService.removeConnectionListener(listener);
    }

    public void getDialogById(String dialogId, final QBEntityCallback<QBChatDialog> callback) {
        QBRestChatService.getChatDialogById(dialogId).performAsync(callback);
    }

    public void createDialogWithSelectedUsersWithPhoto(final List<QBUser> users,
                                                       final QBEntityCallback<QBChatDialog> callback) {


        QBRestChatService.createChatDialog(QbDialogUtils.createDialog(users)).performAsync(
                new QbEntityCallbackWrapper<QBChatDialog>(callback) {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {
                        QBChatDialogHolder.getInstance().putDialog(dialog);
                        QBUsersHolder.getInstance().putUsers(users);
                        super.onSuccess(dialog, args);
                    }
                });
    }

    public void createDialogWithSelectedUsersWithPhoto(final List<QBUser> users,
                                                       final QBEntityCallback<QBChatDialog> callback, String groupName, String photoId) {


        QBRestChatService.createChatDialog(QbDialogUtils.createGroupDialogWithPhoto(users, groupName, photoId)).performAsync(
                new QbEntityCallbackWrapper<QBChatDialog>(callback) {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {
                        QBChatDialogHolder.getInstance().putDialog(dialog);
                        QBUsersHolder.getInstance().putUsers(users);
                        super.onSuccess(dialog, args);
                    }
                });
    }

    public void getDialogs(QBRequestGetBuilder customObjectRequestBuilder, final QBEntityCallback<ArrayList<QBChatDialog>> callback) {
        customObjectRequestBuilder.setLimit(DIALOG_ITEMS_PER_PAGE);

        QBRestChatService.getChatDialogs(null, customObjectRequestBuilder).performAsync(
                new QbEntityCallbackWrapper<ArrayList<QBChatDialog>>(callback) {
                    @Override
                    public void onSuccess(ArrayList<QBChatDialog> dialogs, Bundle args) {
                        Iterator<QBChatDialog> dialogIterator = dialogs.iterator();
                        while (dialogIterator.hasNext()) {
                            QBChatDialog dialog = dialogIterator.next();
                            if (dialog.getType() == QBDialogType.PUBLIC_GROUP) {
                                dialogIterator.remove();
                            }
                        }

                        getUsersFromDialogs(dialogs, callback);
                        // Not calling super.onSuccess() because
                        // we want to load chat users before triggering callback
                    }
                });
    }

    private void getUsersFromDialogs(final ArrayList<QBChatDialog> dialogs,
                                     final QBEntityCallback<ArrayList<QBChatDialog>> callback) {
        List<Integer> userIds = new ArrayList<>();
        for (QBChatDialog dialog : dialogs) {
            userIds.addAll(dialog.getOccupants());
            userIds.add(dialog.getLastMessageUserId());
        }

        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder(userIds.size(), 1);
        QBUsers.getUsersByIDs(userIds, requestBuilder).performAsync(
                new QbEntityCallbackTwoTypeWrapper<ArrayList<QBUser>, ArrayList<QBChatDialog>>(callback) {
                    @Override
                    public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                        QBUsersHolder.getInstance().putUsers(users);
                        callback.onSuccess(dialogs, params);
                    }
                });
    }

    public void deleteDialogs(Collection<QBChatDialog> dialogs, final QBEntityCallback<ArrayList<String>> callback) {
        StringifyArrayList<String> dialogsIds = new StringifyArrayList<>();
        for (QBChatDialog dialog : dialogs) {
            dialogsIds.add(dialog.getDialogId());
        }

        QBRestChatService.deleteDialogs(dialogsIds, false, null).performAsync(callback);
    }


    public void destroy() {
        qbChatService.destroy();
    }


    public void loginToChat(final QBUser user, final QBEntityCallback<Void> callback) {
        if (qbChatService.isLoggedIn()) {
            callback.onSuccess(null, null);
            return;
        }

        qbChatService.login(user, callback);
    }

}
