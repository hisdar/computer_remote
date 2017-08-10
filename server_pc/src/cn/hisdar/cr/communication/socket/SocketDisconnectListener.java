package cn.hisdar.cr.communication.socket;

import java.net.Socket;

public interface SocketDisconnectListener {

	public void socketDisconnectEvent(Socket socket);
}
