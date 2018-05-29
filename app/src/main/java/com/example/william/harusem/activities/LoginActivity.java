package com.example.william.harusem.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.william.harusem.R;
import com.example.william.harusem.util.Extras;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.william.harusem.util.Extras.CONNECTION_STATUS;
import static com.example.william.harusem.util.Extras.USERS_REF;
import static com.example.william.harusem.util.Helper.ONLINE;
import static com.example.william.harusem.util.Helper.buildAlertDialog;
import static com.example.william.harusem.util.Helper.buildProgressDialog;

public class LoginActivity extends AppCompatActivity {

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

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        hideActionBar();

        hideSoftKeyboard();

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

    private void login(String email, String pass) {

        final ProgressDialog loadingPb = buildProgressDialog(this, "Please Wait..", "Loading........", false);
        loadingPb.show();

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dismissDialog(loadingPb);
                if (task.isSuccessful()) {
                    setUserOnline();
                    redirectToMainActivity();
                } else {

                    buildAlertDialog("Login Failed", "Wrong email or password entered!", true, LoginActivity.this);
                }
            }
        });

    }

    private void setUserOnline() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(USERS_REF)
                    .child(userId)
                    .child(CONNECTION_STATUS)
                    .setValue(Extras.ONLINE);
        }

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
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public String getEditTextString(EditText editText) {
        return editText.getText().toString().trim();
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void dismissDialog(ProgressDialog pb) {
        if (pb.isShowing()) {
            pb.dismiss();
        }
    }



}

