package com.example.william.harusem.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.william.harusem.BlockingActivity;
import com.example.william.harusem.R;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.quickblox.chat.QBPrivacyListsManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBPrivacyList;
import com.quickblox.chat.model.QBPrivacyListItem;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by eminesa on 3.07.2018.
 */

public class BlockingAdapter extends RecyclerView.Adapter<BlockingAdapter.MyViewHolder> {
    //Model olarak MyViewHolder Kullanılacağını göz önünde bulundur.

    private static final String TAG = BlockingAdapter.class.getSimpleName();
    private List<QBUser> usersList;
    private QBPrivacyListsManager privacyListsManager;

    public BlockingAdapter( List<QBUser> user, QBPrivacyListsManager privacyListsManager) {
        this.usersList = user;
        this.privacyListsManager = privacyListsManager;
    }

    //Kullanılacak olan item_list_view'i çağıran override Metodu
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.block_list_item, parent, false);
        return new BlockingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

            final QBUser user = usersList.get(position);

            holder.blockUserNameTv.setText(user.getFullName());
            holder.unBlockUserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unBlockUser(user.getId().toString());
                    usersList.remove(usersList.indexOf(user));
                    notifyItemRemoved(position);

                }
            });

            getUserImage(user, holder.userBlockingThumbIv);

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView blockUserNameTv;
        public CircleImageView userBlockingThumbIv;
        public Button unBlockUserBtn;

        public MyViewHolder(View view) {
            super(view);
            blockUserNameTv = view.findViewById(R.id.block_name_tv);
            userBlockingThumbIv = view.findViewById(R.id.block_image_user);
            unBlockUserBtn = view.findViewById(R.id.unblock_btn);
        }
    }

    //Kullanıcının profilini getiren metod
    private void getUserImage(QBUser user, final CircleImageView userThumbIv) {
        if (user.getFileId() != null) {
            int profilePicId = user.getFileId();

            QBContent.getFile(profilePicId).performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(QBFile qbFile, Bundle bundle) {
                    Picasso.get()
                            .load(qbFile.getPublicUrl())
                            .resize(50, 50)
                            .centerCrop()
                            .into(userThumbIv);
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("BlockingAdapter", "onError: ", e);
                    userThumbIv.setImageResource(R.drawable.placeholder_user);
                }
            });
        } else {
            userThumbIv.setImageResource(R.drawable.placeholder_user);
        }
    }


    // Privacy listesini getir
    private QBPrivacyList getPublicPrivacyList() {

        QBPrivacyList publicList = null;

        try {
            publicList = privacyListsManager.getPrivacyList("public");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }

        return publicList;
    }

    // Kullanıcı engellini kaldıran metod
    private void unBlockUser(String uid) {

        QBPrivacyList publicPrivacyList = getPublicPrivacyList();

        ArrayList<QBPrivacyListItem> items = new ArrayList<QBPrivacyListItem>();

        items.addAll(publicPrivacyList.getItems());

        for (QBPrivacyListItem item : publicPrivacyList.getItems()) {
            if (item.getValueForType().contains(uid)) {
                items.remove(item);
            }
        }

        publicPrivacyList.setItems(items);

        updatePrivacyList(publicPrivacyList, items.size());

    }

    //updatePrivacyList
    private void updatePrivacyList(QBPrivacyList publicPrivacyList, int itemsSize) {

        try {
            privacyListsManager.declinePrivacyList();
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "blockUser: ", e);
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            Log.e(TAG, "blockUser: ", e);
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            Log.e(TAG, "blockUser: ", e);
            e.printStackTrace();
        }

        if(itemsSize > 0) {

            try {
                privacyListsManager.createPrivacyList(publicPrivacyList);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            }

            try {
                privacyListsManager.applyPrivacyList(publicPrivacyList);
            } catch (SmackException.NotConnectedException e) {
                Log.e(TAG, "blockUser: ", e);
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                Log.e(TAG, "blockUser: ", e);
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                Log.e(TAG, "blockUser: ", e);
                e.printStackTrace();
            }
        }else{
            try {
                privacyListsManager.deletePrivacyList("public");
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            }

        }

    }


}
