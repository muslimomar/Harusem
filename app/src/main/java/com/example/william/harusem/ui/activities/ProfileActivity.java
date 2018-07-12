package com.example.william.harusem.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.william.harusem.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivacyListsManager;
import com.quickblox.chat.listeners.QBPrivacyListListener;
import com.quickblox.chat.model.QBPrivacyList;
import com.quickblox.chat.model.QBPrivacyListItem;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    @BindView(R.id.friend_name_friend_detail)
    TextView nameTV;

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

        privacyListsManager = QBChatService.getInstance().getPrivacyListsManager();

        Intent intent = getIntent();
        friendUserName = intent.getStringExtra("name");
        nameTV.setText(friendUserName);

        //UserId Setting for intent
        userID = String.valueOf(intent.getStringExtra("user_id"));

        Log.v("emine", "eee" + userID);

        initPrivacyListListener();

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

        Toast.makeText(this, friendUserName + "  You have unblocked your user", Toast.LENGTH_SHORT).show();

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

            Toast.makeText(this, friendUserName + "  you have blocked the user", Toast.LENGTH_SHORT).show();

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
                privacyListsManager.deletePrivacyList("public");
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
    //private void sendPushNotification() {
//        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
//        userIds.add(Integer.valueOf(loginUserId));
//        userIds.add(Integer.valueOf(userID));
//
//        QBEvent event = new QBEvent();
//        event.setUserIds(userIds);
//        event.setEnvironment(QBEnvironment.DEVELOPMENT);
//        event.setNotificationType(QBNotificationType.PUSH);
//        event.setPushType(QBPushType.GCM);
//        HashMap<String, String> data = new HashMap<String, String>();
//        data.put("data.message", "Hello");
//        data.put("data.type", "welcome message");
//        event.setMessage(String.valueOf(data));
//
//        QBPushNotifications.createEvent(event).performAsync(new QBEntityCallback<QBEvent>() {
//            @Override
//            public void onSuccess(QBEvent qbEvent, Bundle bundle) {
//                Toast.makeText(ProfileActivity.this, "You can not send message", Toast.LENGTH_SHORT);
//                //sent
//            }
//
//            @Override
//            public void onError(QBResponseException e) {
//
//            }
//        });
//    }
//

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
