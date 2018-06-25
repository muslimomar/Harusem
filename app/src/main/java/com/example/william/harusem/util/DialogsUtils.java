package com.example.william.harusem.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.william.harusem.Harusem;
import com.example.william.harusem.R;
import com.example.william.harusem.ui.dialog.TwoButtonsDialogFragment;
import com.example.william.harusem.util.consts.Consts;

public class DialogsUtils {

    public static final long OPEN_APP_SETTINGS_DIALOG_DELAY = 500;

    public static void disableCancelableDialog(MaterialDialog materialDialog) {
        // Disable the back button
        DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        };
        materialDialog.setOnKeyListener(keyListener);

        materialDialog.setCanceledOnTouchOutside(false);
    }

    public static void showOpenAppSettingsDialog(FragmentManager fm, final String dialogMessage, final MaterialDialog.ButtonCallback callback) {
        //postDelayed() is temp fix before fixing this bug https://code.google.com/p/android/issues/detail?id=190966
        TwoButtonsDialogFragment.showDelayed(
                fm,
                Harusem.getInstance().getString(R.string.app_name),
                dialogMessage,
                Harusem.getInstance().getString(R.string.dlg_cancel),
                Harusem.getInstance().getString(R.string.dlg_open_app_settings),
                callback,
                OPEN_APP_SETTINGS_DIALOG_DELAY);
    }

    /**    logic for loading dialogs page by page from cache
     *     return result perPage, startRow and needUpdate params by sendBroadcast
     *     @param dialogsCount Amount of all dialogs.
     */
    public static void loadAllDialogsFromCacheByPagesTask(Context context, long dialogsCount, String resultAction) {
        boolean needToLoadMore;

        int startRow = 0;
        int perPage = Consts.CHATS_DIALOGS_PER_PAGE;

        do {
            needToLoadMore = dialogsCount > Consts.CHATS_DIALOGS_PER_PAGE;

            if (!needToLoadMore) {
                perPage = (int) dialogsCount;
            }

            Bundle bundle = new Bundle();
            bundle.putInt(Consts.DIALOGS_START_ROW, startRow);
            bundle.putInt(Consts.DIALOGS_PER_PAGE, perPage);

            sendLoadPageSuccess(context, bundle, resultAction);
            dialogsCount -= perPage;

            startRow += perPage;
        } while (needToLoadMore);
    }

    private static void sendLoadPageSuccess(Context context, Bundle result, String resultAction) {
        Intent intent = new Intent(resultAction);
        if (null != result) {
            intent.putExtras(result);
        }
        Log.v(DialogsUtils.class.getSimpleName(), "broadcast action " + resultAction + " sent " + intent.getExtras());
        LocalBroadcastManager.getInstance(context).sendBroadcastSync(intent);
    }
}