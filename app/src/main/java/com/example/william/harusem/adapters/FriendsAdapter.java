package com.example.william.harusem.adapters;




import android.content.Context;
import android.provider.Contacts;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.models.FriendsClass;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.Holder> {

    public static String TAG = FriendsAdapter.class.getSimpleName();

    private ArrayList<FriendsClass> mFriendsList = new ArrayList<>();
    private Context context;

    public FriendsAdapter(ArrayList<FriendsClass> friend, Context context) {
        this.mFriendsList = friend;
        this.context = context;
    }

    public void addFriend(FriendsClass friend) {
        mFriendsList.add(friend);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item, parent, false);
        Holder holder = new Holder(row,context,mFriendsList);

        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        FriendsClass currentFriend = mFriendsList.get(position);
        holder.userNameTv.setText(currentFriend.getUserName());
        holder.userStatus.setText(currentFriend.getUserStatus());
        //Picasso.with(context).load(currentFriend.getUserPhoto()).into(holder.userImageView);
        //Picasso.with(context).load(currentFriend.getUnFriendImageView()).into(holder.unfriendBtn);
    }

    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        public TextView userNameTv;
        public TextView userStatus;
        public ImageView userImageView;
        public ImageView unfriendBtn;

        Context context;
        ArrayList<FriendsClass> friends = new ArrayList<>();

        public Holder(View itemView, Context context, ArrayList<FriendsClass> friends) {
            super(itemView);
            this.context = context;
            this.friends = friends;
            userImageView = (ImageView) itemView.findViewById(R.id.user_ImageView);
            userNameTv = (TextView) itemView.findViewById(R.id.userName_TextView);
            userStatus=itemView.findViewById(R.id.userState_textView);
            unfriendBtn=itemView.findViewById(R.id.unfriend_Button);
        }
    }
}
