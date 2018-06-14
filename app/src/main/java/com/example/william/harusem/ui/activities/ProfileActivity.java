package com.example.william.harusem.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.william.harusem.R;

import butterknife.ButterKnife;


public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);


    }

}
