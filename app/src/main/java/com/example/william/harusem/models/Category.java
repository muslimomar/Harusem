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
    int lessonsCount;
    int bgColor;
    String parentId;

    public Category() {
    }

    public Category(int imageId, String categoryDisplayName, String parentId, int lessonsCount, int bgColor) {
        this.imageId = imageId;
        this.categoryDisplayName = categoryDisplayName;
        this.lessonsCount = lessonsCount;
        this.bgColor = bgColor;
        this.parentId = parentId;
        this.id = UUID.randomUUID().toString();
    }


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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
}
