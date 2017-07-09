package cn.hisdar.computerremote.server;

import java.net.Socket;

public interface ClientDisconnectListener {

	public void clientDisconnectEvent(Socket socket);
}
