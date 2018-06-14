package com.example.william.harusem.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.william.harusem.R;
import com.example.william.harusem.util.qb.callback.QbEntityCallbackTwoTypeWrapper;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;

import java.io.File;

/**
 * Created by william on 5/27/2018.
 */

public class Utils {

    public static final int OFFLINE = 0;
    public static final int ONLINE = 1;

    public static void buildAlertDialog(String title, String message, boolean isCancelable, Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title);

        if (isCancelable) {
            builder.setPositiveButton(android.R.string.ok, null);
        } else {
            builder.setCancelable(false);
        }
        builder.create().show();
    }

    public static ProgressDialog buildProgressDialog(Context context, String title, String message, boolean isCancellable) {
        ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle(title);
        progress.setMessage(message);
        progress.setCanceledOnTouchOutside(isCancellable);
        progress.setCancelable(isCancellable);
        return progress;
    }

    public static ProgressDialog getHorizontalProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage(context.getString(R.string.please_wait));
        return progressDialog;
    }

}
