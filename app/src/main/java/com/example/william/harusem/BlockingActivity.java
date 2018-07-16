package com.example.william.harusem;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.william.harusem.holder.QBUsersHolder;
import com.example.william.harusem.ui.adapters.BlockingAdapter;
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

    @BindView(R.id.blocked_swipe)
    SwipeRefreshLayout swipe;

    QBPrivacyListsManager privacyListsManager;


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
        QBPrivacyList publicPrivacyList = getPublicPrivacyList();

        if (publicPrivacyList != null) {
            getBlockedUsers(publicPrivacyList);
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
                    adapter = new BlockingAdapter(qbUsers, privacyListsManager);
                    blockListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e(TAG, "onError: ", e);
                }
            });
        } else {
            adapter = new BlockingAdapter(usersByIds, privacyListsManager);
            blockListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

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

}
