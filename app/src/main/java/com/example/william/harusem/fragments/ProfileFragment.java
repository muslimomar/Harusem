package com.example.william.harusem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.FriendRequestsActivity;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nex3z.notificationbadge.NotificationBadge;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);

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

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
