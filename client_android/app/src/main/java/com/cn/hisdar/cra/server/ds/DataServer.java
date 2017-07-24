package com.cn.hisdar.cra.server.ds;

import android.util.Log;

import com.cn.hisdar.cra.activity.CRAActivity;
import com.cn.hisdar.cra.commnunication.AbstractDataType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Hisdar on 2017/7/10.
 */

public class DataServer {

    private static DataServer dataServer = null;
    private ArrayList<ListenerAndType> screenPictureListeners = null;
    private Socket dataSocket;
    private DataServerReader dataServerReader = null;

    private DataServer() {
        screenPictureListeners = new ArrayList<>();
    }

    public static DataServer getInstance() {
        if (dataServer == null) {
            synchronized (DataServer.class) {
                if (dataServer == null) {
                    dataServer = new DataServer();
                }
            }
        }

        return dataServer;
    }

    public void reinitDataServer(Socket dataSocket) {
        dataServerReader.stopServerReader();
        dataServerReader = null;
        initDataServer(dataSocket);
    }

    public void initDataServer(Socket dataSocket) {
        this.dataSocket = dataSocket;

        if (dataServerReader != null && dataServerReader.isAlive()) {
            return;
        }

        dataServerReader = new DataServerReader();
        dataServerReader.startServerReader();
    }

    public void stopDataServer() {
        dataServerReader.stopServerReader();
        dataServerReader = null;
    }

    private void dispatch(byte[] data, int dataType) {
        for (ListenerAndType l : screenPictureListeners) {
            if (l.dataType == dataType) {
                l.listener.screenPictureEvent(data);
            }
        }
    }

    public void addCommunicationEventListener(CommunicationEventListener l, int dataType) {
        ListenerAndType listenerAndType = new ListenerAndType(l, dataType);
        for (ListenerAndType listener : screenPictureListeners) {
            if (listener.equal(listenerAndType)) {
                return;
            }
        }

        screenPictureListeners.add(listenerAndType);
    }

    public void removeCommunicationEventListener(CommunicationEventListener l) {
        screenPictureListeners.remove(l);
    }

    public boolean sendData(AbstractDataType data) {

        if (dataSocket == null) {
            return false;
        }

        try {
            OutputStream out = dataSocket.getOutputStream();

            // write send time
            out.write(data.longToBytes(System.currentTimeMillis()));
            out.flush();

            // write data type
            out.write(data.intToBytes(data.getDataType()));

            // write data length to client, the length is 4, sizeof(int)
            byte[] bytesData = data.encode();

            byte[] dataLength = data.intToBytes(bytesData.length);
            out.write(dataLength);
            out.flush();

            // 2.write data to client
            out.write(bytesData);
            out.flush();
        } catch (IOException e) {
            //Log.e("client ip address:\n" + dataSocket.getInetAddress().getHostAddress());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private class ListenerAndType {
        public CommunicationEventListener listener;
        public int dataType;

        public ListenerAndType(CommunicationEventListener l, int dataType) {
            listener = l;
            this.dataType = dataType;
        }

        public boolean equal(ListenerAndType lt) {
            if (listener == lt.listener
                    && dataType == lt.dataType) {
                return true;
            }

            return false;
        }
    }

    private class DataServerReader extends Thread {

        private boolean isStop = false;

        public void startServerReader() {
            isStop = false;
            start();
        }

        public void stopServerReader() {
            isStop = true;
        }

        private long bytesToLong(byte[] bytesData) {
            // the length of long is 8bytes
            long number = 0;
            for (int i = 0; i < bytesData.length; i++) {
                number |= (bytesData[i] << (i * 8));
            }

            return number;
        }

        private int bytesToInt(byte[] bytesData) {
            // the length of long is 8bytes
            int number = 0;
            for (int i = 0; i < bytesData.length; i++) {
                number |= ((bytesData[i] & 0xff) << (i * 8));
            }

            return number;
        }

        private byte[] readData(InputStream inputStream, int dataLen) throws IOException {

            int offset = 0;
            byte[] data = new byte[dataLen];

            while (dataLen > 0) {
                int readLen = inputStream.read(data, offset, dataLen);
                offset += readLen;
                dataLen -= readLen;
            }

            return data;
        }

        public void run() {

            if (dataSocket == null) {
                Log.e(CRAActivity.TAG, "Server socket is null");
                return;
            }

            InputStream inputStream = null;
            try {
                inputStream = dataSocket.getInputStream();
            } catch (IOException e) {
                Log.e(CRAActivity.TAG, "get server socket input stream fail");
                return;
            }

            // read the data length
            while (!isStop) {

                Log.i(CRAActivity.TAG, "[DataServer] is running");
                try {

                    // read send time
                    byte[] dataTimeByte = readData(inputStream, 8);
                    long dataTime = bytesToLong(dataTimeByte);

                    // read data type
                    byte[] dataTypeByte = readData(inputStream, 4);
                    int dataType = bytesToInt(dataTypeByte);
                    Log.i(CRAActivity.TAG, "[DataServer]dataType=" + dataType);

                    // read data length
                    byte[] dataLenByte = readData(inputStream, 4);
                    int dataLen = bytesToInt(dataLenByte);
                    Log.i(CRAActivity.TAG, "[DataServer]dataLen=" + dataLen);

                    // read data
                    byte[] dataBuf = readData(inputStream, dataLen);

                    // notify data
                    dispatch(dataBuf, dataType);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
