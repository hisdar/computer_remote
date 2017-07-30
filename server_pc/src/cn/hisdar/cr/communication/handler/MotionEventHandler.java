package cn.hisdar.cr.communication.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import cn.hisdar.cr.communication.data.MotionEventData;
import cn.hisdar.cr.communication.socket.SocketIOManager;

/**
 * Created by Hisdar on 2017/7/22.
 */

public class MotionEventHandler extends AbstractDataHandler {

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
    	//CRCSManager.getInstance().getDataServer().registerServerEventListener(this);
    }

    @Override
    public int getDataType() {
        return DATA_TYPE_MOTION_EVENT;
    }

    @Override
    public boolean decode(byte[] data) {
        MotionEventData motionEventData = new MotionEventData();
        motionEventData.decode(data);
        
        System.out.println("MotionEventData:");
        
        for (int i = 0; i < motionEventListeners.size(); i++) {
			motionEventListeners.get(i).motionEvent(motionEventData.getMotionEvent());
		}

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
