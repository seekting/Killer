package com.seekting.common.mvp;


import androidx.fragment.app.Fragment;

public abstract class BaseFragment<P extends Presenter<U>, U extends com.seekting.common.mvp.Ui> extends Fragment {


    protected P mPresenter;

    protected U mUi;

    abstract protected P createPresenter();

    abstract protected U createUi();


    public BaseFragment() {
        mPresenter = createPresenter();
        mUi = createUi();
        mPresenter.mUi = mUi;
    }


}
