package com.example.william.harusem.ui.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.example.william.harusem.ui.activities.ChatActivity;
import com.example.william.harusem.helper.QBFriendListHelper;
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
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.SmackException;

import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {
    public static final String EXTRA_DIALOG_ID = "dialogId";

    private List<QBUser> usersList;
    private Context context;
    private QBFriendListHelper friendListHelper;
    private Collection<Integer> qbusers;

    public FriendsAdapter(List<QBUser> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }
    public FriendsAdapter(Collection<Integer> qbusers,Context context){
        this.qbusers = qbusers;
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
                        showSnackBar(view, "Friend Removed");
                        usersList.remove(usersList.indexOf(user));
                        notifyItemRemoved(position);
                    }

                    @Override
                    public void onError(QBResponseException e) {
    showSnackBar(view,e.getMessage());
                    }
                });

            }
        });

        holder.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPrivateChatDialog(user);

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
                    Log.e("FriendsAdapter", "onError: ",e );
                    holder.userThumbIv.setImageResource(R.drawable.placeholder_user);
                }
            });
        }else{
            holder.userThumbIv.setImageResource(R.drawable.placeholder_user);
        }


    }

    private void createPrivateChatDialog(final QBUser user) {
        final ProgressDialog progressDialog = Utils.buildProgressDialog(context, "", "Loading...", false);
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
                intent.putExtra(EXTRA_DIALOG_ID, qbChatDialog);
                context.startActivity(intent);
                ( (Activity) context).finish();
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                Utils.buildAlertDialog("Error", e.getMessage(),true, context);
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
        public Button unfriendBtn;
        public Button messageBtn;

        public MyViewHolder(View view) {
            super(view);
            userDisplayName = (TextView) view.findViewById(R.id.user_display_name_tv);
            userThumbIv = view.findViewById(R.id.image_user);
            unfriendBtn = view.findViewById(R.id.unfriend_btn);
            messageBtn = view.findViewById(R.id.message_btn);
        }
    }
}