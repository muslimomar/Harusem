package com.example.william.harusem;

import android.app.Application;
import android.util.Log;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.messages.services.QBPushManager;

import static com.example.william.harusem.common.Extras.ACCOUNT_KEY;
import static com.example.william.harusem.common.Extras.APP_ID;
import static com.example.william.harusem.common.Extras.AUTH_KEY;
import static com.example.william.harusem.common.Extras.AUTH_SECRET;

/**
 * Created by william on 6/1/2018.
 */

public class Harusem extends Application {

    public static final String TAG = Harusem.class.getSimpleName();

    private static Harusem instance;

    public static synchronized Harusem getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initializeFramework();

    }


    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
        QBPushManager.getInstance().addListener(new QBPushManager.QBSubscribeListener() {
            @Override
            public void onSubscriptionCreated() {
                Log.e("SUBSCRIPTION", "subscription created success");
            }

            @Override
            public void onSubscriptionError(Exception e, int i) {
                Log.e("SUBSCRIPTOPN", e.getMessage());
            }

            @Override
            public void onSubscriptionDeleted(boolean b) {

            }
        });
    }

}
