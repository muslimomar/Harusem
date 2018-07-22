package com.example.william.harusem.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.william.harusem.BlockingActivity;
import com.example.william.harusem.R;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.example.william.harusem.holder.QBChatDialogHolder;
import com.example.william.harusem.holder.QBFriendRequestsHolder;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.models.UserData;
import com.example.william.harusem.ui.activities.AccountActivity;
import com.example.william.harusem.ui.activities.FriendRequestsActivity;
import com.example.william.harusem.ui.activities.FriendsActivity;
import com.example.william.harusem.ui.activities.LoginActivity;
import com.example.william.harusem.ui.activities.PasswordActivity;
import com.example.william.harusem.util.ChatHelper;
import com.example.william.harusem.util.ChatPingAlarmManager;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.example.william.harusem.util.Toaster;
import com.example.william.harusem.util.Utils;
import com.example.william.harusem.utils.UsersUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.nex3z.notificationbadge.NotificationBadge;
import com.quickblox.chat.QBChatService;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile Fragment";
    int REQUEST_CODE = 65;
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
    @BindView(R.id.profile_loading_pb)
    ProgressBar profileLoadingPb;

    @BindView(R.id.profile_mother_lang)
    TextView profileMotherLang;
    @BindView(R.id.profile_mother_lang_pb)
    RoundCornerProgressBar profileMotherLangPb;
    @BindView(R.id.profile_learning_lang)
    TextView profileLearningLang;
    @BindView(R.id.profile_learn_lang_pb)
    RoundCornerProgressBar profileLearnLangPb;
    @BindView(R.id.profile_country_flag)
    ImageView profileCountryFlag;
    @BindView(R.id.friend_and_point_layout)
    LinearLayout friendAndPointLayout;
    @BindView(R.id.blocking_tv)
    TextView blockingTv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        showProgressBar(profileLoadingPb);
        hideLayout();
        loadUserData();
        return rootView;

    }

    private void loadUserData() {

        QBUsers.getUser(QBChatService.getInstance().getUser().getId())
                .performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle bundle) {
                        getUserCustomData(user);

                        QBUsersHolder.getInstance().putUser(user);

                        if (getActivity() != null && isAdded()) {
                            nameTv.setText(user.getFullName());

                            getUnreadFriendRequests();

                            friendsCountValueTv.setText(String.valueOf(getFriendsCount()));
                        }

                        if (user.getFileId() != null) {

                            int profilePicId = user.getFileId();

                            QBContent.getFile(profilePicId)
                                    .performAsync(new QBEntityCallback<QBFile>() {
                                        @Override
                                        public void onSuccess(QBFile qbFile, Bundle bundle) {

                                            if (getActivity() != null && isAdded()) {

                                                String fileUrl = qbFile.getPublicUrl();
                                                Picasso.get().load(fileUrl)
                                                        .into(profileCircleIv, new Callback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                if (getActivity() != null && isAdded()) {
                                                                    hideProgressBar(profileLoadingPb);
                                                                    showLayout();
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(Exception e) {
                                                                Log.e(TAG, "onError: picasso :", e);

                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onError(QBResponseException e) {
                                            Log.e(TAG, "onError: qbcontent getfile", e);
                                        }
                                    });
                        } else {

                            if (getActivity() != null && isAdded()) {
                                hideProgressBar(profileLoadingPb);
                                showLayout();
                                profileCircleIv.setImageResource(R.drawable.profile_placeholder);
                            }
                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e(TAG, "onError: qbusers get user", e);
                    }
                });

    }

    @SuppressLint("ClickableViewAccessibility")
    public void getUserCustomData(QBUser user) {

        try{
            String customData = user.getCustomData();

            UserData retrievedData = new Gson().fromJson(customData, UserData.class);
            if (retrievedData != null) {
                Log.v("retrieved data", "Data is: " + retrievedData);

                // Mother Language
                String motherLanguage = retrievedData.getMotherLanguage();
                profileMotherLang.setText(motherLanguage);
                profileMotherLangPb.setProgressColor(ContextCompat.getColor(getContext(), R.color.pb_color));
                profileMotherLangPb.setSecondaryProgressColor(ContextCompat.getColor(getContext(), R.color.pb_sec_color));
                profileMotherLangPb.setSecondaryProgress(97);
                profileMotherLangPb.setProgressBackgroundColor(ContextCompat.getColor(getContext(), R.color.pb_bg_color));
                profileMotherLangPb.setRadius(10);
                profileMotherLangPb.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Toast.makeText(getContext(), "I am Native speaker!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                profileMotherLangPb.setMax(100);
                profileMotherLangPb.setProgress(85);
                profileMotherLangPb.setReverse(false);
                profileMotherLangPb.setPadding(5);

                //Learning Languague
                String learningLanguage = retrievedData.getLearningLanguage();
                String languageLevel = retrievedData.getSelectedLanguageLevel();
                profileLearningLang.setText(learningLanguage);
                profileLearnLangPb.setMax(100);
                profileLearnLangPb.setReverse(false);
                profileLearnLangPb.setRadius(10);
                profileLearnLangPb.setPadding(5);
                profileLearnLangPb.setSecondaryProgressColor(ContextCompat.getColor(getContext(), R.color.pb_sec_color));
                profileLearnLangPb.setProgressColor(ContextCompat.getColor(getContext(), R.color.pb_color));
                profileLearnLangPb.setProgressBackgroundColor(ContextCompat.getColor(getContext(), R.color.pb_bg_color));
                switch (languageLevel) {
                    case "Beginner":
                        profileLearnLangPb.setProgress(30);
                        profileLearnLangPb.setSecondaryProgress(40);
                        profileLearnLangPb.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                Toast.makeText(getContext(), "Keep Learning! Your level is beginner!", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });
                        break;
                    case "Intermediate":
                        profileLearnLangPb.setProgress(50);
                        profileLearnLangPb.setSecondaryProgress(65);
                        profileLearnLangPb.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                Toast.makeText(getContext(), "Good! Your level is Intermediate!", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });

                        break;
                    case "Advanced":
                        profileLearnLangPb.setProgress(75);
                        profileLearnLangPb.setSecondaryProgress(90);
                        profileLearnLangPb.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                Toast.makeText(getContext(), "Amazing! Your level is Advanced!", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });

                        break;
                }

                String country = retrievedData.getUserCountry();

                CountryPicker countryPicker =
                        new CountryPicker.Builder().with(getContext()).build();

                List<Country> allCountries = countryPicker.getAllCountries();
                int id = 0;
                for (Country c : allCountries) {
                    if (c.getName().equalsIgnoreCase(country))
                        id = c.getFlag();
                }
                profileCountryFlag.setImageResource(id);
            }
            // Try your GSON thing
        } catch (JsonParseException e){
            Toast.makeText(getContext(), "ERROR! USER MIGHT NOT HAVE CUSTOM DATA!", Toast.LENGTH_SHORT).show();
        }

    }


    private int getFriendsCount() {
        QBFriendListHelper friendListHelper = new QBFriendListHelper(getActivity());
        return friendListHelper.getAllFriends().size();
    }

    private void getUnreadFriendRequests() {
        notificationBadge.clear();
        int size = QBFriendRequestsHolder.getInstance().getAllFriendRequests().size();
        if (size > 0) {
            if (size > 99) {
                size = 99;
            }
            notificationBadge.setText("" + size);
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // LoginActivity is a New Task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // The old task when coming back to this activity should be cleared so we cannot come back to it.
        startActivity(intent);
    }

    private void unsubscribeFromPushes() {
        SubscribeService.unSubscribeFromPushes(getActivity());

    }

    private void destroyRtcClientAndChat() {
        QBRTCClient rtcClient = QBRTCClient.getInstance(getActivity());
        QBChatService chatService = QBChatService.getInstance();
        if (rtcClient != null) {
            rtcClient.destroy();
        }

        try {
            ChatPingAlarmManager.onDestroy();
        } catch (Exception e) {
            Log.e(TAG, "destroyRtcClientAndChat: ",e );
        }

        if (chatService != null) {
            chatService.logout(new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    if (getActivity() != null && isAdded()) {
                        UsersUtils.removeUserData();
                        chatService.destroy();
                    }
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d(TAG, "logout onError " + e.getMessage());
                    chatService.destroy();
                }
            });
        }
    }

    @OnClick(R.id.log_out_layout)
    public void setLogOutTv(View view) {
        disableUserInteraction();

        showProgressBar(logOutPb);
        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        if (getActivity() != null && isAdded()) {
                            unsubscribeFromPushes();
                            destroyRtcClientAndChat();
                        }

                        hideProgressBar(logOutPb);

                        SharedPrefsHelper.getInstance().removeQbUser();
                        ChatHelper.getInstance().destroy();
                        QBChatDialogHolder.getInstance().clear();


                        Log.d(TAG, "onSuccess: logout success!");
                        redirectToLogin();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        hideProgressBar(logOutPb);
                        enableUserInteraction();
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressBar(logOutPb);
                enableUserInteraction();
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showProgressBar(ProgressBar progressBar) {
        if (getActivity() != null && isAdded()) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar(ProgressBar progressBar) {
        if (getActivity() != null && isAdded()) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && isAdded())
            getUnreadFriendRequests();
        friendsCountValueTv.setText(String.valueOf(getFriendsCount()));
    }

    @OnClick(R.id.profile_circle_iv)
    public void onProfileImgClick(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, "Select a Picture"), REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imagePath = data.getData();
            CropImage.activity(imagePath).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final ProgressDialog progressDialog = Utils.buildProgressDialog(getActivity(), "", getString(R.string.please_wait), false);
                progressDialog.show();

                Uri imageUri = result.getUri();

                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    File file = new File(Environment.getExternalStorageDirectory() + "/myimage.png");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bos.toByteArray());
                    fos.flush();
                    fos.close();

                    // Get file size
                    int imageSizeKB = (int) (file.length() / 1024);
                    if (imageSizeKB >= (1024 * 100)) {
                        Toast.makeText(getActivity(), R.string.img_large, Toast.LENGTH_SHORT).show();
                    }

                    QBContent.uploadFileTask(file, true, null).performAsync(new QBEntityCallback<QBFile>() {

                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            QBUser user = new QBUser();
                            user.setId(QBChatService.getInstance().getUser().getId());
                            user.setFileId(Integer.parseInt(qbFile.getId().toString()));

                            QBUsers.updateUser(user)
                                    .performAsync(new QBEntityCallback<QBUser>() {
                                        @Override
                                        public void onSuccess(QBUser user, Bundle bundle) {
                                            progressDialog.dismiss();
                                        }

                                        @Override
                                        public void onError(QBResponseException e) {
                                            Log.e(TAG, "onError:updateuser ", e);
                                        }
                                    });

                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Log.e(TAG, "onError: uploadfiletask", e);
                        }
                    });


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                profileCircleIv.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "onActivityResult: ", result.getError());
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

    @OnClick(R.id.friends_tv)
    public void setFriendsTv(View view) {
        Intent i = new Intent(getActivity(), FriendsActivity.class);
        startActivity(i);
        ((Activity) getActivity()).overridePendingTransition(0, 0);
    }

    @OnClick(R.id.friends_requests_tv)
    public void setFriendsRequestsTv(View view) {
        Intent intent = new Intent(getActivity(), FriendRequestsActivity.class);
        getActivity().startActivity(intent);
    }

    public void hideLayout() {
        topLayout.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
    }

    public void showLayout() {
        topLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
    }

    public void disableUserInteraction() {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUserInteraction() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @OnClick(R.id.blocking_tv) public void setBlockingTv(View view) {
        startActivity(new Intent(getActivity(), BlockingActivity.class));
    }
}