package com.cn.hisdar.cra.server;

import java.net.Socket;

public interface ServerSearcheerEventListener {

	public void socketConnectedEvent(Socket socket);
	public void serverSercherStateEvent(ServerSearcherState message);
}
