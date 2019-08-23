package com.seekting.killer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.seekting.killer.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ControlFrameLayout extends FrameLayout implements View.OnClickListener {
    ImageView imageView;
    View titleLayout;
    TextView mTextView;


    public ControlFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


    }

    public void setUi(int imgRes, int textRes) {
        imageView.setImageResource(imgRes);
        mTextView.setText(textRes);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(this);
        imageView = findViewById(R.id.image_view);
        titleLayout = findViewById(R.id.title_layout);
        mTextView = findViewById(R.id.title);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (height == 0) {
            return;
        }
        int leftMargin = 0;
        FrameLayout.LayoutParams marginLayoutParams = (LayoutParams) imageView.getLayoutParams();
        marginLayoutParams.width = height;
        leftMargin = marginLayoutParams.leftMargin;
        marginLayoutParams = (LayoutParams) titleLayout.getLayoutParams();
        marginLayoutParams.leftMargin = height + leftMargin;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        Log.d("seekting", "ControlFrameLayout.onClick()");

    }
}
