package com.example.william.harusem.util.consts;

import com.example.william.harusem.R;
import android.Manifest;

import com.example.william.harusem.util.ResourceUtils;

public interface Consts {

    String SAMPLE_CONFIG_FILE_NAME = "sample_config.json";

    int PREFERRED_IMAGE_SIZE_PREVIEW = ResourceUtils.getDimen(R.dimen.chat_attachment_preview_size);
    int PREFERRED_IMAGE_SIZE_FULL = ResourceUtils.dpToPx(320);
    String QB_USER_PASSWORD = "qb_user_password";
    //String DEFAULT_USER_PASSWORD = "x6Bt0VDy5";
    int ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS = 422;
    int ERR_MSG_DELETING_HTTP_STATUS = 401;
    int CALL_ACTIVITY_CLOSE = 1000;
    //CALL ACTIVITY CLOSE REASONS
    int CALL_ACTIVITY_CLOSE_WIFI_DISABLED = 1001;
    String WIFI_DISABLED = "wifi_disabled";

    String OPPONENTS = "opponents";
    String CONFERENCE_TYPE = "conference_type";
    String EXTRA_TAG = "currentRoomName";
    int MAX_OPPONENTS_COUNT = 6;

    String PREF_CURREN_ROOM_NAME = "current_room_name";
    String PREF_CURRENT_TOKEN = "current_token";
    String PREF_TOKEN_EXPIRATION_DATE = "token_expiration_date";
    String EXTRA_QB_USER = "qb_user";

    String EXTRA_USER_ID = "user_id";
    String EXTRA_USER_LOGIN = "user_login";
    String EXTRA_USER_PASSWORD = "user_password";
    String EXTRA_PENDING_INTENT = "pending_Intent";

    String EXTRA_CONTEXT = "context";
    String EXTRA_OPPONENTS_LIST = "opponents_list";
    String EXTRA_CONFERENCE_TYPE = "conference_type";
    String EXTRA_IS_INCOMING_CALL = "conversation_reason";

    String EXTRA_LOGIN_RESULT = "login_result";
    String EXTRA_LOGIN_ERROR_MESSAGE = "login_error_message";
    int EXTRA_LOGIN_RESULT_CODE = 1002;

    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    String EXTRA_COMMAND_TO_SERVICE = "command_for_service";
    int COMMAND_NOT_FOUND = 0;
    int COMMAND_LOGIN = 1;
    int COMMAND_LOGOUT = 2;
    String EXTRA_IS_STARTED_FOR_CALL = "isRunForCall";
    String ALREADY_LOGGED_IN = "You have already logged in chat";

    enum StartConversationReason {
        INCOME_CALL_FOR_ACCEPTION,
        OUTCOME_CALL_MADE
    }




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