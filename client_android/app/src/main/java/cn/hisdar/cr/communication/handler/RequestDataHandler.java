package cn.hisdar.cr.communication.handler;

import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.data.RequestData;
import cn.hisdar.cr.communication.socket.SocketIOManager;

public class RequestDataHandler extends AbstractDataHandler {

	private static RequestDataHandler requestDataHandler = null; 
	private RequestData requestData = null;
	private ArrayList<RequestEventListener> requestEventListeners = null;

	private RequestDataHandler() {
		requestEventListeners = new ArrayList<>();
		SocketIOManager.getInstance().addDataHandler(this);
	}

	public static RequestDataHandler getInstance() {
		if (requestDataHandler == null) {
			synchronized (RequestDataHandler.class) {
				if (requestDataHandler == null) {
					requestDataHandler = new RequestDataHandler();
				}
			}
		}
		
		return requestDataHandler;
	} 
	
	public void addRequestEventListener(RequestEventListener l) {
		for (int i = 0; i < requestEventListeners.size(); i++) {
			if (requestEventListeners.get(i) == l) {
				return;
			}
		}
		
		requestEventListeners.add(l);
	}
	
	public void removeRequestEventListener(RequestEventListener l) {
		requestEventListeners.remove(l);
	}
	
	@Override
	public int getDataType() {
		
		return AbstractData.DATA_TYPE_REQUEST;
	}

	@Override
	public boolean decode(byte[] data, Socket socket) {
		if (requestData == null) {
			requestData = new RequestData();
		}

		requestData.decode(data);

		for (int i = 0; i < requestEventListeners.size(); i++) {
			requestEventListeners.get(i).requestEvent(requestData);
		}

		return true;
	}

}
