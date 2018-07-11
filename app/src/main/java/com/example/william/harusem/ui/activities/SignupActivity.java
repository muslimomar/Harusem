// signup

package com.example.william.harusem.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.william.harusem.R;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.OnCountryPickerListener;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.william.harusem.util.Utils.buildAlertDialog;
import static com.example.william.harusem.util.Utils.buildProgressDialog;

public class SignupActivity extends AppCompatActivity {
    public static final String TAG = SignupActivity.class.getSimpleName();
    ProgressDialog loadingPb;
    @BindView(R.id.email_et)
    EditText emailEt;
    @BindView(R.id.password_et)
    EditText passwordEt;
    @BindView(R.id.pass_confirm_et)
    EditText passConfirmEt;
    @BindView(R.id.country_tv)
    TextView countryTv;
    @BindView(R.id.name_et)
    EditText nameEt;

    public static String getEditTextString(EditText editText) {
        return editText.getText().toString().trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        hideSoftKeyboard();

    }

    private void onSignup() {

        if (getEditTextString(nameEt).isEmpty()) {
            nameEt.setError("Please enter your full name!");
            nameEt.requestFocus();
            return;
        }

        if (getEditTextString(emailEt).isEmpty() ||
                !isEmailValid(getEditTextString(emailEt))) {
            emailEt.setError("Please enter a valid email address!");
            emailEt.requestFocus();
            return;
        }

        if (getEditTextString(passwordEt).isEmpty()) {
            passwordEt.setError("Please enter a valid password!");
            passwordEt.requestFocus();
            return;
        }

        if ((getEditTextString(passwordEt).length() < 8)) {
            passwordEt.setError("Password is too short! (minimum is 8 characters)");
            passwordEt.requestFocus();
            return;
        }

        if (!getEditTextString(passConfirmEt).equals(getEditTextString(passwordEt))) {
            passConfirmEt.setError("Confirm password should match the password!");
            passConfirmEt.requestFocus();
            return;
        }

        createAccount(getEditTextString(emailEt), getEditTextString(passwordEt), getEditTextString(nameEt));
    }

    private void createAccount(final String email, final String pass, String name) {

        loadingPb = buildProgressDialog(this, "Please Wait..", "Loading........", false);
        loadingPb.show();

        final QBUser qbUser = new QBUser(email, pass);
        qbUser.setFullName(name);
        qbUser.setEmail(email);
        qbUser.setCustomData(countryTv.getText().toString().trim());

        QBUsers.signUpSignInTask(qbUser).performAsync(new QBEntityCallback<QBUser>() {

            @Override
            public void onSuccess(QBUser user, Bundle bundle) {
                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        dismissDialog(loadingPb);
                        QBUsersHolder.getInstance().putUser(qbUser);
                        QBUsersHolder.getInstance().setSignInQbUser(qbUser);
                        SharedPrefsHelper.getInstance().saveQbUser(qbUser);

                        QBFriendListHelper friendListHelper = new QBFriendListHelper(SignupActivity.this);

                        String fullName = nameEt.getText().toString();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("user_details", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("QB_USER_FULL_NAME_FOR_NOTIFICATIONS", fullName);  // Saving string
                        editor.apply();
                        Toast.makeText(SignupActivity.this, "Save To Success" + fullName, Toast.LENGTH_SHORT).show();
                        redirectToMainActivity();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        dismissDialog(loadingPb);
                        buildAlertDialog("Signup Failed", e.getMessage(), true, SignupActivity.this);
                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {
                dismissDialog(loadingPb);
                buildAlertDialog("Signup Failed", e.getMessage(), true, SignupActivity.this);

            }
        });

    }

    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void dismissDialog(ProgressDialog pb) {
        if (pb.isShowing()) {
            pb.dismiss();
        }
    }

    private void setupCountryPicker() {

        CountryPicker countryPicker = new CountryPicker.Builder().with(this)
                .listener(new OnCountryPickerListener() {
                    @Override
                    public void onSelectCountry(Country country) {
                        countryTv.setText(country.getName());
                    }
                }).build();

        countryPicker.showDialog(getSupportFragmentManager());
    }

    @OnClick(R.id.country_tv)
    public void countryClick(View view) {
        setupCountryPicker();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit:
                onSignup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
