package com.seekting.killer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.seekting.common.DialogUtils;
import com.seekting.common.ToastUtils;
import com.seekting.killer.databinding.LocationActivityBinding;
import com.seekting.killer.model.IPAddress;
import com.seekting.utils.FtpUtils;
import com.seekting.utils.LocationManager;
import com.seekting.utils.PermissionUtil;
import com.seekting.utils.ProgressUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.seekting.MediaUtils.SIMPLE_DATE_FORMAT;

public class LocationActivity extends AppCompatActivity implements LocationManager.Listener, LocationManager.LocationUpdateListener, Callback, FtpUtils.FtpCallBack {

    public static final int REUQESTCODE = 1;
    private static final String WORK_FTP_LOC = "ftp/location/";
    private boolean doLocation = false;
    private Dialog mDialog;
    private LocationActivityBinding mLocationActivityBinding;

    private LocationManager mLocationManager;
    private Location mLocation;

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
            mLocation = location;
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

    public void onUploadClick(View v) {
        IPAddress ip = IPAddress.read();
        String str = IPAddress.getShortAddress(ip);
        if (TextUtils.isEmpty(str)) {
            ToastUtils.showToast(this, "没有服务器地址");
            return;
        }

        if (mLocation == null) {
            return;
        }

        mDialog = ProgressUtils.showProgress(this);
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                File file = new File(getFilesDir(), "location.txt");
                FileWriter fileWriter = null;
                String timeStamp = SIMPLE_DATE_FORMAT.format(new Date());
                String locname = "Location_" + timeStamp + ".txt";
                try {
                    fileWriter = new FileWriter(file);
                    String loc = mLocation.getLongitude() + ":" + mLocation.getLatitude();
                    fileWriter.write(loc);
                    fileWriter.flush();
                    fileWriter.close();
                    FileInputStream fileInputStream = new FileInputStream(file);
                    FtpUtils.getInstance().uploadFile(fileInputStream, WORK_FTP_LOC + locname, LocationActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onFailure(Call call, IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissDialog(mDialog);
                ToastUtils.showToast(LocationActivity.this, "上传失败");
                Log.d("seekting", "LocationActivity.onFailure()", e);
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissDialog(mDialog);
                ToastUtils.showToast(LocationActivity.this, "上传成功");
            }
        });
    }

    @Override
    public void onUploadFail(String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissDialog(mDialog);
                ToastUtils.showToast(LocationActivity.this, msg);
            }
        });

    }

    @Override
    public void onUploadSuc() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissDialog(mDialog);
                ToastUtils.showToast(LocationActivity.this, "上传成功");
            }
        });
    }
}
