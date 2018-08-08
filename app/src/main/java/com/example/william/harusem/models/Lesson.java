package com.example.william.harusem.models;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by william
 * on 07-08-2018.
 */

public class Lesson extends RealmObject {

    @PrimaryKey
    String id;
    int lessonNumber;
    String lessonTitle;
    boolean isFinished;
    boolean isLocked;
    String parentId;

    public Lesson() {
    }

    public Lesson(int lessonNumber, String lessonTitle, boolean isFinished, boolean isLocked, String parentId) {
        this.lessonNumber = lessonNumber;
        this.lessonTitle = lessonTitle;
        this.isFinished = isFinished;
        this.isLocked = isLocked;
        this.parentId = parentId;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public void setLessonNumber(int lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
