package com.example.william.harusem.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.william.harusem.R;
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
    @BindView(R.id.english_progress_bar)
    ProgressBar englishProgressBar;
    @BindView(R.id.turkish_progress_bar)
    ProgressBar turkishProgressBar;
//    @BindView(R.id.arabic_progress_bar)
//    ProgressBar arabicProgressBar;

    private static final String TAG = ProfileActivity.class.getSimpleName();
    QBPrivacyListsManager privacyListsManager;
    QBPrivacyListListener privacyListListener;

    String userID;
    String friendUserName;
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

        Log.v("emine", "eee" + userID);

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

                            getUserCountry(user);

                            // hide the progess bar and show the layout
                            hideProgressBar(progressBar);
                            showLayout();
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

    private void getUserCountry(QBUser user) {
        String country = user.getCustomData();

        countryTv.setText(country);

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

    //kullanıcının ingizlice dil seviyesini getiren ve progress bar'a yükleyen metoddur.
    private void getUserEnglishLanguage(QBUser user) {

        String englishLevel = user.getCustomData();

        //signup sayfasında girilen dil seviyelerini nasıl çağıracağımı tam olarak bilmiyorum.
        if (englishLevel.equals(0)) {
            englishProgressBar.setProgress(25);
        } else if (englishLevel.equals(1)) {
            englishProgressBar.setProgress(50);

        } else if (englishLevel.equals(2)) {
            englishProgressBar.setProgress(75);

        }
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

//    private static class blockTask extends AsyncTask<Void, Void, Void> {
//        ProgressDialog progressDialog;
//        int actionType;
//        private WeakReference<ProfileActivity> profileActivity;
//
//        public blockTask(int actionType, ProfileActivity profileActivity) {
//            this.actionType = actionType;
//            this.profileActivity = new WeakReference<>(profileActivity);
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            if(profileActivity.get() !=null) {
//                progressDialog = Utils.buildProgressDialog(profileActivity.get(), "", "Please Wait...", false);
//                    progressDialog.show();
//            }
//
//        }
//
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            if (profileActivity.get() != null) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                    progressDialog = null;
//                }
//            }
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            if (actionType == BLOCK_USER) {
//                profileActivity.get().blockUser();
//
//            } else if (actionType == UNBLOCK_USER) {
//                profileActivity.get().unblockUser();
//            }
//
//            return null;
//        }
//    }

}
