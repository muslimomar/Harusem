package com.example.william.harusem.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.example.william.harusem.R;

public class ProgressDialog {
    private static ProgressDialog mProgressDialog;
    private Dialog mDialog;

    public ProgressDialog() {
    }

    public static ProgressDialog getInstance() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog();
        }
        return mProgressDialog;
    }

    public void showProgress(Context mContext) {
        mDialog= new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.custom_progress_layout);
        mDialog.findViewById(R.id.avPb).setVisibility(View.VISIBLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    public void hideProgress() {
        if (mDialog!= null) {
            mDialog.dismiss();
            mDialog= null;
        }
    }
}
