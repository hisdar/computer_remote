package cn.hisdar.cr.controler;

import java.awt.AWTException;
import java.awt.RenderingHints.Key;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import cn.hisdar.computerremote.common.Global;
import cn.hisdar.cr.event.HKeyEvent;
import cn.hisdar.cr.event.HKeyEventListener;
import cn.hisdar.lib.log.HLog;

public class KeyControler implements HKeyEventListener {

	public static final int ACTION_DOWN = 0;
	public static final int ACTION_UP = 1;
	
	public static final int KEYCODE_VOLUME_UP = 175;
	public static final int KEYCODE_VOLUME_DOWN = 174;

	@Override
	public void keyEvent(HKeyEvent event) {
		//HLog.i(event);
		switch (event.keyCode) {
		case Global.KEYCODE_VOLUME_UP:
			if (event.keyValue == ACTION_DOWN) {
				keyPress(KeyEvent.VK_UP);
			} else {
				keyRelease(KeyEvent.VK_UP);
			}
			break;
		case Global.KEYCODE_VOLUME_DOWN:
			if (event.keyValue == ACTION_DOWN) {
				keyPress(KeyEvent.VK_DOWN);
			} else {
				keyRelease(KeyEvent.VK_DOWN);
			}
			break;

		default:
			break;
		}
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

}
