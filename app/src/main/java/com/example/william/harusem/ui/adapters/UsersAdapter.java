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
                    Log.e(TAG, "onError: ", e);
                    holder.userThumbIv.setImageResource(R.drawable.placeholder_user);
                }
            });
        } else {
            holder.userThumbIv.setImageResource(R.drawable.placeholder_user);
        }

        setButtonName(holder.button, user);

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (friendListHelper.isFriend(user.getId())) {
                    deleteFriend(view, position, user, holder.button);

                } else if (!friendListHelper.isFriendRequestAlreadySent(user.getId()) && !friendListHelper.isFriend(user.getId())) {
                    sendRequest(user, view, position, holder.button);
                }

            }
        });
    }

    private void setButtonName(Button button, QBUser user) {
        if (friendListHelper.isFriendRequestAlreadySent(user.getId())) {
            setRequestSentBtn(button);

        } else if (friendListHelper.isFriend(user.getId())) {
            setUnfriendBtn(button);

        } else if (!friendListHelper.isFriendRequestAlreadySent(user.getId()) &&
                !friendListHelper.isFriend(user.getId())) {

            setAddFriendBtn(button);
        }
    }

    private void setAddFriendBtn(Button button) {
        button.setText("Add Friend");
        button.setBackgroundColor(Color.parseColor("#239ab6"));
    }

    private void setUnfriendBtn(Button button) {
        button.setText("Unfriend");
        button.setBackgroundColor(Color.parseColor("#d14f4f"));
    }

    private void setRequestSentBtn(Button button) {
        button.setText("Request Sent");
        button.setBackgroundColor(Color.parseColor("#d14f4f"));
        button.setEnabled(false);
    }

    private void deleteFriend(final View view, final int position, QBUser user, final Button addFriendBtn) {
        friendListHelper.removeFriend(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                setAddFriendBtn(addFriendBtn);
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

    private void sendRequest(final QBUser user, final View view, final int position, final Button addFriendBtn) {
        friendListHelper.sendFriendRequest(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                setRequestSentBtn(addFriendBtn);
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
        public Button button;

        public MyViewHolder(View view) {
            super(view);
            userDisplayName = (TextView) view.findViewById(R.id.block_name_tv);
            userThumbIv = view.findViewById(R.id.image_user);
            button = view.findViewById(R.id.unfriend_btn);
        }

    }


}