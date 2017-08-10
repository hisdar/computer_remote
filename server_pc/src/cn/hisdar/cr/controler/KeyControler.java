package cn.hisdar.cr.controler;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import cn.hisdar.computerremote.common.Global;
import cn.hisdar.cr.communication.data.KeyEventData;
import cn.hisdar.cr.communication.handler.KeyEventHandler;
import cn.hisdar.cr.communication.handler.KeyEventListener;
import cn.hisdar.lib.log.HLog;

public class KeyControler implements KeyEventListener {

	public static final int ACTION_DOWN = 0;
	public static final int ACTION_UP = 1;
	
	public static final int KEYCODE_VOLUME_UP = 175;
	public static final int KEYCODE_VOLUME_DOWN = 174;
	
	public KeyControler() {
		KeyEventHandler.getInstance().addKeyEventListener(this);
	}

	public void keyRelease(int button) {
		Robot reRobot = null;
		
		try {
			reRobot = new Robot();
		} catch (AWTException e) {
			HLog.el(e);
			return;
		}
		
		reRobot.keyRelease(button);
	}
	
	public void keyPress(int button) {
		Robot reRobot = null;
		
		try {
			reRobot = new Robot();
		} catch (AWTException e) {
			HLog.el(e);
			return;
		}
		reRobot.keyPress(button);
	}

	@Override
	public void keyEvent(KeyEventData keyEventData) {
		
		HLog.il(keyEventData);
		
		switch (keyEventData.getKeyCode()) {
		case Global.KEYCODE_VOLUME_UP:
			if (keyEventData.getKeyAction() == ACTION_DOWN) {
				keyPress(KeyEvent.VK_UP);
			} else {
				keyRelease(KeyEvent.VK_UP);
			}
			break;
		case Global.KEYCODE_VOLUME_DOWN:
			if (keyEventData.getKeyAction() == ACTION_DOWN) {
				keyPress(KeyEvent.VK_DOWN);
			} else {
				keyRelease(KeyEvent.VK_DOWN);
			}
			break;

		default:
			break;
		}
	}

}
