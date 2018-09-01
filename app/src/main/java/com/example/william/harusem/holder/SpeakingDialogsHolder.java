package com.example.william.harusem.holder;

import com.example.william.harusem.models.SpeakingDialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by william on 5/31/2018.
 */

public class SpeakingDialogsHolder {
    public static SpeakingDialogsHolder instance;
    private HashMap<String, SpeakingDialog> dialogsArray;

    public SpeakingDialogsHolder() {
        dialogsArray = new HashMap<>();
    }

    public static synchronized SpeakingDialogsHolder getInstance() {
        SpeakingDialogsHolder speakingDialogsHolder;
        synchronized (SpeakingDialogsHolder.class) {
            if (instance == null)
                instance = new SpeakingDialogsHolder();
            speakingDialogsHolder = instance;
        }
        return speakingDialogsHolder;
    }

    public void putDialog(SpeakingDialog dialog) {
        dialogsArray.put(dialog.getApiId(), dialog);
    }

    public void putDialogs(String dialogId, List<SpeakingDialog> dialogs) {
        for (SpeakingDialog dialog : dialogs) {
            dialogsArray.put(dialogId, dialog);
        }
    }

    public SpeakingDialog getDialogById(String apiId) {
        return dialogsArray.get(apiId);
    }

    public List<SpeakingDialog> getDialogsByIds(List<String> ids) {
        List<SpeakingDialog> dialogs = new ArrayList<>();
        for (String id : ids) {
            SpeakingDialog dialog = getDialogById(id);
            if (dialog != null)
                dialogs.add(dialog);
        }
        return dialogs;
    }

    public ArrayList<SpeakingDialog> getAllDialogs() {
        ArrayList<SpeakingDialog> result = new ArrayList<>();
        Map<String, SpeakingDialog> sortedMap = getSortedMap(dialogsArray);

        for (Map.Entry<String, SpeakingDialog> entry : sortedMap.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    public ArrayList<SpeakingDialog> getDialogsByParentIdAndIndex(String parentId, int index) {
        ArrayList<SpeakingDialog> result = new ArrayList<>();
        Map<String, SpeakingDialog> sortedMap = getSortedMap(dialogsArray);

        for (Map.Entry<String, SpeakingDialog> entry : sortedMap.entrySet()) {
            if (entry.getValue().getParentId().equals(parentId) && entry.getValue().getIndex() == index) {
                result.add(entry.getValue());
            }
        }

        return result;
    }

    public ArrayList<SpeakingDialog> getDialogsByParentId(String parentId) {
        ArrayList<SpeakingDialog> result = new ArrayList<>();
        Map<String, SpeakingDialog> sortedMap = getSortedMap(dialogsArray);

        for (Map.Entry<String, SpeakingDialog> entry : sortedMap.entrySet()) {
            if (entry.getValue().getParentId().equals(parentId)) {
                result.add(entry.getValue());
            }
        }

        return result;
    }

    public void updateDialog(SpeakingDialog dialog) {
        dialogsArray.put(dialog.getApiId(), dialog);
    }

    public void updateDialogs(List<SpeakingDialog> dialogs) {
        for (SpeakingDialog newDialog : dialogs) {
            updateDialog(newDialog);
        }
    }

    public void clear() {
        dialogsArray.clear();
    }

    private Map<String, SpeakingDialog> getSortedMap(Map<String, SpeakingDialog> unsortedMap) {
        Map<String, SpeakingDialog> sortedMap = new TreeMap(new LastCreatedDateDialogComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }

    static class LastCreatedDateDialogComparator implements Comparator<String> {
        Map<String, SpeakingDialog> map;

        public LastCreatedDateDialogComparator(Map<String, SpeakingDialog> map) {
            this.map = map;
        }

        public int compare(String keyA, String keyB) {

            long valueA = map.get(keyA).getCreatedAt();
            long valueB = map.get(keyB).getCreatedAt();

            if (valueB > valueA) {
                return -1;
            } else {
                return 1;
            }
        }
    }


}
