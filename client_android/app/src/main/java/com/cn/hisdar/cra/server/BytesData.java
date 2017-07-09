package com.cn.hisdar.cra.server;

/**
 * Created by Hisdar on 2017/3/12.
 */
public class BytesData {

    private int dataSize;
    private byte[] data;

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {

        return data;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSizeString) {

        try {
            dataSize = Integer.parseInt(dataSizeString);
        } catch (NumberFormatException e) {
            dataSize = 0;
        }
    }
}
