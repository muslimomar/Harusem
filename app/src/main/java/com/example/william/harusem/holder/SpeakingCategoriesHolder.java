package com.example.william.harusem.holder;

import com.example.william.harusem.models.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by william on 5/31/2018.
 */

public class SpeakingCategoriesHolder {
    public static SpeakingCategoriesHolder instance;
    private HashMap<String, Category> categoryArray;

    public SpeakingCategoriesHolder() {
        categoryArray = new HashMap<>();
    }

    public static synchronized SpeakingCategoriesHolder getInstance() {
        SpeakingCategoriesHolder speakingCategoriesHolder;
        synchronized (SpeakingCategoriesHolder.class) {
            if (instance == null)
                instance = new SpeakingCategoriesHolder();
            speakingCategoriesHolder = instance;
        }
        return speakingCategoriesHolder;
    }

    public void putCategory(Category category) {
        categoryArray.put(category.getApiId(), category);
    }

    public void putCategories(String dialogId, List<Category> categories) {
        for (Category category : categories) {
            categoryArray.put(dialogId, category);
        }
    }

    public Category getCategoryById(String apiId) {
        return categoryArray.get(apiId);
    }

    public List<Category> getCategoriesByIds(List<String> ids) {
        List<Category> categories = new ArrayList<>();
        for (String id : ids) {
            Category category = getCategoryById(id);
            if (category != null)
                categories.add(category);
        }
        return categories;
    }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> result = new ArrayList<>();

        for (Map.Entry<String, Category> entry : categoryArray.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    public void updateCategory(Category category) {
        categoryArray.put(category.getApiId(), category);
    }

    public void updateCategories(List<Category> categories) {
        for (Category newCategory : categories) {
            updateCategory(newCategory);
        }
    }

    public void clear() {
        categoryArray.clear();
    }

}
