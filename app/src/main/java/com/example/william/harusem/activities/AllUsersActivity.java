package com.example.william.harusem.activities;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.william.harusem.R;
import com.example.william.harusem.adapters.UsersListAdapter;
import com.example.william.harusem.common.Common;
import com.example.william.harusem.holder.QBUsersHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.william.harusem.util.Helper.buildProgressDialog;


public class AllUsersActivity extends AppCompatActivity {

    public static final String TAG = AllUsersActivity.class.getSimpleName();
    @BindView(R.id.all_users_list)
    ListView usersRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.create_chat_btn)
    Button createChatBtn;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        ButterKnife.bind(this);

        configRecyclerView();

        retrieveAllUser();

        usersRecyclerView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    private void retrieveAllUser() {

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);

                ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<>();
                for (QBUser user : qbUsers) {
                    if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin())) {
                        qbUserWithoutCurrent.add(user);
                    }
                }

                UsersListAdapter mAdapter = new UsersListAdapter(AllUsersActivity.this, qbUserWithoutCurrent);
                usersRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "1 onError: ", e);
            }
        });

    }


    private void swipeLayoutRefresh() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: refresh
            }
        });
    }


    private void configRecyclerView() {
//        mLayoutManager = new LinearLayoutManager(this);
//        usersRecyclerView.setHasFixedSize(true);
//        usersRecyclerView.setLayoutManager(mLayoutManager);
    }

    @OnClick(R.id.create_chat_btn)
    public void setCreateChatBtn(View view) {
//        int countChoice = usersRecyclerView.getCount();

        if (usersRecyclerView.getCheckedItemPositions().size() == 1)
            createPrivateChat(usersRecyclerView.getCheckedItemPositions());
        else if (usersRecyclerView.getCheckedItemPositions().size() > 1)
            createGroupChat(usersRecyclerView.getCheckedItemPositions());
        else
            Toast.makeText(this, "Please select friend/s to chat with!", Toast.LENGTH_SHORT).show();


    }

    private void createGroupChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog progressDialog = buildProgressDialog(this, "Please Wait", "", false);
        progressDialog.show();

        int countChoice = usersRecyclerView.getCount();
        ArrayList<Integer> occupantIdsList = new ArrayList<>();
        for (int i = 0; i < countChoice; i++) {

            if (checkedItemPositions.get(i)) {
                QBUser user = (QBUser) usersRecyclerView.getItemAtPosition(i);
                occupantIdsList.add(user.getId());
            }

        }

        final QBChatDialog dialog = new QBChatDialog();
        dialog.setName(Common.createChatDialogName(occupantIdsList));
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdsList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                progressDialog.dismiss();
                Toast.makeText(AllUsersActivity.this, "Create Group Chat Dialog Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "2 onError: ", e);
            }
        });

    }

    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {

        final ProgressDialog progressDialog = buildProgressDialog(this, "Please Wait", "", false);
        progressDialog.show();

        int countChoice = usersRecyclerView.getCount();
        for (int i = 0; i < countChoice; i++) {
            if (checkedItemPositions.get(i)) {
                QBUser user = (QBUser) usersRecyclerView.getItemAtPosition(i);
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());

                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        progressDialog.dismiss();
                        Toast.makeText(AllUsersActivity.this, "Create private Chat Dialog Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e(TAG, "3 onError: ", e);
                    }
                });

            }
        }


    }

}
