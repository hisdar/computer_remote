package com.cn.hisdar.cra.server;

import android.util.Log;

import com.cn.hisdar.cra.EventDispatcher;
import com.cn.hisdar.cra.HEventData;
import com.cn.hisdar.cra.activity.CRAActivity;
import com.cn.hisdar.cra.common.Global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Hisdar on 2017/7/10.
 */

public class DataServerReader extends Thread {

    private Socket dataSocket;
    private boolean isStop = false;
    public DataServerReader(Socket dataSocket) {
        this.dataSocket = dataSocket;
    }

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
            number |= (bytesData[i] << (i * 8));
        }

        return number;
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

        int readLen = 0;
        int readedLen = 0;
        int dataLen = 0;
        int dataHeadLen = 4;
        byte[] dataLenBuf = new byte[dataHeadLen];
        readLen = dataHeadLen;
        while (!isStop) {

            // read the data length
            try {
                readedLen = inputStream.read(dataLenBuf, dataHeadLen - readLen, readLen);
                if (readedLen != readLen) {
                    readLen = readLen - readedLen;
                    continue;
                }
                // reset parameters
                readLen = dataHeadLen;

                dataLen = bytesToInt(dataLenBuf);
                Log.i(CRAActivity.TAG, "dataLen=" + dataLen);

                // read data
                byte[] dataBuf = new byte[dataLen];
                readedLen = 0;
                while (dataLen > 0) {
                    readedLen = inputStream.read(dataBuf, readedLen, dataLen);
                    dataLen -= readedLen;
                    readedLen = 0;
                }

                // notify data
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
