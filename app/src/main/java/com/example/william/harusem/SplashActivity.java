package com.example.william.harusem;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.william.harusem.activities.LoginActivity;
import com.example.william.harusem.activities.MainActivity;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private final int SPLASH_DISPLAY_TIMER = 2000;
    @BindView(R.id.image_view)
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

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
        if (QBChatService.getInstance().isLoggedIn()) {
            proceedToMainActivity();
        } else {
            QBUser user = getUserFromSession();
            loginToChat(user);
        }

    }

    private void loginToChat(QBUser user) {
        // show dialog

        QBChatService.getInstance().login(user, new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {
                // dismiss dialog
                proceedToMainActivity();
            }

            @Override
            public void onError(QBResponseException e) {
                // dismiss dialog
                Log.e(TAG, "onError: ", e);
            }
        });


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
        QBUser user = SharedPrefsHelper.getInstance(this).getQbUser();
        user.setId(QBSessionManager.getInstance().getSessionParameters().getUserId());
        return user;
    }

}
