package cn.hisdar.cr.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import cn.hisdar.cr.screen.ScreenHunterListener;
import cn.hisdar.cr.screen.ScreenHunterServer;
import cn.hisdar.cr.screen.ServerEventListener;
import cn.hisdar.lib.adapter.MathAdapter;
import cn.hisdar.lib.log.HLog;

/***
 * @description there are two ServerSocket on server, 
 *              1. ServerSocket used to send and receive command
 *              2. ServerSocket used to send and receive bytes data
 *              
 *              So we need a class to manage the two ServerSockets
 * @author Hisdar
 *
 */
public class CRCSManager implements
	ServerEventListener,
	ClientEventListener,
	ScreenHunterListener {

	private static final int MIN_SOCKET_PORT = 5299;
	private static final int MAX_SOCKET_PORT = 65535;
	
	private static CRCSManager crServerManager = null;
	private static ArrayList<CRClient> socketClients = null;
	private CRServer cmdServer = null;
	private CRServer dataServer = null;
	
	private ArrayList<ServerEventListener> serverEventListeners = null;
	
	public static CRCSManager getInstance() {
		if (crServerManager == null) {
			synchronized (CRCSManager.class) {
				if (crServerManager == null) {
					crServerManager = new CRCSManager();
				}
			}
		}
		
		return crServerManager;
	}
	
	private CRCSManager() {
		
		int cmdServerPort = searchUseablePort(MIN_SOCKET_PORT, MAX_SOCKET_PORT);
		if (cmdServerPort == -1) {
			HLog.el("Can not find a useful port, search port form " + MIN_SOCKET_PORT + " to " + MAX_SOCKET_PORT);
			return;
		}
		
		int dataServerPort = searchUseablePort(cmdServerPort + 1, MAX_SOCKET_PORT);
		if (dataServerPort == -1) {
			HLog.el("Can not find a useful port, search port form " + dataServerPort + " to " + MAX_SOCKET_PORT);
			return;
		}
		
		HLog.dl("cmd port:" + cmdServerPort);
		HLog.dl("data port:" + dataServerPort);
		
		cmdServer = new CRServer(cmdServerPort);
		dataServer = new CRServer(dataServerPort);
		
		cmdServer.registerServerEventListener(this);
		dataServer.registerServerEventListener(this);
		
		socketClients = new ArrayList<CRClient>();
		serverEventListeners = new ArrayList<ServerEventListener>();
		
		cmdServer.startServer();
		dataServer.startServer();
		
		ScreenHunterServer screenHunterServer = ScreenHunterServer.getInstance();
		//screenHunterServer.addScreenHunterListener(this);
	}
	
	public CRServer getCmdServer() {
		return cmdServer;
	}
	
	public CRServer getDataServer() {
		return dataServer;
	}
	
	private int searchUseablePort(int startPort, int endPort) {
		int useablePort = startPort;
		for (; useablePort <= endPort; useablePort++) {
			try {
				ServerSocket serverSocket = new ServerSocket(useablePort);
				serverSocket.close();
				break;
			} catch (IOException e) {
				if (useablePort == MAX_SOCKET_PORT) {
					return -1;
				}
			}
		}
		
		return useablePort;
	}

	private CRClient getCRClientBySocket(Socket socket) {
		// 1. search if CRClient is exist
		CRClient crClient = null;
		for (CRClient crClientTmp : socketClients) {
			// If all the socket have been initialized, this connect is must not for current CRClient
			if (crClientTmp.getCmdSocket() != null && crClientTmp.getDataSocket() != null) {
				continue;
			}
			
			// all the item is empty, this is a bad CRClint.
			if (crClientTmp.getCmdSocket() == null && crClientTmp.getDataSocket() == null) {
				continue;
			}
			
			// someone is empty, you may what I looking for, but I will check!!!
			Socket tempSocket = null;
			if (crClientTmp.getCmdSocket() != null) {
				tempSocket = crClientTmp.getCmdSocket();
			} else {
				tempSocket = crClientTmp.getDataSocket();
			}
			
			// same host, you are what I looking for!!!
			if (tempSocket.getInetAddress().getHostAddress().equals(socket.getInetAddress().getHostAddress())) {
				crClient = crClientTmp;
				break;
			}
		}
		
		return crClient;
	}
	
	@Override
	public void clientConnectEvent(CRServer crServer, Socket socket) {
		
		CRClient crClient = getCRClientBySocket(socket);		
		if (crClient == null) {
			crClient = new CRClient();
		}
		
		socketClients.add(crClient);
		if (crServer == cmdServer) {
			crClient.setCmdSocket(socket);
		} else if (crServer == dataServer) {
			crClient.setDataSocket(socket);
		} else {
			HLog.el("Bad CRServer !!!");
			return;
		}

		crClient.startClient();
		crClient.addClientDisconnectListener(this);
	}

	@Override
	public void serverStateEvent(CRServer crServer, int serverState) {
		notifyServerStateEvent(crServer, serverState);
		
		if (serverState == CRServer.SERVER_STATE_STOP) {
			for (int i = 0; i < socketClients.size(); i++) {
				try {
					Communication communication = new Communication();
					String exitMessage = communication.packageExitEventData();
					
					CRClient crClient = socketClients.get(0);
					crClient.sendCmd(exitMessage);
					
					crClient.getCmdSocket().close();
					crClient.getDataSocket().close();
					socketClients.remove(0);
				} catch (IOException e) {
					HLog.el(e);
				}
			}
		}
	}

	@Override
	public void clientDisconnectEvent(CRClient crClient, Socket socket) {
		
		if (crClient.getCmdSocket() == socket) {
			crClient.setCmdSocket(null);
		} else if (crClient.getDataSocket() == socket) {
			crClient.setDataSocket(null);
		} else {
			HLog.el("Bad CRServer !!!");
			return;
		}
		
		if (crClient.getCmdSocket() == null && crClient.getDataSocket() == null) {
			socketClients.remove(crClient);
		}
	}

	public void addServerEventListener(ServerEventListener listener) {
		for (ServerEventListener l : serverEventListeners) {
			if (l == listener) {
				return;
			}
		}
		
		serverEventListeners.add(listener);
	}
	
	public void removeServerEventListener(ServerEventListener listener) {
		serverEventListeners.remove(listener);
	}
	
	private void notifyServerStateEvent(CRServer crServer, int serverState) {
		for (ServerEventListener l : serverEventListeners) {
			l.serverStateEvent(crServer, serverState);
		}
	}

	@Override
	public void screenPictureChangeEvent(ScreenPictureData screenHunterData) {

		for (CRClient crClient : socketClients) {
			HLog.il("send screen data to:" + crClient.getCmdSocket().getInetAddress().getHostAddress());
			crClient.sendData(screenHunterData);
		}
	}
}
