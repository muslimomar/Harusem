package com.example.william.harusem.ui.adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.interfaces.WordListener;
import com.example.william.harusem.models.SpeakingDialog;

import java.util.List;
import java.util.Locale;

public class SpeakingDialogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_SENDER = 1;
    public static final int VIEW_TYPE_RECIPIENT = 2;
    private WordListener wordListener;
    private List<SpeakingDialog> dialogsList;
    private Context context;
    private TextToSpeech tts;

    public SpeakingDialogsAdapter(Context context, List<SpeakingDialog> dialogsList, WordListener listener) {
        this.dialogsList = dialogsList;
        this.context = context;
        wordListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENDER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_speaker_sender, parent, false);

            return new SenderDialogHolder(view);

        } else if (viewType == VIEW_TYPE_RECIPIENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_speaker_recipient, parent, false);

            return new RecipientDialogHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final SpeakingDialog dialog = dialogsList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SENDER:
                ((SenderDialogHolder) holder).bind(dialog);
                break;
            case VIEW_TYPE_RECIPIENT:
                ((RecipientDialogHolder) holder).bind(dialog);
        }

    }

    @Override
    public int getItemViewType(int position) {
        SpeakingDialog dialog = dialogsList.get(position);

        if (dialog.getDialogType() == VIEW_TYPE_RECIPIENT) {
            return VIEW_TYPE_RECIPIENT;
        } else {
            return VIEW_TYPE_SENDER;
        }

    }

    @Override
    public int getItemCount() {
        return dialogsList.size();
    }

    public SpeakingDialog getItem(int i) {
        return dialogsList.get(i);
    }

    public void updateItem(int i) {
        notifyItemChanged(i);
    }

    public class SenderDialogHolder extends RecyclerView.ViewHolder {
        public TextView dialogTv;
        public ImageView micIv;
        public ProgressBar speakingLevelPb;

        public SenderDialogHolder(View view) {
            super(view);
            dialogTv = (TextView) view.findViewById(R.id.dialog_tv);
            micIv = view.findViewById(R.id.mic_iv);
            speakingLevelPb = view.findViewById(R.id.speaking_level_pb);
        }

        public void bind(SpeakingDialog dialog) {
            dialogTv.setText(dialog.getDialogText());
            speakingLevelPb.setProgress(dialog.getSpeakProgressLevel());

            micIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    wordListener.onSpeakPressed(dialog, getAdapterPosition());
                }
            });
        }

    }

    public class RecipientDialogHolder extends RecyclerView.ViewHolder {
        public TextView dialogTv;
        public ImageView speakerIv;

        public RecipientDialogHolder(View view) {
            super(view);
            dialogTv = (TextView) view.findViewById(R.id.dialog_tv);
            speakerIv = view.findViewById(R.id.speaker_iv);
        }

        public void bind(SpeakingDialog dialog) {
            dialogTv.setText(dialog.getDialogText());

            tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.US);
                    }
                }
            });

            speakerIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tts.isSpeaking()) {
                        tts.stop();
                    }
                    tts.speak(dialog.getDialogText(), TextToSpeech.QUEUE_FLUSH, null);
                }
            });
        }
    }

    public List<SpeakingDialog> getAllDialogs() {
        return dialogsList;
    }

}