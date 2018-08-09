package com.example.william.harusem.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.william.harusem.R;
import com.example.william.harusem.common.Common;
import com.example.william.harusem.models.Category;
import com.example.william.harusem.models.Lesson;
import com.example.william.harusem.ui.adapters.CategoryAdapter;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.Utils;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.william.harusem.common.Common.USERS_CATEGORY_DATA;

public class CategoryFragment extends Fragment {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;
    CategoryAdapter adapter;
    @BindView(R.id.root_layout)
    LinearLayout rootLayout;
    Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        unbinder = ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();
        configRecyclerView();

        getDataFromSDK();

        return view;
    }

    private void getDataFromSDK() {
        RealmResults<Category> realmResults = realm.where(Category.class).findAll();
        if (realmResults.size() > 0) {
            refreshAdapter(realmResults);
        } else {
            ProgressDialog progressDialog = Utils.buildProgressDialog(getContext(), "", "Loading...", false);
            progressDialog.show();
            getUserCategoryClass(progressDialog);
        }
    }

    private void getUserCategoryClass(ProgressDialog progressDialog) {
        QBCustomObjects.getObjects(USERS_CATEGORY_DATA).performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                if (getActivity() != null && isAdded()) {
                    if (qbCustomObjects.size() > 0) {

                        storeSdkCustomData(qbCustomObjects);
                        RealmResults<Category> realmResults = realm.where(Category.class).findAll();
                        refreshAdapter(realmResults);
                        progressDialog.dismiss();
                    } else {
                        getPublicCategories(progressDialog);
                    }
                }
            }

            @Override
            public void onError(QBResponseException e) {
                if (e.getHttpStatusCode() == Common.NOT_FOUND_HTTP_CODE) {
                    if (getActivity() != null && isAdded()) {
                        getPublicCategories(progressDialog);
                    }
                } else {
                    progressDialog.dismiss();
                    showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getUserCategoryClass(progressDialog);
                        }
                    });
                }

            }
        });

    }

    private void getPublicCategories(ProgressDialog progressDialog) {
        QBCustomObjects.getObjects("Categories").performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                List<QBCustomObject> categoriesCustomObjects = new ArrayList<>();
                // add data to list, to store them in realm later
                addUserCatgoriesData(categoriesCustomObjects, qbCustomObjects);
                createUserCategoryClass(categoriesCustomObjects, progressDialog);

            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getDataFromSDK();
                    }
                });

            }
        });

    }

    private void createUserCategoryClass(List<QBCustomObject> categoriesCustomObjects, ProgressDialog progressDialog) {
        QBCustomObjects.createObjects(categoriesCustomObjects).performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> customObjects, Bundle bundle) {
                if (getActivity() != null && isAdded()) {
                    ArrayList<Category> realmCategories = new ArrayList<>();

                    realm.beginTransaction();
                    for (QBCustomObject custom : customObjects) {
                        Integer category_lessons_count = custom.getInteger("category_lessons_count");
                        String category_name = custom.getString("category_name");
                        String category_color = custom.getString("category_color");
                        String icon = custom.getString("icon");
                        String public_category_id = custom.getString("public_category_id");
                        Integer progress = custom.getInteger("progress");
                        String apiId = custom.getCustomObjectId();

                        int resId = getResources().getIdentifier(icon,
                                "drawable", getActivity().getPackageName());


                        Category category = realm.where(Category.class).equalTo("apiId", apiId).findFirst();
                        if (category != null) {
                            category.setLessonsCount(category_lessons_count);
                            category.setCategoryDisplayName(category_name);
                            category.setBgColor(Color.parseColor(category_color));
                            category.setImageId(resId);
                            category.setPublicCategoryId(public_category_id);
                            category.setProgress(progress);
                            category.setApiId(apiId);
                        } else {
                            realmCategories.add(new Category(resId, category_name, category_lessons_count, Color.parseColor(category_color), apiId, progress, public_category_id));
                        }

                    }

                    if (realmCategories.size() > 0) {
                        realm.copyToRealmOrUpdate(realmCategories);
                    }
                    realm.commitTransaction();

                    RealmResults<Category> realmResults = realm.where(Category.class).findAll();

                    refreshAdapter(realmResults);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getDataFromSDK();
                    }
                });
            }
        });

    }

    private void addUserCatgoriesData(List<QBCustomObject> categoriesCustomObjects, ArrayList<QBCustomObject> qbCustomObjects) {
        for (QBCustomObject custom : qbCustomObjects) {
            QBCustomObject userLessonData = new QBCustomObject();
            userLessonData.setClassName(USERS_CATEGORY_DATA);
            userLessonData.putInteger("category_lessons_count", custom.getInteger("category_lessons_count"));
            userLessonData.putString("category_name", custom.getString("category_name"));
            userLessonData.putString("category_color", custom.getString("category_color"));
            userLessonData.putString("icon", custom.getString("icon"));
            userLessonData.putString("public_category_id", custom.getCustomObjectId());
            userLessonData.putInteger("progress", custom.getInteger("progress"));
            userLessonData.setUserId(QBChatService.getInstance().getUser().getId());
            userLessonData.setParentId(custom.getParentId());

            categoriesCustomObjects.add(userLessonData);
        }

    }

    private void storeSdkCustomData(ArrayList<QBCustomObject> qbCustomObjects) {
        ArrayList<Category> realmCategories = new ArrayList<>();
        realm.beginTransaction();
        for (QBCustomObject custom : qbCustomObjects) {
            Integer category_lessons_count = custom.getInteger("category_lessons_count");
            String categoryName = custom.getString("category_name");
            String category_color = custom.getString("category_color");
            String icon = custom.getString("icon");
            String apiId = custom.getCustomObjectId();
            int progress = custom.getInteger("progress");
            String publicCategoryApi = custom.getString("public_category_id");

            int resID = getResources().getIdentifier(icon,
                    "drawable", getActivity().getPackageName());

            Category category = realm.where(Category.class).equalTo("apiId", apiId).findFirst();
            if (category != null) {
                category.setLessonsCount(category_lessons_count);
                category.setCategoryDisplayName(categoryName);
                category.setBgColor(Color.parseColor(category_color));
                category.setImageId(resID);
                category.setPublicCategoryId(publicCategoryApi);
                category.setProgress(progress);
                category.setApiId(apiId);
            } else {
                realmCategories.add(new Category(resID, categoryName, category_lessons_count, Color.parseColor(category_color), apiId, progress, publicCategoryApi));
            }
        }

        if (realmCategories.size() > 0) {
            realm.copyToRealmOrUpdate(realmCategories);
        }
        realm.commitTransaction();
    }


    private void refreshAdapter(RealmResults<Category> categories) {
        adapter = new CategoryAdapter(categories, getActivity());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(rootLayout, resId, e,
                R.string.dlg_retry, clickListener);
    }

    private void configRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Category> all = realm.where(Category.class).findAll();
        CategoryAdapter adapter = new CategoryAdapter(all, getActivity());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
