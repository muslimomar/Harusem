package com.example.william.harusem.models;

import android.widget.ImageView;

/**
 * Created by Mahmoud on 5/17/2018.
 */

public class ChatList {

    int chatImg;
    String chatPartnerName;
    String chatTimeStamp;
    String firstChatWords;

    public ChatList(int chatImg, String chatPartnerName, String chatTimeStamp, String firstChatWords) {
        this.chatImg = chatImg;
        this.chatPartnerName = chatPartnerName;
        this.chatTimeStamp = chatTimeStamp;
        this.firstChatWords = firstChatWords;
    }


    public int getChatImg() {
        return chatImg;
    }

    public void setChatImg(int chatImg) {
        this.chatImg = chatImg;
    }

    public String getChatPartnerName() {
        return chatPartnerName;
    }

    public void setChatPartnerName(String chatPartnerName) {
        this.chatPartnerName = chatPartnerName;
    }

    public String getChatTimeStamp() {
        return chatTimeStamp;
    }

    public void setChatTimeStamp(String chatTimeStamp) {
        this.chatTimeStamp = chatTimeStamp;
    }

    public String getFirstChatWords() {
        return firstChatWords;
    }

    public void setFirstChatWords(String firstChatWords) {
        this.firstChatWords = firstChatWords;
    }
}