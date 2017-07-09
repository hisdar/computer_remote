package cn.hisdar.computerremote.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.net.HInetAddress;

public class CRServer implements ClientDisconnectListener {

	public static final int SERVER_STATE_STOP = 0;
	public static final int SERVER_STATE_START = 1;
	
	private static ArrayList<ClientEventListener> clientEventListeners = null;
	private static ArrayList<ServerEventListener> serverEventListeners = null;

	private static ServerSocket serverSocket = null;
	private static int serverState = SERVER_STATE_STOP;
	
	private int serverPort = 0;
	private boolean isServerStart;
	
	public CRServer(int port) {
		serverPort = port;
		isServerStart = false;
		clientEventListeners = new ArrayList<>();
		serverEventListeners = new ArrayList<>();
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
	

	
	private void sendMessageToClient(Socket socket, String message) {
		try {
			socket.getOutputStream().write(message.getBytes());
		} catch (IOException e) {
			HLog.el("Send message to client fail, message:\n" + message);
			HLog.el("client ip address:\n" + socket.getInetAddress().getHostAddress());
			HLog.el(e);
		}
	}
	
	public void addClientEventListener(ClientEventListener listener) {
		for (int i = 0; i < clientEventListeners.size(); i++) {
			if (clientEventListeners.get(i) == listener) {
				return;
			}
		}
		
		clientEventListeners.add(listener);
	}
	
	public void removeClientEventListener(ClientEventListener listener) {
		for (int i = 0; i < clientEventListeners.size(); i++) {
			if (clientEventListeners.get(i) == listener) {
				clientEventListeners.remove(i);
				return;
			}
		}
	}
	
	public void notifyClientConnectEvent(Socket crc) {
		for (int i = 0; i < clientEventListeners.size(); i++) {
			clientEventListeners.get(i).clientConnectEvent(this, crc);
		}
	}
	
	public void notifyClientDisconnectEvent(Socket crc) {
		for (int i = 0; i < clientEventListeners.size(); i++) {
			clientEventListeners.get(i).clientDisconnectEvent(this, crc);
		}
	}

	public void addServerEventListener(ServerEventListener listener) {
		for (int i = 0; i < serverEventListeners.size(); i++) {
			if (serverEventListeners.get(i) == listener) {
				return;
			}
		}
		
		serverEventListeners.add(listener);
		new ServerEeventNotifyThread(listener).start();
	}
	
	public void removeServerEventListener(ServerEventListener listener) {
		for (int i = 0; i < serverEventListeners.size(); i++) {
			if (serverEventListeners.get(i) == listener) {
				serverEventListeners.remove(i);
				return;
			}
		}
	}
	
	public void notifyServerEvent(ServerSocket serverSocket, int serverState) {
		new ServerEeventNotifyThread(serverEventListeners).start();
	}
	
	@Override
	public void clientDisconnectEvent(Socket socket) {
		notifyClientDisconnectEvent(socket);
	}
	
	private class ServerEeventNotifyThread extends Thread {
		
		private ArrayList<ServerEventListener> serverEventListeners;
		
		public ServerEeventNotifyThread(ServerEventListener listener) {
			serverEventListeners = new ArrayList<>();
			serverEventListeners.add(listener);
		}
		
		public ServerEeventNotifyThread(ArrayList<ServerEventListener> serverEventListeners) {
			this.serverEventListeners = new ArrayList<>();
			this.serverEventListeners.addAll(serverEventListeners);
		}
		
		public void run() {
			for (int i = 0; i < serverEventListeners.size(); i++) {
				serverEventListeners.get(i).serverEvent(serverSocket, serverState);
			}
		}
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
			
			serverState = SERVER_STATE_START;
			notifyServerEvent(serverSocket, serverState);
			
			String[] hostAddresses = HInetAddress.getInetAddresses();
			for (int i = 0; i < hostAddresses.length; i++) {
				HLog.il("hostAddress - " + i + ":" + hostAddresses[i]);
			}
			
			while (isServerStart) {
				try {
					Socket clientSocket = serverSocket.accept();
					clientSockets.add(clientSocket);
					notifyClientConnectEvent(clientSocket);
					
				} catch (IOException e) {
					HLog.el(e);
					break;
				}
			}
			
			for (int i = 0; i < clientSockets.size(); i++) {
				try {
					Communication communication = new Communication();
					String exitMessage = communication.packageExitEventData();
					
					sendMessageToClient(clientSockets.get(0), exitMessage);
					
					clientSockets.get(0).close();
					clientSockets.remove(0);
				} catch (IOException e) {
					HLog.el(e);
				}
			}
			
			serverState = SERVER_STATE_STOP;
			notifyServerEvent(serverSocket, serverState);
		}
	}
}
