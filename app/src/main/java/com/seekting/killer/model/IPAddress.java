package com.seekting.killer.model;

import com.seekting.killer.App;
import com.seekting.killer.BR;

import java.io.Serializable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class IPAddress extends BaseObservable implements Serializable {
    private String ip1 = "192";
    private String ip2 = "168";
    private String ip3 = "31";
    private String ip4 = "163";
    private String port = "60000";

    @Bindable
    public String getShortString() {
        return shortString;
    }

    public void setShortString(String shortString) {
        this.shortString = shortString;
        notifyPropertyChanged(BR.shortString);
    }

    private String shortString = "";

    @Bindable
    public String getIp1() {
        return ip1;
    }

    public void setIp1(String ip1) {
        this.ip1 = ip1;
        setShortString(toShortString(this));
        notifyPropertyChanged(BR.ip1);
    }

    @Bindable
    public String getIp2() {
        return ip2;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
        setShortString(toShortString(this));
        notifyPropertyChanged(BR.ip2);
    }

    @Bindable
    public String getIp3() {
        return ip3;
    }

    public void setIp3(String ip3) {
        this.ip3 = ip3;
        setShortString(toShortString(this));
        notifyPropertyChanged(BR.ip3);
    }

    @Bindable
    public String getIp4() {
        return ip4;
    }

    public void setIp4(String ip4) {

        this.ip4 = ip4;
        setShortString(toShortString(this));

        notifyPropertyChanged(BR.ip4);
    }

    @Bindable
    public String getPort() {
        return port;
    }

    public int getPortInt() {
        return Integer.parseInt(port);
    }

    public void setPort(String port) {
        this.port = port;
        setShortString(toShortString(this));
        notifyPropertyChanged(BR.port);
    }


    private static String toShortString(IPAddress ipAddress) {
        return ipAddress.ip1 + "." + ipAddress.ip2 + "." + ipAddress.ip3 + "." + ipAddress.ip4 + ":" + ipAddress.port;

    }

    public String getIP() {
        return ip1 + "." + ip2 + "." + ip3 + "." + ip4;

    }

    public static IPAddress read() {
        Object o = App.Companion.getDataKeeper().get("ip_address");
        if (o instanceof IPAddress) {
            return (IPAddress) o;
        } else {
            return new IPAddress();
        }

    }

    public static void save(IPAddress ipAddress) {
        App.Companion.getDataKeeper().put("ip_address", ipAddress);
    }
}
