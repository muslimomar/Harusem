package com.example.william.harusem.ui.activities;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.william.harusem.R;
import com.example.william.harusem.helper.QBFriendListHelper;
import com.example.william.harusem.ui.adapters.FriendsAdapter;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FriendsActivity extends AppCompatActivity {

    private static final String TAG = FriendsActivity.class.getSimpleName();

    RecyclerView.LayoutManager mLayoutManager;
    @BindView(R.id.search_view_friends)
    MaterialSearchView searchView;
    @BindView(R.id.toolbar_search_friends)
    Toolbar toolbar;
    @BindView(R.id.friends_list)
    RecyclerView friendsRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    FriendsAdapter adapter;
    QBFriendListHelper qbFriendListHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ButterKnife.bind(this);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search In Your Friend List");

        configRecyclerView();

        qbFriendListHelper = new QBFriendListHelper(this);

        retrieveFriendsFromRoster();

        swipeLayoutRefresh();


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                //Do some magic
                if (newText != null && !newText.isEmpty()) {


                    progressBar.setVisibility(View.VISIBLE);
                    Collection<Integer> allFriends = qbFriendListHelper.getAllFriends();

                    QBUsers.getUsersByIDs(allFriends, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
                        @Override
                        public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                            progressBar.setVisibility(View.GONE);
                            adapter = new FriendsAdapter(qbUsers, FriendsActivity.this);


                            ArrayList<QBUser> lstFound = new ArrayList<>();
                            for (QBUser item : qbUsers) {
                                if (item.getFullName().toLowerCase().contains(newText.toLowerCase()))
                                    lstFound.add(item);

                            }

//                          UsersAdapter adapter = new UsersAdapter(getContext(),android.R.layout.simple_list_item_1,lstFound);
                            FriendsAdapter adapter = new FriendsAdapter(lstFound, FriendsActivity.this);
                            friendsRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            if (mSwipeRefreshLayout.isRefreshing()) {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }

                        }

                        @Override
                        public void onError(QBResponseException e) {
                            progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "onError: ", e);
                        }
                    });


                } else {
                    // if search text is null
                    // return default
//                    FriendsAdapter adapter = new FriendsAdapter(qbFriendListHelper.getAllFriends(), FriendsActivity.this);
//                    friendsRecyclerView.setAdapter(adapter);
                    retrieveFriendsFromRoster();
                }

                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
//                FriendsAdapter adapter = new FriendsAdapter(qbFriendListHelper.getAllFriends(), FriendsActivity.this);
//                friendsRecyclerView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
                retrieveFriendsFromRoster();
            }
        });

    }


    public void retrieveFriendsFromRoster() {
        progressBar.setVisibility(View.VISIBLE);
        Collection<Integer> allFriends = qbFriendListHelper.getAllFriends();

        QBUsers.getUsersByIDs(allFriends, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                progressBar.setVisibility(View.GONE);
                adapter = new FriendsAdapter(qbUsers, FriendsActivity.this);
                friendsRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "onError: ", e);
            }
        });

    }


    private void configRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        friendsRecyclerView.setLayoutManager(mLayoutManager);
    }


    private void swipeLayoutRefresh() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveFriendsFromRoster();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends_search_menu_item, menu);
        MenuItem item = menu.findItem(R.id.action_search_friends);
        searchView.setMenuItem(item);

        return true;
    }
}