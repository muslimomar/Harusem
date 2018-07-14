package com.example.william.harusem.ui.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.ui.adapters.GroupUsersAdapter;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity extends AppCompatActivity {

    @BindView(R.id.group_circle_iv)
    CircleImageView groupCircleIv;
    @BindView(R.id.group_name_et)
    EditText groupNameEt;
    @BindView(R.id.group_upper_layout)
    LinearLayout groupUpperLayout;
    @BindView(R.id.participants_tv)
    TextView participantsTv;
    @BindView(R.id.grid_view)
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        configureActionBar();


        fillParticipants();


    }

    private void fillParticipants() {

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                GroupUsersAdapter adapter = new GroupUsersAdapter(CreateGroupActivity.this, qbUsers);
                gridView.setAdapter(adapter);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }

    private void configureActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("New Group");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_selected_opponents, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.create_action:
                onGroupCreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onGroupCreate() {

    }
}

