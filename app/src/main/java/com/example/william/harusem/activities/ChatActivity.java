package com.example.william.harusem.activities;import android.app.ProgressDialog;import android.content.DialogInterface;import android.content.Intent;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.graphics.Color;import android.net.Uri;import android.os.Bundle;import android.os.Environment;import android.os.Handler;import android.support.v4.app.NavUtils;import android.support.v7.app.AlertDialog;import android.support.v7.app.AppCompatActivity;import android.support.v7.widget.LinearLayoutManager;import android.support.v7.widget.RecyclerView;import android.support.v7.widget.Toolbar;import android.text.Editable;import android.text.TextWatcher;import android.util.Log;import android.view.LayoutInflater;import android.view.Menu;import android.view.MenuItem;import android.view.View;import android.view.WindowManager;import android.widget.EditText;import android.widget.ImageButton;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.TextView;import android.widget.Toast;import com.amulyakhare.textdrawable.TextDrawable;import com.bhargavms.dotloader.DotLoader;import com.example.william.harusem.R;import com.example.william.harusem.adapters.MessagesAdapter;import com.example.william.harusem.common.Common;import com.example.william.harusem.holder.QBChatMessagesHolder;import com.example.william.harusem.util.Helper;import com.quickblox.chat.QBChatService;import com.quickblox.chat.QBIncomingMessagesManager;import com.quickblox.chat.QBRestChatService;import com.quickblox.chat.exception.QBChatException;import com.quickblox.chat.listeners.QBChatDialogMessageListener;import com.quickblox.chat.listeners.QBChatDialogParticipantListener;import com.quickblox.chat.listeners.QBChatDialogTypingListener;import com.quickblox.chat.model.QBChatDialog;import com.quickblox.chat.model.QBChatMessage;import com.quickblox.chat.model.QBDialogType;import com.quickblox.chat.model.QBPresence;import com.quickblox.chat.request.QBDialogRequestBuilder;import com.quickblox.chat.request.QBMessageGetBuilder;import com.quickblox.content.QBContent;import com.quickblox.content.model.QBFile;import com.quickblox.core.QBEntityCallback;import com.quickblox.core.exception.QBResponseException;import com.quickblox.core.request.QBRequestUpdateBuilder;import com.squareup.picasso.Picasso;import org.jivesoftware.smack.SmackException;import org.jivesoftware.smack.XMPPException;import org.jivesoftware.smackx.muc.DiscussionHistory;import java.io.ByteArrayOutputStream;import java.io.File;import java.io.FileNotFoundException;import java.io.FileOutputStream;import java.io.IOException;import java.io.InputStream;import java.util.ArrayList;import java.util.Collection;import butterknife.BindView;import butterknife.ButterKnife;import butterknife.OnClick;import de.hdodenhof.circleimageview.CircleImageView;import static com.example.william.harusem.common.Common.UPDATE_ADD_MODE;import static com.example.william.harusem.common.Common.UPDATE_DIALOG_EXTRA;import static com.example.william.harusem.common.Common.UPDATE_MODE;import static com.example.william.harusem.common.Common.UPDATE_REMOVE_MODE;public class ChatActivity extends AppCompatActivity implements QBChatDialogMessageListener {    public static final String TAG = ChatActivity.class.getSimpleName();    private static final int REQUEST_CODE = 40;    @BindView(R.id.toolbar)    Toolbar toolbar;    @BindView(R.id.bottom_bar)    LinearLayout bottomBar;    @BindView(R.id.small_toolbar)    Toolbar smallToolbar;    @BindView(R.id.chat_recycler_view)    RecyclerView chatListRecyclerView;    @BindView(R.id.message_input_et)    EditText messageInputEt;    @BindView(R.id.status_sign_iv)    ImageView statusSignIv;    @BindView(R.id.send_txt_btn)    ImageButton sendTxtBtn;    @BindView(R.id.friend_name_tv)    TextView friendNameTv;    @BindView(R.id.send_img_btn)    ImageButton sendImgBtn;    QBChatDialog receivedQBChatDialog;    MessagesAdapter adapter;    @BindView(R.id.dialog_info)    LinearLayout dialogInfoLayout;    @BindView(R.id.img_online_ocunt)    ImageView imageOnlineCount;    @BindView(R.id.txt_online_count)    TextView textOnlineCount;    @BindView(R.id.dialog_avatar)    CircleImageView dialogAvatar;    @BindView(R.id.dot_loader)    DotLoader dotLoader;    long TYPING_TIME = 2500;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_chat);        ButterKnife.bind(this);        setSupportActionBar(toolbar);        hideKeyboard();        configRecyclerView();        initChatDialogs();        retrieveMessage();//        setPrivateRecipientStatus();        messageInputEt.addTextChangedListener(new TextWatcher() {            @Override            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {            }            @Override            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {                if (charSequence.toString().trim().isEmpty()) {                    sendImgBtn.setVisibility(View.VISIBLE);                    sendTxtBtn.setVisibility(View.GONE);                } else {                    sendImgBtn.setVisibility(View.GONE);                    sendTxtBtn.setVisibility(View.VISIBLE);                }            }            @Override            public void afterTextChanged(Editable editable) {            }        });        if (receivedQBChatDialog.getType() == QBDialogType.PRIVATE) {            dialogInfoLayout.setVisibility(View.GONE);        } else {            dialogInfoLayout.setVisibility(View.VISIBLE);        }        messageInputEt.addTextChangedListener(new TextWatcher() {            @Override            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {            }            @Override            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {                Log.d(TAG, "onTextChanged: ");                try {                    receivedQBChatDialog.sendIsTypingNotification();                } catch (XMPPException e) {                    e.printStackTrace();                } catch (SmackException.NotConnectedException e) {                    e.printStackTrace();                }            }            @Override            public void afterTextChanged(Editable editable) {                Log.d(TAG, "afterTextChanged: ");                try {                    receivedQBChatDialog.sendStopTypingNotification();                } catch (XMPPException e) {                    e.printStackTrace();                } catch (SmackException.NotConnectedException e) {                    e.printStackTrace();                }            }        });    }    private void setPrivateRecipientStatus() {        statusSignIv.setColorFilter(Color.parseColor("#00c853"));        statusSignIv.setColorFilter(Color.parseColor("#d50000"));    }    private void retrieveMessage() {        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();        messageGetBuilder.setLimit(500);        if (receivedQBChatDialog != null) {            QBRestChatService.getDialogMessages(receivedQBChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {                @Override                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {                    // save message to cache and refresh the list view                    QBChatMessagesHolder.getInstance().putMessages(receivedQBChatDialog.getDialogId(), qbChatMessages);                    adapter = new MessagesAdapter(qbChatMessages);                    chatListRecyclerView.setAdapter(adapter);                    adapter.notifyDataSetChanged();                }                @Override                public void onError(QBResponseException e) {                    Toast.makeText(ChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();                }            });        }    }    private void initChatDialogs() {        receivedQBChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.DIALOG_EXTRA);        if (receivedQBChatDialog.getPhoto() != null && !receivedQBChatDialog.getPhoto().equals("null")) {            QBContent.getFile(Integer.parseInt(receivedQBChatDialog.getPhoto()))                    .performAsync(new QBEntityCallback<QBFile>() {                        @Override                        public void onSuccess(QBFile qbFile, Bundle bundle) {                            String fileURL = qbFile.getPublicUrl();                            Picasso.get()                                    .load(fileURL)                                    .resize(50, 50)                                    .centerCrop()                                    .into(dialogAvatar);                        }                        @Override                        public void onError(QBResponseException e) {                            Log.e(TAG, "onError: ", e);                        }                    });        }        receivedQBChatDialog.initForChat(QBChatService.getInstance());        // set listener for incoming messages        QBIncomingMessagesManager incomingMessage = QBChatService.getInstance().getIncomingMessagesManager();        incomingMessage.addDialogMessageListener(new QBChatDialogMessageListener() {            @Override            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {                chatListRecyclerView.scrollToPosition(adapter.getItemCount() - 1);                hideTypingDots();                Log.e(TAG, "processUserIsTyping processMessage: ");            }            @Override            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {                Log.e(TAG, "processError: ", e);            }        });        registerTypingForChatDialog(receivedQBChatDialog);        // add join group to enable group chat        if (receivedQBChatDialog.getType() == QBDialogType.GROUP || receivedQBChatDialog.getType() == QBDialogType.PUBLIC_GROUP) {            DiscussionHistory discussionHistory = new DiscussionHistory();            discussionHistory.setMaxStanzas(0);            receivedQBChatDialog.join(discussionHistory, new QBEntityCallback() {                @Override                public void onSuccess(Object o, Bundle bundle) {                }                @Override                public void onError(QBResponseException e) {                    Log.e(TAG, "onError: ", e);                }            });        }        QBChatDialogParticipantListener participantListener = new QBChatDialogParticipantListener() {            @Override            public void processPresence(String dialogId, QBPresence qbPresence) {                if (dialogId == receivedQBChatDialog.getDialogId()) {                    QBRestChatService.getChatDialogById(dialogId).performAsync(new QBEntityCallback<QBChatDialog>() {                        @Override                        public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {                            try {                                Collection<Integer> onlineList = qbChatDialog.getOnlineUsers();                                TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig().withBorder(4).endConfig().round();                                TextDrawable online = builder.build("", Color.RED);                                imageOnlineCount.setImageDrawable(online);                                textOnlineCount.setText(String.format("%d/%d online", onlineList.size(), qbChatDialog.getOccupants().size()));                            } catch (XMPPException e) {                                e.printStackTrace();                            } catch (SmackException.NotConnectedException e) {                                e.printStackTrace();                            }                        }                        @Override                        public void onError(QBResponseException e) {                            Log.e(TAG, "onError: participiant listener", e);                        }                    });                }            }        };        receivedQBChatDialog.addParticipantListener(participantListener);        receivedQBChatDialog.addMessageListener(this);        friendNameTv.setText(receivedQBChatDialog.getName());    }    private void registerTypingForChatDialog(QBChatDialog receivedQBChatDialog) {        QBChatDialogTypingListener typingListener = new QBChatDialogTypingListener() {            long currentTime;            @Override            public void processUserIsTyping(String s, Integer integer) {                currentTime = System.currentTimeMillis();                showTypingDots();                Log.d(TAG, "processUserIsTyping: +");            }            @Override            public void processUserStopTyping(String s, Integer integer) {                Log.d(TAG, "processUserStopTyping: -");                final Handler handler = new Handler();                handler.postDelayed(new Runnable() {                    @Override                    public void run() {                        long l = System.currentTimeMillis() - currentTime;                        if (l >= TYPING_TIME) {                            hideTypingDots();                            Log.e(TAG, "run: " );                        }                    }                }, TYPING_TIME);            }        };        receivedQBChatDialog.addIsTypingListener(typingListener);    }    private void hideTypingDots() {        if (dotLoader.getVisibility() != View.INVISIBLE) {            dotLoader.setVisibility(View.INVISIBLE);        }    }    private void showTypingDots() {        if (dotLoader.getVisibility() != View.VISIBLE) {            dotLoader.setVisibility(View.VISIBLE);        }    }    private void configRecyclerView() {        chatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));        chatListRecyclerView.setHasFixedSize(true);    }    private void hideKeyboard() {        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);    }    @OnClick(R.id.send_txt_btn)    public void sendImgBtn(View view) {        if (messageInputEt.getText().toString().trim().isEmpty()) {            return;        }        Log.e(TAG, "sendImgBtn: hidden" );        QBChatMessage chatMessage = new QBChatMessage();        chatMessage.setBody(messageInputEt.getText().toString().trim());        chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());        chatMessage.setSaveToHistory(true);        try {            receivedQBChatDialog.sendMessage(chatMessage);        } catch (SmackException.NotConnectedException e) {            e.printStackTrace();        }        // Fix private chat don't show messages        if (receivedQBChatDialog.getType() == QBDialogType.PRIVATE) {            // Cache message            QBChatMessagesHolder.getInstance().putMessage(receivedQBChatDialog.getDialogId(), chatMessage);            ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(chatMessage.getDialogId());            adapter = new MessagesAdapter(messages);            chatListRecyclerView.setAdapter(adapter);            adapter.notifyDataSetChanged();        }        chatListRecyclerView.scrollToPosition(adapter.getItemCount() - 1);        messageInputEt.setText("");        messageInputEt.setFocusable(true);    }    @Override    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {        QBChatMessagesHolder.getInstance().putMessage(qbChatMessage.getDialogId(), qbChatMessage);        ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatMessage.getDialogId());        adapter = new MessagesAdapter(messages);        chatListRecyclerView.setAdapter(adapter);        adapter.notifyDataSetChanged();    }    @Override    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {        Log.e(TAG, "processError: ", e);    }    @Override    protected void onDestroy() {        super.onDestroy();        receivedQBChatDialog.removeMessageListrener(this);    }    @Override    protected void onStop() {        super.onStop();        receivedQBChatDialog.removeMessageListrener(this);    }    @Override    public boolean onCreateOptionsMenu(Menu menu) {        if (receivedQBChatDialog.getType() == QBDialogType.GROUP || receivedQBChatDialog.getType() == QBDialogType.PUBLIC_GROUP)            getMenuInflater().inflate(R.menu.chat_message_group_menu, menu);        return true;    }    @Override    public boolean onOptionsItemSelected(MenuItem item) {        switch (item.getItemId()) {            case R.id.chat_group_edit_name:                editGroupName();                break;            case R.id.chat_group_add_user:                addUser();                break;            case R.id.chat_group_remove_user:                removeUser();                break;        }        return true;    }    private void removeUser() {        Intent intent = new Intent(this, AllUsersActivity.class);        intent.putExtra(UPDATE_DIALOG_EXTRA, receivedQBChatDialog);        intent.putExtra(UPDATE_MODE, UPDATE_REMOVE_MODE);        startActivity(intent);    }    private void addUser() {        Intent intent = new Intent(this, AllUsersActivity.class);        intent.putExtra(UPDATE_DIALOG_EXTRA, receivedQBChatDialog);        intent.putExtra(UPDATE_MODE, UPDATE_ADD_MODE);        startActivity(intent);    }    private void editGroupName() {        //TODO: show group name then edit it        LayoutInflater inflater = LayoutInflater.from(this);        View view = inflater.inflate(R.layout.edit_group_dialog, null);        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);        alertDialogBuilder.setView(view);        final EditText groupName = view.findViewById(R.id.group_name_et);        alertDialogBuilder.setCancelable(false)                .setPositiveButton("OK", new DialogInterface.OnClickListener() {                    @Override                    public void onClick(DialogInterface dialogInterface, int i) {                        receivedQBChatDialog.setName(groupName.getText().toString().trim());                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();                        QBRestChatService.updateChatDialog(receivedQBChatDialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {                            @Override                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {                                Toast.makeText(ChatActivity.this, "", Toast.LENGTH_SHORT).show();                                friendNameTv.setText(qbChatDialog.getName());                            }                            @Override                            public void onError(QBResponseException e) {                                Toast.makeText(ChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();                            }                        });                    }                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {            @Override            public void onClick(DialogInterface dialogInterface, int i) {                dialogInterface.cancel();            }        });        // Create alert dialog        AlertDialog alertDialog = alertDialogBuilder.create();        alertDialog.show();    }    @OnClick(R.id.dialog_avatar)    public void setDialogAvatar(View view) {        Intent selectImage = new Intent();        selectImage.setType("image/*");        selectImage.setAction(Intent.ACTION_GET_CONTENT);        startActivityForResult(Intent.createChooser(selectImage, "Select a picture"), REQUEST_CODE);    }    @Override    protected void onActivityResult(int requestCode, int resultCode, Intent data) {        if (resultCode == RESULT_OK) {            if (requestCode == REQUEST_CODE) {                Uri selectedImage = data.getData();                final ProgressDialog progressDialog = Helper.buildProgressDialog(this, "", "Please Wait...", false);                progressDialog.show();                try {                    InputStream inputStream = getContentResolver().openInputStream(selectedImage);                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);                    ByteArrayOutputStream bos = new ByteArrayOutputStream();                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);                    File file = new File(Environment.getExternalStorageDirectory() + "/image.png");                    FileOutputStream fileOut = new FileOutputStream(file);                    fileOut.write(bos.toByteArray());                    fileOut.close();                    int imageSizeKb = (int) file.length() / 1024;                    if (imageSizeKb >= (1024 * 100)) {                        Toast.makeText(this, "Error size", Toast.LENGTH_SHORT).show();                        return;                    }                    QBContent.uploadFileTask(file, true, null)                            .performAsync(new QBEntityCallback<QBFile>() {                                @Override                                public void onSuccess(QBFile qbFile, Bundle bundle) {                                    receivedQBChatDialog.setPhoto(qbFile.getId().toString());                                    //Update Chat Dialog                                    QBRequestUpdateBuilder requestBuilder = new QBDialogRequestBuilder();                                    QBRestChatService.updateGroupChatDialog(receivedQBChatDialog, requestBuilder)                                            .performAsync(new QBEntityCallback<QBChatDialog>() {                                                @Override                                                public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {                                                    progressDialog.dismiss();                                                    dialogAvatar.setImageBitmap(bitmap);                                                }                                                @Override                                                public void onError(QBResponseException e) {                                                    progressDialog.dismiss();                                                    Toast.makeText(ChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();                                                }                                            });                                }                                @Override                                public void onError(QBResponseException e) {                                }                            });                } catch (FileNotFoundException e) {                    e.printStackTrace();                } catch (IOException e) {                    e.printStackTrace();                }            }        }    }    @OnClick(R.id.arrow_iv)    public void setArrowIv(View view) {        NavUtils.navigateUpFromSameTask(this);    }}