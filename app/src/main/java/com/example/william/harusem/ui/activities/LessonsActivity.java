package com.example.william.harusem.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.william.harusem.R;
import com.example.william.harusem.common.Common;
import com.example.william.harusem.models.Category;
import com.example.william.harusem.models.Lesson;
import com.example.william.harusem.ui.adapters.CategoryAdapter;
import com.example.william.harusem.ui.adapters.LessonsAdapter;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.Utils;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.william.harusem.common.Common.NOT_FOUND_HTTP_CODE;
import static com.example.william.harusem.common.Common.PARENT_ID;
import static com.example.william.harusem.common.Common.PUBLIC_CATEGORY_LESSONS;
import static com.example.william.harusem.common.Common.USERS_LESSONS_DATA;
import static com.example.william.harusem.ui.adapters.CategoryAdapter.EXTRAS_CATEGORY_API_ID;
import static com.example.william.harusem.ui.adapters.CategoryAdapter.EXTRAS_PUBLIC_CATEGORY_API_ID;
import static com.example.william.harusem.ui.adapters.LessonsAdapter.EXTRAS_PRIVATE_LESSON_API_ID;
import static com.example.william.harusem.ui.adapters.LessonsAdapter.EXTRAS_PRIVATE_NEXT_LESSON_API_ID;
import static com.example.william.harusem.ui.adapters.LessonsAdapter.EXTRAS_PUBLIC_LESSON_API_ID;
import static com.example.william.harusem.ui.adapters.LessonsAdapter.EXTRAS_PUBLIC_NEXT_LESSON_API_ID;
import static com.example.william.harusem.ui.adapters.LessonsAdapter.UPDATE_LESSONS_REQUEST_CODE;

public class LessonsActivity extends AppCompatActivity {

    private static final String TAG = LessonsAdapter.class.getSimpleName();
    LessonsAdapter mAdapter;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    String categoryApiId;
    @BindView(R.id.root_layout)
    LinearLayout rootLayout;
    Realm realm;
    private String categoryPrivateApiId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);
        ButterKnife.bind(this);
        hideActionBar();
        realm = Realm.getDefaultInstance();
        configRecyclerView();
        getCategoryApiId();

        getLessons();

    }

    private void getLessons() {
        ProgressDialog progressDialog = Utils.buildProgressDialog(this, "", "Loading...", false);
        progressDialog.show();

        RealmResults<Lesson> realmLessons = getSelectedCategoryLessons();
        if (realmLessons.size() > 0) {
            // if data already in database get them
            refreshAdapter(realmLessons);
            progressDialog.dismiss();
        } else {
            // if data are not in database request them
            getDataFromSDK(progressDialog);
        }

    }

    private void getDataFromSDK(ProgressDialog progressDialog) {
        getUserLessonClass(progressDialog);
    }

    private void getPublicCategoryLessons(ProgressDialog progressDialog) {
        QBCustomObjects.getObjects(PUBLIC_CATEGORY_LESSONS).performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                List<QBCustomObject> lessonsCustomObjects = new ArrayList<>();
                // add data to list, to store them in realm later
                addUserLessonsData(lessonsCustomObjects, qbCustomObjects);
                createUserLessonClass(lessonsCustomObjects, progressDialog);
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getLessons();
                    }
                });
            }
        });


    }

    private void addUserLessonsData(List<QBCustomObject> lessonsCustomObjects, ArrayList<QBCustomObject> qbCustomObjects) {

        for (QBCustomObject custom : qbCustomObjects) {
            QBCustomObject userLessonData = new QBCustomObject();
            userLessonData.setClassName("Users_Lessons_Data");
            userLessonData.putInteger("lesson_number", custom.getInteger("lesson_number"));
            userLessonData.putString("lesson_title", custom.getString("lesson_title"));
            userLessonData.putBoolean("isFinished", custom.getBoolean("isFinished"));
            userLessonData.putBoolean("isLocked", custom.getBoolean("isLocked"));
            userLessonData.setUserId(QBChatService.getInstance().getUser().getId());
            userLessonData.putString("public_lesson_id", custom.getCustomObjectId());
            userLessonData.setParentId(custom.getParentId());

            lessonsCustomObjects.add(userLessonData);
        }
    }

    private void createUserLessonClass(List<QBCustomObject> customObjects, ProgressDialog progressDialog) {
        QBCustomObjects.createObjects(customObjects).performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                ArrayList<Lesson> realmLessons = new ArrayList<>();

                for (QBCustomObject custom : qbCustomObjects) {
                    Integer lesson_number = custom.getInteger("lesson_number");
                    String lesson_title = custom.getString("lesson_title");
                    Boolean isFinished = custom.getBoolean("isFinished");
                    Boolean isLocked = custom.getBoolean("isLocked");
                    String parentId = custom.getParentId();
                    String lessonApiId = custom.getCustomObjectId();
                    String publicLessonId = custom.getString("public_lesson_id");

                    realmLessons.add(new Lesson(lesson_number, lesson_title, isFinished, isLocked, parentId, lessonApiId, publicLessonId));
                }

                copyListToRealm(realmLessons);

                RealmResults<Lesson> realmResults = getSelectedCategoryLessons();
                refreshAdapter(realmResults);
                progressDialog.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getLessons();
                    }
                });
            }
        });

    }

    private RealmResults<Lesson> getSelectedCategoryLessons() {
        return realm.where(Lesson.class).equalTo(PARENT_ID, categoryApiId).findAll();
    }

    private void copyListToRealm(ArrayList<Lesson> lessons) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(lessons);
        realm.commitTransaction();
    }

    private void getUserLessonClass(ProgressDialog progressDialog) {
        // get custom user lesson data, if the latter doesn't have any data, get them
        // from public lessons class.
        QBCustomObjects.getObjects(USERS_LESSONS_DATA).performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                if (qbCustomObjects.size() > 0) {
                    // data exists in custom user lesson class
                    // add them to realm and populate.
                    storeSdkCustomData(qbCustomObjects);
                    RealmResults<Lesson> realmResults = getSelectedCategoryLessons();
                    refreshAdapter(realmResults);
                    progressDialog.dismiss();
                } else {
                    // get data from public lessons class
                    getPublicCategoryLessons(progressDialog);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                if (e.getHttpStatusCode() == NOT_FOUND_HTTP_CODE) {
                    // get data from public lessons class
                    getPublicCategoryLessons(progressDialog);
                } else {
                    progressDialog.dismiss();
                    showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getUserLessonClass(progressDialog);
                        }
                    });
                }
            }
        });
    }

    private void storeSdkCustomData(ArrayList<QBCustomObject> userCustomObjects) {
        ArrayList<Lesson> realmLessons = new ArrayList<>();
        for (QBCustomObject custom : userCustomObjects) {
            Integer lesson_number = custom.getInteger("lesson_number");
            String lesson_title = custom.getString("lesson_title");
            Boolean isFinished = custom.getBoolean("isFinished");
            Boolean isLocked = custom.getBoolean("isLocked");
            String parentId = custom.getParentId();
            String lessonApiId = custom.getCustomObjectId();
            String publicLessonId = custom.getString("public_lesson_id");

            realmLessons.add(new Lesson(lesson_number, lesson_title, isFinished, isLocked, parentId, lessonApiId, publicLessonId));
        }

        copyListToRealm(realmLessons);
    }

    private void refreshAdapter(RealmResults<Lesson> lessons) {
        mAdapter = new LessonsAdapter(lessons, LessonsActivity.this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void getCategoryApiId() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            categoryApiId = bundle.getString(EXTRAS_CATEGORY_API_ID);
            categoryPrivateApiId = bundle.getString(EXTRAS_PUBLIC_CATEGORY_API_ID);
        }
    }


    private void configRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Lessons");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }
    }

    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(rootLayout, resId, e,
                R.string.dlg_retry, clickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_LESSONS_REQUEST_CODE && resultCode == RESULT_OK) {
            // got data that the lesson is finished, update lesson record in SDK
            saveProgress(data);
        }
    }

    private void saveProgress(Intent data) {
        ProgressDialog progressDialog = Utils.buildProgressDialog(this, "", "Loading...", false);
        progressDialog.show();

        String finishedLessonApiId = data.getStringExtra(EXTRAS_PRIVATE_LESSON_API_ID);
        String nextLessonApiId = data.getStringExtra(EXTRAS_PRIVATE_NEXT_LESSON_API_ID);

        QBCustomObject finishedLesson = new QBCustomObject();
        finishedLesson.setClassName(USERS_LESSONS_DATA);
        finishedLesson.put("isFinished", true);
        finishedLesson.setCustomObjectId(finishedLessonApiId);

        List<QBCustomObject> updatedCustomObjects = new LinkedList<>();

        realm.beginTransaction();
        if (nextLessonApiId != null && !nextLessonApiId.isEmpty()) {
            QBCustomObject nextLesson = new QBCustomObject();
            nextLesson.setClassName(USERS_LESSONS_DATA);
            nextLesson.put("isLocked", false);
            nextLesson.setCustomObjectId(nextLessonApiId);
            updatedCustomObjects.add(nextLesson);

            Lesson nextLessonRealm = realm.where(Lesson.class).equalTo("lessonApiId", nextLessonApiId).findFirst();
            if (nextLessonRealm != null) {
                nextLessonRealm.setLocked(false);
                realm.copyToRealmOrUpdate(nextLessonRealm);
            }
        }

        updatedCustomObjects.add(finishedLesson);

        Lesson finishedLessonRealm = realm.where(Lesson.class).equalTo("lessonApiId", finishedLessonApiId).findFirst();
        if (finishedLessonRealm != null) {
            finishedLessonRealm.setFinished(true);
            realm.copyToRealmOrUpdate(finishedLessonRealm);
        }
        realm.commitTransaction();
        QBCustomObjects.updateObjects(updatedCustomObjects).performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                QBCustomObject record = new QBCustomObject();
                record.setClassName(Common.USERS_CATEGORY_DATA);
                record.setCustomObjectId(categoryPrivateApiId);
                record.putInteger("progress", getProgress());
                QBCustomObjects.updateObject(record).performAsync(new QBEntityCallback<QBCustomObject>() {
                    @Override
                    public void onSuccess(QBCustomObject customObject, Bundle bundle) {
                        Category updatedCategory = realm.where(Category.class).equalTo("apiId", customObject.getCustomObjectId()).findFirst();
                        if (updatedCategory != null) {
                            realm.beginTransaction();
                            updatedCategory.setProgress(customObject.getInteger("progress"));
                            realm.commitTransaction();
                        }
                        RealmResults<Lesson> selectedCategoryLessons = getSelectedCategoryLessons();
                        refreshAdapter(selectedCategoryLessons);

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        progressDialog.dismiss();
                        showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                saveProgress(data);
                            }
                        });
                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveProgress(data);
                    }
                });
            }
        });

    }

    private int getProgress() {
        int finishedLessons = realm.where(Lesson.class).equalTo(PARENT_ID, categoryApiId).and().equalTo("isFinished", true).findAll().size();
        int totalLessons = realm.where(Lesson.class).equalTo(PARENT_ID, categoryApiId).findAll().size();
        return (int) (((double) finishedLessons / totalLessons) * 100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
