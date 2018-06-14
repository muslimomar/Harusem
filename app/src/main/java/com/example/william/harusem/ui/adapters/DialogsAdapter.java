package com.example.william.harusem.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.ui.activities.AttachmentImageActivity;
import com.example.william.harusem.util.ResourceUtils;
import com.example.william.harusem.util.UiUtils;
import com.example.william.harusem.util.baseAdapters.BaseSelectableListAdapter;
import com.example.william.harusem.util.qb.QbDialogUtils;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.william.harusem.ui.activities.MessageActivity.TAG;

public class DialogsAdapter extends BaseSelectableListAdapter<QBChatDialog> {

    private static final String EMPTY_STRING = "";
    private static final int DIALOG_IMAGE = 2;
    private String dialogImage = "";

    public DialogsAdapter(Context context, List<QBChatDialog> dialogs) {
        super(context, dialogs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_chat_dialog, parent, false);

            holder = new ViewHolder();
            holder.rootLayout = (ViewGroup) convertView.findViewById(R.id.root);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.dialog_name);
            holder.lastMessageTextView = (TextView) convertView.findViewById(R.id.last_message_tv);
            holder.dialogImageView = (ImageView) convertView.findViewById(R.id.dialog_circle_iv);
            holder.unreadCounterTextView = (TextView) convertView.findViewById(R.id.text_dialog_unread_count);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final QBChatDialog dialog = getItem(position);

        if (dialog.getPhoto() != null && !dialog.getPhoto().equalsIgnoreCase("null")) {

            QBContent.getFile(Integer.parseInt(dialog.getPhoto())).performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(QBFile qbFile, Bundle bundle) {
                    String fileUrl = qbFile.getPublicUrl();
                    Picasso.get()
                            .load(fileUrl)
                            .resize(50, 50)
                            .centerCrop()
                            .into(holder.dialogImageView);
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e(TAG, "onError: ", e);
                }
            });


        } else {
            if (dialog.getType().equals(QBDialogType.PRIVATE)) {
                QBUser recipient = QBUsersHolder.getInstance().getUserById(dialog.getRecipientId());

                if (recipient!=null && recipient.getFileId() != null) {
                    Integer fileId = recipient.getFileId();
                    getRecipientPhoto(fileId, holder.dialogImageView);
                } else {
                    holder.dialogImageView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.placeholder_user));
                    holder.dialogImageView.setImageDrawable(null);
                }

            } else {
                holder.dialogImageView.setBackgroundDrawable(UiUtils.getGreyCircleDrawable());
                holder.dialogImageView.setImageResource(R.drawable.ic_group_black_24dp);
            }

        }


        holder.nameTextView.setText(QbDialogUtils.getDialogName(dialog));
        prepareTextLastMessage(dialog, holder.lastMessageTextView);

        int unreadMessagesCount = getUnreadMsgCount(dialog);
        if (unreadMessagesCount == 0) {
            holder.unreadCounterTextView.setVisibility(View.GONE);
        } else {
            holder.unreadCounterTextView.setVisibility(View.VISIBLE);
            holder.unreadCounterTextView.setText(String.valueOf(unreadMessagesCount > 99 ? "99+" : unreadMessagesCount));
        }

        holder.rootLayout.setBackgroundColor(isItemSelected(position) ? ResourceUtils.getColor(R.color.selected_list_item_color) :
                ResourceUtils.getColor(android.R.color.transparent));

        holder.dialogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageUrl = "";
                String photo = dialog.getPhoto();
                if (photo != null && !photo.equalsIgnoreCase("null")) {
                    imageUrl = photo;
                } else {
                    QBUser recipient = QBUsersHolder.getInstance().getUserById(dialog.getRecipientId());
                    if (recipient != null && recipient.getFileId() != null) {
                        imageUrl = recipient.getFileId().toString();
                    }
                }

                AttachmentImageActivity.start(context, imageUrl, DIALOG_IMAGE, String.valueOf(dialog.getType()));
            }
        });

        return convertView;
    }

    private void getRecipientPhoto(final Integer fileId, final ImageView dialogImageView) {
        QBContent.getFile(fileId).performAsync(new QBEntityCallback<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {

                String fileUrl = qbFile.getPublicUrl();
                Picasso.get().load(fileUrl)
                        .resize(50, 50)
                        .centerCrop()
                        .into(dialogImageView);
                dialogImage = fileUrl;
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
            }
        });

    }

    private int getUnreadMsgCount(QBChatDialog chatDialog) {
        Integer unreadMessageCount = chatDialog.getUnreadMessageCount();
        if (unreadMessageCount == null) {
            return 0;
        } else {
            return unreadMessageCount;
        }
    }

    private boolean isLastMessageAttachment(QBChatDialog dialog) {
        String lastMessage = dialog.getLastMessage();
        Integer lastMessageSenderId = dialog.getLastMessageUserId();
        return TextUtils.isEmpty(lastMessage) && lastMessageSenderId != null;
    }

    private void prepareTextLastMessage(QBChatDialog chatDialog, TextView lastMessageTextView) {
        if (isLastMessageAttachment(chatDialog)) {
            lastMessageTextView.setText(context.getString(R.string.chat_attachment));
            lastMessageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_photo_camera_black_24dp, 0, 0, 0);
            lastMessageTextView.setCompoundDrawablePadding(context.getResources().getDimensionPixelOffset(R.dimen.photo_icon_padding));
        } else {
            lastMessageTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            lastMessageTextView.setText(chatDialog.getLastMessage());
        }
    }

    private static class ViewHolder {
        ViewGroup rootLayout;
        ImageView dialogImageView;
        TextView nameTextView;
        TextView lastMessageTextView;
        TextView unreadCounterTextView;
    }
}
