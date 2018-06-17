package com.example.william.harusem.ui.activities;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.william.harusem.R;
import com.example.william.harusem.ui.adapters.FriendsAdapter;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FriendsActivity extends AppCompatActivity {

    private static final String TAG = FriendsActivity.class.getSimpleName();
    RecyclerView.LayoutManager mLayoutManager;
    @BindView(R.id.friends_list)
    RecyclerView friendsRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    FriendsAdapter adapter;
    QBFriendListHelper qbFriendListHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ButterKnife.bind(this);
        setTitle("Friends");
        configRecyclerView();

        qbFriendListHelper = new QBFriendListHelper(this);

        retrieveFriendsFromRoster();

        swipeLayoutRefresh();
    }


    public void retrieveFriendsFromRoster() {
        progressBar.setVisibility(View.VISIBLE);
        Collection<Integer> allFriends = qbFriendListHelper.getAllFriends();

        QBUsers.getUsersByIDs(allFriends, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                progressBar.setVisibility(View.GONE);
                adapter = new FriendsAdapter(qbUsers, FriendsActivity.this);
                friendsRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                if(mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "onError: ",e );
            }
        });

    }

    private void configRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.setLayoutManager(mLayoutManager);
    }


    private void swipeLayoutRefresh() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveFriendsFromRoster();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });

    }


}