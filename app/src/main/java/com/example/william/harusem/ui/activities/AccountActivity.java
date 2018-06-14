package com.example.william.harusem.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.william.harusem.R;
import com.example.william.harusem.util.Utils;
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

public class AccountActivity extends AppCompatActivity {

    public static final String TAG = AccountActivity.class.getSimpleName();
    @BindView(R.id.name_et)
    EditText nameEt;
    @BindView(R.id.email_et)
    EditText emailEt;
    @BindView(R.id.country_tv)
    TextView countryTv;
    @BindView(R.id.container_layout)
    LinearLayout containerLayout;
    @BindView(R.id.ui_progress_bar)
    ProgressBar uiProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        setTitle("Account");

        containerLayout.setVisibility(View.GONE);
        uiProgressBar.setVisibility(View.VISIBLE);

        hideSoftKeyboard();
        loadUserData();
    }

    private void loadUserData() {
        QBUser currentUser = QBChatService.getInstance().getUser();
        QBUsers.getUser(currentUser.getId()).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle bundle) {
                String fullName = user.getFullName();
                String email = user.getEmail();
                String country = user.getCustomData();

                nameEt.setText(fullName);
                emailEt.setText(email);
                countryTv.setText(country);
                Log.d(TAG, "onSuccess: loaduserdata");

                uiProgressBar.setVisibility(View.GONE);
                containerLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
            }
        });


    }

    private void saveChanges() {
        final ProgressDialog progressDialog = Utils.buildProgressDialog(this, "", "Updating Profile...", false);

        String fullName = nameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String country = countryTv.getText().toString().trim();


        if (fullName.isEmpty()) {
            nameEt.setError("Please enter a valid name!");
            nameEt.requestFocus();
            return;
        }

        if (email.isEmpty() ||
                !isEmailValid(email)) {
            emailEt.setError("Please enter a valid email address!");
            emailEt.requestFocus();
            return;
        }

        final QBUser user = new QBUser();
        user.setId(QBChatService.getInstance().getUser().getId());
        user.setFullName(fullName);
        user.setEmail(email);
        user.setCustomData(country);

        progressDialog.show();
        QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle bundle) {
                Log.d(TAG, "onSuccess: " + user.getLogin());
                progressDialog.dismiss();
                NavUtils.navigateUpFromSameTask(AccountActivity.this);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                Toast.makeText(AccountActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + user.getLogin(), e);
            }
        });

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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                finish();
                break;
            case R.id.save:
                saveChanges();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    public void setCountryTv(View view) {
        setupCountryPicker();
    }


    boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }


}
