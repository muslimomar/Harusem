package com.example.william.harusem.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.william.harusem.R;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    private static final String TAG = UsersAdapter.class.getSimpleName();
    private List<QBUser> usersList;
    private Context context;
    private QBFriendListHelper friendListHelper;

    public UsersAdapter(List<QBUser> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        friendListHelper = new QBFriendListHelper(context);
        final QBUser user = usersList.get(position);

        holder.userDisplayName.setText(user.getFullName());

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
                    Log.e(TAG, "onError: ",e );
                    holder.userThumbIv.setImageResource(R.drawable.placeholder_user);
                }
            });
        }else {
            holder.userThumbIv.setImageResource(R.drawable.placeholder_user);
        }

        if (friendListHelper.isFriendRequestAlreadySent(user.getId())) {
            holder.addFriendBtn.setText("Request Sent");
            holder.addFriendBtn.setBackgroundColor(Color.parseColor("#d14f4f"));
            holder.addFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    cancelRequest(view, user, position);
                    Log.d(TAG, "onClick: " + "Cancel Request");
                    // if that didn't work try to set the button text after QBCallBack
                }
            });
        } else if (friendListHelper.isFriend(user.getId())) {
            holder.addFriendBtn.setText("Unfriend");
            holder.addFriendBtn.setBackgroundColor(Color.parseColor("#d14f4f"));
            holder.addFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteFriend(view, position, user);
                    Log.d(TAG, "onClick: " + "Delete Friend");
                }
            });
        } else {
            holder.addFriendBtn.setText("Add Friend");
            holder.addFriendBtn.setBackgroundColor(Color.parseColor("#239ab6"));
            holder.addFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendRequest(user, view, position);
                    Log.d(TAG, "onClick: " + "Send Request");
                }
            });
        }
    }

    private void deleteFriend(final View view, final int position, QBUser user) {
        friendListHelper.removeFriend(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                notifyItemChanged(position);
                showSnackBar(view, "Friend Removed");
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cancelRequest(final View view, final QBUser user, final int position) {
        friendListHelper.cancelFriendRequest(user.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                notifyItemChanged(position);
                showSnackBar(view, "Request Cancelled");
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
            }
        });
    }

    private void sendRequest(final QBUser user, final View view, final int position) {
        friendListHelper.sendFriendRequest(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                notifyItemChanged(position);
                showSnackBar(view, "Request Sent");
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "sendFriendRequest onError: ", e);
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
        public TextView userDisplayName;
        public CircleImageView userThumbIv;
        public Button addFriendBtn;

        public MyViewHolder(View view) {
            super(view);
            userDisplayName = (TextView) view.findViewById(R.id.user_display_name_tv);
            userThumbIv = view.findViewById(R.id.user_iv);
            addFriendBtn = view.findViewById(R.id.unfriend_btn);
        }

    }


}