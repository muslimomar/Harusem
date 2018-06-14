package com.example.william.harusem.ui.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.william.harusem.R;
import com.example.william.harusem.ui.adapters.FriendRequestsAdapter;
import com.example.william.harusem.holder.QBFriendRequestsHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendRequestsActivity extends AppCompatActivity {

    private static final String TAG = FriendRequestsActivity.class.getSimpleName();
    @BindView(R.id.request_friend_list_view)
    RecyclerView recyclerView;
    FriendRequestsAdapter adapter;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        ButterKnife.bind(this);
        setTitle("Friend Requests");
        ActionBar actionBar = getSupportActionBar();  //Geri image için bu satır kullanıldı
        actionBar.setDisplayHomeAsUpEnabled(true);
        configRecyclerView();

        adapter = new FriendRequestsAdapter(QBFriendRequestsHolder.getInstance().getAllFriendRequests(), this);
        recyclerView.setAdapter(adapter);

        swipeLayoutRefresh();

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


    private void configRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void swipeLayoutRefresh() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = new FriendRequestsAdapter(QBFriendRequestsHolder.getInstance().getAllFriendRequests(), FriendRequestsActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });


    }

}
