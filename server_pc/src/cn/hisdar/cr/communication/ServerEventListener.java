package cn.hisdar.cr.communication;

import java.net.ServerSocket;
import java.net.Socket;

public interface ServerEventListener {

	public void clientConnectEvent(CRServer crServer, Socket socket);
	public void serverStateEvent(CRServer crServer, int serverState);
}
