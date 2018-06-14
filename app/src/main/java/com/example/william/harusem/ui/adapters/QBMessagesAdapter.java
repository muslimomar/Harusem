package com.example.william.harusem.ui.adapters;

import android.content.Context;

import com.quickblox.chat.model.QBChatMessage;

import java.util.List;

/**
 * Created by william on 6/5/2018.
 */

public class QBMessagesAdapter extends com.quickblox.ui.kit.chatmessage.adapter.QBMessagesAdapter<QBChatMessage>    {

    public QBMessagesAdapter(Context context, List<QBChatMessage> chatMessages) {
        super(context, chatMessages);
    }

}
