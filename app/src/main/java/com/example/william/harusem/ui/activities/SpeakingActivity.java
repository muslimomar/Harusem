package com.example.william.harusem.ui.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.william.harusem.R;
import com.example.william.harusem.interfaces.WordListener;
import com.example.william.harusem.models.SpeakingDialog;
import com.example.william.harusem.ui.adapters.LessonsAdapter;
import com.example.william.harusem.ui.adapters.SpeakingDialogsAdapter;
import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.SystemPermissionHelper;
import com.example.william.harusem.util.Toaster;
import com.example.william.harusem.util.Utils;
import com.example.william.harusem.util.consts.Consts;
import com.example.william.harusem.utils.PermissionsChecker;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.server.Performer;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.william.harusem.common.Common.INDEX;
import static com.example.william.harusem.common.Common.PARENT_ID;
import static com.example.william.harusem.ui.adapters.LessonsAdapter.EXTRAS_PRIVATE_LESSON_API_ID;
import static com.example.william.harusem.ui.adapters.LessonsAdapter.EXTRAS_PRIVATE_NEXT_LESSON_API_ID;
import static com.example.william.harusem.ui.adapters.LessonsAdapter.EXTRAS_PUBLIC_LESSON_API_ID;
import static com.example.william.harusem.ui.adapters.LessonsAdapter.EXTRAS_PUBLIC_NEXT_LESSON_API_ID;

public class SpeakingActivity extends AppCompatActivity implements WordListener {

    private static final int REQUEST_CODE_SPEECH_INPUT = 25;
    private static final String TAG = SpeakingActivity.class.getSimpleName();
    int index = 1;
    @BindView(R.id.progress_bar_number)
    TextView progressBarNumber;
    @BindView(R.id.ic_back_iv)
    ImageView icBackIv;
    @BindView(R.id.progress_bar)
    RoundCornerProgressBar progressBar;
    @BindView(R.id.dialogs_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.next_button)
    Button nextBtn;
    String retrievedSentenceClear;
    SpeakingDialogsAdapter mAdapter;
    int position;
    String lessonApiId;
    Realm realm;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    private String nextLessonApiId;
    private String privateLessonApi;
    private String privateNextLessonApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking);
        ButterKnife.bind(this);

        configureActionBar();
        configureProgressBar();

        realm = Realm.getDefaultInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            lessonApiId = bundle.getString(EXTRAS_PUBLIC_LESSON_API_ID);
            nextLessonApiId = bundle.getString(LessonsAdapter.EXTRAS_PUBLIC_NEXT_LESSON_API_ID);

            privateLessonApi = bundle.getString(EXTRAS_PRIVATE_LESSON_API_ID);
            privateNextLessonApi = bundle.getString(EXTRAS_PRIVATE_NEXT_LESSON_API_ID);
        }

        configRecyclerView();
        getData();
    }

    private void getData() {
        ProgressDialog progressDialog = Utils.buildProgressDialog(SpeakingActivity.this, "", "Loading...", false);
        progressDialog.show();
        fillData(progressDialog);
    }


    private void fillData(ProgressDialog progressDialog) {
        // download data for the first time
        RealmResults<SpeakingDialog> speakingDialogsList = realm.where(SpeakingDialog.class).findAll();
        if (speakingDialogsList.size() > 0) {
            RealmResults<SpeakingDialog> speakingDialogs = getSelectedLesson();
            refreshAdapter(speakingDialogs);
            setDialogsNumber(this.index, getTotalIndexes());
            dismissDialog(progressDialog);
            if (speakingDialogs.size() == 0) {
                Utils.buildAlertDialogButton("Error", "No data at this lesson", false, this, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
            }
        } else {
            getCustomData(progressDialog);
        }

    }

    private RealmResults<SpeakingDialog> getSelectedLesson() {
        return realm.where(SpeakingDialog.class)
                .equalTo(PARENT_ID, lessonApiId)
                .equalTo(INDEX, this.index)
                .findAll();
    }

    private int getTotalIndexes() {
        return realm.where(SpeakingDialog.class)
                .equalTo(PARENT_ID, lessonApiId)
                .distinct("index").findAll().size();
    }

    private void refreshAdapter(RealmResults<SpeakingDialog> speakingDialogs) {
        mAdapter = new SpeakingDialogsAdapter(SpeakingActivity.this, speakingDialogs, SpeakingActivity.this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void setDialogsNumber(int i, int size) {
        progressBarNumber.setText(getString(R.string.speaking_dialogs_size, i, size));
        progressBar.setProgress(getProgressFromTotal(i, size));
    }

    private void getCustomData(ProgressDialog progressDialog) {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.sortAsc("Created at");
        requestBuilder.setLimit(200);

        Performer<ArrayList<QBCustomObject>> object = QBCustomObjects.getObjects("Category_Lessons_Dialogs", requestBuilder);
        object.performAsync(new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                ArrayList<SpeakingDialog> speakingDialogs = new ArrayList<>();
                for (QBCustomObject object : qbCustomObjects) {
                    Integer view_type = object.getInteger("view_type");
                    String dialogText = object.getString("dialog_text");
                    int index = object.getInteger("index");
                    String parentId = object.getParentId();
                    String apiId = object.getCustomObjectId();

                    SpeakingDialog existingDialog = realm.where(SpeakingDialog.class).equalTo("apiId", apiId).findFirst();
                    if (existingDialog != null) {
                        realm.beginTransaction();
                        existingDialog.setDialogType(view_type);
                        existingDialog.setDialogText(dialogText);
                        existingDialog.setIndex(index);
                        existingDialog.setParentId(parentId);
                        existingDialog.setApiId(apiId);
                        realm.commitTransaction();
                    } else {
                        speakingDialogs.add(new SpeakingDialog(view_type, index, dialogText, parentId, apiId));
                    }

                }

                if (speakingDialogs.size() > 0) {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(speakingDialogs);
                    realm.commitTransaction();
                }

                RealmResults<SpeakingDialog> speakingDialogRealmResults = realm.where(SpeakingDialog.class).equalTo(PARENT_ID, lessonApiId).equalTo(INDEX, SpeakingActivity.this.index).findAll();
                refreshAdapter(speakingDialogRealmResults);
                setDialogsNumber(SpeakingActivity.this.index, getTotalIndexes());

                dismissDialog(progressDialog);
            }

            @Override
            public void onError(QBResponseException e) {
                dismissDialog(progressDialog);
                showErrorSnackbar(R.string.dlg_retry, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getCustomData(progressDialog);
                    }
                });
            }
        });

    }

    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(rootLayout, resId, e,
                R.string.dlg_retry, clickListener);
    }

    private void configRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void configureActionBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
    }

    private void configureProgressBar() {
        progressBar.setProgressColor(ContextCompat.getColor(this, R.color.colorPrimary));
        progressBar.setProgressBackgroundColor(ContextCompat.getColor(this, R.color.pb_bg));
        progressBar.setRadius(15);
        progressBar.setMax(100);
        progressBar.setReverse(false);
        progressBar.setPadding(0);
    }

    private void showQuitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to quit the lesson?");
        builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        showQuitConfirmationDialog();
    }

    @Override
    public void onSpeakPressed(SpeakingDialog dialog, int i) {
        position = i;

        if (isPermissionGranted()) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "listening");
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            retrievedSentenceClear = clearPunctuations(dialog.getDialogText());

        } else {
            SystemPermissionHelper permissionHelper = new SystemPermissionHelper(this);
            permissionHelper.requestAllPermissionForAudioRecord();
        }
    }

    private boolean isPermissionGranted() {
        PermissionsChecker checker = new PermissionsChecker(this);
        return !checker.lacksPermissions(Consts.AUDIO_PERMISSONS);
    }


    private String[] getSentenceAsClearArray(String pronouncedSentence) {
        String pSentenceClear = clearPunctuations(pronouncedSentence);
        return pSentenceClear.split(" ");

    }

    public String clearPunctuations(String str) {
        return str.replaceAll("\\p{Punct}|\\d", "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> pronouncedSentences = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String[] clearWordsArray = getSentenceAsClearArray(retrievedSentenceClear);

            ArrayList<String> matchedWords = getMatchedWordsList(pronouncedSentences, clearWordsArray);

            int progress = getProgressFromTotal(matchedWords.size(), clearWordsArray.length);
            SpeakingDialog dialogWithProgress = mAdapter.getItem(position);

            realm.beginTransaction();
            dialogWithProgress.setSpeakProgressLevel(progress);
            realm.copyToRealmOrUpdate(dialogWithProgress);
            realm.commitTransaction();
            mAdapter.updateItem(position);

        }
    }

    private int getProgressFromTotal(int percent, int total) {
        return (int) (((double) percent / total) * 100);
    }


    private ArrayList<String> getMatchedWordsList(ArrayList<String> myArray, String[] totalArray) {
        ArrayList<String> matchedWords = new ArrayList<>();
        for (int j = 0; j < myArray.size(); j++) {
            String[] sentenceAsClearArray = getSentenceAsClearArray(myArray.get(j));

            ArrayList<String> tempMatch = new ArrayList<>();


            if (totalArray.length > sentenceAsClearArray.length) {
                for (int i = 0; i < sentenceAsClearArray.length; i++) {
                    if (sentenceAsClearArray[i].equalsIgnoreCase(totalArray[i])) {
                        tempMatch.add(sentenceAsClearArray[i]);
                    }
                }
            } else {
                for (int i = 0; i < totalArray.length; i++) {
                    if (sentenceAsClearArray[i].equalsIgnoreCase(totalArray[i])) {
                        tempMatch.add(sentenceAsClearArray[i]);
                    }
                }
            }


            if (tempMatch.size() > matchedWords.size()) {
                matchedWords = tempMatch;
            }

        }

        return matchedWords;
    }

    @OnClick(R.id.next_button)
    public void setNextBtn(View view) {
        nextLesson();

    }

    private void nextLesson() {
        if (!areDialogsFinished()) {
            Toaster.shortToast("Please complete all the lessons to proceed!");
            return;
        }

        if (index == getTotalIndexes()) {
            Intent intent = new Intent();
            intent.putExtra(EXTRAS_PRIVATE_LESSON_API_ID, privateLessonApi);
            intent.putExtra(EXTRAS_PRIVATE_NEXT_LESSON_API_ID, privateNextLessonApi);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            index++;
            fillData(null);
        }
    }

    private boolean areDialogsFinished() {
        List<SpeakingDialog> currentDialogs = mAdapter.getAllDialogs();
        for (SpeakingDialog dialog : currentDialogs) {
            if (dialog.getDialogType() == SpeakingDialogsAdapter.VIEW_TYPE_SENDER) {
                if (dialog.getSpeakProgressLevel() < 50) {
                    return false;
                }
            }
        }
        return true;
    }

    public void dismissDialog(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @OnClick(R.id.ic_back_iv)
    public void setIcBackIv(View view) {
        showQuitConfirmationDialog();
    }

}
