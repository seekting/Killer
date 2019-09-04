package com.seekting.killer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.seekting.common.ToastUtils;
import com.seekting.killer.databinding.DataActivityBinding;
import com.seekting.utils.PermissionUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class DataActivity extends AppCompatActivity {

    private DataActivityBinding mDataActivityBinding;

    public static void start(Context context) {
        context.startActivity(new Intent(context, DataActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataActivityBinding = DataBindingUtil.setContentView(this, R.layout.data_activity);
        mDataActivityBinding.setActivity(this);
        PermissionUtil.checkNeedRequestPermissions(this, PermissionUtil.CAMERA_STORAGE, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1) {
            return;
        }
        if (!PermissionUtil.checkCameraStoragePermission(this)) {
            ToastUtils.showToast(this, "权限不足");
            finish();
        }
    }


    public void onDataVideoClick(View v) {
        VideoRecordActivity.start(this);

    }

    public void onDataImgClick(View v) {
//        打开系统拍照
        TakePhotoActivity.start(this);


    }

    public void onDataSoundClick(View v) {
        //实现语音录制
        com.seekting.killer.TakeMicRecordActivity.start(this);

    }

    public void onDataLocationClick(View v) {
//         实现定位
        LocationActivity.start(this);

    }


}
