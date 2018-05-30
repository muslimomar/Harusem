package com.example.william.harusem.common;

import com.example.william.harusem.holder.QBUsersHolder;
import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * Created by william on 5/29/2018.
 */

public class Common {

    public static final String DIALOG_EXTRA = "Dialogs";

    public static String createChatDialogName(List<Integer> qbUsers) {

        List<QBUser> qbUsers1 = QBUsersHolder.getInstance().getUsersByIds(qbUsers);
        StringBuilder name = new StringBuilder();
        for (QBUser user : qbUsers1)
            name.append(user.getFullName()).append(", ");
        if(name.length() > 30)
            name = name.replace(30,name.length()-1, "...");
        return name.toString();
    }

}
