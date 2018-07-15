package com.example.william.harusem.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.william.harusem.R;
import com.example.william.harusem.holder.QBChatDialogHolder;
import com.example.william.harusem.manager.DialogsManager;
import com.example.william.harusem.ui.activities.ChatActivity;
import com.example.william.harusem.ui.activities.SelectUsersActivity;
import com.example.william.harusem.ui.adapters.DialogsAdapter;
import com.example.william.harusem.ui.dialog.ProgressDialogFragment;
import com.example.william.harusem.util.ChatHelper;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.consts.GcmConsts;
import com.example.william.harusem.util.gcm.GooglePlayServicesHelper;
import com.example.william.harusem.util.qb.QbChatDialogMessageListenerImp;
import com.example.william.harusem.util.qb.callback.QbEntityCallbackImpl;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


public class DialogsFragment extends Fragment implements DialogsManager.ManagingDialogsCallbacks {

    private static final String TAG = DialogFragment.class.getSimpleName();
    private static final int REQUEST_SELECT_PEOPLE = 174;
    private static final int REQUEST_DIALOG_ID_FOR_UPDATE = 165;
    private static ArrayList<QBChatDialog> qbUserWithoutCurrent = new ArrayList<>();
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    /////
    @BindView(R.id.search_view_dialog)
    MaterialSearchView searchView;
    @BindView(R.id.toolbar_dialog)
    Toolbar toolbar;
    @BindView(R.id.list_chat_dialogs)
    ListView dialogsListView;
    @BindView(R.id.layout_root)
    RelativeLayout layoutRoot;
    Unbinder unbinder;
    private QBRequestGetBuilder requestBuilder;
    private Menu menu;
    private int skipRecords = 0;
    private boolean isProcessingResultInProgress;
    private BroadcastReceiver pushBroadcastReceiver;
    private GooglePlayServicesHelper googlePlayServicesHelper;
    private DialogsAdapter dialogsAdapter;
    private QBChatDialogMessageListener allDialogsMessagesListener;
    private SystemMessagesListener systemMessagesListener;
    private QBSystemMessagesManager systemMessagesManager;
    private QBIncomingMessagesManager incomingMessagesManager;
    private DialogsManager dialogsManager;
    private QBUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_dialogs, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        setHasOptionsMenu(true);

        googlePlayServicesHelper = new GooglePlayServicesHelper();

        pushBroadcastReceiver = new PushBroadcastReceiver();

        allDialogsMessagesListener = new AllDialogsMessageListener();
        systemMessagesListener = new SystemMessagesListener();

        dialogsManager = new DialogsManager();

        currentUser = ChatHelper.getCurrentUser();

        if (getActivity() != null && isAdded()) {

            initUi();

            registerQbChatListeners();

            if (QBChatDialogHolder.getInstance().getDialogs().size() > 0) {
                loadDialogsFromQb(true, true);
            } else {
                loadDialogsFromQb(false, true);
            }

            registerForContextMenu(dialogsListView);
        }


        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

                qbUserWithoutCurrent = new ArrayList<QBChatDialog>(QBChatDialogHolder.getInstance().getDialogs().values());
                DialogsAdapter adapter = new DialogsAdapter(getContext(), qbUserWithoutCurrent);
                dialogsListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onSearchViewClosed() {

                qbUserWithoutCurrent = new ArrayList<QBChatDialog>(QBChatDialogHolder.getInstance().getDialogs().values());
                DialogsAdapter adapter = new DialogsAdapter(getContext(), qbUserWithoutCurrent);
                dialogsListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {

                    qbUserWithoutCurrent = new ArrayList<QBChatDialog>(QBChatDialogHolder.getInstance().getDialogs().values());

                    ArrayList<QBChatDialog> lstFound = new ArrayList<>();
                    for (QBChatDialog item : qbUserWithoutCurrent) {
                        if (item.getName().toLowerCase().contains(query.toLowerCase()))
                            lstFound.add(item);

                    }

                    DialogsAdapter adapter = new DialogsAdapter(getContext(), lstFound);
                    dialogsListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                } else {
                    // if search text is null
                    // return default
                    DialogsAdapter adapter = new DialogsAdapter(getContext(), qbUserWithoutCurrent);
                    dialogsListView.setAdapter(adapter);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                if (newText != null && !newText.isEmpty()) {

                    qbUserWithoutCurrent = new ArrayList<QBChatDialog>(QBChatDialogHolder.getInstance().getDialogs().values());

                    ArrayList<QBChatDialog> lstFound = new ArrayList<>();
                    for (QBChatDialog item : qbUserWithoutCurrent) {
                        if (item.getName().toLowerCase().contains(newText.toLowerCase()))
                            lstFound.add(item);

                    }

                    DialogsAdapter adapter = new DialogsAdapter(getContext(), lstFound);
                    dialogsListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                } else {
                    // if search text is null
                    // return default
                    DialogsAdapter adapter = new DialogsAdapter(getContext(), qbUserWithoutCurrent);
                    dialogsListView.setAdapter(adapter);
                }
                return true;
            }
        });

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dialog_search_menu_item, menu);
        MenuItem item = menu.findItem(R.id.action_search_dialog);
        searchView.setMenuItem(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_dialog:
//                Toast.makeText(getActivity(), "Add Users", Toast.LENGTH_SHORT).show();
                searchView.setMenuItem(item);
                break;
            case R.id.new_group_item:
                startActivity(new Intent(getActivity(), SelectUsersActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
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
                QBChatDialog dialog = (QBChatDialog) dialogsListView.getItemAtPosition(info.position);
                deleteDialog(dialog);

                break;
        }

        return true;
    }


    private void deleteDialog(final QBChatDialog dialog) {

        ChatHelper.getInstance().deleteDialog(dialog, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatDialogHolder.getInstance().deleteDialog(dialog);
                updateDialogsAdapter();
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.dialogs_deletion_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteDialog(dialog);
                            }
                        });
            }
        });
    }

    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(layoutRoot, resId, e,
                R.string.dlg_retry, clickListener);
    }


    @Override
    public void onResume() {
        super.onResume();

        googlePlayServicesHelper.checkPlayServicesAvailable(getActivity());

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(pushBroadcastReceiver,
                new IntentFilter(GcmConsts.ACTION_NEW_GCM_EVENT));
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(pushBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterQbChatListeners();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            isProcessingResultInProgress = true;
            if (requestCode == REQUEST_SELECT_PEOPLE) {
                ArrayList<QBUser> selectedUsers = (ArrayList<QBUser>) data
                        .getSerializableExtra(SelectUsersActivity.EXTRA_QB_USERS);

                if (isPrivateDialogExist(selectedUsers)) {
                    selectedUsers.remove(ChatHelper.getCurrentUser());
                    QBChatDialog existingPrivateDialog = QBChatDialogHolder.getInstance().getPrivateDialogWithUser(selectedUsers.get(0));
                    isProcessingResultInProgress = false;
                    startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog);
                } else {
                    ProgressDialogFragment.show(getActivity().getSupportFragmentManager(), R.string.create_chat);
                    createDialog(selectedUsers);
                }
            } else if (requestCode == REQUEST_DIALOG_ID_FOR_UPDATE) {
                if (data != null) {
                    String dialogId = data.getStringExtra(ChatActivity.EXTRA_DIALOG_ID);
                    loadUpdatedDialog(dialogId);
                } else {
                    isProcessingResultInProgress = false;
                    updateDialogsList();
                }
            }
        } else {
            updateDialogsAdapter();
        }
    }

    private boolean isPrivateDialogExist(ArrayList<QBUser> allSelectedUsers) {
        ArrayList<QBUser> selectedUsers = new ArrayList<>();
        selectedUsers.addAll(allSelectedUsers);
        selectedUsers.remove(ChatHelper.getCurrentUser());
        return selectedUsers.size() == 1 && QBChatDialogHolder.getInstance().hasPrivateDialogWithUser(selectedUsers.get(0));
    }

    private void loadUpdatedDialog(String dialogId) {
        ChatHelper.getInstance().getDialogById(dialogId, new QbEntityCallbackImpl<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog result, Bundle bundle) {
                isProcessingResultInProgress = false;
                QBChatDialogHolder.getInstance().putDialog(result);
                updateDialogsAdapter();
            }

            @Override
            public void onError(QBResponseException e) {
                isProcessingResultInProgress = false;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    private void updateDialogsList() {
        requestBuilder.setSkip(skipRecords = 0);
        loadDialogsFromQb(true, true);
    }


    private void initUi() {

        dialogsAdapter = new DialogsAdapter(getActivity(),
                new ArrayList<QBChatDialog>(QBChatDialogHolder.getInstance().getDialogs().values()));

        dialogsListView.setAdapter(dialogsAdapter);
        dialogsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                QBChatDialog selectedDialog = (QBChatDialog) adapterView.getItemAtPosition(i);
                startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, selectedDialog);
            }
        });

        requestBuilder = new QBRequestGetBuilder();


        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestBuilder.setSkip(skipRecords += ChatHelper.DIALOG_ITEMS_PER_PAGE);
                loadDialogsFromQb(true, false);
            }
        });
    }


    private void registerQbChatListeners() {
        incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
        systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();

        if (incomingMessagesManager != null) {
            incomingMessagesManager.addDialogMessageListener(allDialogsMessagesListener != null
                    ? allDialogsMessagesListener : new AllDialogsMessageListener());
        }

        if (systemMessagesManager != null) {
            systemMessagesManager.addSystemMessageListener(systemMessagesListener != null
                    ? systemMessagesListener : new SystemMessagesListener());
        }

        dialogsManager.addManagingDialogsCallbackListener(this);
    }

    private void unregisterQbChatListeners() {
        if (incomingMessagesManager != null) {
            incomingMessagesManager.removeDialogMessageListrener(allDialogsMessagesListener);
        }

        if (systemMessagesManager != null) {
            systemMessagesManager.removeSystemMessageListener(systemMessagesListener);
        }

        dialogsManager.removeManagingDialogsCallbackListener(this);
    }


    private void createDialog(final ArrayList<QBUser> selectedUsers) {
        ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers,
                new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {
                        isProcessingResultInProgress = false;
                        dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, dialog);
                        startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, dialog);
                        ProgressDialogFragment.hide(getActivity().getSupportFragmentManager());
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        isProcessingResultInProgress = false;
                        ProgressDialogFragment.hide(getActivity().getSupportFragmentManager());
                        showErrorSnackbar(R.string.dialogs_creation_error, null, null);
                    }
                }
        );
    }

    private void loadDialogsFromQb(final boolean silentUpdate, final boolean clearDialogHolder) {
        isProcessingResultInProgress = true;
        if (!silentUpdate) {
            progressBar.setVisibility(View.VISIBLE);
        }

        ChatHelper.getInstance().getDialogs(requestBuilder, new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> dialogs, Bundle bundle) {
                if (getActivity() != null && isAdded()) {
                    isProcessingResultInProgress = false;
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    if (clearDialogHolder) {
                        QBChatDialogHolder.getInstance().clear();
                    }
                    QBChatDialogHolder.getInstance().putDialogs(dialogs);
                    updateDialogsAdapter();
                }
            }

            @Override
            public void onError(QBResponseException e) {
                isProcessingResultInProgress = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDialogsAdapter() {
        dialogsAdapter.updateList(new ArrayList<>(QBChatDialogHolder.getInstance().getDialogs().values()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onDialogCreated(QBChatDialog chatDialog) {
        updateDialogsAdapter();
    }

    @Override
    public void onDialogUpdated(String chatDialog) {
        updateDialogsAdapter();
    }

    @Override
    public void onNewDialogLoaded(QBChatDialog chatDialog) {
        updateDialogsAdapter();
    }

    public void startForResult(Activity activity, int code, QBChatDialog dialogId) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DIALOG, dialogId);
        startActivityForResult(intent, code);
    }

    public class PushBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra(GcmConsts.EXTRA_GCM_MESSAGE);
            Log.v(TAG, "Received broadcast " + intent.getAction() + " with data: " + message);
            requestBuilder.setSkip(skipRecords = 0);
            loadDialogsFromQb(true, true);
        }
    }

    private class SystemMessagesListener implements QBSystemMessageListener {
        @Override
        public void processMessage(final QBChatMessage qbChatMessage) {
            dialogsManager.onSystemMessageReceived(qbChatMessage);
        }

        @Override
        public void processError(QBChatException e, QBChatMessage qbChatMessage) {

        }
    }

    private class AllDialogsMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(final String dialogId, final QBChatMessage qbChatMessage, Integer senderId) {
            if (!senderId.equals(ChatHelper.getCurrentUser().getId())) {
                dialogsManager.onGlobalMessageReceived(dialogId, qbChatMessage);
            }
        }
    }

}