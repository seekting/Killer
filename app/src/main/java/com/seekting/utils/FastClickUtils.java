package com.seekting.utils;

public class FastClickUtils {

    private static final int DEFAULT_THRESHOLD = 600;
    private static long lastClickTime;

    /**
     * 快速点击，时间间隔小于600ms
     */
    public static boolean isFastClick(long threshold) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD > 0 && timeD < (threshold < 0 ? DEFAULT_THRESHOLD : threshold)) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean isFastClick() {
        return isFastClick(-1);
    }

    /**
     * 正常点击，时间间隔大于600ms
     */
    public static boolean isNormalClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD > DEFAULT_THRESHOLD) {
            lastClickTime = time;
            return true;
        }
        return false;
    }
}
