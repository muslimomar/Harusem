package com.example.william.harusem.ui.activities;import android.app.ProgressDialog;import android.content.DialogInterface;import android.content.Intent;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.net.Uri;import android.os.Bundle;import android.os.Environment;import android.support.v7.app.AlertDialog;import android.support.v7.app.AppCompatActivity;import android.support.v7.widget.RecyclerView;import android.support.v7.widget.Toolbar;import android.view.LayoutInflater;import android.view.View;import android.widget.EditText;import android.widget.ImageButton;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.TextView;import android.widget.Toast;import com.bhargavms.dotloader.DotLoader;import com.example.william.harusem.R;import com.example.william.harusem.ui.adapters.ChatAdapter;import com.example.william.harusem.util.Utils;import com.quickblox.chat.QBRestChatService;import com.quickblox.chat.model.QBChatDialog;import com.quickblox.chat.request.QBDialogRequestBuilder;import com.quickblox.content.QBContent;import com.quickblox.content.model.QBFile;import com.quickblox.core.QBEntityCallback;import com.quickblox.core.exception.QBResponseException;import com.quickblox.core.request.QBRequestUpdateBuilder;import java.io.ByteArrayOutputStream;import java.io.File;import java.io.FileNotFoundException;import java.io.FileOutputStream;import java.io.IOException;import java.io.InputStream;import butterknife.BindView;import butterknife.ButterKnife;import butterknife.OnClick;import de.hdodenhof.circleimageview.CircleImageView;public class MessageActivity extends AppCompatActivity {    public static final String TAG = MessageActivity.class.getSimpleName();    private static final int REQUEST_CODE = 40;    @BindView(R.id.toolbar)    Toolbar toolbar;    @BindView(R.id.bottom_bar)    LinearLayout bottomBar;    @BindView(R.id.small_toolbar)    Toolbar smallToolbar;    @BindView(R.id.chat_recycler_view)    RecyclerView chatListRecyclerView;    @BindView(R.id.message_input_et)    EditText messageInputEt;    @BindView(R.id.status_sign_iv)    ImageView statusSignIv;    @BindView(R.id.send_txt_btn)    ImageButton sendTxtBtn;    @BindView(R.id.opponent_name_tv)    TextView friendNameTv;    QBChatDialog receivedQBChatDialog;    ChatAdapter adapter;    @BindView(R.id.dialog_info)    LinearLayout dialogInfoLayout;    @BindView(R.id.img_online_ocunt)    ImageView imageOnlineCount;    @BindView(R.id.txt_online_count)    TextView textOnlineCount;    @BindView(R.id.dialog_avatar)    CircleImageView dialogAvatar;    @BindView(R.id.dot_loader)    DotLoader dotLoader;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_chat);        ButterKnife.bind(this);    }    private void editGroupName() {        LayoutInflater inflater = LayoutInflater.from(this);        View view = inflater.inflate(R.layout.edit_group_dialog, null);        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);        alertDialogBuilder.setView(view);        final EditText groupName = view.findViewById(R.id.group_name_et);        alertDialogBuilder.setCancelable(false)                .setPositiveButton("OK", new DialogInterface.OnClickListener() {                    @Override                    public void onClick(DialogInterface dialogInterface, int i) {                        receivedQBChatDialog.setName(groupName.getText().toString().trim());                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();                        QBRestChatService.updateChatDialog(receivedQBChatDialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {                            @Override                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {                                Toast.makeText(MessageActivity.this, "", Toast.LENGTH_SHORT).show();                                friendNameTv.setText(qbChatDialog.getName());                            }                            @Override                            public void onError(QBResponseException e) {                                Toast.makeText(MessageActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();                            }                        });                    }                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {            @Override            public void onClick(DialogInterface dialogInterface, int i) {                dialogInterface.cancel();            }        });        // Create alert dialog        AlertDialog alertDialog = alertDialogBuilder.create();        alertDialog.show();    }    @OnClick(R.id.dialog_avatar)    public void setDialogAvatar(View view) {        Intent selectImage = new Intent();        selectImage.setType("image/*");        selectImage.setAction(Intent.ACTION_GET_CONTENT);        startActivityForResult(Intent.createChooser(selectImage, "Select a picture"), REQUEST_CODE);    }    @Override    protected void onActivityResult(int requestCode, int resultCode, Intent data) {        if (resultCode == RESULT_OK) {            if (requestCode == REQUEST_CODE) {                Uri selectedImage = data.getData();                final ProgressDialog progressDialog = Utils.buildProgressDialog(this, "", "Please Wait...", false);                progressDialog.show();                try {                    InputStream inputStream = getContentResolver().openInputStream(selectedImage);                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);                    ByteArrayOutputStream bos = new ByteArrayOutputStream();                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);                    File file = new File(Environment.getExternalStorageDirectory() + "/image.png");                    FileOutputStream fileOut = new FileOutputStream(file);                    fileOut.write(bos.toByteArray());                    fileOut.close();                    int imageSizeKb = (int) file.length() / 1024;                    if (imageSizeKb >= (1024 * 100)) {                        Toast.makeText(this, "Error size", Toast.LENGTH_SHORT).show();                        return;                    }                    QBContent.uploadFileTask(file, true, null)                            .performAsync(new QBEntityCallback<QBFile>() {                                @Override                                public void onSuccess(QBFile qbFile, Bundle bundle) {                                    receivedQBChatDialog.setPhoto(qbFile.getId().toString());                                    //Update Chat Dialog                                    QBRequestUpdateBuilder requestBuilder = new QBDialogRequestBuilder();                                    QBRestChatService.updateGroupChatDialog(receivedQBChatDialog, requestBuilder)                                            .performAsync(new QBEntityCallback<QBChatDialog>() {                                                @Override                                                public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {                                                    progressDialog.dismiss();                                                    dialogAvatar.setImageBitmap(bitmap);                                                }                                                @Override                                                public void onError(QBResponseException e) {                                                    progressDialog.dismiss();                                                    Toast.makeText(MessageActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();                                                }                                            });                                }                                @Override                                public void onError(QBResponseException e) {                                }                            });                } catch (FileNotFoundException e) {                    e.printStackTrace();                } catch (IOException e) {                    e.printStackTrace();                }            }        }    }}