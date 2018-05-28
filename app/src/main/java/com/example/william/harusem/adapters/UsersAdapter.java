package com.example.william.harusem.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.activities.ChatActivity;
import com.example.william.harusem.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<User> mUsers;
    private Context context;
    private String mCurrentUserEmail;
    private String mCurrentUserId;
    private long mCurrentUserCreationDate;


    public UsersAdapter(Context context, List<User> mUsers) {
        this.mUsers = mUsers;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.friends_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        User fireBaseUser = mUsers.get(position);

        holder.userDisplayNameTv.setText(fireBaseUser.getName());

    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void addUser(User user) {
        mUsers.add(user);
        notifyDataSetChanged();
    }

    public void changeUser(int index, User user) {
        mUsers.set(index, user);
        notifyDataSetChanged();
    }

    public void clear() {
        mUsers.clear();
    }

    public void setCurrentUserInfo(String userUid, String email, long createdAt) {
        mCurrentUserId = userUid;
        mCurrentUserEmail = email;
        mCurrentUserCreationDate = createdAt;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userDisplayNameTv;

        ViewHolder(View itemView) {
            super(itemView);
            userDisplayNameTv = itemView.findViewById(R.id.user_display_name_tv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            try {
                User user = mUsers.get(getLayoutPosition());

                String chatRef = user.createUniqueChatRef(mCurrentUserCreationDate, mCurrentUserEmail);

                Intent chattingIntent = new Intent(context, ChatActivity.class);
                chattingIntent.putExtra("CURRENT_USER_ID", mCurrentUserId);
                chattingIntent.putExtra("RECIPIENT_ID", user.getId());
                chattingIntent.putExtra("GENERATED_CHAT_REF", chatRef);
                chattingIntent.putExtra("RECIPIENT_NAME", user.getName());

                context.startActivity(chattingIntent);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}