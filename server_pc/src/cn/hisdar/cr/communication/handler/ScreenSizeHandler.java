package cn.hisdar.cr.communication.handler;

import java.util.ArrayList;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.data.ScreenSizeData;
import cn.hisdar.cr.communication.socket.SocketIOManager;

/**
 * Created by Hisdar on 2017/7/22.
 */

public class ScreenSizeHandler extends AbstractDataHandler {

	private static ScreenSizeHandler screenSizeHandler = null;
	private ScreenSizeData screenSizeData = null;
	private ArrayList<ScreenSizeListener> screenSizeListeners = null;

	public static ScreenSizeHandler getInstance() {
		if (screenSizeHandler == null) {
			synchronized (ScreenSizeHandler.class) {
				if (screenSizeHandler == null) {
					screenSizeHandler = new ScreenSizeHandler();
				}
			}
		}
		
		return screenSizeHandler;
	}
	
	private ScreenSizeHandler() {
		screenSizeListeners = new ArrayList<>();
		SocketIOManager.getInstance().addDataHandler(this);
	}
	
	public void addScreenSizeListener(ScreenSizeListener l) {
		for (int i = 0; i < screenSizeListeners.size(); i++) {
			if (screenSizeListeners.get(i) == l) {
				return;
			}
		}
		
		screenSizeListeners.add(l);
	}

	public void removeScreenSizeListener(ScreenSizeListener l) {
		screenSizeListeners.remove(l);
	}
	
	@Override
	public int getDataType() {
		return AbstractData.DATA_TYPE_SCREEN_SIZE;
	}

	public byte[] encode() {
		if (screenSizeData == null) {
			return null;
		}
		
		return screenSizeData.encode();
	}

	@Override
	public boolean decode(byte[] data) {
		if (data.length != 8) {
			return false;
		}

		if (screenSizeData == null) {
			screenSizeData = new ScreenSizeData();
		}
		
		screenSizeData.decode(data);

		for (int i = 0; i < screenSizeListeners.size(); i++) {
			screenSizeListeners.get(i).screenSizeEvent(screenSizeData);
		}
		
		return true;
	}
}
