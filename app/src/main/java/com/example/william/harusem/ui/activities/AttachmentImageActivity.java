package com.example.william.harusem.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.william.harusem.R;
import com.example.william.harusem.util.consts.Consts;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AttachmentImageActivity extends AppCompatActivity {

    private static final String EXTRA_URL = "url";
    private static final int MESSAGE_ATTACHMENT = 1;
    private static final int DIALOG_IMAGE = 2;
    @BindView(R.id.image_full_view)
    ImageView imageView;
    @BindView(R.id.progress_bar_show_image)
    ProgressBar progressBar;
    int type = 0;
    ActionBar actionBar;


    public static void start(Context context, String url, int type, String dialogType) {
        Intent intent = new Intent(context, AttachmentImageActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra("type", type);
        intent.putExtra("dialog_type", dialogType);
        context.startActivity(intent);
    }

    public static void start(Context context, String url, int type) {
        Intent intent = new Intent(context, AttachmentImageActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        ButterKnife.bind(this);

        actionBar = getSupportActionBar();


        initUI();
        loadImage();
    }

    private void initUI() {
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setElevation(0);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void loadImage() {
        int TYPE = getIntent().getIntExtra("type", 0);
        String url = getIntent().getStringExtra(EXTRA_URL);
        String dialogType = getIntent().getStringExtra("dialog_type");
        progressBar.setVisibility(View.VISIBLE);

        if (TYPE == MESSAGE_ATTACHMENT) {
            if (TextUtils.isEmpty(url)) {
                imageView.setImageResource(R.drawable.ic_error_white);
                return;
            }

            Glide.with(this)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model,
                                                   Target<GlideDrawable> target, boolean isFirstResource) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target, boolean isFromMemoryCache,
                                                       boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .error(R.drawable.ic_error_white)
                    .dontTransform()
                    .override(Consts.PREFERRED_IMAGE_SIZE_FULL, Consts.PREFERRED_IMAGE_SIZE_FULL)
                    .into(imageView);
        } else {
            // Dialog Photo
            if (!url.isEmpty() && !url.equalsIgnoreCase("null")) {

                QBContent.getFile(Integer.parseInt(url)).performAsync(new QBEntityCallback<QBFile>() {
                    @Override
                    public void onSuccess(QBFile qbFile, Bundle bundle) {
                        Glide.with(AttachmentImageActivity.this)
                                .load(qbFile.getPublicUrl())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model,
                                                               Target<GlideDrawable> target, boolean isFirstResource) {
                                        e.printStackTrace();
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                                   Target<GlideDrawable> target, boolean isFromMemoryCache,
                                                                   boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .error(R.drawable.ic_error_white)
                                .dontTransform()
                                .override(Consts.PREFERRED_IMAGE_SIZE_FULL, Consts.PREFERRED_IMAGE_SIZE_FULL)
                                .into(imageView);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("Attachmentactivity", "onError: ", e);
                    }
                });

            } else {
                if (dialogType.equals(QBDialogType.PRIVATE.toString())) {
                    imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.placeholder_user));
                } else {
                    imageView.setImageResource(R.drawable.ic_group_black_24dp);
                }
                progressBar.setVisibility(View.GONE);



                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (80*scale + 0.5f);
                imageView.setPadding(dpAsPixels,dpAsPixels,dpAsPixels,dpAsPixels);

            }


        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);

    }


}