package cn.hisdar.cr.communication.socket;

import java.net.Socket;

public interface SocketIOEventListener {

	public void socketIOEvent(byte[] data, int dataType, Socket socket);
}
