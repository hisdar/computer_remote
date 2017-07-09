package cn.hisdar.computerremote.server;

import java.net.ServerSocket;

public interface ServerEventListener {

	public void serverEvent(ServerSocket serverSocket, int serverState);
}
