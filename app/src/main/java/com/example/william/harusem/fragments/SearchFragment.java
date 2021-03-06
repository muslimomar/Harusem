package com.example.william.harusem.fragments;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.example.william.harusem.R;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.ui.adapters.UsersAdapter;
import com.example.william.harusem.util.ErrorUtils;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.Consts;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class SearchFragment extends Fragment {

    static int userNumber = 1;
    private static final String TAG = SearchFragment.class.getSimpleName();
    private static ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<>();
    Unbinder unbinder;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.toolbar_search)
    Toolbar toolbar;
    @BindView(R.id.search_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.recycler_viewRL)
    RelativeLayout relativeLayout;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);


        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
        setHasOptionsMenu(true);

        showPb();
        hideLayout();
        if (getActivity() != null && isAdded()) {
            retrieveAllUser(1);
        }

        configRecyclerView();


        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                retrieveAllUser(1);
            }

            @Override
            public void onSearchViewClosed() {
                retrieveAllUser(1);
            }
        });


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    QBUsers.getUsersByFullName(query, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
                        @Override
                        public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                            QBUsersHolder.getInstance().putUsers(qbUsers);

                            ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<>();
                            for (QBUser user : qbUsers) {

                                if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin())) {

                                    qbUserWithoutCurrent.add(user);
                                }
                            }

                            updateAdapter(qbUserWithoutCurrent);

                            hidePb();
                            showLayout();
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Log.e(TAG, "1 onError: ", e);
                        }
                    });

                } else {
                    // if search text is null
                    // return default
                    updateAdapter(qbUserWithoutCurrent);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                if (newText != null && !newText.isEmpty()) {
                    QBUsers.getUsersByFullName(newText, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
                        @Override
                        public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                            hidePb();
                            showLayout();


                            QBUsersHolder.getInstance().putUsers(qbUsers);

                            ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<>();
                            for (QBUser user : qbUsers) {

                                if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin())) {

                                    qbUserWithoutCurrent.add(user);
                                }
                            }

                            updateAdapter(qbUserWithoutCurrent);
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Log.e(TAG, "1 onError: ", e);
                        }
                    });

                } else {
                    // if search text is null
                    // return default
                    updateAdapter(qbUserWithoutCurrent);


                }
                return true;
            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_search_menu_item, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                searchView.setMenuItem(item);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void retrieveAllUser(int page) {

        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(page);
        pagedRequestBuilder.setPerPage(100);


        QBUsers.getUsers(pagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                hidePb();
                showLayout();

                QBUsersHolder.getInstance().putUsers(qbUsers);

                ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<>();
                for (QBUser user : qbUsers) {


                    if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin())) {

                        qbUserWithoutCurrent.add(user);
                    }

                    ++userNumber;
                }


                int currentPage = bundle.getInt(Consts.CURR_PAGE);
                int totalEntries = bundle.getInt(Consts.TOTAL_ENTRIES);


                if(userNumber < totalEntries){
                    retrieveAllUser(currentPage+1);

                }

                updateAdapter(qbUserWithoutCurrent);


            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        retrieveAllUser(1);
                    }
                });
            }
        });

    }

    private void updateAdapter(ArrayList<QBUser> qbUserWithoutCurrent) {
        if (getActivity() != null && isAdded()) {
            UsersAdapter adapter = new UsersAdapter(qbUserWithoutCurrent, getContext());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void configRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLayoutManager);
    }


    public void showPb() {
        if (getActivity() != null && isAdded()) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }


    public void hideLayout() {
        relativeLayout.setVisibility(View.GONE);
    }

    public void showLayout() {
        if (getActivity() != null && isAdded()){
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hidePb() {
        if (getActivity() != null && isAdded()) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }


    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(rootLayout, resId, e,
                R.string.dlg_retry, clickListener);
    }

}
