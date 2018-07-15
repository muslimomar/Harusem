package com.example.william.harusem.ui.activities;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.william.harusem.R;
import com.example.william.harusem.manager.DialogsManager;
import com.example.william.harusem.ui.adapters.GroupUsersAdapter;
import com.example.william.harusem.util.ChatHelper;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.Toaster;
import com.example.william.harusem.util.Utils;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity extends AppCompatActivity {

    public static final String USERS_ID_LIST = "USERS_ID_LIST";
    private static final String TAG = CreateGroupActivity.class.getSimpleName();

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
    ArrayList<QBUser> qbUsers;
    ArrayList<QBUser> qbUsersWithoutCurrent;
    @BindView(R.id.root_layout)
    LinearLayout rootLayout;
    DialogsManager dialogsManager;
    private int REQUEST_CODE = 44;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        configureActionBar();

        dialogsManager = new DialogsManager();

        getQbUsers();
        getQbUsersWithoutCurrent();
        fillParticipants(qbUsersWithoutCurrent);
        participantsTv.setText(getString(R.string.participants, qbUsersWithoutCurrent.size()));

    }

    private void getQbUsers() {
        qbUsers = (ArrayList<QBUser>) getIntent().getSerializableExtra(USERS_ID_LIST);
        qbUsersWithoutCurrent = (ArrayList<QBUser>) getIntent().getSerializableExtra(USERS_ID_LIST);
    }

    private void getQbUsersWithoutCurrent() {
        QBUser currentUser = QBChatService.getInstance().getUser();
        qbUsersWithoutCurrent.remove(currentUser);

    }

    private void fillParticipants(ArrayList<QBUser> qbUsers) {
        GroupUsersAdapter adapter = new GroupUsersAdapter(CreateGroupActivity.this, qbUsers);
        gridView.setAdapter(adapter);
    }

    private void configureActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.new_group));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_group_menu, menu);
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
        String groupName = groupNameEt.getText().toString().trim();
        if (groupName.isEmpty()) {
            Toaster.shortToast(R.string.type_group_name);
            return;
        }

        if (photoFile != null) {
            createDialogWithPhoto(qbUsers, groupName);
        } else {
            createDialog(qbUsers, groupName);
        }

    }

    private void createDialog(final ArrayList<QBUser> selectedUsers, String groupName) {
        ProgressDialog progressDialog = Utils.buildProgressDialog(this, "", getString(R.string.please_wait_2), false);
        progressDialog.show();

        ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers, new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle bundle) {
                        endCreatingDialog(progressDialog, dialog);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        progressDialog.dismiss();
                        showErrorSnackbar(R.string.error_creating_group, e, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createDialog(selectedUsers, groupName);
                            }
                        });

                    }
                }, groupName, null
        );

    }

    private void createDialogWithPhoto(final ArrayList<QBUser> selectedUsers, String groupName) {
        ProgressDialog progressDialog = Utils.buildProgressDialog(this, "", getString(R.string.please_wait), false);
        progressDialog.show();

        QBContent.uploadFileTask(photoFile, true, null).performAsync(new QBEntityCallback<QBFile>() {

            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
                ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers, new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog dialog, Bundle bundle) {
                                endCreatingDialog(progressDialog, dialog);
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                progressDialog.dismiss();
                                showErrorSnackbar(R.string.error_creating_group, e, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        createDialogWithPhoto(selectedUsers, groupName);
                                    }
                                });

                            }
                        }, groupName, qbFile.getId().toString()
                );


            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                showErrorSnackbar(R.string.error_update_group_photo, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createDialogWithPhoto(qbUsers, groupName);
                    }
                });
            }
        });


    }

    private void endCreatingDialog(ProgressDialog progressDialog, QBChatDialog dialog) {
        progressDialog.dismiss();
        sendSystemMessage(dialog);
        redirectToChatActivity(dialog);
    }

    private void redirectToChatActivity(QBChatDialog dialog) {
        Intent intent = new Intent(CreateGroupActivity.this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DIALOG, dialog);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendSystemMessage(QBChatDialog dialog) {
        QBSystemMessagesManager systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
        dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, dialog);
    }


    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(rootLayout, resId, e,
                R.string.dlg_retry, clickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imagePath = data.getData();
            CropImage.activity(imagePath).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri imageUri = result.getUri();

                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    File file = new File(Environment.getExternalStorageDirectory() + "/myimage.png");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bos.toByteArray());
                    fos.flush();
                    fos.close();

                    // Get file size
                    int imageSizeKB = (int) (file.length() / 1024);
                    if (imageSizeKB >= (1024 * 100)) {
                        Toast.makeText(this, R.string.img_large, Toast.LENGTH_SHORT).show();
                    }

                    photoFile = file;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                groupCircleIv.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "onActivityResult: ", result.getError());
                Toast.makeText(this, "" + result.getError(), Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.group_circle_iv)
    public void setGroupCircleIv(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_img)), REQUEST_CODE);
    }

}

