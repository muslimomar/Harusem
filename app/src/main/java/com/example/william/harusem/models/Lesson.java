package com.example.william.harusem.models;

import java.util.UUID;

/**
 * Created by william
 * on 07-08-2018.
 */

public class Lesson  {
    int lessonNumber;
    String lessonTitle;
    boolean isFinished;
    boolean isLocked;
    String parentId;
    String lessonApiId;
    String publicLessonId;

    public Lesson() {
    }

    public Lesson(int lessonNumber, String lessonTitle, boolean isFinished, boolean isLocked, String parentId, String lessonApiId,String publicLessonId) {
        this.lessonNumber = lessonNumber;
        this.lessonTitle = lessonTitle;
        this.isFinished = isFinished;
        this.isLocked = isLocked;
        this.parentId = parentId;
        this.lessonApiId = lessonApiId;
        this.publicLessonId = publicLessonId;
    }

    public String getPublicLessonId() {
        return publicLessonId;
    }

    public void setPublicLessonId(String publicLessonId) {
        this.publicLessonId = publicLessonId;
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

    public String getLessonApiId() {
        return lessonApiId;
    }

    public void setLessonApiId(String lessonApiId) {
        this.lessonApiId = lessonApiId;
    }
}
