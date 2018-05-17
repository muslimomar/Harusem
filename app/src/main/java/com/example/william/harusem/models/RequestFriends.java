package com.example.william.harusem.models;


/**
 * Created by eminesa on 15.05.2018.
 */

public class RequestFriends {

    private int profileImage;
    private String nameTextView;
    private String requestTextView;

    private int deleteImage;

    public RequestFriends(int profileImage, String nameTextView, String requestTextView,  int deleteImage) {
        this.profileImage = profileImage;
        this.nameTextView = nameTextView;
        this.requestTextView = requestTextView;

        this.deleteImage = deleteImage;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    public String getNameTextView() {
        return nameTextView;
    }

    public void setNameTextView(String nameTextView) {
        this.nameTextView = nameTextView;
    }

    public String getRequestTextView() {
        return requestTextView;
    }

    public void setRequestTextView(String requestTextView) {
        this.requestTextView = requestTextView;
    }

    public int getDeleteImage() {
        return deleteImage;
    }

    public void setDeleteImage(int deleteImage) {
        this.deleteImage = deleteImage;
    }
}
