package com.example.william.harusem.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.util.TimeUtils;
import com.example.william.harusem.util.UiUtils;
import com.example.william.harusem.util.qb.PaginationHistoryListener;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.helper.CollectionsUtil;
import com.quickblox.users.model.QBUser;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.List;

/**
 * Created by william on 6/10/2018.
 */

public class ChatAdapter extends com.quickblox.ui.kit.chatmessage.adapter.QBMessagesAdapter<QBChatMessage> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private static final String TAG = ChatAdapter.class.getSimpleName();
    private final QBChatDialog chatDialog;
    private PaginationHistoryListener paginationListener;
    private int previousGetCount = 0;


    public ChatAdapter(Context context, QBChatDialog chatDialog, List<QBChatMessage> chatMessages) {
        super(context, chatMessages);
        this.chatDialog = chatDialog;
    }

    public void addToList(List<QBChatMessage> items) {
        chatMessages.addAll(0, items);
        notifyItemRangeInserted(0, items.size());
    }

    @Override
    public void add(QBChatMessage item) {
        this.chatMessages.add(item);
        this.notifyItemInserted(chatMessages.size() - 1);
    }

    @Override
    public void onBindViewHolder(QBMessageViewHolder holder, int position) {
        downloadMore(position);
        QBChatMessage chatMessage = getItem(position);
        if (isIncoming(chatMessage) && !isRead(chatMessage)) {
            readMessage(chatMessage);
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public String getImageUrl(int position) {
        QBAttachment attachment = getQBAttach(position);
        return attachment.getUrl();
    }


    @Override
    protected void onBindViewMsgLeftHolder(TextMessageHolder holder, QBChatMessage chatMessage, int position) {
        holder.timeTextMessageTextView.setVisibility(View.GONE);

        TextView opponentNameTv = holder.itemView.findViewById(R.id.opponent_name_text_view);

        if (chatDialog.getType() == QBDialogType.PRIVATE) {
            opponentNameTv.setVisibility(View.GONE);
        } else {
            opponentNameTv.setVisibility(View.VISIBLE);
            opponentNameTv.setTextColor(UiUtils.getRandomTextColorById(chatMessage.getSenderId()));
            opponentNameTv.setText(getSenderName(chatMessage));
        }

        TextView customMessageTimeTv = holder.itemView.findViewById(R.id.custom_msg_text_time_message);
        customMessageTimeTv.setText(getDate(chatMessage.getDateSent()));

        super.onBindViewMsgLeftHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewAttachLeftHolder(ImageAttachHolder holder, QBChatMessage chatMessage, int position) {
        TextView opponentNameTextView = holder.itemView.findViewById(R.id.opponent_name_attach_view);
        opponentNameTextView.setTextColor(UiUtils.getRandomTextColorById(chatMessage.getSenderId()));
        opponentNameTextView.setText(getSenderName(chatMessage));

        super.onBindViewAttachLeftHolder(holder, chatMessage, position);
    }

    private String getSenderName(QBChatMessage chatMessage) {
        QBUser sender = QBUsersHolder.getInstance().getUserById(chatMessage.getSenderId());
        return sender.getFullName();
    }


    private void readMessage(QBChatMessage chatMessage) {
        try {
            chatDialog.readMessage(chatMessage);
        } catch (XMPPException | SmackException.NotConnectedException e) {
            Log.w(TAG, e);
        }
    }


    private boolean isRead(QBChatMessage chatMessage) {
        Integer currentUserId = QBChatService.getInstance().getUser().getId();
        return !CollectionsUtil.isEmpty(chatMessage.getReadIds()) && chatMessage.getReadIds().contains(currentUserId);
    }

    public void setPaginationHistoryListener(PaginationHistoryListener paginationListener) {
        this.paginationListener = paginationListener;
    }

    private void downloadMore(int position) {
        if (position == 0) {
            if (getItemCount() != previousGetCount) {
                paginationListener.downloadMore();
                previousGetCount = getItemCount();
            }
        }
    }

    // sticker recycler holder
    @Override
    public long getHeaderId(int position) {
        QBChatMessage chatMessage = getItem(position);
        return TimeUtils.getDateAsHeaderId(chatMessage.getDateSent() * 1000);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.view_chat_message_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;
        TextView dateTextView = view.findViewById(R.id.header_date_textview);

        QBChatMessage chatMessage = getItem(position);
        dateTextView.setText(TimeUtils.getDate(chatMessage.getDateSent() * 1000));

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) dateTextView.getLayoutParams();
        if (position == 0) {
            lp.topMargin = (int) context.getResources().getDimension(R.dimen.chat_date_header_top_margin);

        } else {
            lp.topMargin = 0;
        }
        dateTextView.setLayoutParams(lp);

    }
}
