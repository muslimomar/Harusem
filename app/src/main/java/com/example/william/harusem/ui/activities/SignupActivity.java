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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.william.harusem.R;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.models.UserData;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.example.william.harusem.util.Toaster;
import com.google.gson.Gson;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.OnCountryPickerListener;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.List;

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
    @BindView(R.id.mother_language)
    Spinner motherLanguage;
    @BindView(R.id.language_level)
    Spinner languageLevel;
    @BindView(R.id.language_level_tv)
    TextView languageLevelTv;
    String[] languagesArray;
    String[] levelsArray;
    String[] learningLanguagesArray;
    boolean isSelected = false;
    String selectedLanguage, selectedLanguageLevel, selectedLearningLanguage, arabicLevel, englishLevel, turkishLevel;
    @BindView(R.id.learning_language_tv)
    TextView learningLanguageTv;
    @BindView(R.id.learning_language)
    Spinner learningLanguage;
    public static String getEditTextString(EditText editText) {
        return editText.getText().toString().trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        hideSoftKeyboard();
        spinnerMotherLanguage();
        spinnerLearnLanguage();
        spinnerLevel();
    }

    private void spinnerMotherLanguage() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mother_languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        languagesArray = getResources().getStringArray(R.array.mother_languages_array);
        motherLanguage.setAdapter(adapter);
        motherLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = motherLanguage.getSelectedItem().toString();
                if (selectedLanguage.equals("Select your mother language")) {
                    learningLanguage.setVisibility(View.INVISIBLE);
                    learningLanguageTv.setVisibility(View.INVISIBLE);
                    languageLevel.setVisibility(View.INVISIBLE);
                    languageLevelTv.setVisibility(View.INVISIBLE);
                } else {
                    learningLanguage.setVisibility(View.VISIBLE);
                    learningLanguageTv.setVisibility(View.VISIBLE);
                    learningLanguage.setSelection(0);
                    isSelected =true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(SignupActivity.this, R.string.pls_select_moth_lang, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void spinnerLearnLanguage() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.learning_language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        learningLanguagesArray = getResources().getStringArray(R.array.learning_language_array);
        learningLanguage.setAdapter(adapter);
        learningLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLearningLanguage = learningLanguage.getSelectedItem().toString();
                if (selectedLearningLanguage.equals("Select learning language")) {
                    languageLevel.setVisibility(View.INVISIBLE);
                    languageLevelTv.setVisibility(View.INVISIBLE);
                    languageLevel.setVisibility(View.INVISIBLE);
                } else {
                    languageLevel.setVisibility(View.VISIBLE);
                    //learningLanguageTv.setVisibility(View.VISIBLE);
                    languageLevelTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void spinnerLevel() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.levels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelsArray = getResources().getStringArray(R.array.levels_array);
        languageLevel.setAdapter(adapter);
        languageLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedLanguageLevel = languageLevel.getSelectedItem().toString();
                if (selectedLanguageLevel.equals("Select your level")) {
                    languageLevel.setVisibility(View.INVISIBLE);
                    languageLevelTv.setVisibility(View.INVISIBLE);
                    learningLanguage.setSelection(0);
                } else {
                    languageLevel.setVisibility(View.VISIBLE);
                    languageLevelTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void onSignup() {

        if (getEditTextString(nameEt).isEmpty()) {
            nameEt.setError(getString(R.string.pls_enter_full_name));
            nameEt.requestFocus();
            return;
        }

        if (getEditTextString(emailEt).isEmpty() ||
                !isEmailValid(getEditTextString(emailEt))) {
            emailEt.setError(getString(R.string.pls_enter_valid_email));
            emailEt.requestFocus();
            return;
        }

        if (getEditTextString(passwordEt).isEmpty()) {
            passwordEt.setError(getString(R.string.enter_valid_pass));
            passwordEt.requestFocus();
            return;
        }

        if ((getEditTextString(passwordEt).length() < 8)) {
            passwordEt.setError(getString(R.string.pass_too_short));
            passwordEt.requestFocus();
            return;
        }

        if (!getEditTextString(passConfirmEt).equals(getEditTextString(passwordEt))) {
            passConfirmEt.setError(getString(R.string.confirm_pass_notmatch));
            passConfirmEt.requestFocus();
            return;
        }

        if (selectedLanguage.equals("Select your mother language")) {
            Toast.makeText(this, R.string.select_yourlang, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!selectedLanguage.equals("Select your level") && selectedLearningLanguage.equals("Select learning language")) {
            Toast.makeText(this, R.string.select_learn_lang, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedLearningLanguage.equals("Select learning language")) {
            Toast.makeText(this, R.string.select_desired_learning_lang, Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedLanguageLevel.equals("Select your level")){
            Toaster.shortToast(R.string.select_ur_level);
            return;
        }

        if (selectedLanguage.equals(selectedLearningLanguage)){
            Toaster.shortToast(R.string.ur_learning_lang_err);
            return;
        }

        createAccount(getEditTextString(emailEt), getEditTextString(passwordEt), getEditTextString(nameEt));
    }

    private void createAccount(final String email, final String pass, String name) {

        loadingPb = buildProgressDialog(this, getString(R.string.pls_wait), getString(R.string.loading), false);
        loadingPb.show();

        final QBUser qbUser = new QBUser(email, pass);
        qbUser.setFullName(name);
        qbUser.setEmail(email);

        UserData userData = new UserData(selectedLanguage, selectedLearningLanguage, selectedLanguageLevel, countryTv.getText().toString().trim(), name);
        String json = new Gson().toJson(userData);
        qbUser.setCustomData(json);
        Log.d("customdata", "custom data: " + json);
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
                        Log.v("did_saved?", "result: " + fullName);
                        redirectToMainActivity();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        dismissDialog(loadingPb);
                        buildAlertDialog(getString(R.string.signup_failed), e.getMessage(), true, SignupActivity.this);
                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {
                dismissDialog(loadingPb);
                buildAlertDialog(getString(R.string.signup_failed), e.getMessage(), true, SignupActivity.this);

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