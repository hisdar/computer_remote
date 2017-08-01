package cn.hisdar.cr.controler;

import java.util.ArrayList;

import cn.hisdar.cr.communication.handler.HMotionEvent;
import cn.hisdar.cr.communication.handler.MotionEventListener;
import cn.hisdar.cr.event.PinchPointer;
import cn.hisdar.cr.event.Pointer;

public class GestureParser implements MotionEventListener {
	
	public static GestureParser gestureParser = null;
	
	private ArrayList<PinchPointer> pinchHistoryPointers = null;
	private ArrayList<Double> distances = null;
	private ArrayList<GestureListener> gestureListeners = null;
	private GestureParser() {
		
		pinchHistoryPointers = new ArrayList<>();
		distances = new ArrayList<>();
		gestureListeners = new ArrayList<>();
		
		//EventDispatcher eventDispatcher = EventDispatcher.getInstance();
		//eventDispatcher.addHMotionEventListener(this);
	}
	
	public static GestureParser getInstance() {
		if (gestureParser == null) {
			synchronized (GestureParser.class) {
				if (gestureParser == null) {
					gestureParser = new GestureParser();
				}
			}
		}
		
		return gestureParser;
	}
	
	private void pinchGestureParse(HMotionEvent event) {
		PinchPointer pinchPointer = new PinchPointer();
		
		pinchPointer.setX1(event.getX(0));
		pinchPointer.setX2(event.getX(1));
		
		pinchPointer.setY1(event.getY(0));
		pinchPointer.setY2(event.getY(1));
		pinchHistoryPointers.add(pinchPointer);
		
		distances.add(getDistance(pinchPointer.getPointer1(), pinchPointer.getPointer2()));
		
		// discern pinch gesture
		// more than 3 action can trigger pinch discern
		if (pinchHistoryPointers.size() < 3) {
			return;
		}
		
		if (pinchHistoryPointers.size() > 10) {
			pinchHistoryPointers.remove(0);
			distances.remove(0);
		}
		
		if (pinchBigerParse()) {
			for (int i = 0; i < gestureListeners.size(); i++) {
				gestureListeners.get(i).pinckBiggerEvemt(1);
			}
		} else if (pinchSmallerParse()) {
			// notify smaller pinch
			for (int i = 0; i < gestureListeners.size(); i++) {
				gestureListeners.get(i).pinckSmallerEvemt(1);
			}
		}
	}
	
	private double getDistance(Pointer pointer1, Pointer pointer2) {
		float dx = pointer1.getX() - pointer2.getX();
		float dy = pointer1.getY() - pointer2.getY();
		
		return Math.sqrt( (dx * dx) + (dy * dy));
	}
	
	private boolean pinchBigerParse() {
		
		for (int i = 0; i < pinchHistoryPointers.size() - 1; i++) {
			PinchPointer pinchPointer1 = pinchHistoryPointers.get(i);
			PinchPointer pinchPointer2 = pinchHistoryPointers.get(i + 1);
			
			double distance1 = getDistance(pinchPointer1.getPointer1(), pinchPointer1.getPointer2());
			double distance2 = getDistance(pinchPointer2.getPointer1(), pinchPointer2.getPointer2());
			
			if (distance2 - distance1 <= 5) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean pinchSmallerParse() {
		for (int i = 0; i < pinchHistoryPointers.size() - 1; i++) {
			PinchPointer pinchPointer1 = pinchHistoryPointers.get(i);
			PinchPointer pinchPointer2 = pinchHistoryPointers.get(i + 1);
			
			double distance1 = getDistance(pinchPointer1.getPointer1(), pinchPointer1.getPointer2());
			double distance2 = getDistance(pinchPointer2.getPointer1(), pinchPointer2.getPointer2());
			
			if (distance1 - distance2 <= 5) {
				return false;
			}
		}
		
		return true;
	}

	public void addGestureListener(GestureListener l) {
		for (int i = 0; i < gestureListeners.size(); i++) {
			if (gestureListeners.get(i) == l) {
				return;
			}
		}
		
		gestureListeners.add(l);
	}
	
	public void removeGestureListener(GestureListener l) {
		gestureListeners.remove(l);
	}

	@Override
	public void motionEvent(HMotionEvent event) {
		// finger up or finger count less than 2, clear pinch history data
		if (event.action == HMotionEvent.ACTION_UP || event.getPointerCount() != 2) {
			while (pinchHistoryPointers.size() > 0) {
				pinchHistoryPointers.remove(0);
				distances.remove(0);
			}
		}
		
		if (event.getPointerCount() == 2) {
			pinchGestureParse(event);
		}
	}
}
