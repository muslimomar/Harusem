package com.example.william.harusem.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.models.RequestFriends;

import java.util.List;

/**
 * Created by eminesa on 15.05.2018.
 */

public class RequestFriendsAdapter extends ArrayAdapter<RequestFriends> {


    public RequestFriendsAdapter(@NonNull Context context, List<RequestFriends> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.request_friend_list_item, parent, false);
        }

        RequestFriends currentRequestFriend = getItem(position);

        ImageView imageProfile = listItemView.findViewById(R.id.request_friend_list_item_profile_image_view);
        TextView textViewName = listItemView.findViewById(R.id.request_friend_list_item_name_text_view);
        TextView textViewRequest = listItemView.findViewById(R.id.request_friends_list_item_want_text_view);
        ImageView deleteImage = listItemView.findViewById(R.id.request_friend_delete_image_view);


        imageProfile.setImageResource(currentRequestFriend.getProfileImage());
        textViewName.setText(currentRequestFriend.getNameTextView());
        textViewRequest.setText(currentRequestFriend.getRequestTextView());

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return listItemView;
    }
}
