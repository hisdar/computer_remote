package cn.hisdar.computerremote.server;

import java.net.Socket;

public interface ClientEventListener {

	public void clientConnectEvent(CRServer crServer, Socket socket);
	public void clientDisconnectEvent(CRServer crServer, Socket Socket);
}
