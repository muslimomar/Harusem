package com.example.william.harusem.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.william.harusem.R;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.Toaster;
import com.example.william.harusem.util.Utils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.william.harusem.common.Common.NOT_FOUND_HTTP_CODE;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.reset_email_et)
    EditText resetEmailEt;
    @BindView(R.id.btn_reset_password)
    Button btnResetPassword;
    @BindView(R.id.reset_pass_layout)
    LinearLayout resetPassLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setElevation(0);
        }

    }

    @OnClick(R.id.btn_reset_password)
    public void setBtnResetPassword(View view) {
        onPasswordReset();
    }

    private void onPasswordReset() {
        String email = resetEmailEt.getText().toString().trim();
        if (email.isEmpty() || !isEmailValid(email)) {
            Toaster.shortToast(R.string.err_valid_mail);
            return;
        }

        ProgressDialog progressDialog = Utils.buildProgressDialog(this, "", "Please Wait...", false);
        progressDialog.show();

        QBUsers.resetPassword(email).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                progressDialog.dismiss();
                Utils.buildAlertDialog(getString(R.string.done), getString(R.string.link_sent_pw), true, ForgotPasswordActivity.this);
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();

                if (e.getHttpStatusCode() == NOT_FOUND_HTTP_CODE) {
                    Utils.buildAlertDialog("Reset Failed", "No such email in our system!", true, ForgotPasswordActivity.this);
                }
                {
                    showErrorSnackbar(R.string.error_resetting_password, e, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onPasswordReset();
                        }
                    });
                }
            }
        });

    }

    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(resetPassLayout, resId, e,
                R.string.dlg_retry, clickListener);
    }

    boolean isEmailValid(CharSequence email) {
        return (Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reset_pass_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_proceed:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
