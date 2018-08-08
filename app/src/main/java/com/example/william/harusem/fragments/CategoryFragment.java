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
import com.example.william.harusem.models.Category;
import com.example.william.harusem.models.SpeakingDialog;
import com.example.william.harusem.ui.adapters.CategoryAdapter;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.Utils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.Performer;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.william.harusem.common.Common.CATEGORY_API_NAME;

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

        RealmResults<Category> categoryRealmResults = realm.where(Category.class).findAll();
        if (categoryRealmResults.size() > 0) {
            refreshAdapter(categoryRealmResults);
        } else {
            fillData();
        }

        return view;
    }

    private void fillData() {
        ProgressDialog progressDialog = Utils.buildProgressDialog(getContext(), "", "Loading...", false);
        progressDialog.show();

        QBCustomObjects.getObjects("Categories").performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                if (getActivity() != null && isAdded()) {
                    ArrayList<Category> categories = new ArrayList<>();
                    for (QBCustomObject custom : qbCustomObjects) {
                        Integer dialog_count = custom.getInteger("category_dialog_count");
                        String displayName = custom.getString("category_name");
                        String category_color = custom.getString("category_color");
                        String icon = custom.getString("icon");
                        String parentId = custom.getParentId();

                        int resID = getResources().getIdentifier(icon,
                                "drawable", getActivity().getPackageName());

                        categories.add(new Category(resID, displayName, parentId, dialog_count, Color.parseColor(category_color)));
                    }

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(categories);
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
                        fillData();
                    }
                });
            }
        });

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
}
