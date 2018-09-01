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
import com.example.william.harusem.holder.SpeakingCategoriesHolder;
import com.example.william.harusem.models.Category;
import com.example.william.harusem.ui.adapters.CategoryAdapter;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.Utils;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
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
    SpeakingCategoriesHolder categoriesHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        unbinder = ButterKnife.bind(this, view);
        categoriesHolder = SpeakingCategoriesHolder.getInstance();

        configRecyclerView();

        getDataFromSDK();

        return view;
    }

    private void getDataFromSDK() {

        ArrayList<Category> allCategories = categoriesHolder.getAllCategories();
        if (allCategories.size() > 0) {
            refreshAdapter(allCategories);
        } else {
            ProgressDialog progressDialog = Utils.buildProgressDialog(getContext(), "", "Loading...", false);
            progressDialog.show();
            getUserCategoryClass(progressDialog);
        }
    }

    private void getUserCategoryClass(ProgressDialog progressDialog) {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(0);
        requestBuilder.eq("user_id", QBChatService.getInstance().getUser().getId());

        QBCustomObjects.getObjects(USERS_CATEGORY_DATA,requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                if (getActivity() != null && isAdded()) {
                    if (qbCustomObjects.size() > 0) {
                        storeSdkCustomData(qbCustomObjects);
                        ArrayList<Category> allCategories = categoriesHolder.getAllCategories();
                        refreshAdapter(allCategories);
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
                addUserCategoriesData(categoriesCustomObjects, qbCustomObjects);
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
                    ArrayList<Category> newCategoriesList = new ArrayList<>();

//                    realm.beginTransaction();
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

//                        Category category = realm.where(Category.class).equalTo("apiId", apiId).findFirst();
                        Category newCategory = categoriesHolder.getCategoryById(apiId);

                        if (newCategory != null) {
                            newCategory.setLessonsCount(category_lessons_count);
                            newCategory.setCategoryDisplayName(category_name);
                            newCategory.setBgColor(Color.parseColor(category_color));
                            newCategory.setImageId(resId);
                            newCategory.setPublicCategoryId(public_category_id);
                            newCategory.setProgress(progress);
                            newCategory.setApiId(apiId);
                            categoriesHolder.updateCategory(newCategory);
                        } else {
                            newCategoriesList.add(new Category(resId, category_name, category_lessons_count, Color.parseColor(category_color), apiId, progress, public_category_id));
                        }

                    }

                    if (newCategoriesList.size() > 0) {
                        categoriesHolder.updateCategories(newCategoriesList);
                    }

                    ArrayList<Category> allCategories = categoriesHolder.getAllCategories();

                    refreshAdapter(allCategories);
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

    private void addUserCategoriesData(List<QBCustomObject> categoriesCustomObjects, ArrayList<QBCustomObject> qbCustomObjects) {
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
        ArrayList<Category> newCategoriesList = new ArrayList<>();
//        realm.beginTransaction();
        for (QBCustomObject custom : qbCustomObjects) {
            Integer category_lessons_count = custom.getInteger("category_lessons_count");
            String categoryName = custom.getString("category_name");
            String category_color = custom.getString("category_color");
            String icon = custom.getString("icon");
            String apiId = custom.getCustomObjectId();
            int progress = custom.getInteger("progress");
            String publicCategoryApi = custom.getString("public_category_id");

            int resID = getResources().getIdentifier(icon, "drawable", getActivity().getPackageName());

//            Category category = realm.where(Category.class).equalTo("apiId", apiId).findFirst();
            Category newCategory = categoriesHolder.getCategoryById(apiId);

            if (newCategory != null) {
                newCategory.setLessonsCount(category_lessons_count);
                newCategory.setCategoryDisplayName(categoryName);
                newCategory.setBgColor(Color.parseColor(category_color));
                newCategory.setImageId(resID);
                newCategory.setPublicCategoryId(publicCategoryApi);
                newCategory.setProgress(progress);
                newCategory.setApiId(apiId);
                categoriesHolder.updateCategory(newCategory);
            } else {
                newCategoriesList.add(new Category(resID, categoryName, category_lessons_count, Color.parseColor(category_color), apiId, progress, publicCategoryApi));
            }
        }

        if (newCategoriesList.size() > 0) {
//            realm.copyToRealmOrUpdate(newCategoriesList);
            categoriesHolder.updateCategories(newCategoriesList);
        }
    }


    private void refreshAdapter(ArrayList<Category> categories) {
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
        ArrayList<Category> allCategories = categoriesHolder.getAllCategories();
        CategoryAdapter adapter = new CategoryAdapter(allCategories, getActivity());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
