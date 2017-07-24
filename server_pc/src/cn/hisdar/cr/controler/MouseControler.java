package cn.hisdar.cr.controler;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import cn.hisdar.computerremote.common.Global;
import cn.hisdar.cr.event.HMotionEvent;
import cn.hisdar.cr.event.HMotionEventListener;
import cn.hisdar.cr.event.HMouseEvent;
import cn.hisdar.cr.event.HMouseEventListener;
import cn.hisdar.lib.log.HLog;

public class MouseControler implements HMotionEventListener, HMouseEventListener {

	private ArrayList<HMotionEvent> motionEvents = null;
	
	private HMotionEvent lastEvent = null;
	private HMotionEvent downEvent = null;
	private HMotionEvent upEvent = null;
	
	// multi finger action check
	private boolean isSiginFinger = true;
	
	public MouseControler() {
		motionEvents = new ArrayList<>();
	}

	@Override
	public void motionEvent(HMotionEvent event) {
		
		switch (event.action) {
		case HMotionEvent.ACTION_MOVE:
			
			if (event.getPointerCount() > 1) {
				isSiginFinger = false;
				break;
			} else {
				
				// multi finger change to single finger, and jump this event
				if (!isSiginFinger) {
					isSiginFinger = true;
					lastEvent = event;
					break;
				}
			}

			if (lastEvent == null) {
				HLog.dl("last event is null");
				break;
			}
			
			if (event.getPointers().get(0).getId() != lastEvent.getPointers().get(0).getId()) {
				HLog.dl("not the same finger");
				break;
			}
			
			float x = event.getPointers().get(0).getX() - lastEvent.getPointers().get(0).getX();
			float y = event.getPointers().get(0).getY() - lastEvent.getPointers().get(0).getY();
			
			if (x > 100 || y > 100) {
				HLog.dl("x=" + x + ", y=" + y);
				
				HLog.dl("last:" + lastEvent.toString());
				break;
			}
			
			mouseMove((int)(x), (int)(y));
			break;

		case HMotionEvent.ACTION_DOWN:
			downEvent = event;
			break;
			
		case HMotionEvent.ACTION_UP:
			upEvent = event;
			lastEvent = null;
			
			mouseEventEx(downEvent, upEvent);
			break;
		default:
			break;
		}
		
		lastEvent = event;
	}
	
	private void mouseEventEx(HMotionEvent downEvent, HMotionEvent upEvent) {
		if (downEvent == null || upEvent == null) {
			return;
		}
		
		if (upEvent.getEventTime() - downEvent.getEventTime() < 500) {
			float downX = downEvent.getPointers().get(0).getX();
			float downY = downEvent.getPointers().get(0).getY();
			
			float upX = upEvent.getPointers().get(0).getX();
			float upY = upEvent.getPointers().get(0).getY();
			
			float distanceX = upX - downX;
			float distanceY = upY - downY;
			
			double distance = Math.sqrt(Math.abs(distanceX * distanceX + distanceY + distanceY));
			if (distance < 15) {
				mousePress(InputEvent.BUTTON1_MASK);
				mouseRelease(InputEvent.BUTTON1_MASK);
			}
		}
	}
	
	public void mouseMove(int x, int y) {
		Robot reRobot = null;
		
		try {
			reRobot = new Robot();
		} catch (AWTException e) {
			HLog.el(e);
			return;
		}
		
		double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
		double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
		reRobot.mouseMove((int)mouseX + x, (int)mouseY + y);
	}
	
	public void mouseRelease(int button) {
		Robot reRobot = null;
		
		try {
			reRobot = new Robot();
		} catch (AWTException e) {
			HLog.el(e);
			return;
		}
		
		reRobot.mouseRelease(button);
	}
	
	public void mousePress(int button) {
		Robot reRobot = null;
		
		try {
			reRobot = new Robot();
		} catch (AWTException e) {
			HLog.el(e);
			return;
		}
		reRobot.mousePress(button);
	}

	private void mouseButtonAction(int buttonId, int value) {
		if (buttonId == InputEvent.BUTTON1_MASK || buttonId == InputEvent.BUTTON3_MASK) {
			if (value == HMotionEvent.ACTION_DOWN) {
				mousePress(buttonId);
			} else if (value == HMotionEvent.ACTION_UP) {
				mouseRelease(buttonId);
			}
		}
	}
	
	@Override
	public void mouseEvent(HMouseEvent event) {
		if (event.buttonId == Global.BUTTON1) {
			mouseButtonAction(InputEvent.BUTTON1_MASK, event.getValue());
		} else if (event.buttonId == Global.BUTTON3) {
			mouseButtonAction(InputEvent.BUTTON3_MASK, event.getValue());
		} else {
			HLog.el("Unhandled mouse event:" + event.toString());
		}
	}
}
