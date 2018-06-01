package com.example.william.harusem.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.william.harusem.R;
import com.example.william.harusem.activities.AllUsersActivity;
import com.example.william.harusem.activities.ChatActivity;
import com.example.william.harusem.adapters.ChatDialogsAdapter;
import com.example.william.harusem.holder.QBChatDialogHolder;
import com.example.william.harusem.holder.QBUnreadMessageHolder;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.util.Helper;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.auth.session.QBSessionParameters;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.william.harusem.common.Common.DIALOG_EXTRA;


public class ChatDialogsFragment extends Fragment implements QBSystemMessageListener, QBChatDialogMessageListener {

    public static final String TAG = "ChatDialogsFragment";
    @BindView(R.id.list_chat_dialogs)
    ListView listChatDialogs;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    Unbinder unbinder;
    ProgressDialog loadingPd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);
        setHasOptionsMenu(true);
        unbinder = ButterKnife.bind(this, rootView);
        loadingPd = Helper.buildProgressDialog(getContext(), "Please wait", "Loading.......", false);

        createChatSession();
        loadChatDialogs();


        listChatDialogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                QBChatDialog qbChatDialog = (QBChatDialog) listChatDialogs.getAdapter().getItem(i);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(DIALOG_EXTRA, qbChatDialog);
                startActivity(intent);
            }
        });


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Search");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_search_menu_item, menu);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void loadChatDialogs() {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);

        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
                // put all dialogs to cache
                QBChatDialogHolder.getInstance().putDialogs(qbChatDialogs);

                // unread settings
                Set<String> setIds = new HashSet<>();
                for (QBChatDialog chatDialog : qbChatDialogs) {
                    setIds.add(chatDialog.getDialogId());
                }

                // Get Message unread
                QBRestChatService.getTotalUnreadMessagesCount(setIds, QBUnreadMessageHolder.getInstance().getBundle())
                        .performAsync(new QBEntityCallback<Integer>() {
                            @Override
                            public void onSuccess(Integer integer, Bundle bundle) {
                                dismissDialog(loadingPd);

                                // save to cache
                                QBUnreadMessageHolder.getInstance().setBundle(bundle);

                                // Refresh dialogs list
                                ChatDialogsAdapter chatDialogsAdapter = new ChatDialogsAdapter(getContext(), QBChatDialogHolder.getInstance().getAllChatDialogs());
                                listChatDialogs.setAdapter(chatDialogsAdapter);
                                chatDialogsAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Log.e(TAG, "onError: ", e);
                            }
                        });

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
            }
        });


    }

    private void createChatSession() {
        loadingPd.show();

        String user, password;
        user = getActivity().getIntent().getStringExtra("user");
        password = getActivity().getIntent().getStringExtra("password");

        // load all user and save to cache
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
            }
        });

        final QBUser qbUser = new QBUser(user, password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(final QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        dismissDialog(loadingPd);

                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        qbSystemMessagesManager.addSystemMessageListener(ChatDialogsFragment.this);

                        QBIncomingMessagesManager qbIncomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
                        qbIncomingMessagesManager.addDialogMessageListener(ChatDialogsFragment.this);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e(TAG, "onError: ", e);
                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
            }
        });

    }

    private void dismissDialog(ProgressDialog loadingPd) {
        if (loadingPd != null && loadingPd.isShowing()) {
            loadingPd.dismiss();
        }
    }

    @OnClick(R.id.fab)
    public void setFab(View view) {
        Intent intent = new Intent(getActivity(), AllUsersActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChatDialogs();
    }

    @Override
    public void processMessage(QBChatMessage qbChatMessage) {

        // Put dialog to cache
        // Because we send system message with content which is DialogId
        // So we can get dialog by Dialog Id
        //TODO: should use getDialogId instaed of getbody()
        QBRestChatService.getChatDialogById(qbChatMessage.getBody()).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                // put to cache
                QBChatDialogHolder.getInstance().putDialog(qbChatDialog);
                ArrayList<QBChatDialog> adapterSource = QBChatDialogHolder.getInstance().getAllChatDialogs();
                ChatDialogsAdapter adapter = new ChatDialogsAdapter(getContext(), adapterSource);
                listChatDialogs.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
            }
        });

    }

    @Override
    public void processError(QBChatException e, QBChatMessage qbChatMessage) {
        Log.e(TAG, "processError: ", e);
    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        loadChatDialogs();
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.chat_dialog_context_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.context_delete_dialog:
                deleteDialog(info.position);
                break;
        }

        return true;

    }

    private void deleteDialog(int index) {
         QBChatDialog chatDialog = (QBChatDialog) listChatDialogs.getAdapter().getItem(index);
    QBRestChatService.deleteDialog(chatDialog.getDialogId(), false)
            .performAsync(new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {

                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
    }
}

