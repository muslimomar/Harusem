package com.example.william.harusem.utils;

import android.content.Context;
import android.util.Log;

import com.example.william.harusem.ui.activities.CallActivity;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacksImpl;

import static org.webrtc.ContextUtils.getApplicationContext;

/**
 * Created by tereha on 16.05.16.
 */
public class WebRtcSessionManager extends QBRTCClientSessionCallbacksImpl {
    private static final String TAG = WebRtcSessionManager.class.getSimpleName();

    private static WebRtcSessionManager instance;
    private static QBRTCSession currentSession;
    private Context context;

    private WebRtcSessionManager(Context context) {
        this.context = context;
    }

    public static WebRtcSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new WebRtcSessionManager(context);
        }

        return instance;
    }

    public QBRTCSession getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(QBRTCSession qbCurrentSession) {
        currentSession = qbCurrentSession;
    }

    @Override
    public void onReceiveNewSession(QBRTCSession session) {
        Log.d(TAG, "onReceiveNewSession to WebRtcSessionManager");

        if (currentSession == null) {
            setCurrentSession(session);
//            OpponentsActivity.start(context, true);
            if (getCurrentSession() != null) {
                CallActivity.start(context, true);
            }
            PermissionsChecker checker = new PermissionsChecker(getApplicationContext());

        }
    }

    @Override
    public void onSessionClosed(QBRTCSession session) {
        Log.d(TAG, "onSessionClosed WebRtcSessionManager");

        if (session.equals(getCurrentSession())) {
            setCurrentSession(null);
        }
    }
}