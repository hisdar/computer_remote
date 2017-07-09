package cn.hisdar.computerremote.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.net.HInetAddress;

public class ComputerRemoteServer implements ClientDisconnectListener {

	public static final int SERVER_STATE_STOP = 0;
	public static final int SERVER_STATE_START = 1;
	
	private static ComputerRemoteServer computerRemoteServer = null;
	private static ArrayList<ClientEventListener> clientEventListeners = null;
	private static ArrayList<ServerEventListener> serverEventListeners = null;

	private static ServerSocket serverSocket = null;
	private static int serverState = SERVER_STATE_STOP;
	
	private boolean isServerStart;
	
	private ComputerRemoteServer() {
		isServerStart = false;
	}
	
	public static ComputerRemoteServer getInstance() {
		if (computerRemoteServer == null) {
			synchronized (ComputerRemoteServer.class) {
				if (computerRemoteServer == null) {
					computerRemoteServer = new ComputerRemoteServer();
					
					clientEventListeners = new ArrayList<>();
					serverEventListeners = new ArrayList<>();
				}
			}
		}
		
		return computerRemoteServer;
	}
	
	public void startServer() {
		if (isServerStart) {
			return;
		}
		
		HLog.il("Start computer remote server");
		
		Thread controlerListenerServer = new Thread(new ControlerListenerServer(this));
		controlerListenerServer.start();
		
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
	
	private class ControlerListenerServer implements Runnable {

		private static final int DEFAULT_SERVER_PORT = 5299;
		private static final int MAX_SOCKET_PORT = 65535;
		
		private int serverPort = DEFAULT_SERVER_PORT;
		private ComputerRemoteServer computerRemoteServer = null;
		
		private ArrayList<Socket> clientSockets = null;
		
		public ControlerListenerServer(ComputerRemoteServer computerRemoteServer) {
			this.computerRemoteServer = computerRemoteServer;
			clientSockets = new ArrayList<>();
		}
		
		@Override
		public void run() {
			
			for (serverPort = DEFAULT_SERVER_PORT; serverPort <= MAX_SOCKET_PORT; serverPort++) {
				try {
					serverSocket = new ServerSocket(serverPort);
					break;
				} catch (IOException e) {
					HLog.dl(e);
					if (serverPort == MAX_SOCKET_PORT) {
						HLog.el("Init server fail");
						return;
					}
				}
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
					
					HLog.il("Client connect");
					
					clientSockets.add(clientSocket);
					
					ComputerRemoteClient remoteClient = new ComputerRemoteClient(clientSocket);
					Thread remoteClientThread = new Thread(remoteClient);
					remoteClientThread.start();
					remoteClient.addClientDisconnectListener(computerRemoteServer);
					notifyClientConnectEvent(remoteClient);
					
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
	
	public void notifyClientConnectEvent(ComputerRemoteClient crc) {
		for (int i = 0; i < clientEventListeners.size(); i++) {
			clientEventListeners.get(i).clientConnectEvent(crc);
		}
	}
	
	public void notifyClientDisconnectEvent(ComputerRemoteClient crc) {
		for (int i = 0; i < clientEventListeners.size(); i++) {
			clientEventListeners.get(i).clientDisconnectEvent(crc);
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
	public void clientDisconnectEvent(ComputerRemoteClient computerRemoteClient) {
		notifyClientDisconnectEvent(computerRemoteClient);		
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
}
