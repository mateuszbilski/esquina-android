package com.squeezedlemon.esquina.client.android.util;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;

public class AlertDialogUtils {

    public static AlertDialog createAlertDialog(Context context, String message, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder.setMessage(message)
                .setPositiveButton("OK", listener)
                .setCancelable(false)
                .create();
    }
}
