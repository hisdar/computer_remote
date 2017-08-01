package cn.hisdar.cr.communication.handler;

/**
 * Created by Hisdar on 2017/7/22.
 */

public abstract class AbstractDataHandler {

    abstract public int getDataType();
    abstract public boolean decode(byte[] data);

    public static byte[] longToBytes(long data) {
        // the length of long is 8bytes
        byte[] bytesData = new byte[8];
        for (int i = 0; i < bytesData.length; i++) {
            bytesData[i] = (byte)(0xFF & (data >> (i * 8)));
        }

        return bytesData;
    }

    public static long bytesToLong(byte[] bytesData) {
        // the length of long is 8bytes
        long number = 0;
        for (int i = 0; i < bytesData.length; i++) {
            number |= ((bytesData[i] & 0xFF) << (i * 8));
        }

        return number;
    }

    public static byte[] intToBytes(int data) {
        // the length of long is 8bytes
        byte[] bytesData = new byte[4];
        for (int i = 0; i < bytesData.length; i++) {
            bytesData[i] = (byte)(0xFF & (data >> (i * 8)));
        }

        return bytesData;
    }

    public static int bytesToInt(byte[] bytesData) {
        // the length of long is 8bytes
        int number = 0;
        for (int i = 0; i < bytesData.length; i++) {
            number |= ((bytesData[i] & 0xFF) << (i * 8));
        }

        return number;
    }
}
