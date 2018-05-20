package com.example.william.harusem.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.models.ChatList;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Mahmoud on 5/17/2018.
 */





public class ChatMainAdapter extends ArrayAdapter<ChatList> {

    ArrayList<ChatList> dataModels;

    public ChatMainAdapter(Activity context, ArrayList<ChatList> arrayList) {
        super(context, 0, arrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.fragment_chat_list_item, parent,false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        ChatList currentItem = getItem(position);

        ImageView chatIcon = (ImageView) listItemView.findViewById(R.id.ic_profile_chat);
        TextView chatPartnerName = (TextView) listItemView.findViewById(R.id.chat_id_name);
        TextView chatTimeStamp = (TextView) listItemView.findViewById(R.id.chat_time_stamp);
        TextView chatTextPreview = (TextView) listItemView.findViewById(R.id.chat_text_preview);


        chatIcon.setBackgroundResource(R.drawable.ic_chat_profile_photo);
        chatPartnerName.setText(currentItem.getChatPartnerName());
        chatTimeStamp.setText(currentItem.getChatTimeStamp());
        chatTextPreview.setText(currentItem.getFirstChatWords());

        return listItemView;
    }

}
