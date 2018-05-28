package com.example.william.harusem.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.william.harusem.R;
import com.example.william.harusem.adapters.MessagesAdapter;
import com.example.william.harusem.models.ChatMessage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

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

    @BindView(R.id.send_img_btn)
    ImageButton sendImgBtn;

    private String chatRef = "";
    private String recipientId = "";
    private String currentUserId = "";
    private MessagesAdapter messagesAdapter;
    private DatabaseReference messagesDbReference;
    private ChildEventListener messagesChildListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        hideKeyboard();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            chatRef = bundle.getString("GENERATED_CHAT_REF");
            recipientId = bundle.getString("RECIPIENT_ID");
            currentUserId = bundle.getString("CURRENT_USER_ID");
        }

        configFireBase();
        configRecyclerView();

        messageInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    sendImgBtn.setVisibility(View.GONE);
                } else {
                    sendImgBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void configRecyclerView() {

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setHasFixedSize(true);
        messagesAdapter = new MessagesAdapter(new ArrayList<ChatMessage>());
        chatRecyclerView.setAdapter(messagesAdapter);

    }

    private void configFireBase() {
        messagesDbReference = FirebaseDatabase.getInstance().getReference().child(chatRef);
    }

    private void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onStart() {
        super.onStart();

        messagesChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);

                    if(chatMessage.getSender().equals(currentUserId)) {
                        chatMessage.setIsSenderOrRecipient(MessagesAdapter.MESSAGE_TYPE_SENDER);
                    }else{
                        chatMessage.setIsSenderOrRecipient(MessagesAdapter.MESSAGE_TYPE_RECEIVER);
                    }

                    messagesAdapter.addMessage(chatMessage);
                    chatRecyclerView.scrollToPosition(messagesAdapter.getItemCount() -1);

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        messagesDbReference.limitToFirst(50).addChildEventListener(messagesChildListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(messagesChildListener!=null) {
            messagesDbReference.removeEventListener(messagesChildListener);
        }
        messagesAdapter.cleanAdapter();
    }

    @OnClick(R.id.send_img_btn) public void sendImgBtn(View view ){
        String message = messageInputEt.getText().toString().trim();

        if(!message.isEmpty()){
            ChatMessage chatMessage = new ChatMessage(message,currentUserId,recipientId);
            messagesDbReference.push().setValue(chatMessage);

            messageInputEt.setText("");
        }

    }

}
