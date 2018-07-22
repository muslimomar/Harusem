package com.example.william.harusem.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.william.harusem.R;
import com.example.william.harusem.util.Utils;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PasswordActivity extends AppCompatActivity {
    public static final String TAG = PasswordActivity.class.getSimpleName();

    @BindView(R.id.old_password_et)
    EditText oldPasswordEt;
    @BindView(R.id.new_password_et)
    EditText newPasswordEt;
    @BindView(R.id.confirm_password_et)
    EditText confirmNewPasswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
        setTitle(getString(R.string.change_pass));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.password_change_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  // geri butonu için action verildi.
                NavUtils.navigateUpFromSameTask(this);
                finish();
                break;
            case R.id.save:
                changePassword();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changePassword() {
        final ProgressDialog progressDialog = Utils.buildProgressDialog(this, "", getString(R.string.updating_pass), false);

        String oldPass = oldPasswordEt.getText().toString().trim();
        String newPass = newPasswordEt.getText().toString().trim();
        String confNewPass = confirmNewPasswordEt.getText().toString().trim();

        if (oldPass.isEmpty()) {
            oldPasswordEt.setError(getString(R.string.pls_fill_this_filed));
            oldPasswordEt.requestFocus();
            return;
        }

        if (newPasswordEt.length() < 8) {
            newPasswordEt.setError(getString(R.string.pass_too_short_8));
            newPasswordEt.requestFocus();
            return;
        }

        if (!confNewPass.equals(newPass)) {
            confirmNewPasswordEt.setError(getString(R.string.confirm_pass_should_match));
            confirmNewPasswordEt.requestFocus();
            return;
        }

        final QBUser user = new QBUser();
        user.setId(QBChatService.getInstance().getUser().getId());
        user.setOldPassword(oldPass);
        user.setPassword(confNewPass);
        progressDialog.show();
        QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle bundle) {
                Log.d(TAG, "onSuccess: " + user.getLogin());
                progressDialog.dismiss();
                NavUtils.navigateUpFromSameTask(PasswordActivity.this);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                Toast.makeText(PasswordActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + user.getLogin(), e);
            }
        });


    }


}
