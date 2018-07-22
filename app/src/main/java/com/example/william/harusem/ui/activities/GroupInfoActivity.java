package com.example.william.harusem.ui.activities;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.manager.DialogsManager;
import com.example.william.harusem.ui.adapters.GroupUsersAdapter;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.Toaster;
import com.example.william.harusem.util.Utils;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.william.harusem.ui.adapters.DialogsAdapter.DIALOG_IMAGE;

public class GroupInfoActivity extends AppCompatActivity {

    private static final String TAG = GroupInfoActivity.class.getSimpleName();
    @BindView(R.id.group_circle_iv)
    CircleImageView groupCircleIv;
    @BindView(R.id.group_name_et)
    TextView groupNameEt;
    @BindView(R.id.group_upper_layout)
    LinearLayout groupUpperLayout;
    @BindView(R.id.participants_tv)
    TextView participantsTv;
    @BindView(R.id.grid_view)
    GridView gridView;
    ArrayList<QBUser> qbUsers;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    DialogsManager dialogsManager;
    String photoId;
    @BindView(R.id.photo_pb)
    ProgressBar photoPb;
    String imageUrl = "";
    private int REQUEST_CODE = 44;
    private QBChatDialog qbChatDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        ButterKnife.bind(this);
        configureActionBar();

        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(ChatActivity.EXTRA_DIALOG);
        dialogsManager = new DialogsManager();

        setTitle("Group Info");
        groupCircleIv.setImageResource(R.drawable.circle_shape);
        participantsTv.setText(getString(R.string.participants, qbChatDialog.getOccupants().size() - 1));
        groupNameEt.setText(qbChatDialog.getName());

        loadUserData();
    }

    private void loadUserData() {
        ProgressDialog progressDialog = Utils.buildProgressDialog(this, "", getString(R.string.please_wait), false);
        progressDialog.show();

        getUserPhoto();

        List<QBUser> cachedUsers = QBUsersHolder.getInstance().getUsersByIds(qbChatDialog.getOccupants());
        if (cachedUsers.size() == qbChatDialog.getOccupants().size()) {
            applyUsers(progressDialog, cachedUsers);
        } else {
            getUsersFromRest(progressDialog);
        }

    }

    private void getUsersFromRest(ProgressDialog progressDialog) {
        QBUsers.getUsersByIDs(qbChatDialog.getOccupants(), null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {
                applyUsers(progressDialog, users);
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                Toaster.shortToast(e.getMessage());
                showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getUsersFromRest(progressDialog);
                    }
                });
            }
        });
    }

    private void applyUsers(ProgressDialog progressDialog, List<QBUser> users) {
        progressDialog.dismiss();
        ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<>();
        for (QBUser user : users) {
            if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin())) {
                qbUserWithoutCurrent.add(user);
            }
        }

        fillParticipants(qbUserWithoutCurrent);
    }

    private void getUserPhoto() {
        photoPb.setVisibility(View.VISIBLE);
        String photo = qbChatDialog.getPhoto();
        if (photo != null) {
            imageUrl = photo;

            QBContent.getFile(Integer.parseInt(photo)).performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(QBFile file, Bundle bundle) {
                    photoPb.setVisibility(View.GONE);
                    Picasso.get().load(file.getPublicUrl()).into(groupCircleIv);
                }

                @Override
                public void onError(QBResponseException e) {
                    photoPb.setVisibility(View.GONE);
                    Toaster.shortToast(e.getMessage());
                    showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getUserPhoto();
                        }
                    });
                }
            });
        } else {
            photoPb.setVisibility(View.GONE);
            groupCircleIv.setImageResource(R.drawable.ic_group_new);
        }
    }


    private void fillParticipants(ArrayList<QBUser> qbUsers) {
        GroupUsersAdapter adapter = new GroupUsersAdapter(GroupInfoActivity.this, qbUsers);
        gridView.setAdapter(adapter);
    }

    private void configureActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(rootLayout, resId, e,
                R.string.dlg_retry, clickListener);
    }


    @OnClick(R.id.group_circle_iv)
    public void setGroupCircleIv(View view) {
        AttachmentImageActivity.start(this, imageUrl, DIALOG_IMAGE, String.valueOf(qbChatDialog.getType()));
    }

}

