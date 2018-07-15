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
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.Toaster;
import com.example.william.harusem.util.Utils;
import com.quickblox.chat.QBChatService;
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

import static com.example.william.harusem.ui.activities.SelectUsersActivity.EXTRA_GROUP_NAME;
import static com.example.william.harusem.ui.activities.SelectUsersActivity.EXTRA_QB_USERS;
import static com.example.william.harusem.ui.activities.SelectUsersActivity.EXTRA_QB_USER_PHOTO;

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
    @BindView(R.id.root_layout)
    LinearLayout rootLayout;
    DialogsManager dialogsManager;
    String photoId;
    private int REQUEST_CODE = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        configureActionBar();

        dialogsManager = new DialogsManager();

        getQbUsers();

        ArrayList<QBUser> qbUsersWithoutCurrent = new ArrayList<>();
        for (QBUser user : qbUsers) {
            if(!QBChatService.getInstance().getUser().equals(user))
                qbUsersWithoutCurrent.add(user);
        }

        fillParticipants(qbUsersWithoutCurrent);
        participantsTv.setText(getString(R.string.participants, qbUsersWithoutCurrent.size()));

    }

    private void getQbUsers() {
        qbUsers = (ArrayList<QBUser>) getIntent().getSerializableExtra(USERS_ID_LIST);
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
        setTitle("New Group");
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
            Toaster.shortToast("Please type a name for the group!");
            return;
        }

        redirectToDialogFragment(groupName);
    }

    private void redirectToDialogFragment(String groupName) {
        Intent result = new Intent();
        result.putExtra(EXTRA_QB_USERS, qbUsers);
        result.putExtra(EXTRA_QB_USER_PHOTO, photoId);
        result.putExtra(EXTRA_GROUP_NAME, groupName);
        setResult(RESULT_OK, result);
        finish();
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
                ProgressDialog progressDialog = Utils.buildProgressDialog(this, "", "Please Wait...", false);
                progressDialog.show();


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
                        Toast.makeText(this, R.string.img_too_big, Toast.LENGTH_SHORT).show();
                    }

                    uploadPhoto(progressDialog, file);

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

    private void uploadPhoto(ProgressDialog progressDialog, File file) {
        QBContent.uploadFileTask(file, true, null).performAsync(new QBEntityCallback<QBFile>() {

            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
                photoId = qbFile.getId().toString();
                progressDialog.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.dismiss();
                showErrorSnackbar(R.string.error_uploading_photo, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadPhoto(progressDialog, file);
                    }
                });
            }
        });
    }

    @OnClick(R.id.group_circle_iv)
    public void setGroupCircleIv(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select a Picture"), REQUEST_CODE);
    }

}

