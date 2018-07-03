package com.example.william.harusem.util.consts;

import com.example.william.harusem.R;
import com.example.william.harusem.util.ResourceUtils;

public interface Consts {

    String SAMPLE_CONFIG_FILE_NAME = "sample_config.json";

    int PREFERRED_IMAGE_SIZE_PREVIEW = ResourceUtils.getDimen(R.dimen.chat_attachment_preview_size);
    int PREFERRED_IMAGE_SIZE_FULL = ResourceUtils.dpToPx(320);
    String QB_USER_PASSWORD = "qb_user_password";



    public static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; //10Mb
    public static final long MAX_AUDIO_VIDEO_SIZE = 100 * 1024 * 1024; //100mb
    public static final long MAX_FILENAME_LENGTH = 100;


    // AudioVideoRecorder
    public static final int MAX_RECORD_DURATION_IN_SEC = 30;
    public static final int MIN_RECORD_DURATION_IN_SEC = 1;
    public static final int VIDEO_QUALITY_LOW = 0;
    public static final int VIDEO_QUALITY_HIGH = 1;
    public static final int CHRONOMETER_ALARM_SECOND = 27;

    public static final String CHAT_MUC = "@muc.";
    public static final int CHATS_DIALOGS_PER_PAGE = 50;
    public static final int DIALOG_MESSAGES_PER_PAGE = 50;
    public static final String DIALOGS_START_ROW = "dialogs_start_row";
    public static final String DIALOGS_PER_PAGE = "dialogs_per_page";
    public static final String DIALOGS_UPDATE_ALL = "dialogs_update_all";
    public static final String DIALOGS_UPDATE_BY_IDS = "dialogs_update_by_ids";

    public static final String EXTRA_ERROR = "error";
    public static final String EXTRA_ERROR_CODE = "error_code";
    public static final String COMMAND_ACTION = "command_action";


    public static final String LOAD_ATTACH_FILE_ACTION = "load_attach_file_action";
    public static final String EXTRA_FILE = "file";
    public static final String EXTRA_DIALOG_ID = "dialog_id";
    public static final String EXTRA_ATTACH_FILE = "attach_file";
    public static final String EXTRA_FILE_PATH = "file_path";


}