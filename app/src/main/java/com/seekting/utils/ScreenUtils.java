package com.seekting.utils;

import android.content.res.Resources;
import android.util.TypedValue;

public class ScreenUtils {
    public static int dpToPx(Resources resources, float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
    }
}
