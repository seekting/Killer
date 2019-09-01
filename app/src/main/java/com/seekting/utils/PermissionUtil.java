
package com.seekting.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {
    private static final String TAG = "PermissionUtil";
    public static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public static final String[] VIDEO_RECORD_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
    public static final String[] CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,

    };


    public static boolean checkNeedRequestPermissions(
            Activity activity, String[] requestPermissions,
            int requestCode) {
        ArrayList<String> permissionList = new ArrayList<String>();
        for (String permission : requestPermissions) {
            if (!checkSelfPermission(activity, permission)) {
                permissionList.add(permission);
            }
        }

        String[] permissionArr = new String[permissionList.size()];
        permissionList.toArray(permissionArr);
        requestPermissions(activity, permissionArr, requestCode);
        return permissionList.size() > 0;
    }

    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (permissions.length > 0) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    public static boolean checkSelfPermission(Context context, String permission) {
//        if (LogUtil.enable()) {
        Log.i(TAG, "checkSelfPermission(): permission:" + permission);
//        }
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkStoragePermission(Activity activity) {
        return checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                ;
    }

    public static boolean hasLocationPermissions(Context context) {
        return checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) &&
                checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static boolean hasVideoRecordPermissions(Context context) {
        return checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) &&
                checkSelfPermission(context, Manifest.permission.CAMERA);
    }
}
