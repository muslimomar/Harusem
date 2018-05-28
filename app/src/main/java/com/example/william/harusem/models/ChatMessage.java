package com.example.william.harusem.models;

/**
 * Created by william on 5/17/2018.
 */

public class ChatMessage {

    String message;
    String sender;
    String recipient;

    int isSenderOrRecipient;

    public ChatMessage() {
    }

    public ChatMessage(String message, String sender, String recipient) {
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getIsSenderOrRecipient() {
        return isSenderOrRecipient;
    }

    public void setIsSenderOrRecipient(int isSenderOrRecipient) {
        this.isSenderOrRecipient = isSenderOrRecipient;
    }
}
