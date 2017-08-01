package cn.hisdar.cr.communication.handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.zip.CRC32;

import cn.hisdar.cr.communication.client.ClientEventListener;
import cn.hisdar.cr.communication.socket.SocketIOManager;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.net.HInetAddress;

public class CRServer {

	private static CRServer crServer = null;
	
	public static final int SERVER_STATE_STOP = 0;
	public static final int SERVER_STATE_START = 1;

	
	private ServerSocket serverSocket = null;
	
	private ArrayList<ClientEventListener> clientEventListeners = null;
	
	private int serverPort = 0;
	private boolean isServerStart;
	
	public static CRServer getInstance() {
		if (crServer == null) {
			synchronized (CRServer.class) {
				if (crServer == null) {
					crServer = new CRServer(5299);
				}				
			}
		}
		
		return crServer;
	}
	
	private CRServer(int port) {
		
		clientEventListeners = new ArrayList<>();
		
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

		private CRServer crServer = null;
		private ArrayList<Socket> clientSockets = null;
		
		public ServerRunnable(CRServer crServer) {
			this.crServer = crServer;
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
		}
	}
	
	public void addClientEventListener(ClientEventListener l) {
		for (int i = 0; i < clientEventListeners.size(); i++) {
			if (clientEventListeners.get(i) == l) {
				return;
			}
		}
		
		clientEventListeners.add(l);
	}
	
	public void removeClientEventListener(ClientEventListener l) {
		clientEventListeners.remove(l);
	}
	
	private void notifyClientEventListeners(Socket socket) {
		for (int i = 0; i < clientEventListeners.size(); i++) {
			clientEventListeners.get(i).clientConnectEvent(socket);
		}
	}
}
