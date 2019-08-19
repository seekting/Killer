package com.seekting.common;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private ToastUtils() {
    }

    public static void showToast(Context context, int res) {
        Toast.makeText(context, res, Toast.LENGTH_LONG).show();
    }
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
