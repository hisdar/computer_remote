package com.cn.hisdar.cra;

import com.cn.hisdar.cra.server.ServerReader;

import java.io.BufferedReader;
import java.net.Socket;

public class HEventData {

	public String eventData;
	public Socket serverSocket;
	public BufferedReader bufferedReader;
	
	public HEventData(String eventData, ServerReader serverReader) {
		this.eventData = eventData;
	}

}
