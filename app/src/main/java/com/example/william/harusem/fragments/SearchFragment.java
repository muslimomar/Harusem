package com.example.william.harusem.fragments;

import android.os.Bundle;
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

import com.example.william.harusem.R;
import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.ui.activities.UsersActivity;
import com.example.william.harusem.ui.adapters.UsersAdapter;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SearchFragment extends Fragment {

    private static final String TAG = UsersActivity.class.getSimpleName();
    private static ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<>();
    Unbinder unbinder;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.toolbar_search)
    Toolbar toolbar;
    @BindView(R.id.search_recyclerview)
    RecyclerView recyclerView;

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

        if (getActivity() != null && isAdded()) {
            retrieveAllUser();
        }

        configRecyclerView();


        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                retrieveAllUser();
            }

            @Override
            public void onSearchViewClosed() {
                retrieveAllUser();
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


    public void retrieveAllUser() {

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
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

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "1 onError: ", e);
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
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLayoutManager);
    }


}
