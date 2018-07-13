package com.example.william.harusem.ui.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.william.harusem.R;
import com.example.william.harusem.fragments.DialogsFragment;
import com.example.william.harusem.fragments.ProfileFragment;
import com.example.william.harusem.fragments.SearchFragment;
import com.quickblox.chat.model.QBChatDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    AHBottomNavigation bottomNavigation;
    BroadcastReceiver pushBroadcastReceiver;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupBottomNavigation();
        switchFromNotification();
    }

    private void setupBottomNavigation() {

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
                        selectedFragment = new DialogsFragment();
                        break;
                    case 1:
                        selectedFragment = new SearchFragment();
                        break;
                    case 2:
                        selectedFragment = new ProfileFragment();
                        break;
                    default:
                        selectedFragment = new DialogsFragment();
                        break;
                }

                switchFragment(selectedFragment);
                return true;
            }
        });

        // Default tab
        DialogsFragment chatDialogsFragment = new DialogsFragment();
        switchFragment(chatDialogsFragment);
    }

    private void switchFragment(Fragment selectedFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, selectedFragment);
        fragmentTransaction.commit();
    }

    public void switchFromNotification() {

        String fragmentDialog = getIntent().getStringExtra("fragmentName");

        if (fragmentDialog != null) {
            switch (fragmentDialog) {
                case "chatsDialog":
                    //FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    //DialogsFragment dialogsFragment = new DialogsFragment();
                    //fragmentTransaction.replace(R.id.frame_container, dialogsFragment).commit();
                case "dialog":
                    //Intent intent = new Intent(this, ChatActivity.class);
                    //intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);
                    //startActivityForResult(intent, code);
                    break;
            }
        }


    }

    public void startForResult(Activity activity, int code, QBChatDialog dialogId) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);
        startActivityForResult(intent, code);
    }
}
