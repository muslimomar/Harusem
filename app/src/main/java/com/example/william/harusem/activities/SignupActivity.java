package com.example.william.harusem.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.models.User;
import com.example.william.harusem.util.Extras;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.OnCountryPickerListener;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.william.harusem.util.Extras.USERS_REF;
import static com.example.william.harusem.util.Helper.ONLINE;
import static com.example.william.harusem.util.Helper.buildAlertDialog;
import static com.example.william.harusem.util.Helper.buildProgressDialog;

public class SignupActivity extends AppCompatActivity {

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

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public static String getEditTextString(EditText editText) {
        return editText.getText().toString().trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

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

        if (getEditTextString(passwordEt).isEmpty() ||
                (getEditTextString(passwordEt).length() < 6)) {
            passwordEt.setError("Please enter a valid password!");
            passwordEt.requestFocus();
            return;
        }

        if (!getEditTextString(passConfirmEt).equals(getEditTextString(passwordEt))) {
            passConfirmEt.setError("Confirm password should match the password!");
            passConfirmEt.requestFocus();
            return;
        }

        createAccount(getEditTextString(emailEt), getEditTextString(passwordEt));


    }

    private void createAccount(String email, String pass) {

        final ProgressDialog loadingPb = buildProgressDialog(this, "Please Wait..", "Loading........", true);
        loadingPb.show();

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dismissDialog(loadingPb);
                if (task.isSuccessful()) {

                    RegisterUser(task.getResult().getUser().getUid());

                    redirectToMainActivity();

                } else {

                    if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                        buildAlertDialog("Signup Failed", "The email is already used by another user!", true, SignupActivity.this);
                    }else {

                        buildAlertDialog("Signup Failed", "Wrong email or password entered!", true, SignupActivity.this);
                    }
                }

            }
        });


    }

    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private void RegisterUser(String userId) {
        User user = new User(
                getEditTextString(nameEt),
                getEditTextString(emailEt),
                ONLINE,
                new Date().getTime(),
                countryTv.getText().toString().trim()
        );

        mDatabase.child(USERS_REF).child(userId).setValue(user);
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
