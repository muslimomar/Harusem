package com.example.william.harusem.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.quickblox.users.model.QBUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
 
    private List<QBUser> usersList;
 
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userDisplayName;
        public CircleImageView userThumbIv;

        public MyViewHolder(View view) {
            super(view);
            userDisplayName = (TextView) view.findViewById(R.id.user_display_name_tv);
            userThumbIv = view.findViewById(R.id.user_thumb_circle);
        }
    }
 
 
    public UsersAdapter(List<QBUser> usersList) {
        this.usersList = usersList;
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_list_item, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        QBUser user = usersList.get(position);

        holder.userDisplayName.setText(user.getFullName());

    }
 
    @Override
    public int getItemCount() {
        return usersList.size();
    }
}