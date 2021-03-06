package com.example.william.harusem.ui.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bhargavms.dotloader.DotLoader;
import com.crashlytics.android.Crashlytics;
import com.example.william.harusem.Harusem;
import com.example.william.harusem.R;
import com.example.william.harusem.fcm.NotificationHelper;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.example.william.harusem.holder.QBChatDialogHolder;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.models.Attachment;
import com.example.william.harusem.services.CallService;
import com.example.william.harusem.ui.adapters.AttachmentPreviewAdapter;
import com.example.william.harusem.ui.adapters.ChatAdapter;
import com.example.william.harusem.ui.adapters.UsersAdapter;
import com.example.william.harusem.ui.dialog.ProgressDialogFragment;
import com.example.william.harusem.ui.dialog.TwoButtonsDialogFragment;
import com.example.william.harusem.util.ChatHelper;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.MediaUtils;
import com.example.william.harusem.util.MimeTypeAttach;
import com.example.william.harusem.util.QBResRequestExecutor;
import com.example.william.harusem.util.SharedPrefsHelper;
import com.example.william.harusem.util.StringUtils;
import com.example.william.harusem.util.SystemPermissionHelper;
import com.example.william.harusem.util.Toaster;
import com.example.william.harusem.util.UiUtils;
import com.example.william.harusem.util.ValidationUtils;
import com.example.william.harusem.util.consts.Consts;
import com.example.william.harusem.util.imagepick.ImagePickHelper;
import com.example.william.harusem.util.imagepick.OnImagePickedListener;
import com.example.william.harusem.util.qb.PaginationHistoryListener;
import com.example.william.harusem.util.qb.QbChatDialogMessageListenerImp;
import com.example.william.harusem.util.qb.QbDialogUtils;
import com.example.william.harusem.util.qb.VerboseQbChatConnectionListener;
import com.example.william.harusem.utils.PermissionsChecker;
import com.example.william.harusem.utils.PushNotificationSender;
import com.example.william.harusem.utils.WebRtcSessionManager;
import com.example.william.harusem.widget.AttachmentPreviewAdapterView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivacyListsManager;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.chat.model.QBPrivacyList;
import com.quickblox.chat.model.QBPrivacyListItem;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBChatAttachClickListener;
import com.quickblox.ui.kit.chatmessage.adapter.media.SingleMediaManager;
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.AudioRecorder;
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.exceptions.MediaRecorderException;
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.listeners.QBMediaRecordListener;
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.view.QBRecordAudioButton;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.squareup.picasso.Picasso;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

import static com.example.william.harusem.ui.activities.SelectUsersActivity.EXTRA_QB_USERS;

////////For call

/**
 * Created by william on 6/10/2018.
 */
public class ChatActivity extends AppCompatActivity implements OnImagePickedListener, PopupMenu.OnMenuItemClickListener {
    public static final String EXTRA_DIALOG_ID = "dialogId";

    public static final String EXTRA_DIALOG = "dialog";
    public static final String TAG = ChatActivity.class.getSimpleName();
    private static final long ON_ITEM_CLICK_DELAY = TimeUnit.SECONDS.toMillis(10);
    private static final int MESSAGE_ATTACHMENT = 1;
    private static final int REQUEST_CODE_ATTACHMENT = 721;
    private static final int REQUEST_CODE_SELECT_PEOPLE = 752;
    private static final int REQUEST_CODE = 40;
    private static final int POST_DELAY_VIEW = 1000;
    private static final int VIBRATION_DURATION = 100;
    private static final int MAX_RECORD_DURATION = 30;
    private static final int CHRONOMETER_ALARM_SECOND = 27;
    protected List<QBChatMessage> messagesList;
    protected AudioRecorder audioRecorder;
    protected QBResRequestExecutor requestExecutor;
    protected SingleMediaManager mediaManager;
    SharedPrefsHelper sharedPrefsHelper;
    long TYPING_TIME = 2000;
    @BindView(R.id.attach_iv)
    ImageView attachIv;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.send_txt_btn)
    ImageButton sendTxtBtn;
    @BindView(R.id.audio_call_img_btn)
    ImageButton audioCallBtn;
    @BindView(R.id.video_call_img_btn)
    ImageButton videoCallBtn;
    @BindView(R.id.chat_recycler_view)
    RecyclerView chatListRecyclerView;
    @BindView(R.id.layout_attachment_preview_container)
    LinearLayout attachmentPreviewContainerLayout;
    @BindView(R.id.adapter_view_attachment_preview)
    AttachmentPreviewAdapterView previewAdapterView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottom_bar)
    LinearLayout bottomBar;
    @BindView(R.id.small_toolbar)
    Toolbar smallToolbar;
    @BindView(R.id.message_input_et)
    EditText messageInputEt;
    @BindView(R.id.status_sign_iv)
    ImageView statusSignIv;
    @BindView(R.id.opponent_name_tv)
    TextView opponentNameTv;
    @BindView(R.id.dialog_avatar)
    CircleImageView dialogAvatar;
    @BindView(R.id.dot_loader)
    DotLoader dotLoader;
    @BindView(R.id.record_audio_btn)
    QBRecordAudioButton recordAudioBtn;
    @BindView(R.id.layout_chat_audio_container)
    LinearLayout audioLayout;
    @BindView(R.id.chat_audio_record_textview)
    TextView audioRecordTextView;
    @BindView(R.id.chat_audio_record_chronometer)
    Chronometer recordChronometer;
    @BindView(R.id.chat_audio_record_bucket_imageview)
    ImageView bucketView;
    @BindView(R.id.more_iv) ImageButton moreIv;
    QBFriendListHelper qbFriendListHelper;
    QBChatDialogTypingListener typingListener;
    private boolean isRunForCall;
    private QBUsersHolder qbUsersHolder;
    private WebRtcSessionManager webRtcSessionManager;
    private PermissionsChecker checker;
    private QBUser currentUser;
    private QBMediaRecordListenerImpl recordListener;
    private AttachmentPreviewAdapter attachmentPreviewAdapter;
    private Snackbar snackbar;
    private ChatAdapter chatAdapter;
    private UsersAdapter usersAdapter;
    private ConnectionListener chatConnectionListener;
    private ImageAttachClickListener imageAttachClickListener;
    private QBChatDialog qbChatDialog;
    private ArrayList<QBChatMessage> unShownMessages;
    private int skipPagination = 0;
    private ChatMessageListener chatMessageListener;
    private boolean checkAdapterInit;
    private String fullName;
    private SystemPermissionHelper systemPermissionHelper;
    private Vibrator vibrator;
    private boolean isUserBlocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        Fabric.with(this, new Crashlytics());

        requestExecutor = Harusem.getInstance().getQbResRequestExecutor();
        initFieldsFromOppononet();
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        SharedPrefsHelper.getInstance().clearMessagesArray();
        SharedPrefsHelper.getInstance().clearPushDialogIds();

        setupEnvironment();

        //hideKeyboard();
        //setupActionBar();
        loadUserFullName();
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG);
        // null because we have notification
        if (qbChatDialog == null) {
            String dialogId = getIntent().getStringExtra(EXTRA_DIALOG_ID);
            qbChatDialog = QBChatDialogHolder.getInstance().getChatDialogById(dialogId);
            Log.i(TAG, "onCreate: ChatActivity: " + qbChatDialog);
            if (qbChatDialog == null) {
                getDialogFromPush(dialogId);
            }
        }

        if (qbChatDialog != null) {
            setupChat();
        }
        Log.v(TAG, "deserialized dialog = " + qbChatDialog);

        //initViews();
        //initMessagesRecyclerView();
        //initFields();


        //initCustomListeners();


        //initChatConnectionListener();

        //initChat();
        //initIsTypingListener();

        //Go to audio call activity
        audioCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startInnerCall(false);

                //Intent intent= new Intent(ChatActivity.this,CallActivity.class);
                //startActivity(intent);
            }
        });

        //Video call

        videoCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startInnerCall(true);
            }
        });

        isUserBlocked = isUserBlocked(qbChatDialog.getRecipientId().toString());
    }

    private void startInnerCall(Boolean getCallFlag) {

        QBUser current = SharedPrefsHelper.getInstance().getQbUser();
        startLoginService(current, getCallFlag);
    }

    public void setupChat() {
        initChatConnectionListener();
        initMessagesRecyclerView();
        initDialogForChat();
        initChatMessagesListener();
        initChat();
        addChatMessagesAdapterListeners();
        addIsTypingListener();
        initFields();
    }


    private void setupEnvironment() {
        initAudioRecorder();
        initCustomListeners();
        //configureActionBar();
        hideKeyboard();
        initViews();
        EdtTxtListener();
        initChatConnectionListener();
        initIsTypingListener();

    }


    private void EdtTxtListener() {
        messageInputEt.addTextChangedListener(new TextWatcher() {
            long currentTime;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setButtonsVisibility(charSequence.toString());

                currentTime = System.currentTimeMillis();
                if (!charSequence.toString().trim().isEmpty()) {
                    startTypingNotification();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "Typing afterTextChanged: ");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        long l = System.currentTimeMillis() - currentTime;
                        if (l >= TYPING_TIME) {
                            stopTypingNotification();
                            Log.e(TAG, "Typing run:");
                        }
                    }
                }, TYPING_TIME);
            }
        });
    }

    private void configureActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar2 = getSupportActionBar();
        actionBar2.setDisplayHomeAsUpEnabled(true);
    }


    private void initChatMessagesListener() {
        chatMessageListener = new ChatMessageListener();
        qbChatDialog.addMessageListener(chatMessageListener);
    }
//=======
//        //Go to audio call activity
//        audioCallBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////
////                startCall();
//>>>>>>> video_call

    private void initDialogForChat() {
        qbChatDialog.initForChat(QBChatService.getInstance());
    }

    private void getDialogFromPush(final String dialogId) {

        progressBar.setVisibility(View.VISIBLE);

        ChatHelper.getInstance().getDialogById(dialogId, new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog dialog, Bundle bundle) {
                qbChatDialog = dialog;
                setupChat();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
                showErrorSnackbar(0, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getDialogFromPush(dialogId);
                    }
                });
            }
        });

        String currentUserName = SharedPrefsHelper.getInstance().get("QB_USER_FULL_NAME");

        if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
            CallActivity.start(ChatActivity.this, true);
        }

        checker = new PermissionsChecker(getApplicationContext());


    }

    //new
    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }

    //new
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.start_video_call:
                if (isLoggedInChat()) {
                    startCall(true);
                }
                if (checker.lacksPermissions(Consts.PERMISSIONS)) {
                    startPermissionsActivity(false);
                }
                return true;

            case R.id.start_audio_call:
                if (isLoggedInChat()) {
                    startCall(false);
                }
                if (checker.lacksPermissions(Consts.PERMISSIONS[1])) {
                    startPermissionsActivity(true);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void startCall(boolean isVideoCall) {
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

        //ArrayList<Integer> opponentsList = CollectionsUtils.getIdsSelectedOpponents(usersAdapter.);

        QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());
        // the casting would do shitty thing and I know it but  ta da
        List<Integer> occupants = qbChatDialog.getOccupants();
        occupants.remove(SharedPrefsHelper.getInstance().getQbUser().getId());
        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(occupants, conferenceType);

        WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

        PushNotificationSender.sendPushMessage(occupants.get(0), currentUser.getFullName());

        CallActivity.start(this, false);
        Log.d(TAG, "conferenceType = " + conferenceType);
    }

    //new
    private boolean isLoggedInChat() {
        if (!QBChatService.getInstance().isLoggedIn()) {
            Toaster.shortToast(R.string.dlg_signal_error);
            tryReLoginToChat();
            return false;
        }
        return true;
    }

    private void tryReLoginToChat() {
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            CallService.start(this, qbUser);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            isRunForCall = intent.getExtras().getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
            if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
                CallActivity.start(ChatActivity.this, true);
            }
        }
    }

    private void initFieldsFromOppononet() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isRunForCall = extras.getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
        }

        currentUser = sharedPrefsHelper.getQbUser();
        //////////////////////////////////////////////////////
        qbUsersHolder = QBUsersHolder.getInstance();
        webRtcSessionManager = WebRtcSessionManager.getInstance(getApplicationContext());
    }


    //not really sure about this one ask ask !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//    private void startCall() {
//        QBUser current = SharedPrefsHelper.getInstance().getQbUser();
//        startLoginService(current);
//    }

    // private void loginToChat(final ArrayList<QBUser> qbUser) {
    //qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);

    //userForSave = qbUser;
    //startLoginService(qbUser);
    //}


    private void startLoginService(QBUser qbUser, Boolean getCallTag) {

        Intent tempIntent = new Intent(this, CallService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        CallService.start(this, qbUser, pendingIntent);

        // call it with true for the video call when you add the video button
        startCall(getCallTag);
        /////////////// where do we really change the intent
//        startOpponentsActivity();
    }


//    private void startOpponentsActivity() {
//        OpponentsActivity.start(ChatActivity.this, false);
//        finish();
//    }


    private void initCustomListeners() {
        recordAudioBtn.setRecordTouchListener(new RecordTouchListener());
        recordChronometer.setOnChronometerTickListener(new ChronometerTickListener());
    }

    private void initAudioRecorder() {
        audioRecorder = AudioRecorder.newBuilder()
                // Required
                .useInBuildFilePathGenerator(this)
                .setDuration(MAX_RECORD_DURATION)
                .build();
    }

    private void initFields() {
        systemPermissionHelper = new SystemPermissionHelper(this);
        chatMessageListener = new ChatMessageListener();
        qbFriendListHelper = new QBFriendListHelper(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        recordListener = new QBMediaRecordListenerImpl();
        mediaManager = chatAdapter.getMediaManagerInstance();
    }


    private void setButtonsVisibility(String s) {
            if (s.isEmpty() && attachmentPreviewAdapter.getUploadedAttachments().size() == 0) {
                sendTxtBtn.setVisibility(View.GONE);
                recordAudioBtn.setVisibility(View.VISIBLE);
            } else {
                sendTxtBtn.setVisibility(View.VISIBLE);
                recordAudioBtn.setVisibility(View.GONE);
            }
    }

    private void initIsTypingListener() {

        typingListener = new QBChatDialogTypingListener() {

            @Override
            public void processUserIsTyping(String s, Integer integer) {
                showTypingDots();
                Log.d(TAG, "Typing processUserIsTyping: ++++++");
            }

            @Override
            public void processUserStopTyping(String s, Integer integer) {
                Log.d(TAG, "Typing processUserStopTyping: ------");
                hideTypingDots();
            }
        };
    }

    private void stopTypingNotification() {

        try {
            qbChatDialog.sendStopTypingNotification();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

    }

    private void startTypingNotification() {
        try {
            qbChatDialog.sendIsTypingNotification();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void hideTypingDots() {
        if (dotLoader.getVisibility() != View.INVISIBLE) {
            dotLoader.setVisibility(View.INVISIBLE);
        }
    }

    private void showTypingDots() {
        if (dotLoader.getVisibility() != View.VISIBLE) {
            dotLoader.setVisibility(View.VISIBLE);
        }
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (qbChatDialog != null) {
            outState.putString(EXTRA_DIALOG_ID, qbChatDialog.getDialogId());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (qbChatDialog == null) {
            qbChatDialog = QBChatDialogHolder.getInstance().getChatDialogById(savedInstanceState.getString(EXTRA_DIALOG_ID));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        addChatMessagesAdapterListeners();
        ChatHelper.getInstance().addConnectionListener(chatConnectionListener);
        addRosterListener();
        addIsTypingListener();
        resumeMediaPlayer();

        setPrivateRecipientStatus(qbFriendListHelper.getUserPresence(qbChatDialog.getRecipientId()));
    }

    private void resumeMediaPlayer() {
        if (mediaManager.isMediaPlayerReady()) {
            mediaManager.resumePlay();
        }
    }


    private void addIsTypingListener() {
        if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
            if (typingListener != null) {
                qbChatDialog.addIsTypingListener(typingListener);
            }
        }
    }

    private void addRosterListener() {
        if (qbChatDialog != null) {
            if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
                QBRoster roster = QBChatService.getInstance().getRoster();
                if (roster != null) {
                    roster.addRosterListener(new RosterListener());
                }
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        removeChatMessagesAdapterListeners();
        ChatHelper.getInstance().removeConnectionListener(chatConnectionListener);
        removeRosterListener();
        removeIsTypingListener();
        suspendMediaPlayer();

    }

    private void suspendMediaPlayer() {
        if (mediaManager.isMediaPlayerReady()) {
            mediaManager.suspendPlay();
        }
    }

    private void removeIsTypingListener() {
        if (typingListener != null) {
            qbChatDialog.removeIsTypingListener(typingListener);
        }

    }

    private void removeRosterListener() {
        QBRoster roster = QBChatService.getInstance().getRoster();

        if (roster != null) {
            roster.removeRosterListener(new RosterListener());
        }
    }

    @Override
    public void onBackPressed() {
        releaseChat();
        sendDialogId();
        super.onBackPressed();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.activity_chat_menu, popup.getMenu());

        Menu menu = popup.getMenu();
        MenuItem menuItemLeave = menu.findItem(R.id.menu_chat_action_leave);
        MenuItem menuItemViewProfile = menu.findItem(R.id.menu_chat_action_view_profile);
        MenuItem menuItemGroupEdit = menu.findItem(R.id.menu_chat_action_group_edit);
        MenuItem menuItemGroupInfo = menu.findItem(R.id.menu_chat_action_group_info);
        if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
            menuItemLeave.setVisible(false);
            menuItemGroupEdit.setVisible(false);
            menuItemGroupInfo.setVisible(false);
        } else {
            menuItemViewProfile.setVisible(false);
            if (qbChatDialog.getUserId().equals(QBChatService.getInstance().getUser().getId())) {
                // if group owner
                menuItemGroupEdit.setVisible(true);
                menuItemGroupInfo.setVisible(false);
            } else {
                menuItemGroupEdit.setVisible(false);
                menuItemGroupInfo.setVisible(true);
            }
        }

        popup.setOnMenuItemClickListener(this);
            popup.show();
    }

    private void sendDialogId() {
        Intent result = new Intent();
        result.putExtra(EXTRA_DIALOG_ID, qbChatDialog.getDialogId());
        setResult(RESULT_OK, result);
    }

    private void leaveGroupChat() {
        ProgressDialogFragment.show(getSupportFragmentManager());
        ChatHelper.getInstance().exitFromDialog(qbChatDialog, new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbDialog, Bundle bundle) {
                ProgressDialogFragment.hide(getSupportFragmentManager());
                QBChatDialogHolder.getInstance().deleteDialog(qbDialog);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                ProgressDialogFragment.hide(getSupportFragmentManager());
                showErrorSnackbar(R.string.error_leave_chat, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leaveGroupChat();
                    }
                });
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_PEOPLE) {
                ArrayList<QBUser> selectedUsers = (ArrayList<QBUser>) data.getSerializableExtra(EXTRA_QB_USERS);
                String groupName = data.getStringExtra(SelectUsersActivity.EXTRA_GROUP_NAME);
                String groupPhoto = data.getStringExtra(SelectUsersActivity.EXTRA_QB_USER_PHOTO);

                updateDialog(selectedUsers, groupName, groupPhoto);
            }
        }
    }


    @Override
    public void onImagePicked(int requestCode, File file) {
        switch (requestCode) {
            case REQUEST_CODE_ATTACHMENT:
                sendTxtBtn.setVisibility(View.VISIBLE);

                    recordAudioBtn.setVisibility(View.GONE);
                attachmentPreviewAdapter.add(file);
                break;
        }
    }

    @Override
    public void onImagePickError(int requestCode, Exception e) {
        showErrorSnackbar(0, e, null);

    }

    @Override
    public void onImagePickClosed(int requestCode) {

    }


    public void showMessage(QBChatMessage message) {
        if (isAdapterConnected()) {
            chatAdapter.add(message);
            scrollMessageListDown();
        } else {
            delayShowMessage(message);
        }

    }

    private boolean isAdapterConnected() {
        return checkAdapterInit;
    }

    private void delayShowMessage(QBChatMessage message) {
        if (unShownMessages == null) {
            unShownMessages = new ArrayList<>();
        }
        unShownMessages.add(message);
    }


    private void initViews() {

        attachmentPreviewAdapter = new AttachmentPreviewAdapter(this,
                new AttachmentPreviewAdapter.OnAttachmentCountChangedListener() {
                    @Override
                    public void onAttachmentCountChanged(int count) {
                        attachmentPreviewContainerLayout.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
                    }
                },
                new AttachmentPreviewAdapter.OnAttachmentUploadErrorListener() {
                    @Override
                    public void onAttachmentUploadError(QBResponseException e) {
                        showErrorSnackbar(0, e, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setAttachIv(view);
                            }
                        });
                    }
                });

        previewAdapterView.setAdapter(attachmentPreviewAdapter);
    }

    public void initMessagesRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatListRecyclerView.setLayoutManager(layoutManager);

        messagesList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, qbChatDialog, messagesList);
        chatAdapter.setPaginationHistoryListener(new PaginationListener());
        chatListRecyclerView.addItemDecoration(
                new StickyRecyclerHeadersDecoration(chatAdapter)
        );

        chatListRecyclerView.setAdapter(chatAdapter);
        imageAttachClickListener = new ImageAttachClickListener();
    }

    private void sendChatMessage(String text, QBAttachment attachment) {
        QBChatMessage chatMessage = new QBChatMessage();
        if (attachment != null) {
            chatMessage.addAttachment(attachment);
        }
        chatMessage.setBody(text);
        chatMessage.setSaveToHistory(true);
        chatMessage.setDateSent(System.currentTimeMillis() / 1000);
        chatMessage.setMarkable(true);

        if (!QBDialogType.PRIVATE.equals(qbChatDialog.getType()) && !qbChatDialog.isJoined()) {
            Toaster.shortToast(R.string.still_joining);
            return;
        }


        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        userIds.add(qbChatDialog.getRecipientId());
        QBEvent messageEvent = NotificationHelper.createPushEvent(userIds, text, fullName, qbChatDialog.getDialogId());
        QBPushNotifications.createEvent(messageEvent).performAsync(new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle bundle) {
                Log.i(TAG, "QBPushNotifications onSuccess: qbEvent" + qbEvent + "\n bundle" + bundle);
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "QBPushNotifications onError: ", e);
            }
        });


        try {
            qbChatDialog.sendMessage(chatMessage);

            if (QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
                showMessage(chatMessage);
            }

            if (attachment != null) {
                attachmentPreviewAdapter.remove(attachment);
            } else {
                messageInputEt.setText("");
            }
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "sendChatMessage: ", e);
            Toaster.shortToast(R.string.cant_send_msg);
        }

            sendTxtBtn.setVisibility(View.GONE);
            recordAudioBtn.setVisibility(View.VISIBLE);
    }

    private void loadUserFullName() {
        QBUser signInQbUser = QBUsersHolder.getInstance().getSignInQbUser();
        if (signInQbUser == null) {
            signInQbUser = SharedPrefsHelper.getInstance().getQbUser();
        }


        fullName = signInQbUser.getFullName();

    }


    private void initChat() {

        switch (qbChatDialog.getType()) {
            case GROUP:
            case PUBLIC_GROUP:
                statusSignIv.setVisibility(View.GONE);
                videoCallBtn.setVisibility(View.GONE);
                audioCallBtn.setVisibility(View.GONE);
                joinGroupChat();
                break;
            case PRIVATE:
                    statusSignIv.setVisibility(View.VISIBLE);
                loadDialogUsers();
                break;
            default:
                Toaster.shortToast(String.format("%s %s", getString(R.string.chat_unsupported_type), qbChatDialog.getType().name()));
                finish();
                break;

        }

    }

    private void joinGroupChat() {
        progressBar.setVisibility(View.VISIBLE);
        ChatHelper.getInstance().join(qbChatDialog, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                if (snackbar != null) {
                    snackbar.dismiss();
                }
                loadDialogUsers();

            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
                snackbar = showErrorSnackbar(R.string.connection_error, e, null);

            }
        });
    }

    private void leaveGroupDialog() {
        try {
            ChatHelper.getInstance().leaveChatDialog(qbChatDialog);
        } catch (XMPPException | SmackException.NotConnectedException e) {
            Log.w(TAG, e);
        }
    }

    private void releaseChat() {
        qbChatDialog.removeMessageListrener(chatMessageListener);
        if (!QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
            leaveGroupDialog();
        }
    }

    private void updateDialog(final ArrayList<QBUser> selectedUsers, String groupName, String groupPhoto) {
        ChatHelper.getInstance().updateDialog(qbChatDialog, selectedUsers, groupName, groupPhoto, new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog dialog, Bundle bundle) {
                qbChatDialog = dialog;
                QBChatDialogHolder.getInstance().updateDialog(qbChatDialog.getDialogId(), groupName, groupPhoto,
                        qbChatDialog.getOccupants());
                loadDialogUsers();
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.chat_info_add_people_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateDialog(selectedUsers, groupName, groupPhoto);
                            }
                        });
            }
        });

    }

    private void loadDialogUsers() {

        ChatHelper.getInstance().getUsersFromDialog(qbChatDialog, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                setOpponentName();
                loadChatHistory();
                getDialogPhoto();
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.chat_load_users_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadDialogUsers();
                            }
                        });

            }
        });

    }

    private void getDialogPhoto() {
        QBChatDialog dialog = qbChatDialog;

        if (dialog.getPhoto() != null && !dialog.getPhoto().equalsIgnoreCase("null")) {

            QBContent.getFile(Integer.parseInt(dialog.getPhoto())).performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(QBFile qbFile, Bundle bundle) {
                    String fileUrl = qbFile.getPublicUrl();
                    Picasso.get()
                            .load(fileUrl)
                            .resize(50, 50)
                            .centerCrop()
                            .into(dialogAvatar);
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e(TAG, "onError: ", e);
                }
            });


        } else {
            if (dialog.getType().equals(QBDialogType.PRIVATE)) {
                QBUser recipient = QBUsersHolder.getInstance().getUserById(dialog.getRecipientId());
                if (recipient == null) {
                    QBUsers.getUser(dialog.getRecipientId()).performAsync(new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser user, Bundle bundle) {
                            Integer fileId = user.getFileId();
                            if (fileId != null) {
                                getRecipientPhoto(fileId, dialogAvatar);
                            } else {
                                dialogAvatar.setBackgroundDrawable(getResources().getDrawable(R.drawable.placeholder_user));
                                dialogAvatar.setImageDrawable(null);
                            }
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Log.e(TAG, "onError: ",e );
                        }
                    });

                }else{
                    Integer fileId = recipient.getFileId();
                    if (fileId != null) {
                        getRecipientPhoto(fileId, dialogAvatar);
                    } else {
                        dialogAvatar.setBackgroundDrawable(getResources().getDrawable(R.drawable.placeholder_user));
                        dialogAvatar.setImageDrawable(null);
                    }
                }



            } else {
                dialogAvatar.setBackgroundDrawable(UiUtils.getGreyCircleDrawable());
                dialogAvatar.setImageResource(R.drawable.ic_group_new);
            }

        }
    }

    private void getRecipientPhoto(final Integer fileId, final ImageView dialogImageView) {
        QBContent.getFile(fileId).performAsync(new QBEntityCallback<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {

                String fileUrl = qbFile.getPublicUrl();
                Picasso.get().load(fileUrl)
                        .resize(50, 50)
                        .centerCrop()
                        .into(dialogImageView);
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
            }
        });

    }

    private void setOpponentName() {
        String opponentName = QbDialogUtils.getDialogName(qbChatDialog);
        opponentNameTv.setText(opponentName);

    }

    private void loadChatHistory() {
        ChatHelper.getInstance().loadChatHistory(qbChatDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                Collections.reverse(qbChatMessages);
                if (!checkAdapterInit) {
                    checkAdapterInit = true;
                    chatAdapter.addList(qbChatMessages);
                    addDelayedMessagesToAdapter();
                } else {
                    chatAdapter.addToList(qbChatMessages);
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
                skipPagination -= ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
                snackbar = showErrorSnackbar(R.string.connection_error, e, null);
            }
        });
        skipPagination += ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;

    }

    private void addDelayedMessagesToAdapter() {
        if (unShownMessages != null && !unShownMessages.isEmpty()) {
            List<QBChatMessage> chatList = chatAdapter.getList();
            for (QBChatMessage message : unShownMessages) {
                if (!chatList.contains(message)) {
                    chatAdapter.add(message);
                }
            }
        }
    }

    private void scrollMessageListDown() {
        chatListRecyclerView.scrollToPosition(messagesList.size() - 1);
    }

    private void deleteChat() {
        ChatHelper.getInstance().deleteDialog(qbChatDialog, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.dialogs_deletion_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteChat();
                            }
                        });
            }
        });
    }

    private void initChatConnectionListener() {
        chatConnectionListener = new VerboseQbChatConnectionListener(findViewById(R.id.chat_recycler_view)) {
            @Override
            public void reconnectionSuccessful() {
                super.reconnectionSuccessful();
                skipPagination = 0;
                switch (qbChatDialog.getType()) {
                    case GROUP:
                        checkAdapterInit = false;
                        // Join active room if we're in Group Chat
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                joinGroupChat();
                            }
                        });
                        break;
                }
            }
        };
    }

    private void addChatMessagesAdapterListeners() {
        if (chatAdapter != null) {
            chatAdapter.setAttachImageClickListener(imageAttachClickListener);
        }
    }

    private void removeChatMessagesAdapterListeners() {
        if (chatAdapter != null) {
            chatAdapter.removeAttachImageClickListener(imageAttachClickListener);
        }
    }

    private void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private boolean checkRecordPermission() {
        if (systemPermissionHelper.isAllAudioRecordPermissionGranted()) {
            return true;
        } else {
            systemPermissionHelper.requestAllPermissionForAudioRecord();
            return false;
        }
    }

    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(chatListRecyclerView, resId, e,
                R.string.dlg_retry, clickListener);
    }

    @OnClick(R.id.send_txt_btn)
    public void setSendTxtBtn(View view) {
        qbChatDialog.sendStopTypingNotification(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError: ", e);
            }
        });

        int totalAttachmentsCount = attachmentPreviewAdapter.getCount();
        Collection<QBAttachment> uploadedAttachments = attachmentPreviewAdapter.getUploadedAttachments();
        if (!uploadedAttachments.isEmpty()) {
            if (uploadedAttachments.size() == totalAttachmentsCount) {
                for (QBAttachment attachment : uploadedAttachments) {
                    sendChatMessage(getString(R.string.photo_attach), attachment);
                }
            } else {
                Toaster.shortToast(R.string.chat_wait);
            }
        }

        String text = messageInputEt.getText().toString().trim();


        if (isUserBlocked) {
            Toast.makeText(this, R.string.cant_send_msg_blocked, Toast.LENGTH_SHORT).show();
            messageInputEt.setText("");
            return;
        }

        if (!TextUtils.isEmpty(text)) {
            sendChatMessage(text, null);
        }


    }

    // user blocking metod
    boolean isUserBlocked(String userID) {
        QBPrivacyListsManager privacyListsManager = QBChatService.getInstance().getPrivacyListsManager();

        List<QBPrivacyList> lists = null;
        try {
            lists = privacyListsManager.getPrivacyLists();
            for (QBPrivacyList qbPrivacyList : lists) {
                if (qbPrivacyList.getName().equals("public")) {

                    for (QBPrivacyListItem item : qbPrivacyList.getItems()) {
                        if (item.getValueForType().contains(userID)) {
                            return true;
                        }
                    }
                }
            }
        } catch (SmackException.NotConnectedException | SmackException.NoResponseException | XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
        return false;

    }

    @OnClick(R.id.attach_iv)
    public void setAttachIv(View view) {
        new ImagePickHelper().pickAnImage(this, REQUEST_CODE_ATTACHMENT);
    }

    private void setPrivateRecipientStatus(QBPresence qbPresence) {
        if (qbPresence.getType() == QBPresence.Type.online) {
            statusSignIv.setColorFilter(Color.parseColor("#00c853"));
        } else {
            statusSignIv.setColorFilter(Color.parseColor("#d50000"));
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_chat_action_group_edit:
                Intent intent = new Intent(this, CreateGroupActivity.class);
                intent.putExtra(EXTRA_DIALOG, qbChatDialog);
                startActivityForResult(intent, REQUEST_CODE_SELECT_PEOPLE);
                return true;
            case R.id.menu_chat_action_leave:
                leaveGroupChat();
                return true;
            case R.id.menu_chat_action_delete:
                deleteChat();
                return true;
            case R.id.menu_chat_action_view_profile:
                redirectToProfileActivity();
                return true;
            case R.id.menu_chat_action_group_info:
               redirectToGroupInfo();
                return true;
            default:
                return true;
        }

    }

    private void redirectToGroupInfo() {
        Intent intent = new Intent(this, GroupInfoActivity.class);
        intent.putExtra(EXTRA_DIALOG, qbChatDialog);
        startActivity(intent);
    }

    private void redirectToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user_id", "" + qbChatDialog.getRecipientId());
        intent.putExtra("name", QbDialogUtils.getDialogName(qbChatDialog));
        startActivity(intent);
    }

    @OnClick(R.id.arrow_iv)
    public void setbackBtn(View view) {
        onBackPressed();
    }

    @OnClick(R.id.more_iv)
    public void setmoreitv(View view) {
        showPopup(view);
    }

    private void stopRecordByClick() {
        setMessageAttachViewsEnable(true);
        vibrate(VIBRATION_DURATION);
        stopRecord();
    }

    private void stopRecord() {
        audioViewVisibility(View.INVISIBLE);
        stopChronometer();
        audioRecorder.stopRecord();
    }

    private void cancelRecord() {
        stopChronometer();
        setMessageAttachViewsEnable(true);
        setRecorderViewsVisibility(View.INVISIBLE);
        animateCanceling();
        vibrate(VIBRATION_DURATION);
        audioViewPostDelayVisibility(View.INVISIBLE);
        audioRecorder.cancelRecord();
    }

    private void animateCanceling() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        bucketView.startAnimation(shake);
    }

    private void stopChronometer() {
        recordChronometer.stop();
    }

    private void startRecord() {
        setMessageAttachViewsEnable(false);
        setRecorderViewsVisibility(View.VISIBLE);
        audioViewVisibility(View.VISIBLE);
        vibrate(VIBRATION_DURATION);
        audioRecorder.startRecord();
        startChronometer();
    }

    private void startChronometer() {
        recordChronometer.setBase(SystemClock.elapsedRealtime());
        recordChronometer.start();
    }

    private void vibrate(int duration) {
        vibrator.vibrate(duration);
    }

    private void setRecorderViewsVisibility(int visibility) {
        audioRecordTextView.setVisibility(visibility);
        recordChronometer.setVisibility(visibility);
    }

    private void setMessageAttachViewsEnable(boolean enable) {
        messageInputEt.setFocusableInTouchMode(enable);
        messageInputEt.setFocusable(enable);
        attachIv.setEnabled(enable);
    }

    private void audioViewPostDelayVisibility(final int visibility) {
        audioLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                audioViewVisibility(visibility);
            }
        }, POST_DELAY_VIEW);
    }

    private void audioViewVisibility(int visibility) {
        audioLayout.setVisibility(visibility);
    }

    private void addAudioRecorderListener() {
        audioRecorder.setMediaRecordListener(recordListener);
    }

    private void clearAudioRecorder() {
        if (audioRecorder != null) {
            audioRecorder.removeMediaRecordListener();
            stopRecordIfNeed();
        }
    }

    private void stopRecordIfNeed() {
        if (audioRecorder.isRecording()) {
            Log.d(TAG, "stopRecordIfNeed");
            stopRecord();
        }
    }

    protected void startLoadAttachFile(final Attachment.Type type, final Object attachment, final String dialogId) {
        TwoButtonsDialogFragment.show(getSupportFragmentManager(), getString(R.string.dialog_confirm_sending_attach,
                StringUtils.getAttachmentNameByType(this, type)), false,
                new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        showProgress();
                        uploadAttachment((File) attachment, new QBEntityCallback() {
                            @Override
                            public void onSuccess(Object o, Bundle bundle) {
                                sendMessageWithAttachment((QBFile) o, ((File) attachment).getAbsolutePath());
                                hideProgress();
                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });
                    }
                });
    }

    private void hideProgress() {
        ProgressDialogFragment.hide(getSupportFragmentManager());
    }

    private void sendMessageWithAttachment(QBFile qbFile, String absolutePath) {
        QBAttachment audioAttachment = getAudioAttachment(qbFile, absolutePath);

        QBChatMessage message = getQBChatMessage(getString(R.string.voice_attach));
        message.addAttachment(audioAttachment);

        sendMessage(message);
    }

    private void sendMessage(QBChatMessage message) {
        message.setDialogId(qbChatDialog.getDialogId());
        qbChatDialog.initForChat(QBChatService.getInstance());

        if (QBDialogType.GROUP.equals(qbChatDialog.getType())) {
            tryJoinRoomChat(qbChatDialog);
        }

        try {
            qbChatDialog.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
            showMessage(message);
        }
    }

    private DiscussionHistory history() {
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0); // without getting messages
        return history;
    }

    public void joinRoomChat(QBChatDialog dialog) throws XMPPException, SmackException {
        dialog.initForChat(QBChatService.getInstance());
        if (!dialog.isJoined()) { //join only to unjoined dialogs
            dialog.join(history());
        }
    }

    public void tryJoinRoomChat(QBChatDialog dialog) {
        try {
            joinRoomChat(dialog);
        } catch (Exception e) {
            ErrorUtils.logError(e);
        }
    }

    private QBChatMessage getQBChatMessage(String s) {
        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(s);
        chatMessage.setMarkable(true);
        chatMessage.setSaveToHistory(true);
        chatMessage.setDateSent(System.currentTimeMillis() / 1000);
        return chatMessage;
    }

    private QBAttachment getAudioAttachment(QBFile file, String absolutePath) {
        QBAttachment attachment = new QBAttachment(QBAttachment.AUDIO_TYPE);
        attachment.setId(file.getUid());
        attachment.setName(file.getName());
        attachment.setContentType(MimeTypeAttach.AUDIO_MIME);
        attachment.setSize(file.getSize());

        if (!TextUtils.isEmpty(absolutePath)) {
            int durationSec = MediaUtils.getMetaData(absolutePath).durationSec();
            attachment.setDuration(durationSec);
        }
        return attachment;
    }

    private void uploadAttachment(File inputFile, QBEntityCallback callback) {
        QBFile file = null;
        QBContent.uploadFileTask(inputFile, true, null).performAsync(callback);
    }

    public synchronized void showProgress() {
        ProgressDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    protected void onStart() {
        super.onStart();
        addAudioRecorderListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearAudioRecorder();
    }
////After deleting Opponent
//    public static void start(Context context, boolean isRunForCall) {
//        Intent intent = new Intent(context, ChatActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        intent.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, isRunForCall);
//        context.startActivity(intent);
//    }

    @OnClick(R.id.dialog_avatar)
    public void setDialogAvatar(View view) {
        if (qbChatDialog != null && qbChatDialog.getType() == QBDialogType.PRIVATE) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user_id", qbChatDialog.getRecipientId().toString());
            intent.putExtra("name", qbChatDialog.getName());
            startActivity(intent);

        }
    }

    private class ChatMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            showMessage(qbChatMessage);
        }
    }

    private class ImageAttachClickListener implements QBChatAttachClickListener {
        @Override
        public void onLinkClicked(QBAttachment qbAttachment, int position) {
            AttachmentImageActivity.start(ChatActivity.this, qbAttachment.getUrl(), MESSAGE_ATTACHMENT);
        }
    }

    private class PaginationListener implements PaginationHistoryListener {

        @Override
        public void downloadMore() {
            Log.w(TAG, "downloadMore");
            loadChatHistory();
        }
    }

    private class RosterListener implements QBRosterListener {

        @Override
        public void entriesDeleted(Collection<Integer> collection) {
            Log.d(TAG, "RosterListener entriesDeleted: " + collection);
        }

        @Override
        public void entriesAdded(Collection<Integer> collection) {
            Log.d(TAG, "RosterListener entriesAdded: " + collection);

        }

        @Override
        public void entriesUpdated(Collection<Integer> collection) {
            Log.d(TAG, "RosterListener entriesUpdated: " + collection);
        }

        @Override
        public void presenceChanged(QBPresence qbPresence) {
            if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
                Log.d(TAG, "RosterListener presenceChanged: " + qbPresence);
                if (qbPresence != null && qbChatDialog != null) {
                    if (qbPresence.getUserId().equals(qbChatDialog.getRecipientId())) {
                        setPrivateRecipientStatus(qbPresence);
                    }
                }
            }

        }
    }

    protected class RecordTouchListener implements QBRecordAudioButton.RecordTouchEventListener {

        @Override
        public void onStartClick(View view) {
            if (checkRecordPermission()) {
                startRecord();
            }
        }

        @Override
        public void onCancelClick(View view) {
            cancelRecord();
        }

        @Override
        public void onStopClick(View view) {
            stopRecordByClick();

        }
    }

    protected class ChronometerTickListener implements Chronometer.OnChronometerTickListener {
        private long elapsedSecond;

        @Override
        public void onChronometerTick(Chronometer chronometer) {
            setChronometerAppropriateColor();
        }

        private void setChronometerAppropriateColor() {
            elapsedSecond = TimeUnit.MILLISECONDS.toSeconds(SystemClock.elapsedRealtime() - recordChronometer.getBase());
            if (isStartSecond()) {
                setChronometerBaseColor();
            }
            if (isAlarmSecond()) {
                setChronometerAlarmColor();
            }
        }

        private boolean isStartSecond() {
            return elapsedSecond == 0;
        }

        private boolean isAlarmSecond() {
            return elapsedSecond == CHRONOMETER_ALARM_SECOND;
        }

        private void setChronometerAlarmColor() {
            recordChronometer.setTextColor(ContextCompat.getColor(ChatActivity.this, android.R.color.holo_red_light));
        }

        private void setChronometerBaseColor() {
            recordChronometer.setTextColor(ContextCompat.getColor(ChatActivity.this, android.R.color.black));
        }
    }

    private class QBMediaRecordListenerImpl implements QBMediaRecordListener {

        @Override
        public void onMediaRecorded(File file) {
            audioViewVisibility(View.INVISIBLE);
            if (ValidationUtils.validateAttachment(getSupportFragmentManager(), getResources().getStringArray(R.array.supported_attachment_types), Attachment.Type.AUDIO, file)) {
                startLoadAttachFile(Attachment.Type.AUDIO, file, qbChatDialog.getDialogId());
            } else {
                audioRecordErrorAnimate();
            }

        }

        @Override
        public void onMediaRecordError(MediaRecorderException e) {
            audioRecorder.releaseMediaRecorder();
            audioRecordErrorAnimate();
        }

        @Override
        public void onMediaRecordClosed() {

        }

        private void audioRecordErrorAnimate() {
            Animation shake = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.shake_record_button);
            recordAudioBtn.startAnimation(shake);
        }

    }


}