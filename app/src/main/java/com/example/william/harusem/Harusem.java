package com.example.william.harusem;

import android.app.Application;
import android.util.Log;

import com.example.william.harusem.util.ActivityLifecycle;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.messages.services.QBPushManager;
import com.crashlytics.android.Crashlytics;
import com.example.william.harusem.models.QbConfigs;
import com.example.william.harusem.util.QBResRequestExecutor;
import com.example.william.harusem.utils.CoreConfigUtils;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.auth.session.QBSessionParameters;
import android.text.TextUtils;
import com.quickblox.core.ServiceZone;
import io.fabric.sdk.android.Fabric;

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
    private QBResRequestExecutor qbResRequestExecutor;
    private static final String QB_CONFIG_DEFAULT_FILE_NAME = "qb_config.json";
    protected QbConfigs qbConfigs;

    public static synchronized Harusem getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initializeFramework();
        Fabric.with(this, new Crashlytics());
        initQbConfigs();
        initCredentials();
        ActivityLifecycle.init(this);
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
    public synchronized QBResRequestExecutor getQbResRequestExecutor() {
        return qbResRequestExecutor == null
                ? qbResRequestExecutor = new QBResRequestExecutor()
                : qbResRequestExecutor;
    }

    private void initQbConfigs() {
        Log.e(TAG, "QB CONFIG FILE NAME: " + getQbConfigFileName());
        qbConfigs = CoreConfigUtils.getCoreConfigsOrNull(getQbConfigFileName());
    }

    public void initCredentials(){
        if (qbConfigs != null) {

            if (!TextUtils.isEmpty(qbConfigs.getApiDomain()) && !TextUtils.isEmpty(qbConfigs.getChatDomain())) {
                QBSettings.getInstance().setEndpoints(qbConfigs.getApiDomain(), qbConfigs.getChatDomain(), ServiceZone.PRODUCTION);
                QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);
            }
        }
    }

    public QbConfigs getQbConfigs(){
        return qbConfigs;
    }
    protected String getQbConfigFileName(){
        return QB_CONFIG_DEFAULT_FILE_NAME;
    }
}
