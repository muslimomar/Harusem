package com.example.william.harusem.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int MESSAGE_TYPE_SENDER = 1;
    public static final int MESSAGE_TYPE_RECEIVER = 2;

    private List<ChatMessage> mChatList = new ArrayList<>();

    public MessagesAdapter(List<ChatMessage> FireBaseChatsList) {
        this.mChatList = FireBaseChatsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder;
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case MESSAGE_TYPE_SENDER:
                view = inflater.inflate(R.layout.sender_message_item, parent, false);
                holder = new SenderViewHolder(view);
                break;
            case MESSAGE_TYPE_RECEIVER:
                view = inflater.inflate(R.layout.recipient_message_item, parent, false);
                holder = new RecipientViewHolder(view);
                break;
            default:
                view = inflater.inflate(R.layout.sender_message_item, parent, false);
                holder = new SenderViewHolder(view);
                break;
        }


        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = mChatList.get(position);

        switch (holder.getItemViewType()) {
            case MESSAGE_TYPE_SENDER:
                SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
                senderViewHolder.senderMessageTv.setText(chatMessage.getMessage());
                break;
            case MESSAGE_TYPE_RECEIVER:
                RecipientViewHolder recipientViewHolder = (RecipientViewHolder) holder;
                recipientViewHolder.recipientMessageTv.setText(chatMessage.getMessage());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (mChatList.get(position).getIsSenderOrRecipient() == MESSAGE_TYPE_SENDER) {
            return MESSAGE_TYPE_SENDER;
        } else {
            return MESSAGE_TYPE_RECEIVER;
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageTv;

        public SenderViewHolder(View itemView) {
            super(itemView);

            senderMessageTv = itemView.findViewById(R.id.sent_message_tv);
        }
    }

    public class RecipientViewHolder extends RecyclerView.ViewHolder {

        public TextView recipientMessageTv;

        public RecipientViewHolder(View itemView) {
            super(itemView);

            recipientMessageTv = itemView.findViewById(R.id.received_message_tv);
        }
    }

    public void addMessage(ChatMessage chatMessage) {
        mChatList.add(chatMessage);
        notifyItemInserted(mChatList.indexOf(chatMessage));
    }

    public void cleanAdapter() {
        mChatList.clear();
    }

}