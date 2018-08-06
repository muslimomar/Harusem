package com.example.william.harusem.models;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by william
 * on 7/31/2018.
 */

public class SpeakingDialog extends RealmObject {
    @PrimaryKey
    String id;
    int dialogType;
    String dialogText;
    int speakProgressLevel;
    int index;
    boolean isFinished;

    public SpeakingDialog() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public SpeakingDialog(int dialogType, int index, String dialogText,boolean isFinished) {
        this.dialogType = dialogType;
        this.dialogText = dialogText;
        this.index = index;
        this.isFinished = isFinished;
        this.id = UUID.randomUUID().toString();
    }

    public SpeakingDialog(int dialogType, String dialogText, int speakProgressLevel) {
        this.dialogType = dialogType;
        this.dialogText = dialogText;
        this.speakProgressLevel = speakProgressLevel;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSpeakProgressLevel() {
        return speakProgressLevel;
    }

    public void setSpeakProgressLevel(int speakProgressLevel) {
        this.speakProgressLevel = speakProgressLevel;
    }

    public int getDialogType() {
        return dialogType;
    }

    public void setDialogType(int dialogType) {
        this.dialogType = dialogType;
    }

    public String getDialogText() {
        return dialogText;
    }

    public void setDialogText(String dialogText) {
        this.dialogText = dialogText;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}
