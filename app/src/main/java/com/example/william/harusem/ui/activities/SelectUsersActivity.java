package com.example.william.harusem.ui.activities;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.william.harusem.R;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.ui.adapters.newAdapters.CheckboxUsersAdapter;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.Toaster;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SelectUsersActivity extends AppCompatActivity {

    public static final String TAG = SelectUsersActivity.class.getSimpleName();
    public static final String EXTRA_QB_USERS = "qb_users";
    public static final String EXTRA_QB_USER_PHOTO = "qb_user_photo";
    public static final String EXTRA_GROUP_NAME = "group_name";
    public static final int MINIMUM_CHAT_OCCUPANTS_SIZE = 2;
    private static final String EXTRA_QB_DIALOG = "qb_dialog";
    private static final long CLICK_DELAY = TimeUnit.SECONDS.toMillis(2);
    public static final String EDITED_USERS = "edited_users";
    @BindView(R.id.all_users_list)
    ListView usersListView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.layout_1)
    RelativeLayout layout1;
    QBFriendListHelper qbFriendListHelper;
    ArrayList<QBUser> qbUsers;
    private CheckboxUsersAdapter usersAdapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, SelectUsersActivity.class);
        context.startActivity(intent);
    }

    public static void startForResult(Activity activity, int code) {
        startForResult(activity, code, null);
    }

    public static void startForResult(Activity activity, int code, QBChatDialog dialog) {
        Intent intent = new Intent(activity, SelectUsersActivity.class);
        intent.putExtra(EXTRA_QB_DIALOG, dialog);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        ButterKnife.bind(this);

        qbFriendListHelper = new QBFriendListHelper(this);

        setActionBarTitle(R.string.select_users);

        if (!isEditMode()) {
            loadFriends();
        } else {
            loadFriendsEdit();
        }

    }

    private void loadFriendsEdit() {
        progressBar.setVisibility(View.VISIBLE);

        Collection<Integer> friendsIds = qbFriendListHelper.getAllFriends();

        QBUsers.getUsersByIDs(friendsIds, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> allUsers, Bundle params) {
                QBUsersHolder.getInstance().putUsers(allUsers);

                ArrayList<QBUser> qbUsersWithoutCurrent = new ArrayList<>();
                for (QBUser user : allUsers) {
                    if (!QBChatService.getInstance().getUser().equals(user))
                        qbUsersWithoutCurrent.add(user);
                }

                usersAdapter = new CheckboxUsersAdapter(SelectUsersActivity.this, qbUsersWithoutCurrent);

                ArrayList<Integer> userIds = new ArrayList<>();
                for (QBUser user: qbUsers) {
                    userIds.add(user.getId());
                }

                usersAdapter.addSelectedUsers(userIds);
                usersListView.setAdapter(usersAdapter);


                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.select_users_get_users_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadFriendsEdit();
                            }
                        });
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean isEditMode() {
        qbUsers = (ArrayList<QBUser>) getIntent().getSerializableExtra(CreateGroupActivity.EXISTING_USERS_LIST);
        return qbUsers != null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEditMode()) {
            getMenuInflater().inflate(R.menu.reset_pass_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.activity_select_users, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_action:
                if (usersAdapter != null) {
                    List<QBUser> users = new ArrayList<>(usersAdapter.getSelectedUsers());
                    if (users.size() >= MINIMUM_CHAT_OCCUPANTS_SIZE) {
                        passResultToCallerActivity();
                    } else {
                        Toaster.shortToast(R.string.select_users_choose_users);
                    }
                }
                return true;
            case R.id.action_proceed:
                ArrayList<QBUser> selectedUsers = new ArrayList<>(usersAdapter.getSelectedUsers());
                Intent result = new Intent();
                result.putExtra(EDITED_USERS,selectedUsers);
                setResult(RESULT_OK,result);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setActionBarTitle(int title) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }

    }


    private void passResultToCallerActivity() {
        Intent intent = new Intent(this, CreateGroupActivity.class);
        ArrayList<QBUser> selectedUsers = new ArrayList<>(usersAdapter.getSelectedUsers());
        intent.putExtra(CreateGroupActivity.USERS_ID_LIST, selectedUsers);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        finish();
    }

    private void loadFriends() {
        progressBar.setVisibility(View.VISIBLE);

        Collection<Integer> friendsIds = qbFriendListHelper.getAllFriends();

        QBUsers.getUsersByIDs(friendsIds, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                QBChatDialog dialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_QB_DIALOG);
                QBUsersHolder.getInstance().putUsers(users);
                usersAdapter = new CheckboxUsersAdapter(SelectUsersActivity.this, users);
                if (dialog != null) {
                    usersAdapter.addSelectedUsers(dialog.getOccupants());
                }

                usersListView.setAdapter(usersAdapter);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.select_users_get_users_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadFriends();
                            }
                        });
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(layout1, resId, e,
                R.string.dlg_retry, clickListener);
    }

}
