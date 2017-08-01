package cn.hisdar.cr.communication.client;

import java.net.Socket;

public interface ClientEventListener {

	public void clientConnectEvent(Socket clientSocket);
}
