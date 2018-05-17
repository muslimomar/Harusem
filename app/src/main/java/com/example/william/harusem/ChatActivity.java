package com.example.william.harusem;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.william.harusem.adapters.ChatAdapter;
import com.example.william.harusem.models.Message;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    ArrayList<Message> dummyChat = new ArrayList<>();

    ChatAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);


        dummyData();

        mAdapter = new ChatAdapter(dummyChat);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        chatRecyclerView.setLayoutManager(mLayoutManager);
        chatRecyclerView.setAdapter(mAdapter);

        messageInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // disable the button if field is empty
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void dummyData() {
        dummyChat.add(new Message("Hi bro, how are you doing!",null,12));
        dummyChat.add(new Message("Hi, I'm good!",null,12));
        dummyChat.add(new Message("Thank you !",null,12));
        dummyChat.add(new Message("You're welcome bro!",null,12));
        dummyChat.add(new Message("Hi bro, how are you doing!",null,12));
        dummyChat.add(new Message("Hi, I'm good!",null,12));
        dummyChat.add(new Message("Hi, I'm good!",null,12));

    }


}
