package com.example.william.harusem.utils;

import android.text.TextUtils;

import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tereha on 09.06.16.
 */
public class UsersUtils {

    private static SharedPrefsHelper sharedPrefsHelper;
    private static QBUsersHolder qbUsersHolder;

    public static String getUserNameOrId(QBUser qbUser, Integer userId) {
        if (qbUser == null) {
            return String.valueOf(userId);
        }

        String fullName = qbUser.getFullName();

        return TextUtils.isEmpty(fullName) ? String.valueOf(userId) : fullName;
    }

    public static ArrayList<QBUser> getListAllUsersFromIds(ArrayList<QBUser> existedUsers, List<Integer> allIds) {
        ArrayList<QBUser> qbUsers = new ArrayList<>();


        for (Integer userId : allIds) {
            QBUser stubUser = createStubUserById(userId);
            if (!existedUsers.contains(stubUser)) {
                qbUsers.add(stubUser);
            }
        }

        qbUsers.addAll(existedUsers);

        return qbUsers;
    }

    private static QBUser createStubUserById(Integer userId) {
        QBUser stubUser = new QBUser(userId);
        stubUser.setFullName(String.valueOf(userId));
        return stubUser;
    }

    public static ArrayList<Integer> getIdsNotLoadedUsers(ArrayList<QBUser> existedUsers, List<Integer> allIds) {
        ArrayList<Integer> idsNotLoadedUsers = new ArrayList<>();

        for (Integer userId : allIds) {
            QBUser stubUser = createStubUserById(userId);
            if (!existedUsers.contains(stubUser)) {
                idsNotLoadedUsers.add(userId);
            }
        }

        return idsNotLoadedUsers;
    }

    //////////////Context context
    public static void removeUserData() {
        if (sharedPrefsHelper == null) {
            sharedPrefsHelper = SharedPrefsHelper.getInstance();
        }
        sharedPrefsHelper.clearAllData();
        if (qbUsersHolder == null) {
            // qbUsersHolder = QBUsersHolder.getInstance(context);
            qbUsersHolder = QBUsersHolder.getInstance();
        }
        //qbUsersHolder.clearDB();
        /////////////////////////////////
        ////////////////////////////7
    }


}