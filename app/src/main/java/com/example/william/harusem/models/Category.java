package com.example.william.harusem.models;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by william
 * on 05-08-2018.
 */

public class Category  {
    int imageId;
    String categoryDisplayName;
    int lessonsCount;
    int bgColor;
    String apiId;
    int progress;
    String publicCategoryId;

    public String getPublicCategoryId() {
        return publicCategoryId;
    }

    public void setPublicCategoryId(String publicCategoryId) {
        this.publicCategoryId = publicCategoryId;
    }

    public Category() {
    }

    public Category(int imageId, String categoryDisplayName, int lessonsCount, int bgColor, String apiId, int progress,String publicCategoryId) {
        this.imageId = imageId;
        this.categoryDisplayName = categoryDisplayName;
        this.lessonsCount = lessonsCount;
        this.bgColor = bgColor;
        this.apiId = apiId;
        this.progress = progress;
        this.publicCategoryId = publicCategoryId;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
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

    public int getLessonsCount() {
        return lessonsCount;
    }

    public void setLessonsCount(int lessonsCount) {
        this.lessonsCount = lessonsCount;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
