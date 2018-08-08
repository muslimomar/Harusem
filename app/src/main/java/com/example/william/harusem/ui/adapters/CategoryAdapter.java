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
import com.example.william.harusem.models.Category;
import com.example.william.harusem.ui.activities.LessonsActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    public static final String EXTRAS_CATEGORY_NAME = "extras_category_name";
    private List<Category> categoryList;
    private Context context;

    public CategoryAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_category, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Category category = categoryList.get(position);

        holder.categoryNameTv.setText(category.getCategoryDisplayName());
        holder.categoryPhotoIv.setImageResource(category.getImageId());
        holder.percentageTv.setText(context.getResources().getString(R.string.percentage, 10));
        holder.dialogsCountTv.setText(context.getResources().getString(R.string.dialogs_number, category.getLessonsCount()));
        holder.cardView.setCardBackgroundColor(category.getBgColor());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LessonsActivity.class);
                intent.putExtra(EXTRAS_CATEGORY_NAME, category.getParentId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryNameTv, dialogsCountTv, percentageTv;
        public ImageView categoryPhotoIv;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            categoryNameTv = view.findViewById(R.id.category_title_tv);
            categoryPhotoIv = view.findViewById(R.id.category_icon_iv);
            dialogsCountTv = view.findViewById(R.id.dialogs_count_tv);
            percentageTv = view.findViewById(R.id.total_score_tv);
            cardView = view.findViewById(R.id.card_view);
        }
    }
}