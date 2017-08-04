package cn.hisdar.cr.controler;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;

import cn.hisdar.computerremote.common.Global;
import cn.hisdar.cr.communication.data.MouseButtonData;
import cn.hisdar.cr.communication.handler.HMotionEvent;
import cn.hisdar.cr.communication.handler.MotionEventHandler;
import cn.hisdar.cr.communication.handler.MotionEventListener;
import cn.hisdar.cr.communication.handler.MouseButtonEventHandler;
import cn.hisdar.cr.communication.handler.MouseButtonEventListener;
import cn.hisdar.cr.event.HMouseEvent;
import cn.hisdar.cr.event.HMouseEventListener;
import cn.hisdar.lib.log.HLog;

public class MouseControler implements MouseButtonEventListener, MotionEventListener {

	private ArrayList<HMotionEvent> motionEvents = null;
	
	private HMotionEvent lastEvent = null;
	private HMotionEvent downEvent = null;
	private HMotionEvent upEvent = null;
	
	// multi finger action check
	private boolean isSiginFinger = true;
	
	public MouseControler() {
		motionEvents = new ArrayList<>();
		MotionEventHandler.getInstance().addMotionEventListener(this);
		MouseButtonEventHandler.getInstance().addMouseButtonEventListener(this);
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
			
			float x = event.getX(0) - lastEvent.getX(0);
			float y = event.getY(0) - lastEvent.getY(0);
			
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
			float downX = downEvent.getX(0);
			float downY = downEvent.getY(0);
			
			float upX = upEvent.getX(0);
			float upY = upEvent.getY(0);
			
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
	public void mouseButtonEvent(MouseButtonData mouseButtonData) {
		if (mouseButtonData.getButtioID() == MouseButtonData.BUTTON1) {
			
			mouseButtonAction(InputEvent.BUTTON1_MASK, mouseButtonData.getActionCode());
			
		} else if (mouseButtonData.getButtioID() == MouseButtonData.BUTTON3) {
			
			mouseButtonAction(InputEvent.BUTTON3_MASK, mouseButtonData.getActionCode());
			
		} else {
			HLog.el("Unhandled mouse event:" + mouseButtonData.toString());
		}
	}
}
