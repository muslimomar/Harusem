package com.example.william.harusem.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileFragment extends Fragment {

    @BindView(R.id.profile_circle_iv)
    CircularImageView profileCircleIv;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.friends_count_tv)
    TextView friendsCountTv;
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
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        profileCircleIv.setBorderWidth(2f);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
