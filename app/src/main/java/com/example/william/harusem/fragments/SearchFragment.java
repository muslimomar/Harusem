package com.example.william.harusem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.william.harusem.R;
import com.example.william.harusem.ui.activities.FriendsActivity;
import com.example.william.harusem.ui.activities.UsersActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class SearchFragment extends Fragment {

    @BindView(R.id.all_users_btn)
    Button allUsersBtn;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.all_users_btn)
    public void setAllUsersBtn(View view) {
        Intent intent = new Intent(getActivity(), UsersActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.friends_btn)
    public void setFriendsBtn(View view) {
        Intent intent = new Intent(getActivity(), FriendsActivity.class);
        startActivity(intent);
    }


}
