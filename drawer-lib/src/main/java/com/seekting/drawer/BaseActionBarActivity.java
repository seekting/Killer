package com.seekting.drawer;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.seekting.drawer.menu.MaterialMenuDrawable;
import com.seekting.drawer.menu.MaterialMenuView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BaseActionBarActivity extends AppCompatActivity {
    protected ActionBar mActionBar;
    protected View mCustomView;
    protected MaterialMenuView mHomeView;
    protected TextView mTitleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayUseLogoEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setCustomView(R.layout.title_ationbar_home_layout);

        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayUseLogoEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setCustomView(com.seekting.drawer.R.layout.title_ationbar_home_layout);
        mCustomView = mActionBar.getCustomView();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Toolbar toolbar = (Toolbar) mCustomView.getParent();
            toolbar.setContentInsetsRelative(0, 0);
        }

        mHomeView = mCustomView.findViewById(R.id.home_meun);

        mHomeView.setState(MaterialMenuDrawable.IconState.BURGER);
        mTitleView = mCustomView.findViewById(R.id.home_title);
        View.OnClickListener homeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHomeOptionsItemSelected();
            }
        };
        mHomeView.setOnClickListener(homeListener);
    }

    protected void onHomeOptionsItemSelected() {
    }
}
