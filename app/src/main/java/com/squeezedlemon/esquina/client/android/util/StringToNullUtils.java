package com.squeezedlemon.esquina.client.android.util;

public class StringToNullUtils {

    public static String emptyStringToNull(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        } else {
            return s;
        }
    }

    public static String nullToEmptyString(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }
}
