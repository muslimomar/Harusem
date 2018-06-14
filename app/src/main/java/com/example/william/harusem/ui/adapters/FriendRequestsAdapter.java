package com.example.william.harusem.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.example.william.harusem.holder.QBFriendRequestsHolder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.MyViewHolder> {

    private List<QBUser> usersList;
    private Context context;
    private QBFriendListHelper friendListHelper;

    public FriendRequestsAdapter(List<QBUser> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_friend_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final QBUser user = usersList.get(position);

        friendListHelper = new QBFriendListHelper(context);

        holder.userNameTv.setText(user.getFullName());
        holder.acceptIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                friendListHelper.acceptFriendRequest(user, new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        QBFriendRequestsHolder.getInstance().removeFriendRequest(user.getId());
                        notifyItemRemoved(position);
                        showSnackBar(view, "Request Accepted");
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("FriendRequestAdapter", "onError: ", e);
                    }
                });

            }
        });

        holder.declineIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                friendListHelper.declineFriendRequest(user, new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        QBFriendRequestsHolder.getInstance().removeFriendRequest(user.getId());
                        notifyItemRemoved(position);
                        showSnackBar(view, "Request Declined");
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });

            }
        });


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
        public TextView userNameTv;
        public ImageView acceptIv, declineIv;
        public CircleImageView userThumbIv;

        public MyViewHolder(View view) {
            super(view);
            userThumbIv = view.findViewById(R.id.user_iv);
            userNameTv = view.findViewById(R.id.user_name_tv);
//             textViewRequest = view.findViewById(R.id.request_friends_list_item_want_text_view);
            acceptIv = view.findViewById(R.id.accept_iv);
            declineIv = view.findViewById(R.id.decline_iv);

        }
    }
}