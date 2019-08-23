package com.seekting;

import android.util.Log;

import com.google.gson.Gson;
import com.seekting.killer.model.Bar;
import com.seekting.killer.model.Bars;
import com.seekting.killer.model.IPAddress;
import com.seekting.killer.model.IPAddressConnector;
import com.seekting.killer.model.Person;
import com.seekting.killer.model.Persons;
import com.seekting.killer.model.SocketChannelWriteRead;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ConnectManager implements IPAddressConnector.ClientConnectListener {
    private IPAddressConnector mIpAddressConnector;
    private IPAddress mIPAddress;
    private SocketChannelWriteRead mSocketChannelWriteRead;


    private PersonListener mPersonListener;
    private BarListener mBarListener;
    private List<Person> mPersons;
    private List<Bar> mBars;
    private boolean connected = false;
    private IPAddressConnector.ClientConnectListener mClientConnectListener;

    public List<Person> getPersons() {
        return mPersons;
    }

    public List<Bar> getBars() {
        return mBars;
    }

    public void setPersonListener(PersonListener personListener) {
        mPersonListener = personListener;
    }

    public void setBarListener(BarListener barListener) {
        mBarListener = barListener;
    }


    public interface PersonListener {
        void onPersonUpdate(List<Person> list);

    }

    public interface BarListener {
        void onBarUpdate(List<Bar> list);

    }

    public void setClientConnectListener(IPAddressConnector.ClientConnectListener clientConnectListener) {
        mClientConnectListener = clientConnectListener;
    }

    public void cancel() {
        if (mSocketChannelWriteRead != null) {
            mSocketChannelWriteRead.cancel();
            connected = false;
            if (mClientConnectListener != null) {
                mClientConnectListener.onDisConnected();
            }
        }
    }


    private static class Holder {
        private static final ConnectManager instance = new ConnectManager();
    }

    public static ConnectManager getInstance() {
        return Holder.instance;
    }

    private ConnectManager() {
        mIpAddressConnector = IPAddressConnector.getInstance();
    }

    public boolean isConnected() {
        return connected;
    }


    public void startConnect(IPAddress ipAddress) {
        mIPAddress = ipAddress;
        mIpAddressConnector.startConnect(ipAddress, this);
    }

    public void write(String str) {
        if (mSocketChannelWriteRead != null) {
            mSocketChannelWriteRead.write(str);
        }


    }


    @Override
    public void onConnected(SocketChannelWriteRead socketChannel) {
        connected = true;
        this.mSocketChannelWriteRead = socketChannel;

        if (mClientConnectListener != null) {
            mClientConnectListener.onConnected(socketChannel);
        }

    }

    @Override
    public void onConnectedFail() {
        connected = false;
        if (mClientConnectListener != null) {
            mClientConnectListener.onConnectedFail();

        }

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
        this.mPersons = persons.getPersons();
        if (mPersonListener != null) {
            mPersonListener.onPersonUpdate(this.mPersons);
        }
    }

    private void setBars(Bars bars) {
        this.mBars = bars.getBars();
        if (mBarListener != null) {
            mBarListener.onBarUpdate(this.mBars);
        }
    }

    @Override
    public void onDisConnected() {
        connected = false;
        mClientConnectListener.onDisConnected();

    }
}