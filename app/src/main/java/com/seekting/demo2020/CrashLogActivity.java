package com.seekting.demo2020;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;

import com.seekting.killer.IndexActivity;
import com.seekting.utils.ScreenUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

import androidx.annotation.Nullable;


public class CrashLogActivity extends Activity {

    public static final String CRASH_LOG = "crash_log";
    public static final String CLEAR_RECENT = "clear_recent";
    private WebView mWebView;
    private FrameLayout mFrameLayout;

    public static void showCrashLog(Throwable t, Context context) {
        String s = printStackTraceToString(t);
        showLog(context, s);
    }

    public static void showLog(Context context, String s) {
        Intent intent = new Intent(context, CrashLogActivity.class);
        intent.putExtra(CRASH_LOG, s);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    static String printStackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String log = getIntent().getStringExtra(CRASH_LOG);
        mFrameLayout = new FrameLayout(this);
        mWebView = new WebView(this);
        WebSettings settings = mWebView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);

        mWebView.loadDataWithBaseURL(null, log, "text", "UTF-8", null);
        Button button = new Button(this);
        button.setText("重启");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CrashLogActivity.this, IndexActivity.class);
                intent.putExtra(CLEAR_RECENT, true);
                startActivity(intent);
                finish();
            }
        });
        mFrameLayout.addView(mWebView);
        mFrameLayout.addView(button);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) button.getLayoutParams();
        layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        layoutParams.width = ScreenUtils.dpToPx(getResources(), 100);
        layoutParams.height = ScreenUtils.dpToPx(getResources(), 50);
        setContentView(mFrameLayout);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFrameLayout.removeAllViews();
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
    }
}
