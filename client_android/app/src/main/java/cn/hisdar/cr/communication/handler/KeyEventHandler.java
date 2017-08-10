package cn.hisdar.cr.communication.handler;

import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.data.KeyEventData;
import cn.hisdar.cr.communication.socket.SocketIOManager;

/**
 * Created by Hisdar on 2017/8/9.
 */

public class KeyEventHandler extends AbstractHandler {

    private static KeyEventHandler keyEventHandler = null;
    private ArrayList<KeyEventListener> keyEventListeners = null;

    private KeyEventData keyEventData = null;

    private KeyEventHandler() {
        keyEventListeners = new ArrayList<>();
        keyEventData = new KeyEventData();
        SocketIOManager.getInstance().addDataHandler(this);
    }

    public static KeyEventHandler getInstance() {
        if (keyEventHandler == null) {
            synchronized (KeyEventHandler.class) {
                if (keyEventHandler == null) {
                    keyEventHandler = new KeyEventHandler();
                }
            }
        }

        return keyEventHandler;
    }

    @Override
    public int getDataType() {
        return AbstractData.DATA_TYPE_KEY_EVENT;
    }

    @Override
    public boolean decode(byte[] data, Socket socket) {

        if (keyEventData == null) {
            keyEventData = new KeyEventData();
        }

        boolean bRet = keyEventData.decode(data);
        if (!bRet) {
        	return bRet;
        }
        
        for (KeyEventListener itr : keyEventListeners) {
			itr.keyEvent(keyEventData);
		}
        
        return true;
    }

    public void addKeyEventListener(KeyEventListener l) {
        for (KeyEventListener itr: keyEventListeners) {
            if (itr == l) {
                return;
            }
        }

        keyEventListeners.add(l);
    }

    public void removeKeyEventListener(KeyEventListener l) {
        keyEventListeners.remove(l);
    }
}
