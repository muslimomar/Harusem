package com.example.william.harusem.models;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by william
 * on 7/31/2018.
 */

public class SpeakingDialog {
    int dialogType;
    String dialogText;
    int speakProgressLevel;
    int index;
    String parentId;
    String apiId;
    long createdAt;

    public SpeakingDialog() {
    }

    public SpeakingDialog(int dialogType, int index, String dialogText, String parentId, String apiId, long createdAt) {
        this.dialogType = dialogType;
        this.dialogText = dialogText;
        this.index = index;
        this.parentId = parentId;
        this.apiId = apiId;
        this.createdAt = createdAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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
}
