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
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBPrivacyList;
import com.quickblox.chat.model.QBPrivacyListItem;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    TextView nameTV;
    QBPrivacyListsManager privacyListsManager;
    QBPrivacyListListener privacyListListener;
    QBChatDialog qbChatDialog;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        nameTV = (TextView) findViewById(R.id.friend_name_friend_detail);
        Intent intent = getIntent();
        String friendUserName = intent.getStringExtra("name");
        userID = intent.getStringExtra("user_id");
        Toast.makeText(this, "bu bir ID: " + userID, Toast.LENGTH_SHORT).show();
        nameTV.setText(friendUserName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.friend_blocking_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.friend_blocking_icon:
                blockUser();
                break;
        }
        return true;
    }

    private void blockUser() {

        retrievePrivacyList(); //ilk önce gizlilik için listeyi sıralıyoruz

        QBPrivacyList list = new QBPrivacyList();
        list.setName("public");

        ArrayList<QBPrivacyListItem> items = new ArrayList<QBPrivacyListItem>();

        QBPrivacyListItem item1 = new QBPrivacyListItem();

//        Log.e(TAG, "issallow "+item1.isAllow() );
        item1.setAllow(false);
        item1.setType(QBPrivacyListItem.Type.USER_ID);
        item1.setValueForType(userID);
        item1.setMutualBlock(true);

        items.add(item1);

        list.setItems(items);
        Log.e("list1", "privacyCreate:l1 " + list);

        try {
            privacyListsManager.createPrivacyList(list);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }

// set listener
        QBPrivacyListListener privacyListListener = new QBPrivacyListListener() {
            @Override
            public void setPrivacyList(String userID, List<QBPrivacyListItem> listItem) {

            }

            @Override
            public void updatedPrivacyList(String listName) {

            }
        };

        try {
            privacyListsManager.applyPrivacyList("public");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
    }

    private void retrievePrivacyList() {

        privacyListsManager = QBChatService.getInstance().getPrivacyListsManager();
        privacyListsManager.addPrivacyListListener(privacyListListener);

// gizlilik listelerinin adlarını al
        List<QBPrivacyList> lists = null;
        try {
            lists = privacyListsManager.getPrivacyLists();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
    }

}
