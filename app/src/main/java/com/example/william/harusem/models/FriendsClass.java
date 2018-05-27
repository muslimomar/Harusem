package com.example.william.harusem.models;

import android.widget.Button;

public class FriendsClass {

    int userPhoto;
    public String userName;
    String userStatus;
    int unFriendImageView;

    public FriendsClass(int userPhoto, String userName, String userStatus, int unFriendImageView) {
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.userStatus = userStatus;
        this.unFriendImageView = unFriendImageView;
    }

    public void setUserPhoto(int userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public void setUnFriendImageView(int unFriendImageView) {
        this.unFriendImageView = unFriendImageView;
    }

    public int getUserPhoto() {
        return userPhoto;

    }

    public String getUserName() {
        return userName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public int getUnFriendImageView() {
        return unFriendImageView;
    }
}
