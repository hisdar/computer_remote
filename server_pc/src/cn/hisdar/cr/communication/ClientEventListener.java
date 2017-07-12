package cn.hisdar.cr.communication;

import java.net.Socket;

public interface ClientEventListener {

	public void clientDisconnectEvent(CRClient crClient, Socket socket);
}
