package com.seekting.killer.model;

import android.os.Handler;
import android.os.HandlerThread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelWriteRead {
    public static final int CAPACITY = 1024 * 4;
    private static ThreadLocal<ByteBuffer> sByteBufferThreadLocal = new InheritableThreadLocal<>();
    private SocketChannel mSocketChannel;
    private String remoteAddress;
    private String localAddress;

    static HandlerThread mHandlerThread = new HandlerThread("write-network");

    static Handler sHandler;

    static {
        mHandlerThread.start();
        sHandler = new Handler(mHandlerThread.getLooper());


    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getLocalAddress() {
        return localAddress;
    }


    public static ByteBuffer obtain() {
        ByteBuffer byteBuffer = sByteBufferThreadLocal.get();
        if (byteBuffer == null) {
            byteBuffer = ByteBuffer.allocate(CAPACITY);
            sByteBufferThreadLocal.set(byteBuffer);
        }

        return byteBuffer;

    }

    public SocketChannelWriteRead(SocketChannel socketChannel) {
        mSocketChannel = socketChannel;
        Socket socket = mSocketChannel.socket();
        int port = socket.getPort();
        String host = socket.getInetAddress().getHostAddress();
        InetAddress localAddress = socket.getLocalAddress();
        remoteAddress = host + ":" + port;
        this.localAddress = localAddress.toString() + ":" + socket.getLocalPort();


    }


    public void write(String str) {
        // 3、分配指定大小的缓冲区
        IPAddressConnector.getInstance().write(str, mSocketChannel);
//        sHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    ByteBuffer byteBuffer = obtain();
//                    byteBuffer.clear();
//                    byteBuffer.put(str.getBytes());
//                    byteBuffer.flip();
//                    mSocketChannel.write(byteBuffer);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    public String read() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ByteBuffer byteBuffer = obtain();
            while (true) {
                byteBuffer.clear();
                int n = mSocketChannel.read(byteBuffer);
                if (n == 0) {
                    break;
                } else if (n == -1) {
                    return null;
                }
                byteBuffer.flip();
                int limit = byteBuffer.limit();

                for (int i = 0; i < limit; i++) {
                    byte value = byteBuffer.get(i);
                    byteArrayOutputStream.write(value);
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        try {
//            return new String(byteArrayOutputStream.toByteArray(), "GBK");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return new String(byteArrayOutputStream.toByteArray());
    }

    public void cancel() {
        if (mSocketChannel == null) {
            return;

        }
        try {
            mSocketChannel.socket().close();
            mSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mSocketChannel = null;
        }

    }
}
