package com.example.william.harusem.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.william.harusem.R;
import com.example.william.harusem.adapters.UsersAdapter;
import com.example.william.harusem.models.User;
import com.example.william.harusem.util.Extras;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.william.harusem.util.Extras.USERS_REF;


public class FriendsActivity extends AppCompatActivity {

    @BindView(R.id.friends_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String mCurrentUserUid;
    private ArrayList<String> mUsersIdList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersRefDatabase;
    private ChildEventListener mChildEventListener;
    private UsersAdapter mUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ButterKnife.bind(this);

        showProgressBar();

        mAuth = FirebaseAuth.getInstance();
        mUsersRefDatabase = FirebaseDatabase.getInstance().getReference().child(USERS_REF);

        swipeLayoutRefresh();
        configRecyclerView();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        mCurrentUserUid = firebaseUser.getUid();
        queryAllUsers();


    }

    private void swipeLayoutRefresh() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: refresh
            }
        });
    }


    private void configRecyclerView() {

        mUsersAdapter = new UsersAdapter(this, new ArrayList<User>());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mUsersAdapter);
    }

    private void queryAllUsers() {
        hideProgressBar();

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.getKey();

                    if (dataSnapshot.getKey().equals(mCurrentUserUid)) {
                        User currentUser = dataSnapshot.getValue(User.class);
                        mUsersAdapter.setCurrentUserInfo(userId, currentUser.getEmail(), currentUser.getCreationDate());
                    } else {
                        User recipient = dataSnapshot.getValue(User.class);
                        recipient.setId(userId);
                        mUsersIdList.add(userId);
                        mUsersAdapter.addUser(recipient);
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {

                    String userId = dataSnapshot.getKey();

                    if (!userId.equals(mCurrentUserUid)) {

                        User user = dataSnapshot.getValue(User.class);

                        int index = mUsersIdList.indexOf(userId);
                        if (index > -1) {
                            mUsersAdapter.changeUser(index, user);
                        }

                    }

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mUsersRefDatabase.limitToFirst(60).addChildEventListener(mChildEventListener);

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUsersAdapter.clear();
        mUsersIdList.clear();

        if (mChildEventListener != null) {
            mUsersRefDatabase.removeEventListener(mChildEventListener);
        }

    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
    }

}
