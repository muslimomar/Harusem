package com.example.william.harusem.models;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by william
 * on 05-08-2018.
 */

public class Category extends RealmObject {
    @PrimaryKey
    String id;
    int imageId;
    String categoryDisplayName;
    int dialogsCount;
    int bgColor;
    int currentIndex;
    String categoryApiName;

    public Category() {
    }

    public Category(int imageId, String categoryDisplayName, int bgColor) {
        this.imageId = imageId;
        this.categoryDisplayName = categoryDisplayName;
        this.bgColor = bgColor;
    }

    public Category(int imageId, String categoryDisplayName, String categoryApiName, int dialogsCount, int bgColor, int currentIndex) {
        this.imageId = imageId;
        this.categoryDisplayName = categoryDisplayName;
        this.dialogsCount = dialogsCount;
        this.bgColor = bgColor;
        this.currentIndex = currentIndex;
        this.categoryApiName = categoryApiName;
        this.id = UUID.randomUUID().toString();
    }


    public String getCategoryApiName() {
        return categoryApiName;
    }

    public void setCategoryApiName(String categoryApiName) {
        this.categoryApiName = categoryApiName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getCategoryDisplayName() {
        return categoryDisplayName;
    }

    public void setCategoryDisplayName(String categoryDisplayName) {
        this.categoryDisplayName = categoryDisplayName;
    }

    public int getDialogsCount() {
        return dialogsCount;
    }

    public void setDialogsCount(int dialogsCount) {
        this.dialogsCount = dialogsCount;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}
