package cn.hisdar.cr.communication.handler;

/**
 * Created by Hisdar on 2017/7/22.
 */

import java.net.Socket;

public abstract class AbstractHandler {

    abstract public int getDataType();
    abstract public boolean decode(byte[] data, Socket socket);
}
