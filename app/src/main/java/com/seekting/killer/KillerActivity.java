package com.seekting.killer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.seekting.BaseActivity;
import com.seekting.common.ToastUtils;
import com.seekting.killer.databinding.KillerActivityBinding;
import com.seekting.killer.model.Bar;
import com.seekting.killer.model.BarControl;
import com.seekting.killer.model.Bars;
import com.seekting.killer.model.IPAddress;
import com.seekting.killer.model.IPAddressConnector;
import com.seekting.killer.model.Person;
import com.seekting.killer.model.PersonControl;
import com.seekting.killer.model.Persons;
import com.seekting.killer.model.SocketChannelWriteRead;
import com.seekting.utils.ProgressUtils;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

public class KillerActivity extends BaseActivity implements IPAddressConnector.ClientConnectListener {
    private IPAddressConnector mIpAddressConnector;
    private Dialog mDialog;
    private SocketChannelWriteRead socketChannelWriteRead;
    private IPAddress mIpAddress;
    private KillerActivityBinding mKillerActivityBinding;

    public static void start(Activity activity, IPAddress ipAddress) {
        Intent intent = new Intent(activity, KillerActivity.class);
        intent.putExtra("ipAddress", ipAddress);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKillerActivityBinding = DataBindingUtil.setContentView(this, R.layout.killer_activity);
        mKillerActivityBinding.setActivity(this);
        mIpAddressConnector = IPAddressConnector.getInstance();
        mIpAddress = (IPAddress) getIntent().getSerializableExtra("ipAddress");
        startConnect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            IPAddress.save(mIpAddress);
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    if (socketChannelWriteRead != null) {
                        socketChannelWriteRead.write("bye");
                        socketChannelWriteRead.cancel();
                        socketChannelWriteRead = null;
                    }
                }
            });
        }
    }

    private void startConnect() {
        mDialog = ProgressUtils.showProgress(this);
        mIpAddressConnector.startConnect(mIpAddress, this);
    }

    @Override
    public void onConnected(SocketChannelWriteRead socketChannelWriteRead) {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        this.socketChannelWriteRead = socketChannelWriteRead;
        ToastUtils.showToast(this, "连接成功");
        mKillerActivityBinding.remoteAddress.setText(getResources().getString(R.string.remote_address, socketChannelWriteRead.getRemoteAddress()));
        mKillerActivityBinding.localAddress.setText(getResources().getString(R.string.local_address, socketChannelWriteRead.getLocalAddress()));

    }

    @Override
    public void onConnectedFail() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        ToastUtils.showToast(this, "连接失败");
    }

    @Override
    public void onRead(String str) {

        Log.d("seekting", "ConnectActivity.onRead()" + str);
        try {
            JSONObject jsonObject = new JSONObject(str);
            String type = jsonObject.optString("type");
            if ("0".equals(type)) {
                //靶
                Gson gson = new Gson();
                Bars bars = gson.fromJson(str, Bars.class);
                Log.d("seekting", "KillerActivity.onRead()" + bars);
                setBars(bars);
            } else if ("1".equals(type)) {
                //人
                Gson gson = new Gson();
                Persons persons = gson.fromJson(str, Persons.class);

                Log.d("seekting", "KillerActivity.onRead()" + persons);
                setPersons(persons);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void setPersons(Persons persons) {
        ArrayAdapter<Person> arrayAdapter = new ArrayAdapter<Person>(this, R.layout.item);
        arrayAdapter.addAll(persons.getPersons());
        mKillerActivityBinding.persons.setAdapter(arrayAdapter);
    }

    private void setBars(Bars bars) {
        ArrayAdapter<Bar> arrayAdapter = new ArrayAdapter<Bar>(this, R.layout.item);
        arrayAdapter.addAll(bars.getBars());
        mKillerActivityBinding.bars.setAdapter(arrayAdapter);
    }

    @Override
    public void onDisConnected() {
        this.socketChannelWriteRead = null;
        ToastUtils.showToast(getApplicationContext(), "服务器中断连接");
        finish();
        ConnectActivity.start(this);

    }

    /**
     * Person
     *
     * @param v
     */
    public void onAliveClick(View v) {
        if (this.socketChannelWriteRead == null) {
            return;

        }
        controlPerson("1");

    }

    private void controlPerson(String type) {
        Person person = (Person) mKillerActivityBinding.persons.getSelectedItem();
        if (person == null) {
            ToastUtils.showToast(this, "没有要控制的人");
            return;
        }
        String str = person.getId();
        PersonControl personControl = new PersonControl();
        personControl.setPerson(str);
        personControl.setType(type);
        Gson gson = new Gson();
        String json = gson.toJson(personControl);
        socketChannelWriteRead.write(json);
    }

    /**
     * Person
     *
     * @param v
     */
    public void onOutClick(View v) {
        if (this.socketChannelWriteRead == null) {
            return;

        }
        controlPerson("0");
    }

    private void controlBar(String type) {
        Bar bar = (Bar) mKillerActivityBinding.bars.getSelectedItem();
        if (bar == null) {
            ToastUtils.showToast(this, "没有要控制的靶");
            return;
        }
        String str = bar.getId();
        BarControl barControl = new BarControl();
        barControl.setBar(str);
        barControl.setType(type);
        Gson gson = new Gson();
        String json = gson.toJson(barControl);
        socketChannelWriteRead.write(json);
    }

    /**
     * Bar
     *
     * @param v
     */
    public void onUpClick(View v) {
        if (this.socketChannelWriteRead == null) {
            return;

        }
        controlBar("0");
    }

    /**
     * Bar
     *
     * @param v
     */
    public void onDownClick(View v) {
        if (this.socketChannelWriteRead == null) {
            return;

        }
        controlBar("1");
    }


}
