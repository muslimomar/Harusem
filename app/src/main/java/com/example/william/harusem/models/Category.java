package com.example.william.harusem.models;

/**
 * Created by william
 * on 05-08-2018.
 */

public class Category {

    int imageId;
    String categoryName;
    int dialogsCount;
    int percentage;
    int bgColor;

    public Category(int imageId, String categoryName, int bgColor) {
        this.imageId = imageId;
        this.categoryName = categoryName;
        this.bgColor = bgColor;
    }

    public Category(int imageId, String categoryName, int dialogsCount, int percentage) {
        this.imageId = imageId;
        this.categoryName = categoryName;
        this.dialogsCount = dialogsCount;
        this.percentage = percentage;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getDialogsCount() {
        return dialogsCount;
    }

    public void setDialogsCount(int dialogsCount) {
        this.dialogsCount = dialogsCount;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }
}
