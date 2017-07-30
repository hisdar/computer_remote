package cn.hisdar.cr.communication.socket;

import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.handler.AbstractDataHandler;

/***
 * This class is using to manage SocketIO and data handlers
 * @author Hisdar
 *
 */
public class SocketIOManager implements SocketIOEventListener {

	private static SocketIOManager socketIOManager = null;
	private ArrayList<SocketIO> socketIOs = null;
	private ArrayList<Socket> sockets = null;
	private ArrayList<AbstractDataHandler> dataHandlers = null;
	
	private SocketIOManager() {
		sockets = new ArrayList<>();
		socketIOs = new ArrayList<>();
		dataHandlers = new ArrayList<>();
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
		for (int i = 0; i < sockets.size(); i++) {
			if (sockets.get(i) == socket) {
				return;
			}
		}
		
		sockets.add(socket);
		SocketIO socketIO = new SocketIO(socket);
		socketIO.addSocketIOEventListener(this);
		socketIOs.add(socketIO);
	}
	
	public void addDataHandler(AbstractDataHandler dataHandler) {
		for (int i = 0; i < dataHandlers.size(); i++) {
			if (dataHandlers.get(i) == dataHandler) {
				return;
			}
		}
		
		dataHandlers.add(dataHandler);
	}
	
	public void removeDataHandler(AbstractDataHandler dataHandler) {
		dataHandlers.remove(dataHandler);
	}

	@Override
	public void socketIOEvent(byte[] data, int dataType, Socket socket) {
		for (int i = 0; i < dataHandlers.size(); i++) {
			AbstractDataHandler dataHandler = dataHandlers.get(i);
			if (dataHandler.getDataType() == dataType) {
				dataHandler.decode(data);
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
		for (int i = 0; i < socketIOs.size(); i++) {
			if (socket == null) {

			} else {
				if (socketIOs.get(i).getSocket() == socket) {
					return socketIOs.get(i).sendData(data);
				}
			}
		}
		return true;
	}
}
