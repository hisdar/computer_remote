package cn.hisdar.cr.communication.handler;

/**
 * Created by Hisdar on 2017/7/22.
 */

public abstract class AbstractDataHandler {

    abstract public int getDataType();
    abstract public boolean decode(byte[] data);
}
