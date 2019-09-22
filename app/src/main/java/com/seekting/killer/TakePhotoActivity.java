package com.seekting.killer;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.seekting.BaseActivity;
import com.seekting.MediaUtils;
import com.seekting.common.DialogUtils;
import com.seekting.common.ToastUtils;
import com.seekting.killer.databinding.TakePhotoActivityBinding;
import com.seekting.killer.model.IPAddress;
import com.seekting.utils.FtpUtils;
import com.seekting.utils.PermissionUtil;
import com.seekting.utils.ProgressUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TakePhotoActivity extends BaseActivity implements Callback, FtpUtils.FtpCallBack {

    //    public static final String WORK_FTP_IMG = "work/ftp/img/";
    public static final String WORK_FTP_IMG = "ftp/img/";
    private TakePhotoActivityBinding mTakePhotoActivityBinding;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private String mCurrentPhotoPath;
    private Dialog mDialog;

    public static void start(Context context) {
        context.startActivity(new Intent(context, TakePhotoActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        mTakePhotoActivityBinding = DataBindingUtil.setContentView(this, R.layout.take_photo_activity);
        mTakePhotoActivityBinding.setActivity(this);
        boolean isNeedRequest = PermissionUtil.checkNeedRequestPermissions(this, PermissionUtil.CAMERA_STORAGE, REQUEST_IMAGE_CAPTURE);
        if (isNeedRequest) {
            return;
        }
        if (savedInstanceState == null) {
            dispatchTakePictureIntent();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            boolean suc = PermissionUtil.checkStoragePermission(this) && PermissionUtil.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (suc) {
                dispatchTakePictureIntent();
            } else {
                ToastUtils.showToast(getApplicationContext(), "没有相机权限！");
                finish();
            }


        }
    }


    static final int REQUEST_TAKE_PHOTO = 2;

    private void dispatchTakePictureIntent() {
        //action为MediaStore.ACTION_IMAGE_CAPTURE
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 保存所拍照片的文件
            File photoFile = null;
            try {
                photoFile = MediaUtils.createImageFile();
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                //
            }

            if (photoFile != null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    //这里为了兼容7.0以下的设备，需要在清单文件中定义一个FileProvider,这里的
                    //com.jackbear.notificationtimer.fileprovider要和FileProvider中的authorities相同，如下图
                    Uri photoURI = FileProvider.getUriForFile(this,
                            getPackageName() + ".fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                } else {
                    Uri photoURI = Uri.fromFile(photoFile);
                    takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //mCurrentPhotoPath即文件的路径
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                //将照片插入系统相册
                galleryAddPic();
                //显示图片
                setPic();
                break;
            default:
                break;
            }
        }
    }

    private void setPic() {
        // 获取控件尺寸
        int targetW = getResources().getDisplayMetrics().widthPixels;
        int targetH = getResources().getDisplayMetrics().heightPixels;

        // 获取照片bitmap对象尺寸
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // 计算缩放比例
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // 按缩放比例解析出照片文件
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Log.d("seekting", "TakePhotoActivity.setPic()" + mCurrentPhotoPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mTakePhotoActivityBinding.img.setImageBitmap(bitmap);
    }

    public void onUploadClick(View view) {
        IPAddress ip = IPAddress.read();
        String str = IPAddress.getShortAddress(ip);
        if (TextUtils.isEmpty(str)) {
            ToastUtils.showToast(this, "没有服务器地址");
            return;
        }

        try {
            mDialog = ProgressUtils.showProgress(this);
            File file = new File(mCurrentPhotoPath);
            FileInputStream fileInputStream = new FileInputStream(file);
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    FtpUtils.getInstance().uploadFile(fileInputStream, WORK_FTP_IMG + file.getName(), TakePhotoActivity.this);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissDialog(mDialog);
                ToastUtils.showToast(TakePhotoActivity.this, "上传失败");
                Log.d("seekting", "TakePhotoActivity.run()", e);
            }
        });

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissDialog(mDialog);
                ToastUtils.showToast(TakePhotoActivity.this, "上传成功");
            }
        });
    }

    @Override
    public void onUploadFail(String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissDialog(mDialog);
                ToastUtils.showToast(TakePhotoActivity.this, msg);
            }
        });

    }

    @Override
    public void onUploadSuc() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissDialog(mDialog);
                ToastUtils.showToast(TakePhotoActivity.this, "上传成功");
            }
        });
    }
}
