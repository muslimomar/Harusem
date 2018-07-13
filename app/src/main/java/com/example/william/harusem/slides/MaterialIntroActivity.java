package com.example.william.harusem.slides;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.william.harusem.ui.activities.MainActivity;

/**
 * Created by william on 2/22/2018.
 */

public class MaterialIntroActivity extends agency.tango.materialintroscreen.MaterialIntroActivity {

    SlideOneFragment slideOneFragment = new SlideOneFragment();
    SlideTwoFragment slideTwoFragment = new SlideTwoFragment();
    SlideThreeFragment slideThreeFragment = new SlideThreeFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                if (!isFirstStart) {

                    final Intent i = new Intent(MaterialIntroActivity.this, MainActivity.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(i);
                            finish();
                        }
                    });

                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();

                }
            }
        });

        t.start();

        addSlide(slideOneFragment);
        addSlide(slideTwoFragment);
        addSlide(slideThreeFragment);


    }


}
