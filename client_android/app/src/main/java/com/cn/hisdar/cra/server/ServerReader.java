package com.cn.hisdar.cra.server;

import android.util.Log;

import com.cn.hisdar.cra.EventDispatcher;
import com.cn.hisdar.cra.HEventData;
import com.cn.hisdar.cra.activity.CRAActivity;
import com.cn.hisdar.cra.common.Global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerReader extends Thread {

	private Socket serverSocket;
	private boolean isStop = false;
	
	public ServerReader(Socket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	public void startServerReader() {
		isStop = false;
		start();
	}
	
	public void stopServerReader() {
		isStop = true;
	}
	
	public void run() {
		if (serverSocket == null) {
			Log.e(CRAActivity.TAG, "Server socket is null");
			return;
		}

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			inputStream = serverSocket.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		} catch (IOException e) {
			Log.e(CRAActivity.TAG, "get server socket input stream fail");
			return;
		}

		StringBuffer serverData = new StringBuffer();
		String lineString = null;
		while (!isStop) {
			try {
				lineString = bufferedReader.readLine();
				//Log.i(CRAActivity.TAG, lineString);
			} catch (IOException e) {
				// connect close
				Log.e(CRAActivity.TAG, "read server data error");
				break;
			}
			
			if (lineString == null) {
				Log.e(CRAActivity.TAG, "client exit");
				break;
			}
			
			if (lineString.trim().equals(Global.DATA_BEGIN_FLAG.trim()) || lineString.trim().equals(Global.DATA_END_FLAG.trim())) {
				// submit command
				//HLog.il("\n" + clientData);
				
				if (serverData.toString().trim().length() <= 0) {
					continue;
				}
				
				EventDispatcher eventDispatcher = EventDispatcher.getInstance();
				HEventData eventData = new HEventData(serverData.toString(), this);
				eventData.serverSocket = serverSocket;
				eventData.bufferedReader = bufferedReader;
				eventDispatcher.dispatch(eventData);

				//
				serverData.delete(0, serverData.length());
			} else {
				serverData.append(lineString);
				serverData.append("\n");
			}
		}
		
		notifyServerExitEvent();
	}

	private void notifyServerExitEvent() {
		String serverExitData = String.format(Global.SERVER_EXIT_DATA_FORMAT, Global.SERVER_EVENT_EXIT);
		String dataType = String.format(Global.DATA_TYPE_FORMAT, Global.DATA_TYPE_SERVER_EVENT);
		String eventData = String.format(Global.CONTROL_DATA_LABEL_2, dataType, serverExitData);
		eventData = Global.XML_FILE_HEAD + eventData;
		
		EventDispatcher eventDispatcher = EventDispatcher.getInstance();
		HEventData hEventData = new HEventData(eventData.toString(), this);

		eventDispatcher.dispatch(hEventData);
	}
}
