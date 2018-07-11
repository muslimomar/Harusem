package com.example.william.harusem;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.william.harusem.services.CallService;
import com.example.william.harusem.ui.activities.LoginActivity;
import com.example.william.harusem.ui.activities.MainActivity;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.example.william.harusem.ui.dialog.ProgressDialogFragment;
import com.example.william.harusem.util.ChatHelper;
import com.example.william.harusem.util.ChatPingAlarmManager;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.example.william.harusem.utils.SettingsUtil;
import com.example.william.harusem.utils.WebRtcSessionManager;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;

import org.jivesoftware.smackx.ping.PingFailedListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private final int SPLASH_DISPLAY_TIMER = 2000;
    @BindView(R.id.image_view)
    ImageView imageView;
    private QBRTCClient rtcClient;
    private QBChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        chatService = QBChatService.getInstance();
        rtcClient = QBRTCClient.getInstance(this);

        startSplash();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    private void startSplash() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkSignIn()) {
                    restoreChatSession();
                } else {
                    proceedToLogin();
                }
            }
        }, SPLASH_DISPLAY_TIMER);


        StartAnimations();
    }

    private void restoreChatSession() {
        if (ChatHelper.getInstance().isLogged()) {
            QBFriendListHelper friendListHelper = new QBFriendListHelper(SplashActivity.this);
            proceedToMainActivity();
        } else {
            QBUser user = getUserFromSession();
            loginToChat(user);
        }

    }

    private void loginToChat(final QBUser user) {
        // show dialog
        ProgressDialogFragment.show(getSupportFragmentManager(), R.string.dlg_restoring_chat_session);

        ChatHelper.getInstance().loginToChat(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                // dismiss dialog
                initPingListener();
                initQBRTCClient();

                ProgressDialogFragment.hide(getSupportFragmentManager());
                QBFriendListHelper friendListHelper = new QBFriendListHelper(SplashActivity.this);
                proceedToMainActivity();

            }

            @Override
            public void onError(QBResponseException e) {
                // dismiss dialog
                ProgressDialogFragment.hide(getSupportFragmentManager());
                Log.e(TAG, "onError: ", e);

                showErrorSnackbar( findViewById(R.id.lin_lay), R.string.error_recreate_session, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loginToChat(user);
                            }
                        });

            }
        });

    }

    protected Snackbar showErrorSnackbar(View chatListRecyclerView,@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(chatListRecyclerView, resId, e,
                R.string.dlg_retry, clickListener);
    }


    private void proceedToLogin() {
        Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void proceedToMainActivity() {
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }


    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l = findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.image_view);
        iv.clearAnimation();
        iv.startAnimation(anim);
    }

    protected boolean checkSignIn() {
        return QBSessionManager.getInstance().getSessionParameters() != null;
    }

    private QBUser getUserFromSession() {
        QBUser user = SharedPrefsHelper.getInstance().getQbUser();
        user.setId(QBSessionManager.getInstance().getSessionParameters().getUserId());
        return user;
    }

    private void initPingListener() {
        ChatPingAlarmManager.onCreate(this);
        ChatPingAlarmManager.getInstanceFor().addPingListener(new PingFailedListener() {
            @Override
            public void pingFailed() {
                Log.d(TAG, "Ping chat server failed");
            }
        });
    }

    private void initQBRTCClient() {
        rtcClient = QBRTCClient.getInstance(getApplicationContext());
        // Add signalling manager
        chatService.getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                if (!createdLocally) {
                    rtcClient.addSignaling((QBWebRTCSignaling) qbSignaling);
                }
            }
        });

        // Configure
        QBRTCConfig.setDebugEnabled(true);
        SettingsUtil.configRTCTimers(this);

        // Add service as callback to RTCClient
        rtcClient.addSessionCallbacksListener(WebRtcSessionManager.getInstance(this));
        rtcClient.prepareToProcessCalls();
    }

}
