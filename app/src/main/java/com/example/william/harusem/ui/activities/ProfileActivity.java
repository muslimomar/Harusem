package com.example.william.harusem.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.william.harusem.R;
import com.example.william.harusem.models.UserData;
import com.google.gson.Gson;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivacyListsManager;
import com.quickblox.chat.listeners.QBPrivacyListListener;
import com.quickblox.chat.model.QBPrivacyList;
import com.quickblox.chat.model.QBPrivacyListItem;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.friend_name_friend_detail)
    TextView nameTV;
    @BindView(R.id.country_tv)
    TextView countryTv;
    @BindView(R.id.friend_detail_image_view)
    CircleImageView userImageView;
    @BindView(R.id.flag)
    ImageView flagIv;
    @BindView(R.id.fragment_activity_tab_layout)
    LinearLayout linearLayout;
    @BindView(R.id.card_view_friend_country)
    CardView cardView;
    @BindView(R.id.profile_activity_loading_pb)
    ProgressBar progressBar;


    private static final String TAG = ProfileActivity.class.getSimpleName();
    QBPrivacyListsManager privacyListsManager;
    QBPrivacyListListener privacyListListener;

    String userID;
    String friendUserName;
    @BindView(R.id.mother_language_tv)
    TextView motherLanguageTv;
    @BindView(R.id.learning_language_tv)
    TextView learningLanguageTv;
    @BindView(R.id.mother_language_pb)
    RoundCornerProgressBar motherLanguagePb;
    @BindView(R.id.learning_language_pb)
    RoundCornerProgressBar learningLanguagePb;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        showProgressBar(progressBar);
        hideLayout();

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setElevation(0);
        }

        privacyListsManager = QBChatService.getInstance().getPrivacyListsManager();

        Intent intent = getIntent();
        friendUserName = intent.getStringExtra("name");
        nameTV.setText(friendUserName);

        //UserId Setting for intent
        userID = String.valueOf(intent.getStringExtra("user_id"));
        Log.v("UserID", "User id is : " + userID);

        initPrivacyListListener();

        getUserImageView(userImageView);

    }

    private void checkBlockingStatus() {

        MenuItem menuItem = menu.findItem(R.id.friend_blocking_icon);
        if (isUserBlocked(userID)) {
            menuItem.setTitle("UnBlock");
        } else {
            menuItem.setTitle("Block");
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (privacyListListener != null) {
            privacyListsManager.removePrivacyListListener(privacyListListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (privacyListListener != null) {
            privacyListsManager.addPrivacyListListener(privacyListListener);
        }
    }

    private void initPrivacyListListener() {
        privacyListListener = new QBPrivacyListListener() {
            @Override
            public void setPrivacyList(String s, List<QBPrivacyListItem> list) {
                Log.i(TAG, "userblocking setPrivacyList: ");

            }

            @Override
            public void updatedPrivacyList(String s) {
                Log.i(TAG, "userblocking updatedPrivacyList: ");
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.friend_blocking_menu, menu);
        this.menu = menu;

        checkBlockingStatus();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.friend_blocking_icon:

                if (isUserBlocked(userID)) {
                    unblockUser();
                    item.setTitle("UnBlock");
                } else {
                    blockUser();
                    item.setTitle("Block");
                }
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                finish();
        }
        return true;
    }

    private void unblockUser() {

        QBPrivacyList publicPrivacyList = getPublicPrivacyList();

        ArrayList<QBPrivacyListItem> items = new ArrayList<QBPrivacyListItem>();

        items.addAll(publicPrivacyList.getItems());

        for (QBPrivacyListItem item : publicPrivacyList.getItems()) {
            if (item.getValueForType().contains(userID)) {
                items.remove(item);
            }
        }

        publicPrivacyList.setItems(items);

        updatePrivacyList(publicPrivacyList, items.size());

        checkBlockingStatus();

        Toast.makeText(this, friendUserName + getString(R.string.you_unblocked_user), Toast.LENGTH_SHORT).show();

    }

    private void blockUser() {

        QBPrivacyList publicPrivacyList = getPublicPrivacyList();

        if (publicPrivacyList == null) {
            QBPrivacyList list = new QBPrivacyList();
            list.setName("public");

            ArrayList<QBPrivacyListItem> items = new ArrayList<QBPrivacyListItem>();

            QBPrivacyListItem item1 = new QBPrivacyListItem();

            item1.setAllow(false);
            item1.setType(QBPrivacyListItem.Type.USER_ID);
            item1.setValueForType(userID);
            item1.setMutualBlock(true);

            items.add(item1);

            list.setItems(items);
            list.setDefaultList(true);

            createPublicPrivacyList(list);

            Toast.makeText(this, friendUserName + getString(R.string.you_blocked_user), Toast.LENGTH_SHORT).show();

        } else {
            ArrayList<QBPrivacyListItem> items = new ArrayList<QBPrivacyListItem>();

            QBPrivacyListItem item1 = new QBPrivacyListItem();

            item1.setAllow(false);
            item1.setType(QBPrivacyListItem.Type.USER_ID);
            item1.setValueForType(userID);
            item1.setMutualBlock(true);

            items.addAll(publicPrivacyList.getItems());
            items.add(item1);

            publicPrivacyList.setItems(items);

            updatePrivacyList(publicPrivacyList, items.size());

        }

        checkBlockingStatus();
    }

    private void updatePrivacyList(QBPrivacyList publicPrivacyList, int itemsSize) {

        try {
            privacyListsManager.declinePrivacyList();
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "blockUser: ", e);
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            Log.e(TAG, "blockUser: ", e);
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            Log.e(TAG, "blockUser: ", e);
            e.printStackTrace();
        }

        if (itemsSize > 0) {

            try {
                privacyListsManager.createPrivacyList(publicPrivacyList);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            }

            try {
                privacyListsManager.applyPrivacyList(publicPrivacyList);
            } catch (SmackException.NotConnectedException e) {
                Log.e(TAG, "blockUser: ", e);
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                Log.e(TAG, "blockUser: ", e);
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                Log.e(TAG, "blockUser: ", e);
                e.printStackTrace();
            }
        } else {
            try {
                privacyListsManager.deletePrivacyList("public ");
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            }

        }

    }

    private void createPublicPrivacyList(QBPrivacyList list) {
        try {
            privacyListsManager.createPrivacyList(list);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
    }

    private QBPrivacyList getPublicPrivacyList() {

        QBPrivacyList publicList = null;

        try {
            publicList = privacyListsManager.getPrivacyList("public");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }

        return publicList;
    }


    boolean isUserBlocked(String userID) {

        QBPrivacyList list = null;
        try {
            list = privacyListsManager.getPrivacyList("public");

            for (QBPrivacyListItem item : list.getItems()) {
                if (item.getValueForType().contains(userID)) {
                    return true;
                }
            }
        } catch (SmackException.NotConnectedException | SmackException.NoResponseException | XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
        return false;

    }

    public void getUserImageView(CircleImageView userThumbIv) {

        QBUsers.getUser(Integer.parseInt(userID)).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle bundle) {
                getUserCustomData(user);
                hideProgressBar(progressBar);
                showLayout();

                if (user.getFileId() != null) {
                    int profilePicId = user.getFileId();

                    QBContent.getFile(profilePicId).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            Picasso.get()
                                    .load(qbFile.getPublicUrl())
                                    .resize(50, 50)
                                    .centerCrop()
                                    .into(userThumbIv);

                            // hide the progess bar and show the layout

                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Log.e("Profile Activity", "onError: ", e);
                            userThumbIv.setImageResource(R.drawable.placeholder_user);
                        }
                    });
                } else {
                    userThumbIv.setImageResource(R.drawable.placeholder_user);
                }
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }


    private void showProgressBar(ProgressBar progressBar) {
        if (ProfileActivity.this != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar(ProgressBar progressBar) {
        if (ProfileActivity.this != null) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
        }

    }

    public void hideLayout() {
        linearLayout.setVisibility(View.GONE);
        cardView.setVisibility(View.GONE);
    }

    public void showLayout() {
        linearLayout.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);

    }


    @SuppressLint("ClickableViewAccessibility")
    public void getUserCustomData(QBUser user) {
        String customData = user.getCustomData();

        if (customData != null) {

            UserData retrievedData = new Gson().fromJson(customData, UserData.class);

            // Mother Language
            String motherLanguage = retrievedData.getMotherLanguage();
            motherLanguageTv.setText(motherLanguage);
            motherLanguagePb.setProgressColor(ContextCompat.getColor(this, R.color.pb_color));
            motherLanguagePb.setSecondaryProgressColor(ContextCompat.getColor(this, R.color.pb_sec_color));
            motherLanguagePb.setSecondaryProgress(97);
            motherLanguagePb.setProgressBackgroundColor(ContextCompat.getColor(this, R.color.pb_bg_color));
            motherLanguagePb.setRadius(10);
            motherLanguagePb.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Toast.makeText(getApplicationContext(), motherLanguage + " is my mother language!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            motherLanguagePb.setMax(100);
            motherLanguagePb.setProgress(85);
            motherLanguagePb.setReverse(false);
            motherLanguagePb.setPadding(5);

            //Learning Languague
            String learningLanguage = retrievedData.getLearningLanguage();
            String languageLevel = retrievedData.getSelectedLanguageLevel();
            learningLanguageTv.setText(learningLanguage);
            learningLanguagePb.setMax(100);
            learningLanguagePb.setReverse(false);
            learningLanguagePb.setRadius(10);
            learningLanguagePb.setPadding(5);
            learningLanguagePb.setSecondaryProgressColor(ContextCompat.getColor(this, R.color.pb_sec_color));
            learningLanguagePb.setProgressColor(ContextCompat.getColor(this, R.color.pb_color));
            learningLanguagePb.setProgressBackgroundColor(ContextCompat.getColor(this, R.color.pb_bg_color));
            switch (languageLevel) {
                case "Beginner":
                    learningLanguagePb.setProgress(30);
                    learningLanguagePb.setSecondaryProgress(45);
                    learningLanguagePb.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Toast.makeText(getApplicationContext(), "I'm still Beginner!", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });
                    break;
                case "Intermediate":
                    learningLanguagePb.setProgress(50);
                    learningLanguagePb.setSecondaryProgress(65);
                    learningLanguagePb.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Toast.makeText(getApplicationContext(), "I'm Intermediate", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });

                    break;
                case "Advanced":
                    learningLanguagePb.setProgress(75);
                    learningLanguagePb.setSecondaryProgress(90);
                    learningLanguagePb.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Toast.makeText(getApplicationContext(), "My level is Advanced!", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });

                    break;
            }

            String country = retrievedData.getUserCountry();

            CountryPicker countryPicker =
                    new CountryPicker.Builder().with(this).build();

            List<Country> allCountries = countryPicker.getAllCountries();
            int id = 0;
            for (Country c : allCountries) {
                if (c.getName().equalsIgnoreCase(country))
                    id = c.getFlag();
            }
            flagIv.setImageResource(id);
        }
    }
}