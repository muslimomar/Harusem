package com.example.william.harusem.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.william.harusem.R;
import com.example.william.harusem.adapters.ChatDialogsAdapter;
import com.example.william.harusem.common.Common;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.util.Extras;
import com.example.william.harusem.util.Helper;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static com.example.william.harusem.common.Common.DIALOG_EXTRA;

public class ChatDialogsActivity extends AppCompatActivity {

    public static final String TAG = ChatDialogsActivity.class.getSimpleName();
    @BindView(R.id.list_chat_dialogs)
    ListView listChatDialogs;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialogs);
        ButterKnife.bind(this);

        createChatSession();

        loadChatDialogs();

        listChatDialogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                QBChatDialog qbChatDialog = (QBChatDialog) listChatDialogs.getAdapter().getItem(i);
                Intent intent = new Intent(ChatDialogsActivity.this, ChatActivity.class);
                intent.putExtra(DIALOG_EXTRA,qbChatDialog);
                startActivity(intent);
            }
        });
    }

    private void loadChatDialogs() {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);

        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
                ChatDialogsAdapter chatDialogsAdapter = new ChatDialogsAdapter(getBaseContext(), qbChatDialogs);
                listChatDialogs.setAdapter(chatDialogsAdapter);
                chatDialogsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
            }
        });

    }

    private void createChatSession() {
        final ProgressDialog loadingPd = Helper.buildProgressDialog(this, "Please wait", "Loading.......", false);
        loadingPd.show();

        String user, password;
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");

        // load all user and save to cache
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

        final QBUser qbUser = new QBUser(user, password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        loadingPd.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e(TAG, "onError: ", e);
                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }


    @OnClick(R.id.fab)
    public void setFab(View view) {
        Intent intent = new Intent(this, AllUsersActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChatDialogs();
    }
}
