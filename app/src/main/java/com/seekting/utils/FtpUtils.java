package com.seekting.utils;

import android.util.Log;

import com.seekting.killer.model.IPAddress;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;

public class FtpUtils {
    private static final boolean DEBUG = true;
    private static final String TAG = "FtpUtils";
    private boolean ftpLoginResult = false;
    private final FTPClient mFtpClient;

    public interface FtpCallBack {
        void onUploadFail(String msg);

        void onUploadSuc();
    }

    private static class Holder {
        private static final FtpUtils instance = new FtpUtils();
    }

    public static FtpUtils getInstance() {
        return Holder.instance;
    }

    private FtpUtils() {

        mFtpClient = new FTPClient();
        useCompressedTransfer();


    }

    void checkConnect() {
        //connect("10.221.128.89", 21, "seekting", "123");
        IPAddress address = IPAddress.read();
        connect(address.getIP(), 21, "ftp", "ftp");
    }

    public void useCompressedTransfer() {
        try {
            mFtpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.COMPRESSED_TRANSFER_MODE);
            // 使用被动模式设为默认
            mFtpClient.enterLocalPassiveMode();
            // 二进制文件支持
            mFtpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //设置缓存
            mFtpClient.setBufferSize(1024);
            //设置编码格式，防止中文乱码
            mFtpClient.setControlEncoding("UTF-8");
            //设置连接超时时间
            this.mFtpClient.setConnectTimeout(10 * 1000);
            //设置数据传输超时时间
            mFtpClient.setDataTimeout(10 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean connect(String ip, int port, String userName, String pass) {
        boolean status = false;

        try {
            if (!mFtpClient.isConnected()) {
                mFtpClient.connect(ip, port);
                status = mFtpClient.login(userName, pass);
                ftpLoginResult = status;
            }

            Log.i(TAG, "connect: " + status);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;
    }


    public boolean isConnected() {

        boolean connected = false;
        if (mFtpClient != null) {
            mFtpClient.isConnected();
        }
        return connected;
    }

    public void uploadFile(final InputStream srcFileStream, final String name, FtpCallBack ftpCallBack) {
        try {
            checkConnect();
            if (isConnected()) {
                ftpCallBack.onUploadFail("连接失败");
                srcFileStream.close();
                return;
            }
            mFtpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //图片支持二进制上传  如果采用ASCII_FILE_TYPE(默认)，虽然上传后有数据，但图片无法打开
            boolean status = mFtpClient.storeFile(name, srcFileStream);
            srcFileStream.close();
            if (status) {
                ftpCallBack.onUploadSuc();
            } else {

                ftpCallBack.onUploadFail("上传失败");
            }
            Log.d("seekting", "FtpUtils.uploadFile()" + status);
        } catch (Exception e) {
            Log.i(TAG, "run: " + e.toString());
        }

    }

    public void disconnect() {
        try {
            if (mFtpClient != null && mFtpClient.isConnected()) {
                mFtpClient.logout();//退出
                mFtpClient.disconnect();//断开
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
