package com.example.william.harusem.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.example.william.harusem.ui.activities.ProfileActivity;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {

    private List<QBUser> usersList;
    private Context context;
    private QBFriendListHelper friendListHelper;

    public FriendsAdapter(List<QBUser> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        friendListHelper = new QBFriendListHelper(context);

        final QBUser user = usersList.get(position);

        holder.userDisplayName.setText(user.getFullName());
        holder.unfriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                friendListHelper.removeFriend(user, new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        showSnackBar(view, context.getString(R.string.friend_removed));
                        usersList.remove(usersList.indexOf(user));
                        notifyItemRemoved(position);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        showSnackBar(view, e.getMessage());
                    }
                });

            }
        });


        if (user.getFileId() != null) {
            int profilePicId = user.getFileId();

            QBContent.getFile(profilePicId).performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(QBFile qbFile, Bundle bundle) {
                    Picasso.get()
                            .load(qbFile.getPublicUrl())
                            .resize(50, 50)
                            .centerCrop()
                            .into(holder.userThumbIv);
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("FriendsAdapter", "onError: ", e);
                    holder.userThumbIv.setImageResource(R.drawable.ic_user_new);
                }
            });
        } else {
            holder.userThumbIv.setImageResource(R.drawable.ic_user_new);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToProfileActivity(user);
            }
        });

    }

    private void redirectToProfileActivity(QBUser user) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("user_id", "" + user.getId());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public void showSnackBar(View v, String text) {
        Snackbar snackbar = Snackbar
                .make(v, text, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userDisplayName;
        public CircleImageView userThumbIv;
        public Button unfriendBtn;

        public MyViewHolder(View view) {
            super(view);
            userDisplayName = (TextView) view.findViewById(R.id.block_name_tv);
            userThumbIv = view.findViewById(R.id.image_user);
            unfriendBtn = view.findViewById(R.id.add_friend_btn);
        }
    }
}