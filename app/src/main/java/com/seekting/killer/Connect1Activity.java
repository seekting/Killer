package com.seekting.killer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.seekting.ConnectManager;
import com.seekting.common.ToastUtils;
import com.seekting.killer.databinding.Connect1ActivityBinding;
import com.seekting.killer.model.IPAddress;
import com.seekting.killer.model.IPAddressConnector;
import com.seekting.killer.model.SocketChannelWriteRead;
import com.seekting.utils.DialogUtils;
import com.seekting.utils.FastClickUtils;
import com.seekting.utils.ProgressUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class Connect1Activity extends AppCompatActivity implements IPAddressConnector.ClientConnectListener {
    Connect1ActivityBinding mConnect1ActivityBinding;
    private IPAddress mIPAddress;
    private ConnectManager mConnectManager;
    private Dialog mDialog;

    public static void start(Context context) {
        context.startActivity(new Intent(context, Connect1Activity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        mIPAddress = IPAddress.read();
        mConnect1ActivityBinding = DataBindingUtil.setContentView(this, R.layout.connect1_activity);
        mConnect1ActivityBinding.setIPAddress(mIPAddress);
        mConnect1ActivityBinding.setActivity(this);

        mConnectManager = ConnectManager.getInstance();
        mConnectManager.setClientConnectListener(this);
        update();


    }

    public void onCancelClick(View v) {
        if (FastClickUtils.isFastClick()) {
            return;
        }
        mConnectManager.cancel();
    }

    public void onConnectedClick(View v) {
        if (FastClickUtils.isFastClick()) {
            return;
        }
        boolean verify = mIPAddress.verify();
        if (verify) {
            mDialog = ProgressUtils.showProgress(this);
            mConnectManager.startConnect(mIPAddress);

        } else {
            ToastUtils.showToast(this, "格式错误！");
        }


    }

    @Override
    public void onConnected(SocketChannelWriteRead socketChannel) {
        update();
    }

    private void update() {
        DialogUtils.dismissDialog(mDialog);
        mDialog = null;
        mConnect1ActivityBinding.okAction.setEnabled(!mConnectManager.isConnected());
        mConnect1ActivityBinding.ipInput.setEnabled(!mConnectManager.isConnected());
        mConnect1ActivityBinding.cancelAction.setEnabled(mConnectManager.isConnected());
    }

    @Override
    public void onConnectedFail() {
        update();

    }

    @Override
    public void onRead(String str) {

    }

    @Override
    public void onDisConnected() {
        update();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            IPAddress.save(mIPAddress);
        }
    }
}