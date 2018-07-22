package com.example.william.harusem.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupUsersAdapter extends BaseAdapter {
    private static final String TAG = GroupUsersAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<QBUser> qbUsers;

    public GroupUsersAdapter(Context c, ArrayList<QBUser> qbUsers) {
        mContext = c;
        this.qbUsers = qbUsers;
    }


    @Override
    public int getCount() {
        return qbUsers.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.group_user_list_item, parent,false);

            final ImageView participantPhotoIv = (ImageView) convertView.findViewById(R.id.participant_photo_iv);
            final TextView participantNameIv = (TextView) convertView.findViewById(R.id.participant_name_iv);

            final ViewHolder viewHolder = new ViewHolder(participantPhotoIv, participantNameIv);
            convertView.setTag(viewHolder);

        }

        QBUser qbUser = qbUsers.get(position);

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        getUserPhoto(qbUser, viewHolder.participantPhotoIv);
        viewHolder.participantNameIv.setText(qbUser.getFullName());


        return convertView;
    }

    private void getUserPhoto(QBUser user, final ImageView participantPhotoIv) {
        if (user.getFileId() != null) {
            int profilePicId = user.getFileId();

            QBContent.getFile(profilePicId).performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(QBFile qbFile, Bundle bundle) {
                    Picasso.get()
                            .load(qbFile.getPublicUrl())
                            .resize(50, 50)
                            .centerCrop()
                            .into(participantPhotoIv);
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e(TAG, "onError: ", e);
                    participantPhotoIv.setImageResource(R.drawable.ic_user_new);
                }
            });
        } else {
            participantPhotoIv.setImageResource(R.drawable.ic_user_new);
        }
    }

    private class ViewHolder {
        private final ImageView participantPhotoIv;
        private final TextView participantNameIv;

        public ViewHolder(ImageView participantPhotoIv, TextView participantNameIv) {
            this.participantPhotoIv = participantPhotoIv;
            this.participantNameIv = participantNameIv;
        }
    }
}