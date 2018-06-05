package com.example.william.harusem.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.william.harusem.R;
import com.example.william.harusem.activities.AllUsersActivity;
import com.example.william.harusem.activities.ChatActivity;
import com.example.william.harusem.adapters.ChatDialogsAdapter;
import com.example.william.harusem.holder.QBChatDialogHolder;
import com.example.william.harusem.holder.QBUnreadMessageHolder;
import com.example.william.harusem.util.Helper;
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
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;

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

    QBSystemMessagesManager systemMessagesManager;
    QBIncomingMessagesManager incomingMessagesManager;
    @BindView(R.id.filter_iv)
    ImageView filterIv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat_dialogs, container, false);
        setHasOptionsMenu(true);
        unbinder = ButterKnife.bind(this, rootView);
        loadingPd = Helper.buildProgressDialog(getContext(), "Please wait", "Loading.......", false);

        loadChatDialogs();

        registerForContextMenu(listChatDialogs);

        listChatDialogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                QBChatDialog qbChatDialog = (QBChatDialog) listChatDialogs.getAdapter().getItem(i);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(DIALOG_EXTRA, qbChatDialog);
                startActivity(intent);
            }
        });

        registerQBChatListener();

        return rootView;
    }

    private void registerQBChatListener() {
        incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
        systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();

        if (incomingMessagesManager != null) {
            incomingMessagesManager.addDialogMessageListener(this);
        }

        if (systemMessagesManager != null) {
            systemMessagesManager.addSystemMessageListener(this);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_search_menu_item, menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (incomingMessagesManager != null) {
            incomingMessagesManager.removeDialogMessageListrener(this);
        }

        if (systemMessagesManager != null) {
            systemMessagesManager.removeSystemMessageListener(this);
        }
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
                                refreshAdapter(QBChatDialogHolder.getInstance().getAllChatDialogs());
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                dismissDialog(loadingPd);
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

    private void refreshAdapter(ArrayList<QBChatDialog> allChatDialogs) {

        ChatDialogsAdapter mAdapter = new ChatDialogsAdapter(getContext(), allChatDialogs);
        if (getActivity() != null && isAdded()) {
            listChatDialogs.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
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
                refreshAdapter(adapterSource);
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
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.context_delete_dialog:
                deleteDialog(info.position);
                break;
        }

        return true;
    }

    private void deleteDialog(int index) {
        final QBChatDialog chatDialog = (QBChatDialog) listChatDialogs.getAdapter().getItem(index);
        QBRestChatService.deleteDialog(chatDialog.getDialogId(), false)
                .performAsync(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        QBChatDialogHolder.getInstance().removeDialog(chatDialog.getDialogId());
                        refreshAdapter(QBChatDialogHolder.getInstance().getAllChatDialogs());
                        Log.d(TAG, "onSuccess: dialog deletion success\n" + aVoid);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e(TAG, "onError: delete dialog", e);
                    }
                });
    }

    @OnClick(R.id.filter_iv)
    public void setFilterIv(View view) {

        String[] strings = new String[]{"name", "Latest Message", "A-Z", "Z-A"};

        new LovelyChoiceDialog(getActivity())
                .setTopColor(getResources().getColor(R.color.colorSecondary))
                .setTitle("Sort Chats By:")
                .setIcon(R.drawable.ic_filter_list_black_48dp)
                .setItems(strings, new LovelyChoiceDialog.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(int position, String item) {
                        switch (item) {
                            case "Latest Message":
                                Toast.makeText(getActivity(), "last message", Toast.LENGTH_SHORT).show();
                                filterDialogs();
                                break;
                            case "A-Z":
                                Toast.makeText(getActivity(), "A-Z", Toast.LENGTH_SHORT).show();
                                break;
                            case "Z-A":
                                Toast.makeText(getActivity(), "Z-A", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .show();


    }

    private void filterDialogs() {
        // TODO: to be finished
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);
//        requestBuilder.sor

    }

}

