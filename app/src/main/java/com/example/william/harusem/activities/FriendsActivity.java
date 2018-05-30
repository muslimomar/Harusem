package com.example.william.harusem.activities;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import com.example.william.harusem.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FriendsActivity extends AppCompatActivity {

    RecyclerView.LayoutManager mLayoutManager;
    @BindView(R.id.friends_list)
    RecyclerView friendsRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ButterKnife.bind(this);

        configRecyclerView();

    }


    private void configRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.setLayoutManager(mLayoutManager);
    }

}
