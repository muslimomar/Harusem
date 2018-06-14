package com.example.william.harusem.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.example.william.harusem.R;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.ui.adapters.UsersAdapter;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.users.model.QBUser;

import java.util.List;

//import com.quickblox.sample.chat.R;
//import com.quickblox.sample.chat.ui.adapter.UsersAdapter;
//import com.quickblox.sample.chat.utils.qb.QbUsersHolder;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatInfoActivity extends AppCompatActivity {
    private static final String EXTRA_DIALOG = "dialog";
    @BindView(R.id.list_chat_info_users)
    ListView usersListView;

    private QBChatDialog qbDialog;

    public static void start(Context context, QBChatDialog qbDialog) {
        Intent intent = new Intent(context, ChatInfoActivity.class);
        intent.putExtra(EXTRA_DIALOG, qbDialog);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);
        ButterKnife.bind(this);

        qbDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        buildUserList();
    }

    private void buildUserList() {
        List<Integer> userIds = qbDialog.getOccupants();
        List<QBUser> users = QBUsersHolder.getInstance().getUsersByIds(userIds);

        UsersAdapter adapter = new UsersAdapter(users, this);
//        usersListView.setAdapter(adapter);

    }
}
