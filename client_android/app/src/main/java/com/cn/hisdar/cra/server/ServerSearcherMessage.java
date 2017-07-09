package com.cn.hisdar.cra.server;


public class ServerSearcherMessage {

	public static final int MESSAGE_WIFI_NOT_CONNECTED = 0xE0001;
	public static final int SEARCH_FINISHED            = 0x00001;
	
	public int message;
	
	public ServerSearcherMessage(int message) {
		this.message = message;
	}
}
