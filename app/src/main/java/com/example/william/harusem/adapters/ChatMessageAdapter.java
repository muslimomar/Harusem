package com.example.william.harusem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;

/**
 * Created by william on 5/29/2018.
 */

public class ChatMessageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QBChatMessage> qbChatMessages;

    public ChatMessageAdapter(Context context, ArrayList<QBChatMessage> qbChatMessages) {
        this.context = context;
        this.qbChatMessages = qbChatMessages;
    }

    @Override
    public int getCount() {
        return qbChatMessages.size();
    }

    @Override
    public Object getItem(int i) {
        return qbChatMessages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view1 = convertView;
        if (view1 == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (qbChatMessages.get(i).getSenderId().equals(QBChatService.getInstance().getUser().getId())) {
                view1 = inflater.inflate(R.layout.sender_message_item, null);

                TextView sentMessageTv = (TextView) view1.findViewById(R.id.sent_message_tv);
                sentMessageTv.setText(qbChatMessages.get(i).getBody());

            } else {
                view1 = inflater.inflate(R.layout.recipient_message_item, null);

                TextView receivedMessageTv = (TextView) view1.findViewById(R.id.received_message_tv);
                receivedMessageTv.setText(qbChatMessages.get(i).getBody());

            }


        }

        return view1;
    }
}
