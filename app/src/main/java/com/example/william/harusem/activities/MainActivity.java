package com.example.william.harusem.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.william.harusem.R;
import com.example.william.harusem.fragments.ChatDialogsFragment;
import com.example.william.harusem.fragments.ProfileFragment;
import com.example.william.harusem.fragments.SearchFragment;
import com.quickblox.auth.session.QBSessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    AHBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Chats", R.drawable.ic_chat_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Search", R.drawable.ic_search_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Account", R.drawable.ic_account_24dp);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigation.setAccentColor(getResources().getColor(R.color.colorPrimary));

        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                Fragment selectedFragment = null;
                switch (position) {
                    case 0:
                        //set title
                        selectedFragment = new ChatDialogsFragment();
                        break;
                    case 1:
                        selectedFragment = new SearchFragment();
                        break;
                    case 2:
                        selectedFragment = new ProfileFragment();
                        break;
                    default:
                        selectedFragment = new ChatDialogsFragment();
                        break;
                }

                switchFragment(selectedFragment);
                return true;
            }
        });

        // Default tab
        ChatDialogsFragment chatDialogsFragment = new ChatDialogsFragment();
        switchFragment(chatDialogsFragment);

    }

    private void switchFragment(Fragment selectedFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, selectedFragment);
        fragmentTransaction.commit();

    }
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // LoginActivity is a New Task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // The old task when coming back to this activity should be cleared so we cannot come back to it.
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (!isSignedIn()) {
//            redirectToLogin();
//        }
    }

    private boolean isSignedIn() {
        return QBSessionManager.getInstance().getSessionParameters()!=null;
    }

}
