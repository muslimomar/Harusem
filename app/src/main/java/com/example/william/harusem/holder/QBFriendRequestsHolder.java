package com.example.william.harusem.holder;

import android.util.SparseArray;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by william on 5/29/2018.
 */

public class QBFriendRequestsHolder {

    private static QBFriendRequestsHolder instance;

    private SparseArray<QBUser> qbFriendRequestsArray;

    private QBFriendRequestsHolder() {
        qbFriendRequestsArray = new SparseArray<>();
    }

    public static synchronized QBFriendRequestsHolder getInstance() {
        if (instance == null)
            instance = new QBFriendRequestsHolder();
        return instance;
    }

    public void putRequests(List<QBUser> users) {
        for (QBUser user : users) {
            putRequest(user);
        }
    }

    public void putRequest(QBUser user) {
        qbFriendRequestsArray.put(user.getId(), user);
    }

    public QBUser getRequestById(int id) {
        return qbFriendRequestsArray.get(id);
    }

    public List<QBUser> getRequestsById(List<Integer> ids) {
        List<QBUser> qbUser = new ArrayList<>();
        for (Integer id : ids) {
            QBUser user = getRequestById(id);
            if (user != null)
                qbUser.add(user);
        }
        return qbUser;
    }


    public ArrayList<QBUser> getAllFriendRequests() {
        ArrayList<QBUser> result = new ArrayList<>();
        for (int i = 0; i < qbFriendRequestsArray.size(); i++)
            result.add(qbFriendRequestsArray.valueAt(i));
        return result;
    }


    public void removeFriendRequest(int id) {
        qbFriendRequestsArray.remove(id);
    }
}
