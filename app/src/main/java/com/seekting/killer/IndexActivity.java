package com.seekting.killer;

import android.os.Bundle;
import android.view.View;

import com.seekting.killer.databinding.IndexActivityBinding;
import com.seekting.killer.view.ControlFrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class IndexActivity extends AppCompatActivity implements View.OnClickListener {
    ControlFrameLayout barsLayout, personLayout, dataLayout, connectLayout;
    private IndexActivityBinding mIndexActivityBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        mIndexActivityBinding = DataBindingUtil.setContentView(this, R.layout.index_activity);
        barsLayout = (ControlFrameLayout) mIndexActivityBinding.bars;
        personLayout = (ControlFrameLayout) mIndexActivityBinding.persons;
        dataLayout = (ControlFrameLayout) mIndexActivityBinding.data;
        connectLayout = (ControlFrameLayout) mIndexActivityBinding.connect;

        barsLayout.setUi(R.mipmap.daokong, R.string.daokong_control);
        personLayout.setUi(R.mipmap.caiding, R.string.caiding_control);
        dataLayout.setUi(R.mipmap.shujucaiji, R.string.data_update);
        connectLayout.setUi(R.mipmap.shujutongxing, R.string.data_connect);


        barsLayout.setOnClickListener(this);
        personLayout.setOnClickListener(this);
        dataLayout.setOnClickListener(this);
        connectLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == barsLayout) {
            BarsControlActivity.start(this);

        } else if (v == connectLayout) {
            Connect1Activity.start(this);

        } else if (v == dataLayout) {
        } else if (v == personLayout) {
            PersonControlActivity.start(this);

        }

    }


}
