package com.squeezedlemon.esquina.client.android.util;


import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;

public class ViewUtils {
    public static void clearErrors(View v) {
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            for (int i = 0; i < group.getChildCount(); i++) {
                clearErrors(group.getChildAt(i));
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setError(null);
        }
    }

    public static void appendError(TextView textView, String error) {
        if (textView.getError() != null) {
            String merged = textView.getError() + "\n" + error;
            textView.setError(merged);
        } else {
            textView.setError(error);
        }
    }
}
