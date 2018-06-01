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
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.william.harusem.common.Common.UPDATE_ADD_MODE;
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
    String mode = "";
    QBChatDialog qbChatDialog;
    List<QBUser> addedUsers = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        ButterKnife.bind(this);

        mode = getIntent().getStringExtra(Common.UPDATE_MODE);
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.UPDATE_DIALOG_EXTRA);


        configRecyclerView();

        retrieveAllUser();

        usersRecyclerView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        if (mode == null && qbChatDialog == null) {
            retrieveAllUser();
        } else {
            if (mode.equals(UPDATE_ADD_MODE))
                loadListAvailableUser();
            else if (mode.equals(Common.UPDATE_REMOVE_MODE))
                loadListUserInGroup();
        }

    }

    private void loadListUserInGroup() {

        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId())
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        List<Integer> occupantsId = qbChatDialog.getOccupants();
                        List<QBUser> listUsersAlreadyInGroup = QBUsersHolder.getInstance().getUsersByIds(occupantsId);
                        ArrayList<QBUser> users = new ArrayList<>();
                        users.addAll(listUsersAlreadyInGroup);

                        UsersListAdapter adapter = new UsersListAdapter(getBaseContext(), users);
                        usersRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        addedUsers = users;

                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });

    }

    private void loadListAvailableUser() {
        createChatBtn.setText("Add User");

        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId())
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        ArrayList<QBUser> listUsers = QBUsersHolder.getInstance().getAllUsers();
                        List<Integer> occupantsId = qbChatDialog.getOccupants();
                        List<QBUser> listUserAlreadyInChatGroup = QBUsersHolder.getInstance().getUsersByIds(occupantsId);

                        for (QBUser user : listUserAlreadyInChatGroup)
                            listUsers.remove(user);

                        if (listUsers.size() > 0) {
                            UsersListAdapter adapter = new UsersListAdapter(getBaseContext(), listUsers);
                            usersRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            addedUsers = listUsers;
                        }

                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });

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

        if (mode == null) {

            if (usersRecyclerView.getCheckedItemPositions().size() == 1)
                createPrivateChat(usersRecyclerView.getCheckedItemPositions());
            else if (usersRecyclerView.getCheckedItemPositions().size() > 1)
                createGroupChat(usersRecyclerView.getCheckedItemPositions());
            else
                Toast.makeText(this, "Please select friend/s to chat with!", Toast.LENGTH_SHORT).show();
        } else if (mode.equals(UPDATE_ADD_MODE) && qbChatDialog != null) {

            if (addedUsers.size() > 0) {
                QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();

                int cntChoice = usersRecyclerView.getCount();
                SparseBooleanArray checkedItemPositions = usersRecyclerView.getCheckedItemPositions();
                for (int i = 0; i < cntChoice; i++) {

                    if (checkedItemPositions.get(i)) {
                        QBUser user = (QBUser) usersRecyclerView.getItemAtPosition(i);
                        requestBuilder.addUsers(user);
                    }
                }


                // Call Services
                QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder)
                        .performAsync(new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                Toast.makeText(AllUsersActivity.this, "User added successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });

            }

        }else if(mode.equals(Common.UPDATE_REMOVE_MODE) && qbChatDialog!=null) {
            if(addedUsers.size() > 0) {
                QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();

                int cntChoice = usersRecyclerView.getCount();
                SparseBooleanArray checkedItemPositions = usersRecyclerView.getCheckedItemPositions();

                for (int i = 0; i < cntChoice; i++) {

                    if (checkedItemPositions.get(i)) {
                        QBUser user = (QBUser) usersRecyclerView.getItemAtPosition(i);
                        requestBuilder.removeUsers(user);
                    }
                }

                // Call Services
                QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder)
                        .performAsync(new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                Toast.makeText(AllUsersActivity.this, "User removed successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });


            }
        }


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

                // Send system message to recipient Id user
                QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setBody(qbChatDialog.getDialogId());

                for (int i = 0; i < qbChatDialog.getOccupants().size(); i++) {
                    qbChatMessage.setRecipientId(qbChatDialog.getOccupants().get(i));
                    try {
                        qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }

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
                final QBUser user = (QBUser) usersRecyclerView.getItemAtPosition(i);
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());

                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        progressDialog.dismiss();
                        Toast.makeText(AllUsersActivity.this, "Create private Chat Dialog Successfully", Toast.LENGTH_SHORT).show();

                        // Send system message to recipient Id user
                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        QBChatMessage qbChatMessage = new QBChatMessage();
                        qbChatMessage.setRecipientId(user.getId());
                        qbChatMessage.setBody(qbChatDialog.getDialogId());
                        try {
                            qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }


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
