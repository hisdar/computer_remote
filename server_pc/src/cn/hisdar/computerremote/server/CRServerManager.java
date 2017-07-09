package cn.hisdar.computerremote.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.omg.CORBA.PRIVATE_MEMBER;

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
public class CRServerManager implements ClientEventListener {

	private static final int MIN_SOCKET_PORT = 5299;
	private static final int MAX_SOCKET_PORT = 65535;
	
	private static CRServerManager crServerManager = null;
	private static ArrayList<CRClient> socketClients = null;
	private CRServer cmdServer;
	private CRServer dataServer;
	
	public static CRServerManager getInstance() {
		if (crServerManager == null) {
			synchronized (CRServerManager.class) {
				if (crServerManager == null) {
					crServerManager = new CRServerManager();
				}
			}
		}
		
		return crServerManager;
	}
	
	private CRServerManager() {
		
		int cmdServerPort = searchUseablePort(MIN_SOCKET_PORT, MAX_SOCKET_PORT);
		if (cmdServerPort == -1) {
			HLog.el("Can not find a useful port, search port form " + MIN_SOCKET_PORT + " to " + MAX_SOCKET_PORT);
			return;
		}
		
		int dataServerPort = searchUseablePort(cmdServerPort, MAX_SOCKET_PORT);
		if (dataServerPort == -1) {
			HLog.el("Can not find a useful port, search port form " + dataServerPort + " to " + MAX_SOCKET_PORT);
			return;
		}
		
		cmdServer = new CRServer(cmdServerPort);
		dataServer = new CRServer(dataServerPort);
		
		cmdServer.addClientEventListener(this);
		dataServer.addClientEventListener(this);
		
		socketClients = new ArrayList<CRClient>();
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
				HLog.dl(e);
				if (useablePort == MAX_SOCKET_PORT) {
					HLog.el("Init server fail");
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
		
		if (crServer == cmdServer) {
			crClient.setCmdSocket(socket);
		} else if (crServer == dataServer) {
			crClient.setDataSocket(socket);
		} else {
			HLog.el("Bad CRServer !!!");
			return;
		}

		Thread remoteClientThread = new Thread(crClient);
		remoteClientThread.start();
		crClient.addClientDisconnectListener(crServer);
	}

	@Override
	public void clientDisconnectEvent(CRServer crServer, Socket socket) {
		CRClient crClient = getCRClientBySocket(socket);
		if (crClient == null) {
			return;
		}
		
		if (crServer == cmdServer) {
			crClient.setCmdSocket(null);
		} else if (crServer == dataServer) {
			crClient.setDataSocket(null);
		} else {
			HLog.el("Bad CRServer !!!");
			return;
		}
		
		if (crClient.getCmdSocket() == null && crClient.getDataSocket() == null) {
			socketClients.remove(crClient);
		}
	}

}
