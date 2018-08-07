package com.example.william.harusem.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.william.harusem.R;

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

    public static void buildAlertDialogButton(String title, String message, boolean isCancelable, Context context, String buttonString,
                                              DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title);

        builder.setCancelable(isCancelable);
        builder.setPositiveButton(buttonString,onClickListener);

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
