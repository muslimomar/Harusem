package com.example.william.harusem.audioAttachment.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.example.william.harusem.util.ErrorUtils;
import com.example.william.harusem.util.consts.Consts;
import com.quickblox.core.exception.QBResponseException;

public abstract class ServiceCommand implements Command {

    protected final Context context;
    protected final String successAction;
    protected final String failAction;

    public ServiceCommand(Context context, String successAction, String failAction) {
        this.context = context;
        this.successAction = successAction;
        this.failAction = failAction;
    }

    public void execute(Bundle bundle) throws Exception {
        Bundle result;
        try {
            result = perform(bundle);
            sendResult(result, successAction);
        } catch (QBResponseException e) {
            ErrorUtils.logError(e);
            result = new Bundle();
            result.putSerializable(Consts.EXTRA_ERROR, e);
            result.putInt(Consts.EXTRA_ERROR_CODE, e.getHttpStatusCode());
            result.putString(Consts.COMMAND_ACTION, failAction);
            sendResult(result, failAction);
            throw e;
        } catch (Exception e) {
            ErrorUtils.logError(e);
            result = new Bundle();
            result.putSerializable(Consts.EXTRA_ERROR, e);
            result.putString(Consts.COMMAND_ACTION, failAction);
            sendResult(result, failAction);
            throw e;
        }
    }

    protected void sendResult(Bundle result, String action) {
        Intent intent = new Intent(action);
        if (null != result) {
            intent.putExtras(result);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    protected abstract Bundle perform(Bundle extras) throws Exception;
}