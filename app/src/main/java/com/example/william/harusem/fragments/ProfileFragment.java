package com.example.william.harusem.fragments;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.william.harusem.AccountActivity;
import com.example.william.harusem.PasswordActivity;
import com.example.william.harusem.R;
import com.example.william.harusem.activities.AllUsersActivity;
import com.example.william.harusem.activities.FriendRequestsActivity;
import com.example.william.harusem.activities.LoginActivity;
import com.nex3z.notificationbadge.NotificationBadge;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile Fragment";
    int requestCode = 65;
    Unbinder unbinder;
    @BindView(R.id.profile_circle_iv)
    CircleImageView profileCircleIv;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.friends_count_key_tv)
    TextView friendsCountKeyTv;
    @BindView(R.id.friends_count_value_tv)
    TextView friendsCountValueTv;
    @BindView(R.id.top_layout)
    RelativeLayout topLayout;
    @BindView(R.id.account_tv)
    TextView accountTv;
    @BindView(R.id.password_tv)
    TextView passwordTv;
    @BindView(R.id.friends_requests_tv)
    TextView friendsRequestsTv;
    @BindView(R.id.friends_tv)
    TextView friendsTv;
    @BindView(R.id.log_out_tv)
    TextView logOutTv;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.notification_badge)
    NotificationBadge notificationBadge;
    @BindView(R.id.log_out_pb)
    ProgressBar logOutPb;
    @BindView(R.id.log_out_layout)
    RelativeLayout logOutLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);


        friendsRequestsTv.setOnClickListener(new View.OnClickListener() {
            int number = 0;

            @Override
            public void onClick(View view) {
                number++;

                notificationBadge.setNumber(number);
                Intent intent = new Intent(getActivity(), FriendRequestsActivity.class);

                getActivity().startActivity(intent);

            }
        });


        friendsTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), AllUsersActivity.class);
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0, 0);

            }
        });

        return rootView;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // LoginActivity is a New Task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // The old task when coming back to this activity should be cleared so we cannot come back to it.
        startActivity(intent);
    }

    @OnClick(R.id.log_out_layout)
    public void setLogOutTv(View view) {
        showProgressBar();

        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        hideProgressBar();
                        Log.d(TAG, "onSuccess: logout success!");
                        redirectToLogin();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        hideProgressBar();
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressBar();
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void showProgressBar() {
        logOutPb.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        if (logOutPb.getVisibility() == View.VISIBLE) {
            logOutPb.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @OnClick(R.id.profile_circle_iv)
    public void onProfileImgClick(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestCode && resultCode == RESULT_OK) {

            Uri imagePath = data.getData();
            CropImage.activity(imagePath).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri imageUri = result.getUri();

                profileCircleIv.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @OnClick(R.id.account_tv)
    public void setAccountTv(View view) {
        startActivity(new Intent(getActivity(), AccountActivity.class));
    }

    @OnClick(R.id.password_tv)
    public void setPasswordTv(View view) {
        startActivity(new Intent(getActivity(), PasswordActivity.class));
    }

}
