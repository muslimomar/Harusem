package com.example.william.harusem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by william on 5/29/2018.
 */

public class UsersListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QBUser> qbUsers;

    public UsersListAdapter(Context context, ArrayList<QBUser> qbUsers) {
        this.context = context;
        this.qbUsers = qbUsers;
    }

    @Override
    public int getCount() {
        return qbUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return qbUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view1 = view;
        if (view1 == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view1 = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, null);

//            TextView txtTitle;
//            TextView statusTv;

            TextView textView = (TextView) view1.findViewById(android.R.id.text1);
            textView.setText(qbUsers.get(i).getLogin());

//            txtTitle = view1.findViewById(R.id.user_display_name_tv);
//            statusTv = view1.findViewById(R.id.status_tv);
//            statusTv.setVisibility(View.GONE);
//            txtTitle.setText(qbUsers.get(i).getFullName());


        }

        return view1;
    }
}
