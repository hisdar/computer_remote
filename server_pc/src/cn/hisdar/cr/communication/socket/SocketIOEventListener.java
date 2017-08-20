package cn.hisdar.cr.communication.socket;

import java.net.Socket;

public interface SocketIOEventListener {

	public void socketIOEvent(SocketIOData data, Socket socket);
	public void socketDisconnectEvent(Socket socket);
}
