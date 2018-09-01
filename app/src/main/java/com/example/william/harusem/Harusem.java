package com.example.william.harusem;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.william.harusem.models.QbConfigs;
import com.example.william.harusem.util.ActivityLifecycle;
import com.example.william.harusem.util.QBResRequestExecutor;
import com.example.william.harusem.utils.CoreConfigUtils;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.ServiceZone;
import com.quickblox.messages.services.QBPushManager;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.example.william.harusem.common.Extras.ACCOUNT_KEY;
import static com.example.william.harusem.common.Extras.APP_ID;
import static com.example.william.harusem.common.Extras.AUTH_KEY;
import static com.example.william.harusem.common.Extras.AUTH_SECRET;
import static com.quickblox.core.QBSettingsSaver.API_DOMAIN;
import static com.quickblox.core.QBSettingsSaver.CHAT_DOMAIN;

/**
 * Created by william on 6/1/2018.
 */

public class Harusem extends Application {

    public static final String TAG = Harusem.class.getSimpleName();

    private static Harusem instance;
    protected QbConfigs qbConfigs;
    private QBResRequestExecutor qbResRequestExecutor;

    private static final String QB_CONFIG_DEFAULT_FILE_NAME = "qb_config.json";

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
//        Realm.init(this);
//        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
//                .name("harusem.realm")
//                .schemaVersion(0)
//                .build();
//        Realm.setDefaultConfiguration(realmConfig);

        QBPushManager.getInstance().addListener(new QBPushManager.QBSubscribeListener() {
            @Override
            public void onSubscriptionCreated() {
                Log.v("Pushsubscription!","succes!");
            }

            @Override
            public void onSubscriptionError(Exception e, int i) {
                Log.v("Pushsubscription!","error!"+e.getMessage());
            }

            @Override
            public void onSubscriptionDeleted(boolean b) {


            }
        });
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

    public void initCredentials() {
        if (qbConfigs != null) {

            if (!TextUtils.isEmpty(qbConfigs.getApiDomain()) && !TextUtils.isEmpty(qbConfigs.getChatDomain())) {
                QBSettings.getInstance().setEndpoints(qbConfigs.getApiDomain(), qbConfigs.getChatDomain(), ServiceZone.PRODUCTION);
                QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);
            }
        }
    }

    public QbConfigs getQbConfigs() {
        return qbConfigs;
    }

    protected String getQbConfigFileName() {
        return QB_CONFIG_DEFAULT_FILE_NAME;
    }
}
