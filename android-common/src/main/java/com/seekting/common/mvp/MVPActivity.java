package com.seekting.common.mvp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class MVPActivity<P extends Presenter<U>, U extends Ui> extends AppCompatActivity {

    protected P mPresenter;

    protected U mUi;

    abstract protected P createPresenter();

    abstract protected U createUi();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUi = createUi();
        mPresenter = createPresenter();
        mPresenter.mUi = mUi;
    }
}
