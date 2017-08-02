package cn.hisdar.cr;

import java.net.Socket;

public interface SocketAccepterListener {

	public void socketAccepterEvent(int state);
	public void clientConnectEvent(Socket clientSocket);
}
