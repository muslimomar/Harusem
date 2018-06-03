package com.example.william.harusem;

import android.app.Application;

import com.quickblox.auth.session.QBSettings;

import static com.example.william.harusem.util.Extras.ACCOUNT_KEY;
import static com.example.william.harusem.util.Extras.APP_ID;
import static com.example.william.harusem.util.Extras.AUTH_KEY;
import static com.example.william.harusem.util.Extras.AUTH_SECRET;

/**
 * Created by william on 6/1/2018.
 */

public class Harusem extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        initializeFramework();

    }


    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }

}
