package com.example.william.harusem.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.models.Friend;
import com.example.william.harusem.models.User;
import com.example.william.harusem.util.Extras;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.william.harusem.util.Extras.FRIENDS_REF;
import static com.example.william.harusem.util.Extras.ONLINE;
import static com.example.william.harusem.util.Extras.USERS_REF;


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
