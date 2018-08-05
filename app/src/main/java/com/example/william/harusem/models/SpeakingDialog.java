package com.example.william.harusem.models;

/**
 * Created by william
 * on 7/31/2018.
 */

public class SpeakingDialog {

    int dialogType;
    String dialogText;
    int speakProgressLevel;

    public SpeakingDialog(int dialogType, String dialogText) {
        this.dialogType = dialogType;
        this.dialogText = dialogText;
    }

    public SpeakingDialog(int dialogType, String dialogText, int speakProgressLevel) {
        this.dialogType = dialogType;
        this.dialogText = dialogText;
        this.speakProgressLevel = speakProgressLevel;
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
