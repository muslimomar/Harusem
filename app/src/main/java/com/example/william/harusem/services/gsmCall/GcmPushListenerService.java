package com.example.william.harusem.services.gsmCall;

import android.os.Bundle;
import android.util.Log;

import com.example.william.harusem.services.CallService;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.example.william.harusem.util.consts.GcmConsts;
import com.google.android.gms.gcm.GcmListenerService;
import com.quickblox.users.model.QBUser;

/**
 * Created by tereha on 13.05.16.
 */
public class GcmPushListenerService extends GcmListenerService {
    private static final String TAG = GcmPushListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString(GcmConsts.EXTRA_GCM_MESSAGE);
        Log.v(TAG, "From: " + from);
        Log.v(TAG, "Message: " + message);

/////////////////////////////////////////////////////////////////// I used getBaseContext()
         SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
          if (sharedPrefsHelper.hasQbUser()) {
            Log.d(TAG, "App have logined user");
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            startLoginService(qbUser);
        }
    }

    private void startLoginService(QBUser qbUser){
        CallService.start(this, qbUser);
    }
}