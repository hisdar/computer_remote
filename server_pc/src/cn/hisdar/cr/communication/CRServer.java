package cn.hisdar.cr.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.net.HInetAddress;

public class CRServer {

	public static final int SERVER_STATE_STOP = 0;
	public static final int SERVER_STATE_START = 1;

	private ServerSocket serverSocket = null;
	
	private ServerEventListener serverEventListener = null;
	
	private int serverPort = 0;
	private boolean isServerStart;
	
	public CRServer(int port) {
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
	
	public void registerServerEventListener(ServerEventListener listener) {
		this.serverEventListener = listener;
	}
	
	public void removeServerEventListener() {
		this.serverEventListener = null;
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
			
			serverEventListener.serverStateEvent(crServer, SERVER_STATE_START);
			
			String[] hostAddresses = HInetAddress.getInetAddresses();
			for (int i = 0; i < hostAddresses.length; i++) {
				HLog.il("hostAddress - " + i + ":" + hostAddresses[i]);
			}
			
			while (isServerStart) {
				try {
					Socket clientSocket = serverSocket.accept();
					clientSockets.add(clientSocket);
					serverEventListener.clientConnectEvent(crServer, clientSocket);					
				} catch (IOException e) {
					HLog.el(e);
					break;
				}
			}

			serverEventListener.serverStateEvent(crServer, SERVER_STATE_STOP);
		}
	}
}
