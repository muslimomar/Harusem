package com.example.william.harusem.common;

import com.example.william.harusem.holder.QBUsersHolder;
import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * Created by william on 5/29/2018.
 */

public class Common {

    public static final String DIALOG_EXTRA = "Dialogs";

    public static final String UPDATE_DIALOG_EXTRA = "ChatDialogs";
    public static final String UPDATE_MODE = "Mode";
    public static final String UPDATE_ADD_MODE = "add";
    public static final String UPDATE_REMOVE_MODE = "remove";
    public static final int NOT_FOUND_HTTP_CODE = 404;
    public static final int INTERNAL_SERVER_ERROR_HTTP_CODE = 500;
    public static final int NO_INTERNET_CONNECTION_HTTP_CODE = 0;
    public static final String CATEGORY_API_NAME = "category_api_name";
    public static final String PARENT_ID = "parentId";
    public static final String INDEX = "index";
    public static final String USERS_LESSONS_DATA = "Users_Lessons_Data";
    public static final String USERS_CATEGORY_DATA = "Categories_Data";
    public static final String PUBLIC_CATEGORY_LESSONS = "Category_Lessons";

    public static String createChatDialogName(List<Integer> qbUsers) {

        List<QBUser> qbUsers1 = QBUsersHolder.getInstance().getUsersByIds(qbUsers);
        StringBuilder name = new StringBuilder();
        for (QBUser user : qbUsers1)
            name.append(user.getFullName()).append(", ");
        if (name.length() > 30) {
            name = name.replace(30, name.length() - 1, "...");
        } else {
            name.deleteCharAt(name.length() - 2);
        }

        return name.toString();
    }
}
