package com.example.william.harusem.holder;

import com.example.william.harusem.models.Lesson;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by william on 5/31/2018.
 */

public class LessonsHolder {
    public static LessonsHolder instance;
    private HashMap<String, Lesson> lessonsArray;

    public LessonsHolder() {
        lessonsArray = new HashMap<>();
    }

    public static synchronized LessonsHolder getInstance() {
        LessonsHolder speakingLessonsHolder;
        synchronized (LessonsHolder.class) {
            if (instance == null)
                instance = new LessonsHolder();
            speakingLessonsHolder = instance;
        }
        return speakingLessonsHolder;
    }

    public void putLesson(Lesson lesson) {
        lessonsArray.put(lesson.getLessonApiId(), lesson);
    }

    public void putLessons(String id, List<Lesson> lessons) {
        for (Lesson lesson : lessons) {
            lessonsArray.put(id, lesson);
        }
    }

    public Lesson getLessonById(String apiId) {
        return lessonsArray.get(apiId);
    }

    public List<Lesson> getLessonsByIds(List<String> ids) {
        List<Lesson> lessons = new ArrayList<>();
        for (String id : ids) {
            Lesson lesson = getLessonById(id);
            if (lesson != null)
                lessons.add(lesson);
        }
        return lessons;
    }


    public void updateLesson(Lesson lesson) {
        lessonsArray.put(lesson.getLessonApiId(), lesson);
    }

    public void updateLessons(List<Lesson> lessons) {
        for (Lesson newLesson : lessons) {
            updateLesson(newLesson);
        }
    }

    public ArrayList<Lesson> getAllLessons() {
        ArrayList<Lesson> result = new ArrayList<>();

        for (Map.Entry<String, Lesson> entry : lessonsArray.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    public ArrayList<Lesson> getAllLessonsByParentId(String parentId) {
        ArrayList<Lesson> result = new ArrayList<>();

        Map<String, Lesson> sortedMap = getSortedMap(lessonsArray);

        for (Map.Entry<String, Lesson> entry : sortedMap.entrySet()) {
            if (entry.getValue().getParentId().equals(parentId)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    public ArrayList<Lesson> getFinishedLessonsByParentId(String parentId) {
        ArrayList<Lesson> result = new ArrayList<>();

        for (Map.Entry<String, Lesson> entry : lessonsArray.entrySet()) {
            if (entry.getValue().getParentId().equals(parentId) && entry.getValue().isFinished()) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    public void clear() {
        lessonsArray.clear();
    }

    private Map<String, Lesson> getSortedMap(Map<String, Lesson> unsortedMap) {
        Map<String, Lesson> sortedMap = new TreeMap(new LessonsHolder.CreationDateLessonComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }

    static class CreationDateLessonComparator implements Comparator<String> {
        Map<String, Lesson> map;

        public CreationDateLessonComparator(Map<String, Lesson> map) {
            this.map = map;
        }

        public int compare(String keyA, String keyB) {

            long valueA = map.get(keyA).getLessonNumber();
            long valueB = map.get(keyB).getLessonNumber();

            if (valueB > valueA) {
                return -1;
            } else {
                 return 1;
            }
        }
    }

}
