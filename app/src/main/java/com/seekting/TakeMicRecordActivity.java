package com.seekting.killer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TakeMicRecordActivity extends AppCompatActivity {
    public static final int REQUEST_RECORDER = 1;
    public MediaPlayer mediaPlayer;

    public static void start(Context context) {
        context.startActivity(new Intent(context, TakeMicRecordActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);

        mediaPlayer = new MediaPlayer();
        try {
            startActivityForResult(intent, REQUEST_RECORDER);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && REQUEST_RECORDER == requestCode) {
            Uri uri = data.getData();
            Log.d("seekting", "TakeRecordActivity.onActivityResult()" + uri);
            if (uri != null) {
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
}
