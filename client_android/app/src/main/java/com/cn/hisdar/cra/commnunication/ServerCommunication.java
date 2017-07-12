package com.cn.hisdar.cra.commnunication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.cn.hisdar.cra.MotionEventTool;
import com.cn.hisdar.cra.activity.CRAActivity;
import com.cn.hisdar.cra.common.Global;
import com.cn.hisdar.cra.server.CmdServerReader;
import com.cn.hisdar.cra.server.ds.DataServer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

@SuppressLint("HandlerLeak")
public class ServerCommunication extends Thread {

	private static final int CONNECT_TO_SERVER = 1;
	private static final int SEND_EVENT_DATA = 2;
	private static final int DISCONNECT = 3;
	
	private static final String IP_ADDRESS = "IP_ADDRESS";
	private static final String CMD_SERVER_PORT = "CMD_SERVER_PORT";
	private static final String DATA_SERVER_PORT = "DATA_SERVER_PORT";
	private static final String EVENT_DATA = "MOTION_EVENT";

	private static ServerCommunication serverCommunication = null;
	
	private Message messageToHandle = null;
	private Thread messageOwner = null;
	
	private Socket cmdSocket = null;
	private Socket dataSocket = null;
	private String currentIpAddress = null;
	
	private CmdServerReader cmdServerReader = null;
	private DataServer dataServer = null;
	
	private ServerCommunication() {		
		start();
	}
	
	public static ServerCommunication getInstance() {
		if (serverCommunication ==null) {
			synchronized (ServerCommunication.class) {
				if (serverCommunication ==null) {
					serverCommunication = new ServerCommunication();
				}
			}
		}
		
		return serverCommunication;
	}
	
	public void run() {
		while (true) {
			try {
				sleep(1000);
			} catch (InterruptedException e) {
			}
			
			//Log.i(ComputerRemoteServerActivity.TAG, "communicatuin thread");
			
			if (messageToHandle != null) {
				//serverCommunicationEventHnadler.sendMessage(messageToHandle);
				
				switch (messageToHandle.what) {
				case CONNECT_TO_SERVER:
					connectToServerEventHandler(messageToHandle);
					break;
				case DISCONNECT:
					disconnectEventHandler(messageToHandle);
					break;
				case SEND_EVENT_DATA:
					sendCmd(messageToHandle);
					break;
				default:
					break;
				}
				
				messageToHandle = null;
				messageOwner.interrupt();
			} 
		}
	}
	
	private boolean sendCmd(Message message) {
		
		String data = message.getData().getString(EVENT_DATA);
		
		data = Global.DATA_BEGIN_FLAG + "\n" + data + "\n" + Global.DATA_END_FLAG + "\n";
		
		//Log.i(ComputerRemoteServerActivity.TAG, data);
		if (cmdSocket == null) {
			Log.e(CRAActivity.TAG, "communicationSocket is null");
			return false;
		}
		
		try {
			cmdSocket.getOutputStream().write(data.getBytes());
			cmdSocket.getOutputStream().flush();
		} catch (IOException e) {
			Log.e(CRAActivity.TAG, "socket send data fail:" + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean connectToCmdServer(Thread owner, String ipAddress, int cmd_server_port, int data_server_port) {
	
		Log.i(CRAActivity.TAG, "connect to server");
		Message connectToServerMessage = new Message();
		Bundle data = new Bundle();
		data.putString(IP_ADDRESS, ipAddress);
		data.putInt(CMD_SERVER_PORT, cmd_server_port);
		data.putInt(DATA_SERVER_PORT, data_server_port);
		connectToServerMessage.what = CONNECT_TO_SERVER;
		connectToServerMessage.setData(data);
		
		return execCommandAndWaitSync(owner, connectToServerMessage);
	}
	
	public boolean disconnect(Thread owner, String ipAddress, int port) {
		
		Log.i(CRAActivity.TAG, "connect to server");
		Message connectToServerMessage = new Message();
		Bundle data = new Bundle();
		data.putString(IP_ADDRESS, ipAddress);
		data.putInt(CMD_SERVER_PORT, port);
		connectToServerMessage.what = DISCONNECT;
		connectToServerMessage.setData(data);
		
		return execCommandAndWaitSync(owner, connectToServerMessage);
	}
	
	private Message packageEventDataMessage(String srcData) {
		Message eventMessage = new Message();
		Bundle data = new Bundle();
		data.putString(EVENT_DATA, srcData);
		eventMessage.what = SEND_EVENT_DATA;
		eventMessage.setData(data);
		
		return eventMessage;
	}
	
	public boolean sendTouchEvent(Thread owner, MotionEvent event) {
		
		//Log.d(CRAActivity.TAG, event.toString());
		
		String eventXmlData = MotionEventTool.toXmlString(event);
		Message touchEventMessage = packageEventDataMessage(eventXmlData);
		
		return execCommandAndWaitSync(owner, touchEventMessage);
	}
	
	public boolean sendMouseButtonEvent(Thread owner, int buttonId, int value) {
		
		String eventXmlData = MotionEventTool.toMouseButtonActionXmlString(buttonId, value);
		Message buttonEventMessage = packageEventDataMessage(eventXmlData);
		
		return execCommandAndWaitSync(owner, buttonEventMessage);
	}
	
	public boolean sendKeyButtonEvent(Thread owner, int buttonId, int value) {
		
		String eventXmlData = MotionEventTool.toKeyButtonActionXmlString(buttonId, value);
		//Log.i(CRAActivity.TAG, eventXmlData);
		Message buttonEventMessage = packageEventDataMessage(eventXmlData);
		
		return execCommandAndWaitSync(owner, buttonEventMessage);
	}
	
	public boolean sendScreenSize(Thread owner, int width, int height) {
		String screenSizeData = MotionEventTool.toScreenSizeXmlString(width, height);
		Message screenSizeMessage = packageEventDataMessage(screenSizeData);
		
		return execCommandAndWaitSync(owner, screenSizeMessage);
	}
	
	/**
	 * @description exec command and wait for command exec finish, this function is synchronized
	 * @param owner the thread to call this function
	 * @param message command to exec
	 * @return if success, return true, otherwise return false
	 */
	private boolean execCommandAndWaitSync(Thread owner, Message message) {
		synchronized (ServerCommunication.class) {
			return execCommandAndWait(owner, message);
		}
	}
	
	/**
	 * @description exec command and wait for command exec finish, this function is not synchronized
	 * @param owner the thread to call this function
	 * @param message command to exec
	 * @return if success, return true, otherwise return false
	 */
	private boolean execCommandAndWait(Thread owner, Message message) {
		messageToHandle = message;
		messageOwner = owner;
		
		interrupt();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * @description connect to server
	 * @return if connect success, return true, otherwise return false
	 */
	private boolean connectToServerEventHandler(Message messsage) {
		
		String ipAddress = messageToHandle.getData().getString(IP_ADDRESS);
		int cmdPort = messageToHandle.getData().getInt(CMD_SERVER_PORT);
		int dataPort = messageToHandle.getData().getInt(DATA_SERVER_PORT);
		
		// if socket is connect, check is this is the target connection
		if (cmdSocket != null && cmdSocket.isConnected()) {
			
			if (currentIpAddress.equals(ipAddress)) {
				Log.e(CRAActivity.TAG, "connection already on");
				return true;
			} else {
				try {
					cmdSocket.close();
				} catch (IOException e) {
					Log.e(CRAActivity.TAG, "close connection fail:" + e.getMessage());
				}

				cmdSocket = null;
			}
		}
		
		// if not connect, connect to server
		try {
			cmdSocket = new Socket(ipAddress, cmdPort);
			dataSocket = new Socket(ipAddress, dataPort);
			currentIpAddress = cmdSocket.getInetAddress().getHostAddress();
			Log.e(CRAActivity.TAG, "connect to server success:");
			
			// start thread to read server message
			cmdServerReader = new CmdServerReader(cmdSocket);
			cmdServerReader.startServerReader();

			dataServer = DataServer.getInstance();
			dataServer.initDataServer(dataSocket);
			
		} catch (UnknownHostException e) {
			Log.e(CRAActivity.TAG, "connect to server fail:" + e.getMessage().toString());
			return false;
		} catch (IOException e) {
			Log.e(CRAActivity.TAG, "connect to server fail:" + e.getMessage().toString());
			return false;
		}
		
		return true;
	}
	
	private void disconnectEventHandler(Message messageToHandle2) {
		String ipAddress = messageToHandle.getData().getString(IP_ADDRESS);
		int port = messageToHandle.getData().getInt(CMD_SERVER_PORT);
		
		// if socket is connect, check is this is the target connection
		if (cmdSocket != null && cmdSocket.isConnected()) {
			
			if (currentIpAddress.equals(ipAddress)) {
				try {
					cmdServerReader.stopServerReader();
					cmdSocket.close();
				} catch (IOException e) {
					Log.e(CRAActivity.TAG, "close connection fail:" + e.getMessage());
				}

				cmdSocket = null;
			}
		}
	}
}
