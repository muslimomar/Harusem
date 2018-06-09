package com.example.william.harusem.adapters;

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
import com.example.william.harusem.activities.ChatActivity;
import com.example.william.harusem.common.Common;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.william.harusem.common.Common.DIALOG_EXTRA;

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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
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
                        notifyItemRemoved(position);
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }
        });

        holder.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QBChatDialog privateChatDialog = createPrivateChatDialog(user);

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(DIALOG_EXTRA, privateChatDialog);
                context.startActivity(intent);
            }
        });

    }

    private QBChatDialog createPrivateChatDialog(final QBUser user) {

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
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("FriendsAdapter", "onError: ",e );
            }
        });

        return chatDialog;
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
            userThumbIv = view.findViewById(R.id.user_iv);
            unfriendBtn = view.findViewById(R.id.unfriend_btn);
            messageBtn = view.findViewById(R.id.message_btn);
        }
    }
}