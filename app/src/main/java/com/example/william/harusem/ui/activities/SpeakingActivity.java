package com.example.william.harusem.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.william.harusem.R;
import com.example.william.harusem.interfaces.WordListener;
import com.example.william.harusem.models.SpeakingDialog;
import com.example.william.harusem.ui.adapters.SpeakingDialogsAdapter;
import com.example.william.harusem.util.SystemPermissionHelper;
import com.example.william.harusem.util.consts.Consts;
import com.example.william.harusem.utils.PermissionsChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpeakingActivity extends AppCompatActivity implements WordListener {

    private static final int REQUEST_CODE_SPEECH_INPUT = 25;
    private static final String TAG = SpeakingActivity.class.getSimpleName();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking);
        ButterKnife.bind(this);

        configureActionBar();
        configureProgressBar();
        configRecyclerView();
        fillRecyclerView();
    }

    private void fillRecyclerView() {
        List<SpeakingDialog> speakingDialogList = new ArrayList<>();
        speakingDialogList.add(new SpeakingDialog(SpeakingDialogsAdapter.VIEW_TYPE_RECIPIENT, "Hi! I am the bank representative. How may I help you?"));
        speakingDialogList.add(new SpeakingDialog(SpeakingDialogsAdapter.VIEW_TYPE_SENDER, "Hi! My name is John. I would like to open an account."));
        speakingDialogList.add(new SpeakingDialog(SpeakingDialogsAdapter.VIEW_TYPE_RECIPIENT, "Well, can I have your phone number and ID No?"));
        speakingDialogList.add(new SpeakingDialog(SpeakingDialogsAdapter.VIEW_TYPE_SENDER, "Yes, of course! I do not have a mobile phone nor an ID!"));
        speakingDialogList.add(new SpeakingDialog(SpeakingDialogsAdapter.VIEW_TYPE_RECIPIENT, "Hi! I am the bank representative. How may I help you?"));
        speakingDialogList.add(new SpeakingDialog(SpeakingDialogsAdapter.VIEW_TYPE_SENDER, "Hi! My name is John. I would like to open an account."));
        speakingDialogList.add(new SpeakingDialog(SpeakingDialogsAdapter.VIEW_TYPE_RECIPIENT, "Well, can I have your phone number and ID No?"));
        speakingDialogList.add(new SpeakingDialog(SpeakingDialogsAdapter.VIEW_TYPE_SENDER, "Yes, of course! I do not have a mobile phone nor an ID!"));
        speakingDialogList.add(new SpeakingDialog(SpeakingDialogsAdapter.VIEW_TYPE_RECIPIENT, "Hi! I am the bank representative. How may I help you?"));
        speakingDialogList.add(new SpeakingDialog(SpeakingDialogsAdapter.VIEW_TYPE_SENDER, "Hi! My name is John. I would like to open an account."));
        speakingDialogList.add(new SpeakingDialog(SpeakingDialogsAdapter.VIEW_TYPE_RECIPIENT, "Well, can I have your phone number and ID No?"));
        speakingDialogList.add(new SpeakingDialog(SpeakingDialogsAdapter.VIEW_TYPE_SENDER, "Yes, of course! I do not have a mobile phone nor an ID!"));

        mAdapter = new SpeakingDialogsAdapter(this, speakingDialogList, this);
        recyclerView.setAdapter(mAdapter);

        progressBarNumber.setText(getString(R.string.speaking_dialogs_size, 2, speakingDialogList.size()));
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
        progressBar.setProgress(85);
        progressBar.setReverse(false);
        progressBar.setPadding(0);

    }

    private void showQuitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to quit the lesson?");
        builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(SpeakingActivity.this, MainActivity.class));
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
            long startTime = System.currentTimeMillis();
            ArrayList<String> pronouncedSentences = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            String[] clearSpeechWordsArray = getSentenceAsClearArray(pronouncedSentences.get(0));
            String[] clearWordsArray = getSentenceAsClearArray(retrievedSentenceClear);

            ArrayList<String> matchedWords = getMatchedWordsList(pronouncedSentences, clearWordsArray);

            int progress = getSuccessProgress(matchedWords.size(), clearWordsArray.length);
            SpeakingDialog dialogWithProgress = mAdapter.getItem(position);
            dialogWithProgress.setSpeakProgressLevel(progress);
            mAdapter.updateItem(position, dialogWithProgress);

//            Log.i(TAG, "speechrecognition speech:" + Arrays.toString(clearSpeechWordsArray) + " - " + clearSpeechWordsArray.length);
            Log.i(TAG, "retrievedtext speech:" + Arrays.toString(clearWordsArray) + " - " + clearWordsArray.length);
            Log.i(TAG, "speech match: " + matchedWords.toString());
            Log.i(TAG, "speech progress: " + progress);

            Log.i(TAG, "onActivityResult: time " + (System.currentTimeMillis() - startTime));
        }
    }

    private int getSuccessProgress(int percent, int total) {
        return (int) (((double) percent / total) * 100);
    }


    private ArrayList<String> getMatchedWordsList(ArrayList<String> myArray, String[] totalArray) {
        ArrayList<String> matchedWords = new ArrayList<>();
        for (int j = 0; j < myArray.size(); j++) {
            String[] sentenceAsClearArray = getSentenceAsClearArray(myArray.get(j));

            ArrayList<String> tempMatch = new ArrayList<>();
            for (int i = 0; i < sentenceAsClearArray.length; i++) {
                if (sentenceAsClearArray[i].equalsIgnoreCase(totalArray[i])) {
                    tempMatch.add(sentenceAsClearArray[i]);
                }
            }

            if (tempMatch.size() > matchedWords.size()) {
                matchedWords = tempMatch;
            }

        }

        return matchedWords;
    }

}
