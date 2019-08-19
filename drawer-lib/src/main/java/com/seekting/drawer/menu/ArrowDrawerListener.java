package com.seekting.drawer.menu;

import android.util.Log;
import android.view.View;

import androidx.drawerlayout.widget.DrawerLayout;


public class ArrowDrawerListener extends DrawerLayout.SimpleDrawerListener {
    protected static final boolean DEBUG = false;
    private static final String TAG = "ArrowDrawerListener";

    private final MaterialMenuView mMaterialMenu;
    protected boolean animateEnabled = true;
    private boolean isDrawerClosed = false;

    public ArrowDrawerListener(MaterialMenuView materialMenu) {
        mMaterialMenu = materialMenu;
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        isDrawerClosed = true;
        //        if (mMaterialMenu != null && !animateEnabled) {
        //            mMaterialMenu.setState(IconState.BURGER);
        //        }
        if (DEBUG) {
            Log.i(TAG, "onDrawerClosed");
        }
    }

    ;

    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        //        if (mMaterialMenu != null && !animateEnabled) {
        //            mMaterialMenu.setState(IconState.ARROW);
        //        }
        if (DEBUG) {
            Log.i(TAG, "onDrawerOpened");
        }
    }

    ;

    @Override
    public void onDrawerStateChanged(int newState) {
        super.onDrawerStateChanged(newState);
    }

    /**
     * 是否开启动画
     *
     * @param animateEnabled
     */
    public void setAnimationEnable(boolean animateEnabled) {
        this.animateEnabled = animateEnabled;
    }

    ;

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
        if (DEBUG) {
            Log.i(TAG, "onDrawerSlide");
        }
        if (isDrawerClosed) {
            isDrawerClosed = false;
        }
        if (animateEnabled) {
            mMaterialMenu.setTransformationOffset(MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                    isDrawerClosed ? 2 - slideOffset : slideOffset);
        }
    }

    public void setCloseArrow() {
        if (mMaterialMenu != null) {
            mMaterialMenu.setState(MaterialMenuDrawable.IconState.BURGER);
        }
    }
}
