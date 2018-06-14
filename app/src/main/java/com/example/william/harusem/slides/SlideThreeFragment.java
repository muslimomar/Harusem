package com.example.william.harusem.slides;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.william.harusem.ui.activities.MainActivity;
import com.example.william.harusem.R;

import agency.tango.materialintroscreen.SlideFragment;


public class SlideThreeFragment extends SlideFragment {

    private static final int PERMISSION_REQUEST_CODE = 18;
    public static String TAG = "IntroActivity";
    SharedPreferences getPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.slide_3, parent, false);

        Log.d(TAG, "onCreateView: 3");

        getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        TextView slideOneTitle = rootView.findViewById(R.id.slide_2_title);
        TextView slideOneDescription = rootView.findViewById(R.id.slide_2_description);

        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GothamMedium.ttf");
        slideOneTitle.setTypeface(custom_font);
        slideOneDescription.setTypeface(custom_font);

        return rootView;

    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
        }
    }


    private boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int backgroundColor() {
        return android.R.color.transparent;
    }

    @Override
    public int buttonsColor() {
        return R.color.slide_three_button;
    }

    @Override
    public boolean canMoveFurther() {

        if (checkPermission()) {
            SharedPreferences.Editor e = getPrefs.edit();
            e.putBoolean("firstStart", false);
            e.apply();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
            return super.canMoveFurther();

        } else {
            requestPermission();
        }

        return false;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return "You need to grant permissions to proceed!";
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        if (visible) {
            requestPermission();
        }
        super.setMenuVisibility(visible);
    }


}

