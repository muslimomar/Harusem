package com.example.william.harusem.holder;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by william on 5/31/2018.
 */

public class QBChatDialogHolder {

    private static QBChatDialogHolder instance;
    private HashMap<String, QBChatDialog> dialogsMap;

    public QBChatDialogHolder() {
        this.dialogsMap = new HashMap<>();
    }

    public static synchronized QBChatDialogHolder getInstance() {
        QBChatDialogHolder qbChatDialogHolder;
        synchronized (QBChatDialogHolder.class) {
            if (instance == null) {
                instance = new QBChatDialogHolder();
            }
            qbChatDialogHolder = instance;
            return qbChatDialogHolder;
        }
    }

    public void putDialogs(List<QBChatDialog> dialogs) {
        for (QBChatDialog qbChatDialog : dialogs)
            putDialog(qbChatDialog);
    }

    public void putDialog(QBChatDialog qbChatDialog) {
        if (qbChatDialog != null) {
            dialogsMap.put(qbChatDialog.getDialogId(), qbChatDialog);
        }
    }

    public QBChatDialog getChatDialogById(String dialogId) {
        return (QBChatDialog) dialogsMap.get(dialogId);
    }

    public List<QBChatDialog> getChatDialogsByIds(List<String> dialogIds) {
        List<QBChatDialog> chatDialogs = new ArrayList<>();
        for (String id : dialogIds) {
            QBChatDialog chatDialog = getChatDialogById(id);
            if (chatDialog != null)
                chatDialogs.add(chatDialog);
        }
        return chatDialogs;
    }

    public ArrayList<QBChatDialog> getAllChatDialogs() {
        ArrayList<QBChatDialog> qbChat = new ArrayList<>();
        for (String key : dialogsMap.keySet())
            qbChat.add(dialogsMap.get(key));
        return qbChat;
    }

    public void removeDialog(String id) {
        dialogsMap.remove(id);
    }

    public void deleteDialogs(ArrayList<String> dialogsIds) {
        for (String dialogId : dialogsIds) {
            deleteDialog(dialogId);
        }
    }


    public void deleteDialog(QBChatDialog chatDialog) {
        dialogsMap.remove(chatDialog.getDialogId());
    }

    public void deleteDialog(String dialogId) {
        dialogsMap.remove(dialogId);
    }

    public boolean hasDialogWithId(String dialogId) {
        return dialogsMap.containsKey(dialogId);
    }


    public void updateDialog(String dialogId, QBChatMessage qbChatMessage) {
        QBChatDialog updatedDialog = getChatDialogById(dialogId);
        updatedDialog.setLastMessage(qbChatMessage.getBody());
        updatedDialog.setLastMessageDateSent(qbChatMessage.getDateSent());
        updatedDialog.setUnreadMessageCount(updatedDialog.getUnreadMessageCount() != null
                ? updatedDialog.getUnreadMessageCount() + 1 : 1);
        updatedDialog.setLastMessageUserId(qbChatMessage.getSenderId());

        dialogsMap.put(updatedDialog.getDialogId(), updatedDialog);
    }

    public void updateDialog(String dialogId, String groupName,String photo,List<Integer> integers) {
        QBChatDialog updatedDialog = getChatDialogById(dialogId);
        updatedDialog.setUnreadMessageCount(updatedDialog.getUnreadMessageCount() != null
                ? updatedDialog.getUnreadMessageCount() + 1 : 1);
        updatedDialog.setName(groupName);
        updatedDialog.setPhoto(photo);
        updatedDialog.setOccupantsIds(integers);

        dialogsMap.put(updatedDialog.getDialogId(), updatedDialog);
    }

    public Map<String, QBChatDialog> getDialogs() {
        return getSortedMap(dialogsMap);
    }

    private Map<String, QBChatDialog> getSortedMap(Map<String, QBChatDialog> unsortedMap) {
        Map<String, QBChatDialog> sortedMap = new TreeMap(new LastMessageDateSentComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }

    static class LastMessageDateSentComparator implements Comparator<String> {
        Map<String, QBChatDialog> map;

        public LastMessageDateSentComparator(Map<String, QBChatDialog> map) {
            this.map = map;
        }

        public int compare(String keyA, String keyB) {

            long valueA = map.get(keyA).getLastMessageDateSent();
            long valueB = map.get(keyB).getLastMessageDateSent();

            if (valueB < valueA) {
                return -1;
            } else {
                return 1;
            }
        }
    }


    public void clear() {
        dialogsMap.clear();
    }

    public boolean hasPrivateDialogWithUser(QBUser user) {
        return getPrivateDialogWithUser(user) != null;
    }

    public QBChatDialog getPrivateDialogWithUser(QBUser user) {
        for (QBChatDialog chatDialog : dialogsMap.values()) {
            if (QBDialogType.PRIVATE.equals(chatDialog.getType())
                    && chatDialog.getOccupants().contains(user.getId())) {
                return chatDialog;
            }
        }

        return null;
    }

}

