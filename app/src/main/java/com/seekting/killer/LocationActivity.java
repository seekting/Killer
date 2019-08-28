package com.seekting.killer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.seekting.common.DialogUtils;
import com.seekting.common.ToastUtils;
import com.seekting.killer.databinding.LocationActivityBinding;
import com.seekting.utils.LocationManager;
import com.seekting.utils.PermissionUtil;
import com.seekting.utils.ProgressUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class LocationActivity extends AppCompatActivity implements LocationManager.Listener, LocationManager.LocationUpdateListener {

    public static final int REUQESTCODE = 1;
    private TextView mTextView;
    private boolean doLocation = false;
    private Dialog mDialog;
    private LocationActivityBinding mLocationActivityBinding;
    private LocationManager mLocationManager;

    public static void start(Context context) {
        context.startActivity(new Intent(context, LocationActivity.class));

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = LocationManager.instance();

        mLocationActivityBinding = DataBindingUtil.setContentView(this, R.layout.location_activity);
        mLocationActivityBinding.setActivity(this);

        mLocationManager.setListener(this);
        mLocationManager.setLocationUpdateListener(this);
        mTextView = new TextView(this);


        boolean needRequest = PermissionUtil.checkNeedRequestPermissions(
                this, PermissionUtil.LOCATION_PERMISSIONS, REUQESTCODE);
        if (needRequest) {
            return;
        }
        doLocation();
    }

    private void doLocation() {

        mLocationManager.recordLocation(true, this);
        mLocationManager.setListener(this);
        setLocationText(mLocationManager.getLastKnownLocation());
        mDialog = ProgressUtils.showProgress(this);
        doLocation = true;


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
            mLocationManager.unsetListener(this);
            mLocationManager.setLocationUpdateListener(null);
            mLocationManager.recordLocation(false, this);
        }
    }

    @WorkerThread
    @Override
    public void showGpsOnScreenIndicator(boolean hasSignal) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLocationActivityBinding.gpsImg.setVisibility(hasSignal ? View.VISIBLE : View.INVISIBLE);
            }
        });

    }

    private void setLocationText(Location location) {
        if (location != null) {
            DialogUtils.dismissDialog(mDialog);
            String text = location.toString();
            mLocationActivityBinding.locationText.setText(text);
        }
    }

    @WorkerThread
    @Override
    public void hideGpsOnScreenIndicator() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLocationActivityBinding.gpsImg.setVisibility(View.INVISIBLE);
            }
        });
    }

    @WorkerThread
    @Override
    public void onLocationUpdate(Location location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                setLocationText(location);
            }
        });

    }
}
