package cn.hisdar.cr;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.cr.communication.socket.SocketIOManager;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.net.HInetAddress;

public class SocketAccepter {

	private static SocketAccepter socketAccepter = null;
	
	public static final int SERVER_STATE_STOP = 0;
	public static final int SERVER_STATE_START = 1;

	
	private ServerSocket serverSocket = null;
	
	private ArrayList<SocketAccepterListener> socketAccepterListeners = null;
	
	private int serverPort = 0;
	private boolean isServerStart;
	
	public static SocketAccepter getInstance() {
		if (socketAccepter == null) {
			synchronized (SocketAccepter.class) {
				if (socketAccepter == null) {
					socketAccepter = new SocketAccepter(5299);
				}				
			}
		}
		
		return socketAccepter;
	}
	
	private SocketAccepter(int port) {
		
		socketAccepterListeners = new ArrayList<>();
		
		serverPort = port;
		isServerStart = false;
	}
	
	public void startServer() {
		if (isServerStart) {
			return;
		}
		
		HLog.il("Start computer remote server");
		Thread serverThread = new Thread(new ServerRunnable(this));
		serverThread.start();
		
		isServerStart = true;
	}
	
	public void stopServer() {
		if (!isServerStart) {
			return;
		}
		
		try {
			serverSocket.close();
		} catch (IOException e) {
			HLog.el(e);
		}
		
		HLog.il("Stop computer remote server");

		serverSocket = null;
		isServerStart = false;
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	private class ServerRunnable implements Runnable {

		private ArrayList<Socket> clientSockets = null;
		
		public ServerRunnable(SocketAccepter crServer) {
			clientSockets = new ArrayList<>();
		}
		
		@Override
		public void run() {
			
			try {
				serverSocket = new ServerSocket(serverPort);
			} catch (IOException e1) {
				HLog.el(e1);
				HLog.el("Start server socket fail, port=" + serverPort);
				return;
			}

			String[] hostAddresses = HInetAddress.getInetAddresses();
			for (int i = 0; i < hostAddresses.length; i++) {
				HLog.il("hostAddress - " + i + ":" + hostAddresses[i]);
			}
			
			// notify server start
			notifySocketAccepterEvent(SERVER_STATE_START);
			
			while (isServerStart) {
				try {
					Socket clientSocket = serverSocket.accept();
					clientSockets.add(clientSocket);
					SocketIOManager.getInstance().addSocket(clientSocket);
					
					// client connect, notify listeners
					notifyClientEventListeners(clientSocket);
				} catch (IOException e) {
					HLog.el(e);
					break;
				}
			}
			
			// notify socket accepter
			notifySocketAccepterEvent(SERVER_STATE_STOP);
		}
	}
	
	public void addSocketAccepterListener(SocketAccepterListener l) {
		for (int i = 0; i < socketAccepterListeners.size(); i++) {
			if (socketAccepterListeners.get(i) == l) {
				return;
			}
		}
		
		socketAccepterListeners.add(l);
	}
	
	public void removeSocketAccepterListener(SocketAccepterListener l) {
		socketAccepterListeners.remove(l);
	}
	
	private void notifySocketAccepterEvent(int state) {
		for (int i = 0; i < socketAccepterListeners.size(); i++) {
			socketAccepterListeners.get(i).socketAccepterEvent(state);
		}
	}
	
	private void notifyClientEventListeners(Socket socket) {
		for (int i = 0; i < socketAccepterListeners.size(); i++) {
			socketAccepterListeners.get(i).clientConnectEvent(socket);
		}
	}
}

