package com.example.william.harusem.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.example.william.harusem.R;
import com.example.william.harusem.models.Lesson;
import com.example.william.harusem.ui.adapters.CategoryAdapter;
import com.example.william.harusem.ui.adapters.LessonsAdapter;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.Utils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.william.harusem.common.Common.CATEGORY_API_NAME;
import static com.example.william.harusem.common.Common.CATEGORY_API_NAME_REALM;

public class LessonsActivity extends AppCompatActivity {

    LessonsAdapter mAdapter;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    String categoryName;
    @BindView(R.id.root_layout)
    LinearLayout rootLayout;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);
        ButterKnife.bind(this);
        hideActionBar();
        realm = Realm.getDefaultInstance();
        configRecyclerView();
        getCategoryName();

        getLessons();

    }

    private void getLessons() {
        ProgressDialog progressDialog = Utils.buildProgressDialog(this, "", "Loading...", false);
        progressDialog.show();

        RealmResults<Lesson> realmLessons = realm.where(Lesson.class).equalTo(CATEGORY_API_NAME_REALM, categoryName).findAll();
        if (realmLessons.size() > 0) {
            refreshAdapter(realmLessons);
            progressDialog.dismiss();
        } else {
            getDataFromSDK(progressDialog);
        }

    }

    private void getDataFromSDK(ProgressDialog progressDialog) {
        QBCustomObjects.getObjects("Category_Lessons").performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                ArrayList<Lesson> lessons = new ArrayList<>();

                for (QBCustomObject custom : qbCustomObjects) {
                    Integer lesson_number = custom.getInteger("lesson_number");
                    String lesson_title = custom.getString("lesson_title");
                    Boolean isFinished = custom.getBoolean("isFinished");
                    Boolean isLocked = custom.getBoolean("isLocked");
                    String parentId = custom.getParentId();
                    lessons.add(new Lesson(lesson_number, lesson_title, isFinished, isLocked, parentId));
                }

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(lessons);
                realm.commitTransaction();

                RealmResults<Lesson> realmResults = realm.where(Lesson.class).equalTo(CATEGORY_API_NAME_REALM, categoryName).findAll();
                refreshAdapter(realmResults);

                progressDialog.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.buildAlertDialogButton("Error", e.getMessage(), false, LessonsActivity.this, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                    }
                });
            }
        });

    }

    private void refreshAdapter(RealmResults<Lesson> lessons) {
        mAdapter = new LessonsAdapter(lessons, LessonsActivity.this);
        recyclerView.setAdapter(mAdapter);
    }

    private void getCategoryName() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            categoryName = bundle.getString(CategoryAdapter.EXTRAS_CATEGORY_NAME);
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
            actionBar.hide();
        }
    }

    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(rootLayout, resId, e,
                R.string.dlg_retry, clickListener);
    }

}
