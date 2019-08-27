package com.seekting.killer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.seekting.common.DialogUtils;
import com.seekting.common.ToastUtils;
import com.seekting.utils.GPSUtils;
import com.seekting.utils.PermissionUtil;
import com.seekting.utils.ProgressUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LocationActivity extends AppCompatActivity {

    public static final int REUQESTCODE = 1;
    private Location mLocation;
    private TextView mTextView;
    private boolean doLocation = false;
    private GPSUtils.OnLocationResultListener mOnLocationResultListener;
    private Dialog mDialog;

    public static void start(Context context) {
        context.startActivity(new Intent(context, LocationActivity.class));

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextView = new TextView(this);
        setContentView(mTextView);

        mOnLocationResultListener = new GPSUtils.OnLocationResultListener() {
            @Override
            public void onLocationResult(Location location) {
                Log.d("seekting", "LocationActivity.onLocationResult()" + location, new NullPointerException());

                mTextView.setText(location.toString());
                DialogUtils.dismissDialog(mDialog);
            }

            @Override
            public void OnLocationChange(Location location) {
                Log.d("seekting", "LocationActivity.OnLocationChange()" + location, new NullPointerException());
                mTextView.setText(location.toString());
                DialogUtils.dismissDialog(mDialog);
            }
        };
        boolean needRequest = PermissionUtil.checkNeedRequestPermissions(
                this, PermissionUtil.LOCATION_PERMISSIONS, REUQESTCODE);
        if (needRequest) {
            return;
        }
        doLocation();
    }

    private void doLocation() {

        mDialog = ProgressUtils.showProgress(this);
        doLocation = true;
        GPSUtils.getInstance(this).getLngAndLat(mOnLocationResultListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REUQESTCODE) {
            if (PermissionUtil.hasLocationPermissions(this)) {
                doLocation();
            } else {
                ToastUtils.showToast(this.getApplicationContext(), "定位失败，没有定位权限！");
                finish();
            }

        }
    }

    boolean destroyInvoked = false;

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing() && !destroyInvoked) {
            destroy();
            destroyInvoked = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!destroyInvoked) {
            destroy();
            destroyInvoked = true;
        }
    }

    private void destroy() {
        if (doLocation) {
            DialogUtils.dismissDialog(mDialog);
            GPSUtils.getInstance(this).removeListener();
        }
    }
}
