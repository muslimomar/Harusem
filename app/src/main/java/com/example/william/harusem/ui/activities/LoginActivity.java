package com.example.william.harusem.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.william.harusem.R;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.william.harusem.common.Extras.ACCOUNT_KEY;
import static com.example.william.harusem.common.Extras.APP_ID;
import static com.example.william.harusem.common.Extras.AUTH_KEY;
import static com.example.william.harusem.common.Extras.AUTH_SECRET;
import static com.example.william.harusem.util.Utils.buildAlertDialog;
import static com.example.william.harusem.util.Utils.buildProgressDialog;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 90;
    private static final String TAG = LoginActivity.class.getSimpleName();
    ProgressDialog loadingPb;

    @BindView(R.id.email_et)
    EditText emailEt;
    @BindView(R.id.login_password_et)
    EditText passwordEt;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.signup_btn)
    Button signupBtn;
    @BindView(R.id.forgot_pass_tv)
    TextView forgotPassTv;
    @BindView(R.id.avPb)
    AVLoadingIndicatorView avPb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        requestPerms();
//        initializeFramework();
        hideActionBar();

        hideSoftKeyboard();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Log.d(TAG, "onRequestPermissionsResult:Permission Granted ");
            else {
                Log.d(TAG, "onRequestPermissionsResult: Permissions denied ");
            }
        }

    }


    private void requestPerms() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CODE);
        }

    }

    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }

    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private void onLogin() {
        if (getEditTextString(emailEt).isEmpty() && getEditTextString(passwordEt).isEmpty()) {
            emailEt.setError("Please enter your email and password!");
            animateError(emailEt);
            animateError(passwordEt);
        }

        if (getEditTextString(emailEt).isEmpty() ||
                !isEmailValid(getEditTextString(emailEt))) {
            emailEt.setError("Please enter a valid email address!");
            animateError(emailEt);
            return;
        }

        if (getEditTextString(passwordEt).isEmpty() ||
                (getEditTextString(passwordEt).length() < 6)) {
            passwordEt.setError("Please enter a valid password!");
            animateError(passwordEt);
            return;
        }
        login(getEditTextString(emailEt), getEditTextString(passwordEt));
    }

    public void animateError(EditText field) {
        YoYo.with(Techniques.Shake).duration(700)
                .playOn(field);
    }

    private void login(final String email, final String pass) {

        //loadingPb = buildProgressDialog(this, getString(R.string.please_wait), getString(R.string.dlg_loading), false);
        //loadingPb.show();
        avPb.setIndicatorColor(getResources().getColor(R.color.colorPrimary));
        avPb.show();

        final QBUser user = new QBUser(email, pass);

        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                QBChatService.getInstance().login(user, new QBEntityCallback() {

                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        //dismissDialog(loadingPb);
                        avPb.hide();
                        qbUser.setPassword(user.getPassword());
                        SharedPrefsHelper.getInstance().saveQbUser(qbUser);
                        QBUsersHolder.getInstance().setSignInQbUser(qbUser);
                        QBFriendListHelper friendListHelper = new QBFriendListHelper(LoginActivity.this);
                        loadUserFullName();
                        redirectToMainActivity(email, pass);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        //dismissDialog(loadingPb);
                        avPb.hide();
                        buildAlertDialog("Login Failed", e.getMessage(), true, LoginActivity.this);
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
                //dismissDialog(loadingPb);
                avPb.hide();
                buildAlertDialog("Login Failed", e.getMessage(), true, LoginActivity.this);
            }

        });

    }

    ;

    private void hideActionBar() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @OnClick(R.id.forgot_pass_tv)
    public void onPassReset(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    @OnClick(R.id.signup_btn)
    public void setSignupBtn(View view) {
        startActivity(new Intent(this, SignupActivity.class));
    }

    @OnClick(R.id.login_btn)
    public void setLoginBtn(View view) {
        onLogin();
    }

    boolean isEmailValid(CharSequence email) {
        return (Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }


    public String getEditTextString(EditText editText) {
        return editText.getText().toString().trim();
    }


    private void redirectToMainActivity(String email, String pass) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("user", email);
        intent.putExtra("password", pass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void dismissDialog(ProgressDialog pb) {
        if (pb.isShowing()) {
            pb.dismiss();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    private void loadUserFullName() {
        QBUser currentUser = QBChatService.getInstance().getUser();
        QBUsers.getUser(currentUser.getId()).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle bundle) {
                String fullName = user.getFullName();
                SharedPreferences pref = getApplicationContext().getSharedPreferences("user_details", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("QB_USER_FULL_NAME_FOR_NOTIFICATIONS", fullName);  // Saving string
                editor.apply();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
            }
        });
    }
}
