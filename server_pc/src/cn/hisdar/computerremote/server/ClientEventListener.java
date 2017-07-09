package cn.hisdar.computerremote.server;

import java.net.Socket;

public interface ClientEventListener {

	public void clientDisconnectEvent(CRClient crClient, Socket socket);
}
