package com.seekting.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

public class DialogUtils {
    private static final boolean DEBUG = true;

    /**
     * 安全地显示dialog
     *
     * @param dialog
     */
    public static void showDialog(Dialog dialog) {
        try {
            if (dialog == null) {
                return;
            }
            Context context = dialog.getContext();
            if (context != null && context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    return;
                }
            }
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            dialog.show();
        } catch (Throwable t) {
        }
    }

    /**
     * 安全地关闭dialog
     *
     * @param dialog
     */
    public static void dismissDialog(Dialog dialog) {
        try {
            if (dialog == null) {
                return;
            }
            Context context = dialog.getContext();
            if (context != null && context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    return;
                }
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Throwable t) {
        }
    }

    /**
     * 安全地打开Dialog（ID篇）
     * <p>
     * 至于为什么需要调用该方法，可参见Docs目录下的《Android的Crash原因总结》中的第10页
     *
     * @param activity
     * @param dialogId
     */
    public static void showDialog(Activity activity, int dialogId) {
        if (activity == null) {
            throw new IllegalArgumentException();
        }

        if (activity.isFinishing()) {
            return;
        }

        // 这里不会出现Crash，因此可直接使用
        activity.showDialog(dialogId);
    }

    /**
     * 安全地关闭Dialog（ID篇）
     * <p>
     * 至于为什么需要调用该方法，可参见Docs目录下的《Android的Crash原因总结》中的第10页
     *
     * @param activity
     * @param dialogId
     */
    public static void dismissDialog(Activity activity, int dialogId) {
        if (activity == null) {
            throw new IllegalArgumentException();
        }

        if (activity.isFinishing()) {
            return;
        }

        try {
            activity.dismissDialog(dialogId);
        } catch (IllegalArgumentException e) {

            // 若这个没有显示过，或Activity已被关闭，则系统会抛出IllegalArgumentException，类似于：
            // java.lang.IllegalArgumentException: no dialog with id 881007 was ever shown via Activity#showDialog
            //      at android.app.Activity.missingDialog(Activity.java:3186)
            //      at android.app.Activity.dismissDialog(Activity.java:3171)

            // 遇到这样的错误，我们只能想办法Catch住
            // 正式版：Nothing

        }
    }


}