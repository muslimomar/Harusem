package com.example.william.harusem.activities;

import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.adapters.MessagesAdapter;
import com.example.william.harusem.models.ChatMessage;
import com.example.william.harusem.util.Extras;

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
    @BindView(R.id.status_sign_iv)
    ImageView statusSignIv;
    @BindView(R.id.send_img_btn)
    ImageButton sendImgBtn;
    @BindView(R.id.friend_name_tv)
    TextView friendNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        hideKeyboard();

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
//        messagesAdapter = new MessagesAdapter(new ArrayList<ChatMessage>());
//        chatRecyclerView.setAdapter(messagesAdapter);

    }


    private void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @OnClick(R.id.send_img_btn)
    public void sendImgBtn(View view) {

    }

}
