package cn.hisdar.cr.communication.socket;

import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.handler.AbstractHandler;

/***
 * This class is using to manage SocketIO and data handlers
 * @author Hisdar
 *
 */
public class SocketIOManager implements SocketIOEventListener {

	private static SocketIOManager socketIOManager = null;
	private ArrayList<SocketIO> socketIOs = null;
	private ArrayList<AbstractHandler> dataHandlers = null;
	private ArrayList<SocketDisconnectListener> socketDisconnectListeners = null;
	
	private SocketIOManager() {
		socketIOs = new ArrayList<>();
		dataHandlers = new ArrayList<>();
		socketDisconnectListeners = new ArrayList<>();
	}
	
	public static SocketIOManager getInstance() {
		if (socketIOManager == null) {
			synchronized (SocketIOManager.class) {
				if (socketIOManager == null) {
					socketIOManager = new SocketIOManager();
				}
			}
		}
		
		return socketIOManager;
	}
	
	public void addSocket(Socket socket) {
		for (int i = 0; i < socketIOs.size(); i++) {
			if (socketIOs.get(i).getSocket() == socket) {
				return;
			}
		}

		SocketIO socketIO = new SocketIO(socket);
		socketIO.addSocketIOEventListener(this);
		socketIOs.add(socketIO);
	}
	
	public void addDataHandler(AbstractHandler dataHandler) {
		for (int i = 0; i < dataHandlers.size(); i++) {
			if (dataHandlers.get(i) == dataHandler) {
				return;
			}
		}
		
		dataHandlers.add(dataHandler);
	}
	
	public void removeDataHandler(AbstractHandler dataHandler) {
		dataHandlers.remove(dataHandler);
	}

	public Socket getSocketByIP(String ip) {
		for (int i = 0; i < socketIOs.size(); i++) {
			String curIP = socketIOs.get(i).getSocket().getInetAddress().getHostAddress();
			if (curIP.equals(ip)) {
				return socketIOs.get(i).getSocket();
			}
		}

		return null;
	}

	public void removeSocketByIP(String ip) {
		for (int i = 0; i < socketIOs.size(); i++) {
			String curIP = socketIOs.get(i).getSocket().getInetAddress().getHostAddress();
			if (curIP.equals(ip)) {
				socketIOs.remove(i);
				return ;
			}
		}
	}

	public ArrayList<Socket> getAllSockets() {
		ArrayList<Socket> allSockets = new ArrayList<>();
		for (int i = 0; i < socketIOs.size(); i++) {
			allSockets.add(socketIOs.get(i).getSocket());
		}

		return allSockets;
	}

	@Override
	public void socketIOEvent(byte[] data, int dataType, Socket socket) {
		for (int i = 0; i < dataHandlers.size(); i++) {
			AbstractHandler dataHandler = dataHandlers.get(i);
			if (dataHandler.getDataType() == dataType) {
				dataHandler.decode(data, socket);
			}
		}
	}
	
	public boolean sendData(byte[] data, int dataType) {
		
		return true;
	}
	
	public boolean sendDataToAll(byte[] data, int dataType) {
		return true;
	}
	
	public boolean sendDataToClient(AbstractData data, Socket socket) {

		if (socket != null) {
			for (int i = 0; i < socketIOs.size(); i++) {
				if (socketIOs.get(i).getSocket() == socket) {
					return socketIOs.get(i).sendDataMutual(data);
				}
			}
			
			//HLog.dl("No socket found in socketIOs");
		} else {
			// send to all the connection
			int sendCount = 0;
			for (int i = 0; i < socketIOs.size(); i++) {
				if (socketIOs.get(i).getSocket() != null) {
					socketIOs.get(i).sendDataMutual(data);
					sendCount += 1;
				}
			}
			
			//HLog.dl("Send socket count:" + sendCount);
		}

		return true;
	}

	@Override
	public void socketDisconnectEvent(Socket socket) {
		
		SocketIO socketIO = null;
		for (int i = 0; i < socketIOs.size(); i++) {
			if (socketIOs.get(i).getSocket() == socket) {
				socketIO = socketIOs.get(i);
				break;
			}
		}
		
		if (socketIO != null) {
			socketIO.removeSocketIOEventListener(this);
			socketIOs.remove(socketIO);
		}
		
		for (SocketDisconnectListener itr : socketDisconnectListeners) {
			itr.socketDisconnectEvent(socket);
		}
	}
	
	public void addSocketDisconnectListener(SocketDisconnectListener l) {
		for (SocketDisconnectListener itr : socketDisconnectListeners) {
			if (itr == l) {
				return;
			}
		}
		
		socketDisconnectListeners.add(l);
	}
	
	public void removeSocketDisconnectListener(SocketDisconnectListener l) {
		socketDisconnectListeners.remove(l);
	}
}
