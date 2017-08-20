package cn.hisdar.cr.communication.handler;

import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.data.ResponseData;
import cn.hisdar.cr.communication.socket.SocketIOManager;

public class ResponseHandler extends AbstractHandler {

	private static ResponseHandler responseHandler = null;
	
	private ResponseData responseData = null;
	private ArrayList<ResponseListener> responseListeners = null; 
	
	private ResponseHandler() {
		responseData = new ResponseData();
		responseListeners = new ArrayList<>();
		
		SocketIOManager.getInstance().addDataHandler(this);
	}
	
	public static ResponseHandler getInstance() {
		
		if (responseHandler == null) {
			synchronized (ResponseHandler.class) {
				if (responseHandler == null) {
					responseHandler = new ResponseHandler();
				}
			}
		}
		
		return responseHandler;
	}
	
	@Override
	public int getDataType() {
		
		return AbstractData.DATA_TYPE_RESPONSE;
	}

	@Override
	public boolean decode(byte[] data, Socket socket) {
		
		if (responseData == null) {
			responseData = new ResponseData();
		}
		
		boolean bRet = responseData.decode(data);
		if (!bRet) {
			return false;
		}
		
		for (ResponseListener l : responseListeners) {
			l.responseEvent(responseData);
		}
		
		return true;
	}
	
	public void addResponseListener(ResponseListener l) {
		for (ResponseListener responseListener : responseListeners) {
			if (responseListener == l) {
				return;
			}
		}
		
		responseListeners.add(l);
	}
	
	public void removeResponseListener(ResponseListener l) {
		responseListeners.remove(l);
	}

}
