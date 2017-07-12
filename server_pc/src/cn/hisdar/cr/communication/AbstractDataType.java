package cn.hisdar.cr.communication;

/**
 * Created by Hisdar on 2017/7/22.
 */

public abstract class AbstractDataType {

    public static final int DATA_TYPE_SCREEN_PICTURE = 0x10001;

    abstract public int getDataType();
    abstract public byte[] encode();
    abstract public boolean decode(byte[] data);
}
