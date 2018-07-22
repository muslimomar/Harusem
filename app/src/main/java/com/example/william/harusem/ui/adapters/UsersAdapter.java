package com.example.william.harusem.ui.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
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
import com.example.william.harusem.fcm.NotificationHelper;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.ui.activities.ChatActivity;
import com.example.william.harusem.ui.activities.ProfileActivity;
import com.example.william.harusem.util.Utils;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.SmackException;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.william.harusem.ui.activities.ChatActivity.EXTRA_DIALOG;
import static com.example.william.harusem.ui.activities.MainActivity.REQUEST_DIALOG;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    private static final String TAG = UsersAdapter.class.getSimpleName();
    private List<QBUser> usersList;
    private Context context;
    private QBFriendListHelper friendListHelper;
    private @LayoutRes
    int resource;


    public UsersAdapter(List<QBUser> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;

    }

    public UsersAdapter(Context context, @LayoutRes int resource, List<QBUser> usersList) {
        this.resource = resource;
        this.context = context;
        this.usersList = usersList;


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
                    createPrivateChatDialog(user);

                } else if (!friendListHelper.isFriendRequestAlreadySent(user.getId()) && !friendListHelper.isFriend(user.getId())) {
                    sendRequest(user, view, position, holder.button);
                    sendNotification(user, view, position, holder.button);
                }

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToProfileActivity(user);
            }
        });
    }

    private void sendNotification(final QBUser user, final View view, final int position, Button button) {
        QBUser currentUser = QBUsersHolder.getInstance().getUserById(QBChatService.getInstance().getUser().getId());
        String currentt = currentUser.getFullName();
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        userIds.add(user.getId());
        QBEvent messageEvent = NotificationHelper.friendRequestPushEvent(userIds, currentt);

        QBPushNotifications.createEvent(messageEvent).performAsync(new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle bundle) {
                Log.i(TAG, "QBPushNotifications onSuccess: Friend Request Sent!" + qbEvent + "\n bundle" + bundle);
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "QBPushNotifications error!: Friend Request Sent", e);
            }
        });
    }

    private void setButtonName(Button button, QBUser user) {
        if (friendListHelper.isFriend(user.getId())) {
            setMessageBtn(button);

        } else if (friendListHelper.isFriendRequestAlreadySent(user.getId())) {
            setRequestSentBtn(button);

        } else if (!friendListHelper.isFriendRequestAlreadySent(user.getId()) &&
                !friendListHelper.isFriend(user.getId())) {

            setAddFriendBtn(button);
        }
    }

    private void setAddFriendBtn(Button button) {
        button.setText(R.string.add_friend);
        button.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        button.setBackgroundResource(R.drawable.border_radius);
    }

    private void setMessageBtn(Button button) {
        button.setText(R.string.msg_user);
        button.setTextColor(Color.WHITE);
        button.setBackgroundResource(R.drawable.primary_button_radius);
    }

    private void setRequestSentBtn(Button button) {
        button.setText(R.string.request_sent);
        button.setTextColor(Color.WHITE);
        button.setBackgroundResource(R.drawable.red_button_radius);
        button.setEnabled(false);
    }

    private void deleteFriend(final View view, final int position, QBUser user, final Button addFriendBtn) {
        friendListHelper.removeFriend(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                setAddFriendBtn(addFriendBtn);
                showSnackBar(view, context.getString(R.string.friend_removed2));
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
                showSnackBar(view, context.getString(R.string.request_cancled));
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

                showSnackBar(view, context.getString(R.string.request_sent));
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

    private void redirectToProfileActivity(QBUser user) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("user_id", "" + user.getId());
        intent.putExtra("name", user.getFullName());
        context.startActivity(intent);
    }

    private void createPrivateChatDialog(final QBUser user) {
        final ProgressDialog progressDialog = Utils.buildProgressDialog(context, "", context.getString(R.string.loading_load), false);
        progressDialog.show();

        QBChatDialog chatDialog = DialogUtils.buildPrivateDialog(user.getId());

        QBRestChatService.createChatDialog(chatDialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                // dismiss dialog
                QBSystemMessagesManager systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setRecipientId(user.getId());
                qbChatMessage.setBody(qbChatDialog.getDialogId());

                try {
                    systemMessagesManager.sendSystemMessage(qbChatMessage);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(EXTRA_DIALOG, qbChatDialog);
                ((Activity) context).startActivityForResult(intent, REQUEST_DIALOG);
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                Utils.buildAlertDialog("Error", e.getMessage(), true, context);
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userDisplayName;
        public CircleImageView userThumbIv;
        public Button button;

        public MyViewHolder(View view) {
            super(view);
            userDisplayName = (TextView) view.findViewById(R.id.block_name_tv);
            userThumbIv = view.findViewById(R.id.image_user);
            button = view.findViewById(R.id.add_friend_btn);
        }

    }

}