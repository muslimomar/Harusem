package com.example.william.harusem.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.model.QBUser;

public class SharedPrefsHelper {
    private static final String SHARED_PREFS_NAME = "qb";
    private static final String QB_USER_ID = "qb_user_id";
    private static final String QB_USER_LOGIN = "qb_user_login";
    private static final String QB_USER_PASSWORD = "qb_user_password";
    private static final String QB_USER_FULL_NAME = "qb_user_full_name";
    private static final String QB_USER_TAGS = "qb_user_tags";
    private static final String QB_USER_CUSTOM_DATA = "qb_user_custom_data";
    private static final String QB_USER_EMAIL = "qb_user_email";
    private static SharedPrefsHelper instance;

    private Context cx;
    private SharedPreferences sharedPreferences;

    private SharedPrefsHelper(Context context) {
        instance = this;
        this.cx = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefsHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void delete(String key) {
        if (sharedPreferences.contains(key)) {
            getEditor().remove(key).commit();
        }
    }

    public void save(String key, Object value) {
        SharedPreferences.Editor editor = getEditor();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-supported preference");
        }

        editor.commit();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public boolean has(String key) {
        return sharedPreferences.contains(key);
    }


    public void saveQbUser(QBUser qbUser) {
        save(QB_USER_ID, qbUser.getId());
        save(QB_USER_LOGIN, qbUser.getLogin());
        save(QB_USER_PASSWORD, qbUser.getPassword());
        save(QB_USER_FULL_NAME, qbUser.getFullName());
        save(QB_USER_TAGS, qbUser.getTags().getItemsAsString());
        save(QB_USER_CUSTOM_DATA, qbUser.getCustomData());
        save(QB_USER_EMAIL, qbUser.getEmail());
    }

    public void removeQbUser() {
        delete(QB_USER_ID);
        delete(QB_USER_LOGIN);
        delete(QB_USER_PASSWORD);
        delete(QB_USER_FULL_NAME);
        delete(QB_USER_TAGS);
        delete(QB_USER_CUSTOM_DATA);
        delete(QB_USER_EMAIL);
    }

    public QBUser getQbUser() {
        if (hasQbUser()) {
            Integer id = get(QB_USER_ID);
            String login = get(QB_USER_LOGIN);
            String password = get(QB_USER_PASSWORD);
            String fullName = get(QB_USER_FULL_NAME);
            String tagsInString = get(QB_USER_TAGS);
            String customData = get(QB_USER_CUSTOM_DATA);
            String email = get(QB_USER_EMAIL);

            StringifyArrayList<String> tags = null;

            if (tagsInString != null) {
                tags = new StringifyArrayList<>();
                tags.add(tagsInString.split(","));
            }

            QBUser user = new QBUser(login, password);
            user.setId(id);
            user.setFullName(fullName);
            user.setTags(tags);
            user.setCustomData(customData);
            user.setEmail(email);
            return user;
        } else {
            return null;
        }
    }

    public boolean hasQbUser() {
        return has(QB_USER_LOGIN) && has(QB_USER_PASSWORD);
    }

    public void clearAllData() {
        SharedPreferences.Editor editor = getEditor();
        editor.clear().commit();
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }
}
