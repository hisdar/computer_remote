package com.cn.hisdar.cra.server;

public interface ServerSearcherEventListener {

	public void newServerFoundEvent(ServerInformation serverInfo);
	public void serverSercherMessageEvent(ServerSearcherMessage message);
}
