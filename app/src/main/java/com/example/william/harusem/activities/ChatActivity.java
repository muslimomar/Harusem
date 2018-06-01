package com.example.william.harusem.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.william.harusem.R;
import com.example.william.harusem.adapters.MessagesAdapter;
import com.example.william.harusem.common.Common;
import com.example.william.harusem.holder.QBChatMessagesHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogParticipantListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.william.harusem.common.Common.UPDATE_ADD_MODE;
import static com.example.william.harusem.common.Common.UPDATE_DIALOG_EXTRA;
import static com.example.william.harusem.common.Common.UPDATE_MODE;
import static com.example.william.harusem.common.Common.UPDATE_REMOVE_MODE;


public class ChatActivity extends AppCompatActivity implements QBChatDialogMessageListener {

    public static final String TAG = ChatActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottom_bar)
    LinearLayout bottomBar;
    @BindView(R.id.small_toolbar)
    Toolbar smallToolbar;
    @BindView(R.id.chat_recycler_view)
    RecyclerView chatRecyclerView;
    @BindView(R.id.message_input_et)
    EditText messageInputEt;
    @BindView(R.id.status_sign_iv)
    ImageView statusSignIv;
    @BindView(R.id.send_txt_btn)
    ImageButton sendTxtBtn;
    @BindView(R.id.friend_name_tv)
    TextView friendNameTv;
    @BindView(R.id.send_img_btn)
    ImageButton sendImgBtn;
    QBChatDialog receivedQBChatDialog;
    MessagesAdapter adapter;
    @BindView(R.id.dialog_info) LinearLayout dialogInfoLayout;
    @BindView(R.id.img_online_ocunt) ImageView imageOnlineCount;
    @BindView(R.id.txt_online_count) TextView textOnlineCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        hideKeyboard();
        configRecyclerView();
        initChatDialogs();

        retrieveMessage();

        messageInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) {
                    sendImgBtn.setVisibility(View.VISIBLE);
                    sendTxtBtn.setVisibility(View.GONE);
                } else {
                    sendImgBtn.setVisibility(View.GONE);
                    sendTxtBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(receivedQBChatDialog.getType() == QBDialogType.PRIVATE) {
            dialogInfoLayout.setVisibility(View.GONE);
        }else{
            dialogInfoLayout.setVisibility(View.VISIBLE);
        }

    }

    private void retrieveMessage() {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500);

        if (receivedQBChatDialog != null) {

            QBRestChatService.getDialogMessages(receivedQBChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    // save message to cache and refresh the list view
                    QBChatMessagesHolder.getInstance().putMessages(receivedQBChatDialog.getDialogId(), qbChatMessages);

                    adapter = new MessagesAdapter(qbChatMessages);
                    chatRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onError(QBResponseException e) {
                    Toast.makeText(ChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    private void initChatDialogs() {

        receivedQBChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.DIALOG_EXTRA);
        receivedQBChatDialog.initForChat(QBChatService.getInstance());

        // set listener for incoming messages
        QBIncomingMessagesManager incomingMessage = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessage.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
            }
        });

        // add join group to enable group chat
        if (receivedQBChatDialog.getType() == QBDialogType.GROUP || receivedQBChatDialog.getType() == QBDialogType.PUBLIC_GROUP) {

            DiscussionHistory discussionHistory = new DiscussionHistory();
            discussionHistory.setMaxStanzas(0);

            receivedQBChatDialog.join(discussionHistory, new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {

                }

                @Override
                public void onError(QBResponseException e) {
                    Toast.makeText(ChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    Log.e(TAG, "onError: ", e);
                }
            });
        }

        final QBChatDialogParticipantListener participantListener = new QBChatDialogParticipantListener() {
            @Override
            public void processPresence(String dialogId, QBPresence qbPresence) {
        // TODO: replace with .equals
                if(dialogId == receivedQBChatDialog.getDialogId() ) {
                    QBRestChatService.getChatDialogById(dialogId).performAsync(new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {

                            try {
                                Collection<Integer> onlineList = qbChatDialog.getOnlineUsers();
                                TextDrawable.IBuilder builder = TextDrawable.builder()
                                        .beginConfig()
                                        .withBorder(4)
                                        .endConfig()
                                        .round();

                                TextDrawable online = builder.build("", Color.RED);
                                imageOnlineCount.setImageDrawable(online);

                                textOnlineCount.setText(String.format("%d/%d online", onlineList.size(), qbChatDialog.getOccupants().size()));


                            } catch (XMPPException e) {
                                e.printStackTrace();
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
                }

            }
        };

        receivedQBChatDialog.addParticipantListener(participantListener);
        receivedQBChatDialog.addMessageListener(this);


        friendNameTv.setText(receivedQBChatDialog.getName());



    }


    private void configRecyclerView() {
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setHasFixedSize(true);
    }


    private void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @OnClick(R.id.send_txt_btn)
    public void sendImgBtn(View view) {

        if (messageInputEt.getText().toString().trim().isEmpty()) {
            return;
        }

        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(messageInputEt.getText().toString().trim());
        chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
        chatMessage.setSaveToHistory(true);

        try {
            receivedQBChatDialog.sendMessage(chatMessage);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        // Fix private chat don't show messages
        if (receivedQBChatDialog.getType() == QBDialogType.PRIVATE) {
            // Cache message
            QBChatMessagesHolder.getInstance().putMessage(receivedQBChatDialog.getDialogId(), chatMessage);
            ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(chatMessage.getDialogId());
            adapter = new MessagesAdapter(messages);
            chatRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }


        messageInputEt.setText("");
        messageInputEt.setFocusable(true);


    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        QBChatMessagesHolder.getInstance().putMessage(qbChatMessage.getDialogId(), qbChatMessage);
        ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatMessage.getDialogId());
        adapter = new MessagesAdapter(messages);
        chatRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
        Log.e(TAG, "processError: ", e);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        receivedQBChatDialog.removeMessageListrener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        receivedQBChatDialog.removeMessageListrener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (receivedQBChatDialog.getType() == QBDialogType.GROUP || receivedQBChatDialog.getType() == QBDialogType.PUBLIC_GROUP)
            getMenuInflater().inflate(R.menu.chat_message_group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_group_edit_name:
                editGroupName();
                break;
            case R.id.chat_group_add_user:
                addUser();
                break;
            case R.id.chat_group_remove_user:
                removeUser();
                break;
        }
        return true;
    }

    private void removeUser() {
        Intent intent = new Intent(this, AllUsersActivity.class);
        intent.putExtra(UPDATE_DIALOG_EXTRA, receivedQBChatDialog);
        intent.putExtra(UPDATE_MODE, UPDATE_REMOVE_MODE);
        startActivity(intent);
    }

    private void addUser() {
        Intent intent = new Intent(this, AllUsersActivity.class);
        intent.putExtra(UPDATE_DIALOG_EXTRA, receivedQBChatDialog);
        intent.putExtra(UPDATE_MODE, UPDATE_ADD_MODE);
        startActivity(intent);

    }

    private void editGroupName() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.edit_group_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(view);
        final EditText groupName = view.findViewById(R.id.group_name_et);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        receivedQBChatDialog.setName(groupName.getText().toString().trim());

                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                        QBRestChatService.updateChatDialog(receivedQBChatDialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                Toast.makeText(ChatActivity.this, "", Toast.LENGTH_SHORT).show();
                                friendNameTv.setText(qbChatDialog.getName());
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Toast.makeText(ChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        // Create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

}
