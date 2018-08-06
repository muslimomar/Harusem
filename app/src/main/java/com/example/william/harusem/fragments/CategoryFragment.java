package com.example.william.harusem.fragments;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.william.harusem.R;
import com.example.william.harusem.models.Category;
import com.example.william.harusem.ui.adapters.CategoryAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CategoryFragment extends Fragment {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;
    CategoryAdapter adapter;
    ArrayList<Category> categories = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        unbinder = ButterKnife.bind(this, view);

        configRecyclerView();
        categories.add(new Category(R.drawable.ic_bank, "Bank", Color.parseColor("#66bb6a")));
        categories.add(new Category(R.drawable.ic_business, "Business", Color.parseColor("#42a5f5")));
        categories.add(new Category(R.drawable.ic_hospital, "Hospital", Color.parseColor("#7e57c2")));
        categories.add(new Category(R.drawable.ic_family, "Family", Color.parseColor("#ef5350")));

        adapter = new CategoryAdapter(categories, getActivity());
        recyclerView.setAdapter(adapter);

        return view;
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
