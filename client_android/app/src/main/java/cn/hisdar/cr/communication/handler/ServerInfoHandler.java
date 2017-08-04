package cn.hisdar.cr.communication.handler;

import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.data.ServerInfoData;
import cn.hisdar.cr.communication.socket.SocketIOManager;

/**
 * Created by Hisdar on 2017/8/1.
 */

public class ServerInfoHandler extends AbstractHandler {

    private static int serverId = 0x1001;
    private static ServerInfoHandler serverInfoHandler = null;
    private ArrayList<ServerInfoListener> serverInfoListeners = null;
    private ServerInfoData serverInfoData = null;

    private ServerInfoHandler() {
        serverInfoListeners = new ArrayList<>();
        SocketIOManager.getInstance().addDataHandler(this);
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

    public void addServerInfoListener(ServerInfoListener l) {
        for (int i = 0; i < serverInfoListeners.size(); i++) {
            if (serverInfoListeners.get(i) == l) {
                return;
            }
        }

        serverInfoListeners.add(l);
    }

    public void removeServerInfoListener(ServerInfoListener l) {
        serverInfoListeners.remove(l);
    }

    @Override
    public int getDataType() {
        return AbstractData.DATA_TYPE_SERVER_INFO;
    }

    @Override
    public boolean decode(byte[] data, Socket socket) {
        if (serverInfoData == null) {
            serverInfoData = new ServerInfoData();
        }

        boolean bRet = serverInfoData.decode(data);
        if (!bRet) {
            return bRet;
        }

        serverInfoData.setIpAddress(socket.getInetAddress().getHostAddress());
        serverInfoData.setPort(String.format("%d", 5299));
        serverInfoData.setId(allocServerId());

        // notify server info event to listeners
        for (int i = 0; i < serverInfoListeners.size(); i++) {
            serverInfoListeners.get(i).serverInfoEvent(serverInfoData);
        }

        return true;
    }

    private int allocServerId() {
        return serverId++;
    }
}
