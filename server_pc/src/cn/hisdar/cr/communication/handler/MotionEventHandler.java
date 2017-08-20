package cn.hisdar.cr.communication.handler;

import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.data.MotionEventData;
import cn.hisdar.cr.communication.socket.SocketIOManager;
import cn.hisdar.cr.debug.DelayDebuger;
import cn.hisdar.lib.log.HLog;

/**
 * Created by Hisdar on 2017/7/22.
 */

public class MotionEventHandler extends AbstractHandler {

	private static MotionEventHandler motionEventHandler = null;
	
	private ArrayList<MotionEventListener> motionEventListeners = null;

    public static MotionEventHandler getInstance() {
    	if (motionEventHandler == null) {
    		synchronized (MotionEventHandler.class) {
    			if (motionEventHandler == null) {
    				motionEventHandler = new MotionEventHandler();
    			}
			}
    	}
    	
    	return motionEventHandler;
    }
    
    private MotionEventHandler() {
    	motionEventListeners = new ArrayList<>();
    	SocketIOManager.getInstance().addDataHandler(this);
    }

    @Override
    public int getDataType() {
        return AbstractData.DATA_TYPE_MOTION_EVENT;
    }

    @Override
	public boolean decode(byte[] data, Socket socket) {
        MotionEventData motionEventData = new MotionEventData();
        motionEventData.decode(data);
        
        //DelayDebuger debuger = new DelayDebuger();
        for (int i = 0; i < motionEventListeners.size(); i++) {
			motionEventListeners.get(i).motionEvent(motionEventData.getMotionEvent());
		}

        //HLog.dl("MotionEvent dispatch delay:" + debuger.getDelay());
        
        return true;
    }
    
    public void addMotionEventListener(MotionEventListener listener) {
    	for (int i = 0; i < motionEventListeners.size(); i++) {
			if (motionEventListeners.get(i) == listener) {
				return;
			}
		}
    	
    	motionEventListeners.add(listener);
    }
    
    public void removeMotionEventListener(MotionEventListener l) {
    	motionEventListeners.remove(l);
    }
}
