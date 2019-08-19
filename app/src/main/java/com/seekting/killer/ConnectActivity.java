package com.seekting.killer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.seekting.killer.databinding.ConnectActivityBinding;
import com.seekting.killer.model.IPAddress;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class ConnectActivity extends AppCompatActivity {

    private ConnectActivityBinding mConnectActivityBinding;
    private IPAddress mIpAddress;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ConnectActivity.class));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnectActivityBinding = DataBindingUtil.setContentView(this, R.layout.connect_activity);
        mIpAddress = IPAddress.read();
        mConnectActivityBinding.ipInput1.setPort(false);
        mConnectActivityBinding.ipInput2.setPort(false);
        mConnectActivityBinding.ipInput3.setPort(false);
        mConnectActivityBinding.ipInput4.setPort(false);
        mConnectActivityBinding.port.setPort(true);
        mConnectActivityBinding.setIpAddress(mIpAddress);
        mConnectActivityBinding.setActivity(this);


    }

    public void onConnectedClick(View v) {
        KillerActivity.start(this, mIpAddress);

    }


}
