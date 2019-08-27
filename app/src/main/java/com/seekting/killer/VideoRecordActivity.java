package com.seekting.killer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import com.seekting.killer.view.CircleButtonView;
import com.seekting.utils.OkHttpCallBackWrap;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitEventListenerAdapter;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VideoRecordActivity extends AppCompatActivity {
    private com.wonderkiln.camerakit.CameraView cameraKitView;
    private CircleButtonView mCircleButtonView;
    private View back;
    private VideoView mVideoView;
    private Handler mHandler = new Handler();


    private Stage mStage;

    enum Stage {
        PrePare, Recording, Recorded
    }

    private CameraKitEventListener mCameraKitEventListener;

    public static void start(Context context) {
        context.startActivity(new Intent(context, VideoRecordActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_upload_activity);
        mVideoView = findViewById(R.id.video_view);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                Log.d("seekting", "VideoRecordActivity.onPrepared()" + mVideoView.getMeasuredHeight());
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVideoView.start();
            }
        });
        back = findViewById(R.id.back);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        getSupportActionBar().hide();
        mCircleButtonView = findViewById(R.id.record_button);

        mCircleButtonView.setOnLongClickListener(new CircleButtonView.OnLongClickListener() {
            @Override
            public void onLongClick() {
                Log.d("seekting", "VideoRecordActivity.onLongClick()");
                onVideoStartClick(mCircleButtonView);
            }

            @Override
            public void onNoMinRecord(int currentTime) {
                Log.d("seekting", "VideoRecordActivity.onNoMinRecord()currentTime=" + currentTime + ",");
                cancelVideo(mCircleButtonView);

            }

            @Override
            public void onRecordFinishedListener() {

                Log.d("seekting", "VideoRecordActivity.onRecordFinishedListener()");
                onVideoStopClick(mCircleButtonView);
            }
        });
        cameraKitView = findViewById(R.id.camera);
        mCameraKitEventListener = new CameraKitEventListenerAdapter() {
            @Override
            public void onImage(CameraKitImage image) {
                super.onImage(image);
                Log.d("seekting", "VideoRecordActivity.onImage()image=" + image + ",");
            }

            @Override
            public void onVideo(CameraKitVideo video) {
                super.onVideo(video);
                Log.d("seekting", "VideoRecordActivity.onVideo()video=" + video + ",");
                cameraKitView.setVisibility(View.GONE);
                mVideoView.setVisibility(View.VISIBLE);
                File file = new File(getExternalMediaDirs()[0], "record.mp4");
                mVideoView.setVideoPath(file.getAbsolutePath());
                mVideoView.start();

            }

            public void onEvent(CameraKitEvent event) {
//                Log.d("seekting", "VideoRecordActivity.onEvent()event=" + event + ",");
            }

            public void onError(CameraKitError error) {
                Log.w("seekting", "VideoRecordActivity.onError()error=", error.getException());
            }

        };

        cameraKitView.addCameraKitListener(mCameraKitEventListener);

        setStage(Stage.PrePare);
    }

    public void setStage(Stage stage) {
        if (this.mStage == stage) {
            return;
        }
        mStage = stage;
        switch (mStage) {

        case PrePare:
            cameraKitView.setVisibility(View.VISIBLE);
            mVideoView.setVisibility(View.GONE);
            if (!cameraKitView.isStarted()) {
                cameraKitView.start();

            }
            findViewById(R.id.record_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.complete_layout).setVisibility(View.GONE);
            break;
        case Recording:
            findViewById(R.id.record_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.complete_layout).setVisibility(View.GONE);
            cameraKitView.setVisibility(View.VISIBLE);
            mVideoView.setVisibility(View.GONE);
            if (mVideoView.canPause()) {
                mVideoView.pause();
            }
            startRecord();
            break;
        case Recorded:
            findViewById(R.id.record_layout).setVisibility(View.GONE);
            findViewById(R.id.complete_layout).setVisibility(View.VISIBLE);
            cameraKitView.stopVideo();
            break;
        }


    }

    private void startRecord() {
        Log.d("seekting", "VideoRecordActivity.onVideoStartClick()" + cameraKitView.getMeasuredHeight());
        File file = new File(getExternalMediaDirs()[0], "record.mp4");
        if (file.exists()) {
            file.delete();
        }
        Log.d("seekting", "MediaUpLoadA ctivity.onVideoStartClick()" + file);
        cameraKitView.captureVideo(file);
    }


    protected void onResume() {
        super.onResume();
        if (mStage == Stage.Recording || mStage == null) {
            if (!cameraKitView.isStarted()) {
                Log.d("seekting", "cameraKitView.start()");

                cameraKitView.start();
            }
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        if (mStage == Stage.Recording || mStage == null) {
            if (cameraKitView.isStarted()) {
                Log.d("seekting", "cameraKitView.stop()");
                cameraKitView.stop();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onVideoStartClick(View v) {


        setStage(Stage.Recording);

    }

    public void onVideoStopClick(View v) {
        Log.d("seekting", "VideoRecordActivity.onVideoStopClick()");
        setStage(Stage.Recorded);


    }

    public void cancelVideo(View v) {
        Log.d("seekting", "VideoRecordActivity.cancelVideo()");
        cameraKitView.stopVideo();

    }

    public void onBackClick(View v) {
        String url = "http://10.232.52.250:80";
        File file = new File(getExternalMediaDirs()[0], "record.mp4");
        try {
            OkHttpCallBackWrap.post(url, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onCompleteBackClick(View v) {
        setStage(Stage.PrePare);

    }

    public void onCompleteOkClick(View v) {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
        finish();

    }

    @Override
    public void onBackPressed() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
        super.onBackPressed();
    }

    private boolean isDestroy = false;

    private void destroy() {
        if (!isDestroy) {
            //释放资源
//            mVideoView.stopPlayback();
//            cameraKitView.addCameraKitListener();

            isDestroy = true;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            destroy();//释放资源
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();//释放资源,在onDestroy检测释放回收
    }

}
