package com.example.william.harusem;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.ui.adapters.BlockingAdapter;
import com.example.william.harusem.util.ErrorUtils;
import com.quickblox.chat.JIDHelper;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivacyListsManager;
import com.quickblox.chat.model.QBPrivacyList;
import com.quickblox.chat.model.QBPrivacyListItem;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlockingActivity extends AppCompatActivity {

    private static final String TAG = BlockingActivity.class.getSimpleName();
    @BindView(R.id.block_list_view)
    RecyclerView blockListView;
    BlockingAdapter adapter;
    QBPrivacyListsManager privacyListsManager;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.root_layout)
    RelativeLayout layoutRoot;
    @BindView(R.id.empty_tv)
    TextView emptyTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocking);
        ButterKnife.bind(this);
        setTitle(getString(R.string.block_activity_block_list));


        privacyListsManager = QBChatService.getInstance().getPrivacyListsManager();
        configRecyclerView();

        fillAdapter();
    }

    private void configRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        blockListView.setHasFixedSize(true);
        blockListView.setLayoutManager(mLayoutManager);
    }

    private void fillAdapter() {
        progressBar.setVisibility(View.VISIBLE);
        QBPrivacyList publicPrivacyList = getPublicPrivacyList();

        if (publicPrivacyList != null) {
            getBlockedUsers(publicPrivacyList);
        } else {
            progressBar.setVisibility(View.GONE);
            showEmptyLayout();
        }
    }

    private void getBlockedUsers(QBPrivacyList publicPrivacyList) {
        ArrayList<Integer> userIds = new ArrayList<>();
        Collection<Integer> collection = new ArrayList<>();

        publicPrivacyList.getItems();
        for (QBPrivacyListItem item : publicPrivacyList.getItems()) {
            int id = JIDHelper.INSTANCE.parseUserId(item.getValueForType());
            userIds.add(id);
            collection.add(id);
        }

        List<QBUser> usersByIds = QBUsersHolder.getInstance().getUsersByIds(userIds);
        if (publicPrivacyList.getItems().size() != usersByIds.size()) {
            QBUsers.getUsersByIDs(collection, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                    progressBar.setVisibility(View.GONE);
                    adapter = new BlockingAdapter(qbUsers, privacyListsManager);
                    blockListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(QBResponseException e) {
                    progressBar.setVisibility(View.GONE);
                    showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fillAdapter();
                        }
                    });
                }
            });
        } else {
            adapter = new BlockingAdapter(usersByIds, privacyListsManager);
            blockListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }

    }

    private QBPrivacyList getPublicPrivacyList() {

        QBPrivacyList publicList = null;

        try {
            publicList = privacyListsManager.getPrivacyList("public");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }

        return publicList;
    }

    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(layoutRoot, resId, e,
                R.string.dlg_retry, clickListener);
    }

    public void showEmptyLayout() {
        emptyTv.setVisibility(View.VISIBLE);
        blockListView.setVisibility(View.GONE);
    }

    public void hideEmptyLayout() {
        emptyTv.setVisibility(View.GONE);
        blockListView.setVisibility(View.VISIBLE);
    }

}
