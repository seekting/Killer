package com.seekting.killer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.seekting.BaseActivity;
import com.seekting.MediaUtils;
import com.seekting.common.DialogUtils;
import com.seekting.common.ToastUtils;
import com.seekting.killer.databinding.TakeMicRecordActivityBinding;
import com.seekting.killer.model.IPAddress;
import com.seekting.utils.FtpUtils;
import com.seekting.utils.ProgressUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TakeMicRecordActivity extends BaseActivity implements Callback, FtpUtils.FtpCallBack {
    public static final int REQUEST_RECORDER = 1;
    private static final String WORK_FTP_AUDIO = "ftp/audio/";
    public MediaPlayer mediaPlayer;
    private TakeMicRecordActivityBinding mTakeMicRecordActivityBinding;
    private AlertDialog mDialog;
    private File mFile;

    public static void start(Context context) {
        context.startActivity(new Intent(context, TakeMicRecordActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        mTakeMicRecordActivityBinding = DataBindingUtil.setContentView(this, R.layout.take_mic_record_activity);
        mTakeMicRecordActivityBinding.setActivity(this);

        mediaPlayer = new MediaPlayer();
        mTakeMicRecordActivityBinding.upload.setEnabled(false);
        try {
            startActivityForResult(intent, REQUEST_RECORDER);
        } catch (Exception e) {

        }
    }

    private File toFile(Uri uri) {
        //uri转换成file
        String[] arr = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, arr, null, null, null);
        int img_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String img_path = cursor.getString(img_index);
        File file = new File(img_path);

        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && REQUEST_RECORDER == requestCode) {
            Uri uri = data.getData();
            mFile = toFile(uri);
            Log.d("seekting", "TakeRecordActivity.onActivityResult()" + uri);
            if (uri != null) {
                mTakeMicRecordActivityBinding.upload.setEnabled(true);
                if (mediaPlayer != null) {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(TakeMicRecordActivity.this, uri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else
                    Toast.makeText(this, "没有成功创建Mediaplayer", Toast.LENGTH_SHORT).show();
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
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    public void onUploadClick(View v) {


        IPAddress ip = IPAddress.read();
        String str = IPAddress.getShortAddress(ip);
        if (TextUtils.isEmpty(str)) {
            ToastUtils.showToast(this, "没有服务器地址");
            return;
        }

        String url = str + "/audio";
        mDialog = ProgressUtils.showProgress(this);

//            OkHttpCallBackWrap.post(url, mFile, this);
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(mFile);
                    String timeStamp = MediaUtils.SIMPLE_DATE_FORMAT.format(new Date());
                    String audioName = "Audio_" + timeStamp + ".mp3";
                    FtpUtils.getInstance().uploadFile(fileInputStream, WORK_FTP_AUDIO + audioName, TakeMicRecordActivity.this);
                } catch (FileNotFoundException e) {
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
                ToastUtils.showToast(TakeMicRecordActivity.this, "上传失败");
                Log.d("seekting", "TakeMicRecordActivity.onFailure()", e);
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissDialog(mDialog);
                ToastUtils.showToast(TakeMicRecordActivity.this, "上传成功");
            }
        });
    }

    @Override
    public void onUploadFail(String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissDialog(mDialog);
                ToastUtils.showToast(TakeMicRecordActivity.this, msg);
            }
        });

    }

    @Override
    public void onUploadSuc() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissDialog(mDialog);
                ToastUtils.showToast(TakeMicRecordActivity.this, "上传成功");
            }
        });
    }
}
