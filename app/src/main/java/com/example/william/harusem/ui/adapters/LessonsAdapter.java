package com.example.william.harusem.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.william.harusem.R;
import com.example.william.harusem.models.Lesson;
import com.example.william.harusem.ui.activities.SpeakingActivity;
import com.example.william.harusem.util.Utils;

import java.util.List;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.MyViewHolder> {

    private String EXTRAS_LESSON_PARENT_ID = "extras_lesson_number";
    private List<Lesson> lessonsList;
    private Context context;

    public LessonsAdapter(List<Lesson> lessonsList, Context context) {
        this.lessonsList = lessonsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lesson_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Lesson lesson = lessonsList.get(position);

        holder.lessonNumberTv.setText(context.getResources().getString(R.string.lesson_number, lesson.getLessonNumber()));
        holder.lessonTitleTv.setText(lesson.getLessonTitle());

        if (lesson.isFinished()) {
            holder.lessonNumberTv.setTextColor(context.getResources().getColor(R.color.gray_dark));
            holder.lessonTitleTv.setTextColor(context.getResources().getColor(R.color.black));
            holder.startIv.setImageResource(R.drawable.ic_check);
            holder.arrowIv.setColorFilter(null);
        } else {
            if (lesson.isLocked()) {
                holder.lessonNumberTv.setTextColor(context.getResources().getColor(R.color.disabled_gray_2));
                holder.lessonTitleTv.setTextColor(context.getResources().getColor(R.color.disabled_gray));
                holder.startIv.setImageResource(R.drawable.ic_lock);
                holder.arrowIv.setColorFilter(context.getResources().getColor(R.color.disabled_gray));
            } else {
                holder.lessonNumberTv.setTextColor(context.getResources().getColor(R.color.gray_dark));
                holder.lessonTitleTv.setTextColor(context.getResources().getColor(R.color.black));
                holder.startIv.setImageResource(R.drawable.ic_play);
                holder.arrowIv.setColorFilter(null);
            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lesson.isLocked()) {
                    Utils.buildAlertDialog("Locked Level", "Complete the previous lesson to unlock this one", true, context);
                } else {
                    Intent intent = new Intent(context, SpeakingActivity.class);
                    intent.putExtra(EXTRAS_LESSON_PARENT_ID, lesson.getParentId());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lessonsList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView lessonNumberTv, lessonTitleTv;
        public ImageView startIv, arrowIv;

        public MyViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card_view);
            lessonNumberTv = view.findViewById(R.id.lesson_number_tv);
            lessonTitleTv = view.findViewById(R.id.lesson_title_tv);
            startIv = view.findViewById(R.id.start_iv);
            arrowIv = view.findViewById(R.id.arrow_iv);
        }
    }
}