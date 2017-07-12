package cn.hisdar.cr.screen;

import java.net.ServerSocket;
import java.net.Socket;

import cn.hisdar.cr.communication.CRServer;

public interface ServerEventListener {

	public void clientConnectEvent(CRServer crServer, Socket socket);
	public void serverStateEvent(CRServer crServer, int serverState);
}
