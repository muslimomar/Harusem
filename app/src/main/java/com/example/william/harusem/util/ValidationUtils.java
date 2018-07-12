package com.example.william.harusem.util;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.example.william.harusem.R;
import com.example.william.harusem.models.Attachment;
import com.example.william.harusem.ui.dialog.OneButtonDialogFragment;
import com.example.william.harusem.util.consts.Consts;
import com.example.william.harusem.util.consts.MimeType;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final int FULL_NAME_MIN_LENGTH = 3;
    private static final int FULL_NAME_MAX_LENGTH = 200;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private final static String NULL = "null";

    private Context context;

    public ValidationUtils(Context context) {
        this.context = context;
    }

    public boolean isFullNameValid(TextInputLayout fullNameTextInputLayout, String newFullName) {
        boolean fullNameEntered = !TextUtils.isEmpty(newFullName.trim());
        boolean valid = true;

        if (fullNameEntered) {
            if (newFullName.length() < FULL_NAME_MIN_LENGTH) {
                valid = false;
                fullNameTextInputLayout.setError(context.getString(R.string.auth_full_name_field_is_too_short));
            } else if (newFullName.length() > FULL_NAME_MAX_LENGTH) {
                valid = false;
                fullNameTextInputLayout.setError(context.getString(R.string.auth_full_name_field_is_too_long));
            }
        } else {
            valid = false;
            fullNameTextInputLayout.setError(context.getString(R.string.profile_full_name_not_entered));
        }

        return valid;
    }

    public static boolean isNull(String value) {
        return value == null || value.equals(NULL);
    }

    public static boolean validateAttachment(FragmentManager fragmentManager, String[] supportedAttachmentTypes, Attachment.Type type, Object attachment) {

        if (!isSupportAttachmentType(supportedAttachmentTypes, type)) {
            OneButtonDialogFragment.show(fragmentManager, R.string.dlg_unsupported_file, false);
            return false;
        }

        if (attachment instanceof File) {
            String mimeType = StringUtils.getMimeType(Uri.fromFile((File) attachment));
            if (!isValidFileType(mimeType)) {
                OneButtonDialogFragment.show(fragmentManager, R.string.dlg_unsupported_file, false);
                return false;
            }

            if (!isValidMaxLengthName((File) attachment)) {
                OneButtonDialogFragment.show(fragmentManager, R.string.dlg_filename_long, false);
                return false;
            }

            if (type.equals(Attachment.Type.IMAGE)) {
                if (!isValidMaxImageSize((File) attachment)) {
                    OneButtonDialogFragment.show(fragmentManager, R.string.dlg_image_big, false);
                    return false;
                }
            } else if (type.equals(Attachment.Type.AUDIO) || type.equals(Attachment.Type.VIDEO)) {
                if (!isValidMaxAudioVideoSize((File) attachment)) {
                    OneButtonDialogFragment.show(fragmentManager, R.string.dlg_audio_video_big, false);
                    return false;
                }
            }
            if (type.equals(Attachment.Type.AUDIO)) {
                if (!isValidMinAudioDuration((File) attachment)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isValidMaxLengthName(File file) {
        return file.getName().length() < Consts.MAX_FILENAME_LENGTH;
    }

    private static boolean isValidMaxImageSize(File file) {
        return file.length() < Consts.MAX_IMAGE_SIZE;
    }

    private static boolean isValidMaxAudioVideoSize(File file) {
        return file.length() < Consts.MAX_AUDIO_VIDEO_SIZE;
    }

    private static boolean isValidMinAudioDuration(File file) {
        int duration = MediaUtils.getMetaData(file.getPath()).durationSec();
        return duration >= Consts.MIN_RECORD_DURATION_IN_SEC;
    }

    private static boolean isSupportAttachmentType(String[] supportedAttachmentTypes, Attachment.Type type) {
        boolean supported = false;
        for (String supportedTypeSrt : supportedAttachmentTypes) {
            Attachment.Type supportedType = Attachment.Type.valueOf(supportedTypeSrt);
            if (type.equals(supportedType)) {
                supported = true;
                break;
            }
        }

        return supported;
    }

    private static boolean isValidFileType(String mimeType) {
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        return isValidMimeType(mimeType) || isValidExtension(extension);
    }

    private static boolean isValidMimeType(String mimeType) {
        return !TextUtils.isEmpty(mimeType) && (mimeType.startsWith(MimeType.IMAGE_MIME_PREFIX) || mimeType.equals(MimeType.AUDIO_MIME_MP3));
    }

    private static boolean isValidExtension(String extension) {
        return !TextUtils.isEmpty(extension) && (extension.equals(MimeType.VIDEO_MIME_EXTENSION_MP4) || extension.equals(MimeType.AUDIO_MIME_EXTENSION_MP3));
    }

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}