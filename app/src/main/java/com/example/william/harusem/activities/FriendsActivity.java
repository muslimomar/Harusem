package com.example.william.harusem.activities;


import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import com.example.william.harusem.R;
import com.example.william.harusem.adapters.FriendsAdapter;
import com.example.william.harusem.models.FriendsClass;
import java.util.ArrayList;


public class FriendsActivity extends AppCompatActivity  {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<FriendsClass> friendsList = new ArrayList<>();
    private FriendsAdapter friendsAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        configViews();

        friendsList.add(new FriendsClass(R.drawable.ic_friends_profile, "john vatic","active", R.drawable.ic_log_out_profile));
        friendsList.add(new FriendsClass(R.drawable.ic_friends_profile, "shahd ghrsi","active", R.drawable.ic_log_out_profile));
        friendsList.add(new FriendsClass(R.drawable.ic_friends_profile, "reem ghrsi","active", R.drawable.ic_log_out_profile));
        friendsList.add(new FriendsClass(R.drawable.ic_friends_profile, "sidra muntaha","active", R.drawable.ic_log_out_profile));
        friendsList.add(new FriendsClass(R.drawable.ic_friends_profile, "john","active", R.drawable.ic_log_out_profile));
        friendsList.add(new FriendsClass(R.drawable.ic_friends_profile, "john","active", R.drawable.ic_log_out_profile));

    }

    private void configViews() {
        mRecyclerView = (RecyclerView) this.findViewById(R.id.friends_list);

        //friendsAdapter= new FriendsAdapter (FriendsActivity.this, 0, friendsList);
        //listview.setAdapter(adbPerson)
        mSwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe);


        friendsAdapter = new FriendsAdapter(friendsList, this);
        mRecyclerView.setAdapter(friendsAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               // mController.startFetching(photoPosition);
            }
        });


    }


}
