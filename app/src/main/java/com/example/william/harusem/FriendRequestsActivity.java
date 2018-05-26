package com.example.william.harusem;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.william.harusem.adapters.RequestFriendsAdapter;
import com.example.william.harusem.models.RequestFriends;

import java.util.ArrayList;

public class FriendRequestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        ArrayList arrayList = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();  //Geri image için bu satır kullanıldı
        actionBar.setDisplayHomeAsUpEnabled(true);

        RequestFriends one = new RequestFriends(R.drawable.ic_friends_profile, "eminesa", "seni takip etmek istiyor", R.drawable.ic_close);
        RequestFriends two = new RequestFriends(R.drawable.ic_friends_profile, "eminesa", "seni takip etmek istiyor", R.drawable.ic_close);

        arrayList.add(one);
        arrayList.add(two);

        ListView listView = findViewById(R.id.request_friend_list_view);

        RequestFriendsAdapter adapter = new RequestFriendsAdapter(this, arrayList);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  // geri butonu için action verildi.
             finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
