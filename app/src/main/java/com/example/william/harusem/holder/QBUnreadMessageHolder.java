package com.example.william.harusem.holder;

import android.os.Bundle;

/**
 * Created by william on 5/31/2018.
 */

public class QBUnreadMessageHolder {

    private static QBUnreadMessageHolder instance;
    private Bundle bundle;

    public static synchronized QBUnreadMessageHolder getInstance() {
        QBUnreadMessageHolder qbUnreadMessageHolder;
        synchronized (QBUnreadMessageHolder.class) {
            if (instance == null)
                instance = new QBUnreadMessageHolder();
            qbUnreadMessageHolder = instance;
        }
        return qbUnreadMessageHolder;
    }

    public QBUnreadMessageHolder() {
        bundle = new Bundle();
    }

    public Bundle getBundle() {
        return this.bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public int getUnreadMessageByDialogId(String id) {
        return this.bundle.getInt(id);

    }


}
