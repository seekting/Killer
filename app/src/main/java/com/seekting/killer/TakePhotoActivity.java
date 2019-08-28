package com.seekting.killer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.seekting.common.ToastUtils;
import com.seekting.utils.PermissionUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

public class TakePhotoActivity extends AppCompatActivity {

    private ImageView mImageView;

    public static void start(Context context) {
        context.startActivity(new Intent(context, TakePhotoActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();


        mImageView = new ImageView(this);
        setContentView(mImageView);
        boolean isNeedRequest = PermissionUtil.checkNeedRequestPermissions(this, PermissionUtil.CAMERA, REQUEST_IMAGE_CAPTURE);
        if (isNeedRequest) {
            return;
        }
        if (savedInstanceState == null) {
            dispatchTakePictureIntent();
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    String mCurrentPhotoPath;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            boolean suc = PermissionUtil.checkSelfPermission(this, "android.permission.CAMERA");
            if (suc) {
                dispatchTakePictureIntent();
            } else {
                ToastUtils.showToast(getApplicationContext(), "没有相机权限！");
                finish();
            }


        }
    }

    private File createImageFile() throws IOException {
        // 唯一的文件名称
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //        File publicDirectory = Environment.getExternalStoragePublicDirectory
        //                (Environment.DIRECTORY_PICTURES);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* 文件名前缀 */
                ".jpg",         /* 文件名后缀 */
                storageDir      /* 路径 */
        );

        // mCurrentPhotoPath下面会被发送到系统相册里去显示
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 2;

    private void dispatchTakePictureIntent() {
        //action为MediaStore.ACTION_IMAGE_CAPTURE
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 保存所拍照片的文件
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //
            }
            if (photoFile != null) {
                //这里为了兼容7.0以下的设备，需要在清单文件中定义一个FileProvider,这里的
                //com.jackbear.notificationtimer.fileprovider要和FileProvider中的authorities相同，如下图
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider",
                        photoFile);
                //如果photoFile路径是公共的系统图片路径即通过Environment.getExternalStoragePublicDirectory
                //(Environment.DIRECTORY_PICTURES)获取的话则不能用FileProvider的方式了，否则会异常
//                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
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
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

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
        mImageView.setImageBitmap(bitmap);
    }
}
