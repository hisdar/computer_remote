package cn.hisdar.computerremote.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

import cn.hisdar.computerremote.common.Global;
import cn.hisdar.computerremote.debug.DebugerTimeDataContainer;
import cn.hisdar.computerremote.event.EventDispatcher;
import cn.hisdar.computerremote.event.HEventData;
import cn.hisdar.lib.log.HLog;

/**
 * @description dataSocket used to send and receive byte data
 *              cmdSocket used to send and receive cmd(String) data
 * @author Hisdar
 *
 */

public class CRClient implements Runnable, ScreenHunterListener {

	private ArrayList<ClientDisconnectListener> clientDisconnectListeners;
	private ScreenHunterServer screenHunterServer = null;
	private Socket cmdSocket;
	private Socket dataSocket;
	
	private boolean isStopListen = false;
	private static boolean isPrint = true;
	
	public void stopListen() {
		
	}
	
	public CRClient() {
		clientDisconnectListeners = new ArrayList<>();
		screenHunterServer = ScreenHunterServer.getInstance();
		screenHunterServer.addScreenHunterListener(this);
	}

	public CRClient(Socket cmdsocket) {
		clientDisconnectListeners = new ArrayList<>();
		this.cmdSocket = cmdsocket;

		screenHunterServer = ScreenHunterServer.getInstance();
		screenHunterServer.addScreenHunterListener(this);
	}
	
	@Override
	public void run() {
		if (cmdSocket == null) {
			return;
		}
		
		InputStream clientInputStream = null;
		try {
			clientInputStream = cmdSocket.getInputStream();
		} catch (IOException e) {
			HLog.el(e);
			return;
		}
		
		try {
			sendServerInfor(cmdSocket.getOutputStream());
		} catch (IOException e1) {
			HLog.el(e1);
		}
		
		InputStreamReader clientInputStreamReader = new InputStreamReader(clientInputStream);
		BufferedReader clientReader = new BufferedReader(clientInputStreamReader);
		
		StringBuffer clientData = new StringBuffer();
		String lineString = null;
		while (!isStopListen) {
			try {
				lineString = clientReader.readLine();
				//System.out.println(lineString);
			} catch (IOException e) {
				HLog.el(e);
				break;
			}
			
			if (lineString == null) {
				HLog.el("client exit");
				break;
			}
			
			if (lineString.trim().equals(Global.DATA_BEGIN_FLAG.trim()) || lineString.trim().equals(Global.DATA_END_FLAG.trim())) {
				// submit command
				//HLog.il("\n" + clientData);
				
				if (clientData.toString().trim().length() <= 0) {
					continue;
				}
				
				// 
				DebugerTimeDataContainer timeDataContainer = new DebugerTimeDataContainer();
				timeDataContainer.addTimeData("start dispatch packet", new Date().getTime());
				
				EventDispatcher eventDispatcher = EventDispatcher.getInstance();
				HEventData eventData = new HEventData(clientData.toString(), this, timeDataContainer);
				eventDispatcher.dispatch(eventData);

				//
				clientData.delete(0, clientData.length());
			} else {
				clientData.append(lineString);
				clientData.append("\n");
			}
		}
		
		try {
			cmdSocket.close();
		} catch (IOException e) {
			HLog.el(e);
		}
		
		notifyClientDisconnectEvent(cmdSocket);
		
		cmdSocket = null;
	}
	
	public void addClientDisconnectListener(ClientDisconnectListener listener) {
		for (int i = 0; i < clientDisconnectListeners.size(); i++) {
			if (clientDisconnectListeners.get(i) == listener) {
				return;
			}
		}
		
		clientDisconnectListeners.add(listener);
	}
	
	public void removeClientDiscnectListener(ClientDisconnectListener listener) {
		for (int i = 0; i < clientDisconnectListeners.size(); i++) {
			if (clientDisconnectListeners.get(i) == listener) {
				clientDisconnectListeners.remove(i);
				return;
			}
		}
	}
	
	private void notifyClientDisconnectEvent(Socket socket) {
		for (int i = 0; i < clientDisconnectListeners.size(); i++) {
			clientDisconnectListeners.get(i).clientDisconnectEvent(socket);
		}
	}
	
	private void sendServerInfor(OutputStream outputStream) {
		
		String serverName = null;
		try {
			serverName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			HLog.el(e);
			return;
		}
		
		String serverInfor = Global.DATA_BEGIN_FLAG + "\n"
				+ "<server-information>\n"
				+ "<" + Global.SERVER_INFO_SERVER_NAME + ">" + serverName + "</" + Global.SERVER_INFO_SERVER_NAME + ">\n"
				+ "</server-information>\n"
				+ Global.DATA_END_FLAG + "\n";
		
		try {
			outputStream.write(serverInfor.getBytes());
			outputStream.flush();
		} catch (IOException e) {
			HLog.el(e);
			return;
		}
	}
	
	public boolean sendResponseData(String data) {
		String dataType = String.format(Global.DATA_TYPE_FORMAT, Global.DATA_TYPE_RESPONSE_DATA);
		return sendDataToClient(dataType + data);
	}
	
	public boolean sendDataToClient(String data) {
		
		String serverData = String.format(Global.CONTROL_DATA_LABEL_1, data);
		serverData = Global.XML_FILE_HEAD + serverData;
		serverData = Global.DATA_BEGIN_FLAG + "\n" + serverData + Global.DATA_END_FLAG + "\n";
		
		//HLog.il(serverData);
		try {
			OutputStream inputStream = cmdSocket.getOutputStream();
			inputStream.write(serverData.getBytes());
			inputStream.flush();
		} catch (IOException e) {
			HLog.el(e);
			return false;
		}
		
		return true;
	}
	
	public boolean sendByteDataToClient(byte[] data) {
		
		HLog.il("Send screen picture data to client");
		if (cmdSocket == null) {
			return true;
		}
		
		String serverData = String.format(Global.BYTE_DATA_HEAD_FORMAT, data.length); // 基本数据封装
		String dataType = String.format(Global.DATA_TYPE_FORMAT, Global.DATA_TYPE_SCREEN_PICTURE);
		serverData = String.format(Global.CONTROL_DATA_LABEL_2, dataType, serverData);          // computerRemote头封装
		serverData = Global.XML_FILE_HEAD + serverData;
		serverData = Global.DATA_BEGIN_FLAG + "\n" + serverData + Global.DATA_END_FLAG + "\n";
		
		HLog.il(serverData);
		try {
			OutputStream inputStream = cmdSocket.getOutputStream();
			inputStream.write(serverData.getBytes());
			inputStream.flush();
			
			FileOutputStream fileOutputStream = new FileOutputStream(new File("D:/test.bmp"));
			fileOutputStream.write(data);
			fileOutputStream.flush();
			fileOutputStream.close();
			
			printByteData(data);
			inputStream.write(data);
			inputStream.flush();
		} catch (IOException e) {
			HLog.el(e);
			return false;
		}
		
		return true;
	}
	
	public void printByteData(byte[] data) {
		if (!isPrint) {
			return;
		}
		
		isPrint = false;
		for (int i = 0; i < data.length / 8 && i < 50; i++) {
			for (int j = 0; j < 8; j++) {
				int index = 8 * i + j;
				System.out.printf("0x%02x ", data[index]);
			}
			
			System.out.println();
		}
	}

	public Socket getCmdSocket() {
		return cmdSocket;
	}

	public void setCmdSocket(Socket clientSocket) {
		this.cmdSocket = clientSocket;
	}

	public Socket getDataSocket() {
		return dataSocket;
	}

	public void setDataSocket(Socket dataSocket) {
		this.dataSocket = dataSocket;
	}

	@Override
	public void screenPictureChangeEvent(ScreenHunterData screenHunterData) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(screenHunterData.getScreenImage(), "png", byteArrayOutputStream);
		} catch (IOException e) {
			HLog.el(e.getMessage());
			HLog.el(e);
			return;
		}
		
		sendByteDataToClient(byteArrayOutputStream.toByteArray());
	}
}
