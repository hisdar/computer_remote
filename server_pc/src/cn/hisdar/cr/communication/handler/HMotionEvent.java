package cn.hisdar.cr.communication.handler;

import java.util.ArrayList;

/**
 * Created by Hisdar on 2017/7/23.
 */

public class HMotionEvent {
	
	public static final int ACTION_DOWN = 0;
	public static final int ACTION_UP = 1;
	public static final int ACTION_MOVE = 2;
	public static final int ACTION_CANCEL = 3;
	public static final int ACTION_OUTSIDE = 4;
	public static final int ACTION_POINTER_DOWN = 5;
	public static final int ACTION_POINTER_UP = 6;
	public static final int ACTION_HOVER_MOVE = 7;
	public static final int ACTION_SCROLL = 8;
	public static final int ACTION_HOVER_ENTER = 9;
	public static final int ACTION_HOVER_EXIT = 10;
	
    public int action;
    public int actionIndex;
    public int buttonState;
    public int metaState;
    public int flags;
    public int edgeFlags;
    public int pointerCount;
    public int historySize;
    public long eventTime;
    public long downTime;
    public int deviceId;
    public int source;

    private ArrayList<HPoint> points;
    
    public HMotionEvent() {
        points = new ArrayList<>();
    }

    public float getX(int i) {
    	if (i >= points.size()) {
    		return 0;
    	}
    	
        return points.get(i).x;
    }
    public float getY(int i) {
    	if (i >= points.size()) {
    		return 0;
    	}
    	
        return points.get(i).y;
    }

    public int getToolType(int i) {
        return points.get(i).toolType;
    }

    public void setX(int index, float x) {
        points.get(index).x = x;
    }

    public void setY(int index, float y) {
        points.get(index).y = y;
    }

    public void setToolType(int index, int toolType) {
        points.get(index).toolType = toolType;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getActionIndex() {
        return actionIndex;
    }

    public void setActionIndex(int actionIndex) {
        this.actionIndex = actionIndex;
    }

    public int getButtonState() {
        return buttonState;
    }

    public void setButtonState(int buttonState) {
        this.buttonState = buttonState;
    }

    public int getMetaState() {
        return metaState;
    }

    public void setMetaState(int metaState) {
        this.metaState = metaState;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getEdgeFlags() {
        return edgeFlags;
    }

    public void setEdgeFlags(int edgeFlags) {
        this.edgeFlags = edgeFlags;
    }

    public int getPointerCount() {
        return pointerCount;
    }

    public void setPointerCount(int pointCount) {
        this.pointerCount = pointCount;
        while (points.size() > 0) {
            points.remove(0);
        }

        for (int i = 0; i < pointerCount; i++) {
            points.add(new HPoint());
        }
    }

    public int getHistorySize() {
        return historySize;
    }

    public void setHistorySize(int historySize) {
        this.historySize = historySize;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public long getDownTime() {
        return downTime;
    }

    public void setDownTime(long downTime) {
        this.downTime = downTime;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

	public ArrayList<HPoint> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<HPoint> points) {
		this.points = points;
	}
    
    
}
