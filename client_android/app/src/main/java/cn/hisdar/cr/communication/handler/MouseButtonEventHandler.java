package cn.hisdar.cr.communication.handler;

import java.net.Socket;
import java.util.ArrayList;

import cn.hisdar.cr.communication.data.AbstractData;
import cn.hisdar.cr.communication.data.MouseButtonData;

/**
 * Created by Hisdar on 2017/8/4.
 */

public class MouseButtonEventHandler extends AbstractHandler {

    private MouseButtonData mouseButtonData = null;
    private static MouseButtonEventHandler mouseButtonEventHandler = null;
    private ArrayList<MouseButtonEventListener> mouseButtonEventListeners = null;

    private MouseButtonEventHandler() {
        mouseButtonEventListeners = new ArrayList<>();
    }

    public static MouseButtonEventHandler getInstance() {

        if (mouseButtonEventHandler == null) {
            synchronized (MouseButtonEventHandler.class) {
                if (mouseButtonEventHandler == null) {
                    mouseButtonEventHandler = new MouseButtonEventHandler();
                }
            }
        }

        return mouseButtonEventHandler;
    }

    public void addMouseButtonEventListener(MouseButtonEventListener l) {
        for (MouseButtonEventListener listener: mouseButtonEventListeners) {
            if (listener == l) {
                return;
            }
        }

        mouseButtonEventListeners.add(l);
    }

    public void removeMouseButtonEventListener(MouseButtonEventListener l) {
        mouseButtonEventListeners.remove(l);
    }

    @Override
    public int getDataType() {
        return AbstractData.DATA_TYPE_MOUSE_BUTTON;
    }

    @Override
    public boolean decode(byte[] data, Socket socket) {

        if (mouseButtonData == null) {
            mouseButtonData = new MouseButtonData();
        }

        boolean bRet = mouseButtonData.decode(data);
        if (!bRet) {
            return false;
        }

        for (MouseButtonEventListener l: mouseButtonEventListeners) {
            l.mouseButtonEvent(mouseButtonData);
        }

        return true;
    }
}
