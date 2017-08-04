package cn.hisdar.cr.communication.handler;

import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.data.ScreenPictureData;
import cn.hisdar.cr.communication.socket.SocketIOManager;

public class ScreenPictureHandler extends AbstractHandler {

	private static ScreenPictureHandler screenPictureHandler = null;
	
	private ArrayList<ScreenPictureListener> screenPictureListeners = null;
	private ScreenPictureData screenPictureData = null;
	
	private ScreenPictureHandler() {
		screenPictureListeners = new ArrayList<>();
		SocketIOManager.getInstance().addDataHandler(this);
	}
	
	public static ScreenPictureHandler getInstance() {
		
		if (screenPictureHandler == null) {
			synchronized (ScreenPictureHandler.class) {
				if (screenPictureHandler == null) {
					screenPictureHandler = new ScreenPictureHandler();
				}
			}			
		}
		
		return screenPictureHandler;
	}

    @Override
    public int getDataType() {
        return AbstractData.DATA_TYPE_SCREEN_PICTURE;
    }

	@Override
	public boolean decode(byte[] data, Socket socket) {
		if (screenPictureData == null) {
			screenPictureData = new ScreenPictureData();
		}
		
		boolean bRet = screenPictureData.decode(data);
		if (!bRet) {
			return bRet;
		}
		
		for (int i = 0; i < screenPictureListeners.size(); i++) {
			screenPictureListeners.get(i).screenPictureEvent(screenPictureData);
		}
		
		return true;
	}
	
	public void addScreenPictureListener(ScreenPictureListener l) {
		
		for (int i = 0; i < screenPictureListeners.size(); i++) {
			if (screenPictureListeners.get(i) == l) {
				return;
			}
		}
		
		screenPictureListeners.add(l);
	}
	
	public void removeScreenPictureListener(ScreenPictureListener l) {
		screenPictureListeners.remove(l);
	}

}
