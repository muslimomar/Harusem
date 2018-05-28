package com.example.william.harusem.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.activities.FriendRequestsActivity;
import com.example.william.harusem.activities.FriendsActivity;
import com.example.william.harusem.activities.LoginActivity;
import com.example.william.harusem.util.Extras;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nex3z.notificationbadge.NotificationBadge;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.william.harusem.util.Extras.CONNECTION_STATUS;
import static com.example.william.harusem.util.Extras.USERS_REF;
import static com.example.william.harusem.util.Helper.OFFLINE;

public class ProfileFragment extends Fragment {


    Unbinder unbinder;
    @BindView(R.id.profile_circle_iv)
    CircularImageView profileCircleIv;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.friends_count_key_tv)
    TextView friendsCountKeyTv;
    @BindView(R.id.friends_count_value_tv)
    TextView friendsCountValueTv;
    @BindView(R.id.top_layout)
    RelativeLayout topLayout;
    @BindView(R.id.account_tv)
    TextView accountTv;
    @BindView(R.id.password_tv)
    TextView passwordTv;
    @BindView(R.id.friends_requests_tv)
    TextView friendsRequestsTv;
    @BindView(R.id.friends_tv)
    TextView friendsTv;
    @BindView(R.id.log_out_tv)
    TextView logOutTv;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.notification_badge)
    NotificationBadge notificationBadge;
    @BindView(R.id.log_out_pb)
    ProgressBar logOutPb;
    @BindView(R.id.log_out_layout)
    RelativeLayout logOutLayout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mUsersRefDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        configFireBase();
        profileCircleIv.setBorderWidth(2f);

        friendsRequestsTv.setOnClickListener(new View.OnClickListener() {
            int number = 0;

            @Override
            public void onClick(View view) {
                number++;

                notificationBadge.setNumber(number);
                Intent intent = new Intent(getActivity(), FriendRequestsActivity.class);

                getActivity().startActivity(intent);

            }
        });


        friendsTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), FriendsActivity.class);
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0, 0);

            }
        });

        return rootView;
    }

    private void configFireBase() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                hideProgressBar();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    redirectToLogin();
                }
            }
        };

        mAuth = FirebaseAuth.getInstance();
        mUsersRefDatabase = FirebaseDatabase.getInstance().getReference().child(USERS_REF);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // LoginActivity is a New Task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // The old task when coming back to this activity should be cleared so we cannot come back to it.
        startActivity(intent);
    }

    @OnClick(R.id.log_out_layout)
    public void setLogOutTv(View view) {

        showProgressBar();
        setUserOffline();
        mAuth.signOut();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void setUserOffline() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            mUsersRefDatabase.child(userId).child(CONNECTION_STATUS).setValue(OFFLINE);
        }
    }

    private void showProgressBar() {
        logOutPb.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        if (logOutPb.getVisibility() == View.VISIBLE) {
            logOutPb.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
