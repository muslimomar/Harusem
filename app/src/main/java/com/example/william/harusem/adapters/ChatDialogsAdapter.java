package com.example.william.harusem.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.william.harusem.R;
import com.example.william.harusem.holder.QBUnreadMessageHolder;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;

/**
 * Created by william on 5/29/2018.
 */

public class ChatDialogsAdapter extends BaseAdapter {
    int switcher = 0;
    private Context context;
    private ArrayList<QBChatDialog> qbChatDialogs;

    public ChatDialogsAdapter(Context context, ArrayList<QBChatDialog> qbChatDialogs) {
        this.context = context;
        this.qbChatDialogs = qbChatDialogs;
    }

    @Override
    public int getCount() {
        return qbChatDialogs.size();
    }

    @Override
    public Object getItem(int i) {
        return qbChatDialogs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view1 = view;
        if (view1 == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view1 = inflater.inflate(R.layout.list_chat_dialog, null);

            TextView txtTitle, txtMessage;
            ImageView imageView, imageUnread;

            txtTitle = view1.findViewById(R.id.list_chat_dialog_title);
            txtMessage = view1.findViewById(R.id.list_chat_dialog_message);
            imageView = view1.findViewById(R.id.image_chatDialog);
            imageUnread = view1.findViewById(R.id.image_unRead);

            txtTitle.setText(qbChatDialogs.get(i).getName());
            txtMessage.setText(qbChatDialogs.get(i).getLastMessage());

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int randomColor = generator.getRandomColor();

            TextDrawable.IBuilder builder = TextDrawable.builder()
                    .beginConfig()
                    .endConfig()
                    .round();

            TextDrawable drawable = builder.build(txtTitle.getText().toString().trim().substring(0, 1).toUpperCase(), getColor(i));
            imageView.setImageDrawable(drawable);

            // set message unread count
            TextDrawable.IBuilder unreadBuilder = TextDrawable.builder()
                    .beginConfig()
                    .endConfig()
                    .round();

            int unreadCount = QBUnreadMessageHolder.getInstance().getBundle().getInt(qbChatDialogs.get(i).getDialogId());
            if (unreadCount > 0) {
                TextDrawable unreadDrawable = unreadBuilder.build(String.valueOf(unreadCount), context.getResources().getColor(R.color.colorAccent));
                imageUnread.setImageDrawable(unreadDrawable);
            }

        }

        return view1;
    }

    private int getColor(int i) {
        int color;

        if (switcher == 0) {
            // orange
            color = Color.parseColor("#f44336");
            switcher = 1;
        } else if (switcher == 1) {
            color = Color.parseColor("#E91E63");
            switcher = 2;
        } else if (switcher == 2) {
            color = Color.parseColor("#4CAF50");
            switcher = 3;
        } else if (switcher == 3) {
            color = Color.parseColor("#009688");
            switcher = 4;
        } else if (switcher == 4) {
            color = Color.parseColor("#9C27B0");
            switcher = 5;
        }else if (switcher == 5) {
            color = Color.parseColor("#607D8B");
            switcher = 0;
        }else{
            color = Color.parseColor("#000000");
            switcher = 0;
        }

        return color;
    }

}
