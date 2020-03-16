package com.seekting.killer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.seekting.BaseActivity;
import com.seekting.killer.databinding.ScoreActivityBinding;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

public class ScoreActivity extends BaseActivity {

    private ScoreActivityBinding mScoreActivityBinding;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ScoreActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScoreActivityBinding = DataBindingUtil.setContentView(ScoreActivity.this, R.layout.score_activity);
    }
}