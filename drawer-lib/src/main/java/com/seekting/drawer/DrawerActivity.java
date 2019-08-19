package com.seekting.drawer;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.seekting.drawer.menu.ArrowDrawerListener;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

public class DrawerActivity extends BaseActionBarActivity {


    protected DrawerLayout mDrawerLayout;
    protected FrameLayout mLeftLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mLeftLayout = findViewById(R.id.left_drawer);
        mDrawerLayout.addDrawerListener(new ArrowDrawerListener(mHomeView));


    }


    protected void onHomeOptionsItemSelected() {

        if (!mDrawerLayout.isDrawerOpen(mLeftLayout)) {
            mDrawerLayout.openDrawer(mLeftLayout);
        } else {
            mDrawerLayout.closeDrawer(mLeftLayout);
        }
    }
}
