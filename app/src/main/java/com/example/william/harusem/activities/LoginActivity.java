package com.example.william.harusem.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.william.harusem.R;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.william.harusem.util.Extras.ACCOUNT_KEY;
import static com.example.william.harusem.util.Extras.APP_ID;
import static com.example.william.harusem.util.Extras.AUTH_KEY;
import static com.example.william.harusem.util.Extras.AUTH_SECRET;
import static com.example.william.harusem.util.Helper.buildAlertDialog;
import static com.example.william.harusem.util.Helper.buildProgressDialog;

public class LoginActivity extends AppCompatActivity {
    ProgressDialog loadingPb;

    @BindView(R.id.email_et)
    EditText emailEt;
    @BindView(R.id.password_et)
    EditText passwordEt;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.signup_btn)
    Button signupBtn;
    @BindView(R.id.forgot_pass_tv)
    TextView forgotPassTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        initializeFramework();

        hideActionBar();

        hideSoftKeyboard();

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
        if (getEditTextString(emailEt).isEmpty() ||
                !isEmailValid(getEditTextString(emailEt))) {
            emailEt.setError("Please enter a valid email address!");
            emailEt.requestFocus();
            return;
        }

        if (getEditTextString(passwordEt).isEmpty() ||
                (getEditTextString(passwordEt).length() < 6)) {
            passwordEt.setError("Please enter a valid password!");
            passwordEt.requestFocus();
            return;
        }

        login(getEditTextString(emailEt), getEditTextString(passwordEt));

    }

    private void login(final String email, final String pass) {
        loadingPb = buildProgressDialog(this, "Please Wait..", "Loading........", false);
        loadingPb.show();


        QBUser qbUser = new QBUser(email, pass);

        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                dismissDialog(loadingPb);
                redirectToMainActivity(email, pass);
            }

            @Override
            public void onError(QBResponseException e) {
                dismissDialog(loadingPb);
                buildAlertDialog("Login Failed", e.getMessage(), true, LoginActivity.this);
            }
        });

    }


    private void hideActionBar() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @OnClick(R.id.forgot_pass_tv)
    public void onPassReset(View view) {
        Toast.makeText(this, "reset password", Toast.LENGTH_SHORT).show();
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
}

