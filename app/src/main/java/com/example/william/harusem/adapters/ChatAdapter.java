package com.example.william.harusem.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.models.Message;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private static final int TYPE_MESSAGE_SENT = 1;
    private static final int TYPE_MESSAGE_RECEIVED = 2;

    private List<Message> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sentMessageTv,receivedMessageTv;

        public MyViewHolder(View view) {
            super(view);
            sentMessageTv = (TextView) view.findViewById(R.id.sent_message_tv);
            receivedMessageTv = (TextView) view.findViewById(R.id.received_message_tv);

        }
    }


    public ChatAdapter(List<Message> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = moviesList.get(position);

        holder.receivedMessageTv.setText(message.getMessage());
        holder.sentMessageTv.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}