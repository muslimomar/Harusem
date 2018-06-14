package com.example.william.harusem.util.consts;

import com.example.william.harusem.R;
import com.example.william.harusem.util.ResourceUtils;

public interface Consts {

    String SAMPLE_CONFIG_FILE_NAME = "sample_config.json";

    int PREFERRED_IMAGE_SIZE_PREVIEW = ResourceUtils.getDimen(R.dimen.chat_attachment_preview_size);
    int PREFERRED_IMAGE_SIZE_FULL = ResourceUtils.dpToPx(320);
    String QB_USER_PASSWORD = "qb_user_password";
}