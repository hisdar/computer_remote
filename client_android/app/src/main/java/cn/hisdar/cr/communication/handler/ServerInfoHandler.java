package cn.hisdar.cr.communication.handler;

import cn.hisdar.cr.communication.data.AbstractData;

/**
 * Created by Hisdar on 2017/8/1.
 */

public class ServerInfoHandler extends AbstractDataHandler {

    private static ServerInfoHandler serverInfoHandler = null;

    private ServerInfoHandler() {

    }

    public static ServerInfoHandler getInstance() {
        if (serverInfoHandler == null) {
            synchronized (ServerInfoHandler.class) {
                if (serverInfoHandler == null) {
                    serverInfoHandler = new ServerInfoHandler();
                }
            }
        }

        return serverInfoHandler;
    }

    @Override
    public int getDataType() {
        return AbstractData.DATA_TYPE_SERVER_INFO;
    }

    @Override
    public boolean decode(byte[] data) {
        return false;
    }
}
