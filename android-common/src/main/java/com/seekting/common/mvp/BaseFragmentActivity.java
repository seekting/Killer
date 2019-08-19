package com.seekting.common.mvp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public abstract class BaseFragmentActivity<P extends Presenter<U>, U extends com.seekting.common.mvp.Ui> extends FragmentActivity {

    protected P mPresenter;

    protected U mUi;

    abstract protected P createPresenter();

    abstract protected U createUi();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mUi = createUi();
        mPresenter.mUi = mUi;
    }
}
