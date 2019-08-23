package com.seekting.killer.model;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class IPAddressConnector {

    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private Selector mSelector;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private Map<SocketChannelWriteRead, ClientConnectListener> mSCWMap = new HashMap<>();
    private Map<SocketChannel, SocketChannelWriteRead> mSCWRMap = new HashMap<>();
    private List<Runnable> mConnectTasks = new ArrayList<>();
    private List<Runnable> mWriteTasks = new ArrayList<>();


    private class WriteTask implements Runnable {

        String str;

        public WriteTask(String str, SocketChannel socketChannel) {
            this.str = str;
            mSocketChannel = socketChannel;
        }

        SocketChannel mSocketChannel;

        @Override
        public void run() {
            ByteBuffer byteBuffer = SocketChannelWriteRead.obtain();
            byteBuffer.clear();
            byteBuffer.put(str.getBytes());
            byteBuffer.flip();
            try {
                mSocketChannel.write(byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectTask implements Runnable {
        ClientConnectListener clientConnectListener;
        IPAddress mIPAddress;

        public ConnectTask(ClientConnectListener clientConnectListener, IPAddress IPAddress) {
            this.clientConnectListener = clientConnectListener;
            mIPAddress = IPAddress;
        }

        @Override
        public void run() {
            //打开链接，如果异常说明连接不上，如果往下走说明连上了
            try {
                InetSocketAddress mSocketAddress = new InetSocketAddress(mIPAddress.getIP(), mIPAddress.getPortInt());
                SocketChannel socketChannel = SocketChannel.open(mSocketAddress);

                // 2、切换成非阻塞模式

                socketChannel.configureBlocking(false);

                uiOnConnectSuc(clientConnectListener, socketChannel);
                socketChannel.register(mSelector, SelectionKey.OP_READ);
            } catch (IOException e) {
                uiOnConnectFail(clientConnectListener);
            } catch (IllegalArgumentException i) {
                //ip地址或port问题如端口号最大65534
                uiOnConnectFail(clientConnectListener);

            }
        }
    }

    public interface ClientConnectListener {
        void onConnected(SocketChannelWriteRead socketChannel);

        void onConnectedFail();

        void onRead(String str);

        void onDisConnected();

    }

    private static class Holder {
        private static final IPAddressConnector instance = new IPAddressConnector();
    }

    public static IPAddressConnector getInstance() {
        return Holder.instance;
    }

    private IPAddressConnector() {
        mHandlerThread = new HandlerThread("network");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        prepareSelector();
    }

    private void prepareSelector() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mSelector = Selector.open();
                    loopConnect();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    SystemClock.sleep(5000);
                    Log.d("seekting", "IPAddressConnector.prepareSelector()");
                    prepareSelector();
                }
            }
        });

    }


//

    public void startConnect(IPAddress ipAddress, ClientConnectListener clientConnectListener) {
        synchronized (this) {
            ConnectTask connectTask = new ConnectTask(clientConnectListener, ipAddress);
            mConnectTasks.add(connectTask);
        }
        mSelector.wakeup();
    }

    public void write(String str, SocketChannel socketChannel) {
        synchronized (this) {
            WriteTask connectTask = new WriteTask(str, socketChannel);
            mWriteTasks.add(connectTask);
        }
        mSelector.wakeup();
    }

    private void uiOnConnectFail(ClientConnectListener clientConnectListener) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                clientConnectListener.onConnectedFail();
            }
        });
        clientConnectListener.onConnectedFail();
    }

    private void uiOnConnectSuc(ClientConnectListener clientConnectListener, SocketChannel socketChannel) {
        SocketChannelWriteRead socketChannelWriteRead = new SocketChannelWriteRead(socketChannel);
        synchronized (IPAddressConnector.this) {
            mSCWMap.put(socketChannelWriteRead, clientConnectListener);
            mSCWRMap.put(socketChannel, socketChannelWriteRead);
        }
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                clientConnectListener.onConnected(socketChannelWriteRead);


            }
        });
    }

    private void loopConnect() {
        try {
            while (true) {
                int number = mSelector.select();
                Set selectedKeys = mSelector.selectedKeys();
                Log.d("seekting", "IPAddressConnector.loopConnect()" + number);
                Iterator keyIterator = selectedKeys.iterator();
                List<Runnable> list = null;
                List<Runnable> writeTasks = null;
                synchronized (this) {
                    if (!mConnectTasks.isEmpty()) {
                        list = new ArrayList<>();
                        list.addAll(mConnectTasks);
                    }
                    if (!mWriteTasks.isEmpty()) {
                        writeTasks = new ArrayList<>();
                        writeTasks.addAll(mWriteTasks);
                    }

                }
                if (list != null && !list.isEmpty()) {
                    for (Runnable connectTask : list) {
                        connectTask.run();

                    }

                }
                if (writeTasks != null && !writeTasks.isEmpty()) {
                    for (Runnable writeTask : writeTasks) {
                        writeTask.run();

                    }
                }
                synchronized (this) {
                    if (list != null) {
                        mConnectTasks.removeAll(list);
                    }
                    if (writeTasks != null) {
                        mWriteTasks.removeAll(writeTasks);
                    }
                }

                while (keyIterator.hasNext()) {
                    SelectionKey key = (SelectionKey) keyIterator.next();
                    SelectableChannel selectableChannel = key.channel();
                    if (key.isReadable()) {
                        SocketChannelWriteRead reader = null;
                        synchronized (IPAddressConnector.this) {
                            reader = mSCWRMap.get(selectableChannel);
                        }
                        String read = reader.read();
                        if (read == null) {
                            synchronized (IPAddressConnector.this) {
                                SocketChannelWriteRead socketChannelWriteRead = mSCWRMap.get(selectableChannel);
                                ClientConnectListener listener = mSCWMap.get(socketChannelWriteRead);
                                mSCWRMap.remove(selectableChannel);
                                mSCWMap.remove(socketChannelWriteRead);
                                selectableChannel.close();
                                if (listener != null) {
                                    uiHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onDisConnected();
                                        }
                                    });
                                }
                            }
                        } else {
                            ClientConnectListener clientConnectListener = null;
                            synchronized (IPAddressConnector.this) {
                                SocketChannelWriteRead socketChannelWriteRead = mSCWRMap.get(selectableChannel);
                                if (socketChannelWriteRead != null) {
                                    clientConnectListener = mSCWMap.get(socketChannelWriteRead);
                                }
                            }

                            if (clientConnectListener != null) {
                                final ClientConnectListener clientConnectListener1 = clientConnectListener;
                                uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        synchronized (IPAddressConnector.this) {
                                            clientConnectListener1.onRead(read);
                                        }
                                    }
                                });
                            }
                        }
                    }
                    keyIterator.remove();

                }
                synchronized (IPAddressConnector.this) {
                    Iterator<Map.Entry<SocketChannel, SocketChannelWriteRead>> i = mSCWRMap.entrySet().iterator();
                    while (i.hasNext()) {
                        Map.Entry<SocketChannel, SocketChannelWriteRead> enttry = i.next();
                        boolean isOpen = enttry.getKey().isOpen();
                        boolean isConnect = enttry.getKey().isConnected();
                        if (!isConnect) {
                            i.remove();
                            mSCWMap.remove(enttry.getValue());
                        }


                    }
                    Log.d("seekting", "IPAddressConnector.mSCWRMapSize()" + mSCWRMap.size());
                    Log.d("seekting", "IPAddressConnector.mSCWMapSize()" + mSCWMap.size());

                }
            }

        } catch (IOException e) {
            uiDisconnected();

        }
    }

    private void uiDisconnected() {
//        mSelector = null;
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (IPAddressConnector.this) {
                    Iterator<Map.Entry<SocketChannelWriteRead, ClientConnectListener>> iterator = mSCWMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<SocketChannelWriteRead, ClientConnectListener> entry = iterator.next();
                        entry.getKey().cancel();
                        entry.getValue().onDisConnected();

                    }
                    mSCWMap.clear();
                    mSCWRMap.clear();

                    prepareSelector();

                }

            }
        });
    }


}
