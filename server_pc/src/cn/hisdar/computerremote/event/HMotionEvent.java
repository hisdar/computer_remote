package cn.hisdar.computerremote.event;

import java.util.ArrayList;

import cn.hisdar.lib.adapter.IntegerAdapter;
import cn.hisdar.lib.adapter.LongAdapter;

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
	public int actionButton;
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
	
	public ArrayList<Pointer> pointers;
	
	public HMotionEvent() {
		initData();
	}
	
	public HMotionEvent(HMotionEvent srcEvent) {
		initData();
		
		this.action = srcEvent.action;
		this.actionButton = srcEvent.actionButton;
		this.buttonState = srcEvent.buttonState;
		this.metaState = srcEvent.metaState;
		this.flags = srcEvent.flags;
		this.edgeFlags = srcEvent.edgeFlags;
		this.pointerCount = srcEvent.pointerCount;
		this.historySize = srcEvent.historySize;
		this.eventTime = srcEvent.eventTime;
		this.downTime = srcEvent.downTime;
		this.deviceId = srcEvent.deviceId;
		this.source = srcEvent.source;
		
		for (int i = 0; i < srcEvent.getPointers().size(); i++) {
			Pointer pointer = new Pointer(srcEvent.getPointers().get(i));
			this.pointers.add(pointer);
		}
	}
	
	private void initData() {
		
		pointers = new ArrayList<>();
	}

	public void setAction(String actionValue) {
		action = IntegerAdapter.parseInt(actionValue, -1);
	}

	public void setActionButton(String actionButton) {
		this.actionButton = IntegerAdapter.parseInt(actionButton, -1);
	}

	public void setButtonState(String buttonState) {
		this.buttonState = IntegerAdapter.parseInt(buttonState, -1);
	}

	public void setMetaState(String metaState) {
		this.metaState = IntegerAdapter.parseInt(metaState, -1);
	}

	public void setFlags(String flags2) {
		this.flags = IntegerAdapter.parseInt(flags2, 0);
	}

	public void setEdgeFlags(String edgeFlags) {
		this.edgeFlags = IntegerAdapter.parseInt(edgeFlags, 0);
	}

	public void setPpointerCount(String pointerCount2) {
		this.pointerCount = IntegerAdapter.parseInt(pointerCount2, 0);
	}

	public void setHistorySize(String historySize2) {
		this.historySize = IntegerAdapter.parseInt(historySize2, 0);
	}

	public void setEventTime(String eventTime2) {
		this.eventTime = LongAdapter.parseLong(eventTime2, -1);
	}

	public void setDownTime(String downTime2) {
		this.downTime = LongAdapter.parseLong(downTime2, -1);
	}

	public void setDeviceId(String deviceId2) {
		this.deviceId = IntegerAdapter.parseInt(deviceId2, -1);
	}

	public void setSource(String source2) {
		this.source = IntegerAdapter.parseInt(source2, 0);
	}

	public void addPointer(Pointer pointer) {
		pointers.add(pointer);
	}

	public int getActionButton() {
		return actionButton;
	}

	public void setActionButton(int actionButton) {
		this.actionButton = actionButton;
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

	public void setPointerCount(int pointerCount) {
		this.pointerCount = pointerCount;
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

	public ArrayList<Pointer> getPointers() {
		return pointers;
	}

	public void setPointers(ArrayList<Pointer> pointers) {
		this.pointers = pointers;
	}

	public int getAction() {
		return action;
	}
	
	public float getX(int index) {
		if (pointers.size() - 1 <= index) {
			pointers.get(index).getX();
		}
		
		return -1;
	}
	
	public float getY(int index) {
		if (pointers.size() - 1 <= index) {
			pointers.get(index).getY();
		}
		
		return -1;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + action;
		result = prime * result + actionButton;
		result = prime * result + buttonState;
		result = prime * result + deviceId;
		result = prime * result + (int) (downTime ^ (downTime >>> 32));
		result = prime * result + edgeFlags;
		result = prime * result + (int) (eventTime ^ (eventTime >>> 32));
		result = prime * result + flags;
		result = prime * result + historySize;
		result = prime * result + metaState;
		result = prime * result + pointerCount;
		result = prime * result + ((pointers == null) ? 0 : pointers.hashCode());
		result = prime * result + source;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HMotionEvent other = (HMotionEvent) obj;
		if (action != other.action)
			return false;
		if (actionButton != other.actionButton)
			return false;
		if (buttonState != other.buttonState)
			return false;
		if (deviceId != other.deviceId)
			return false;
		if (downTime != other.downTime)
			return false;
		if (edgeFlags != other.edgeFlags)
			return false;
		if (eventTime != other.eventTime)
			return false;
		if (flags != other.flags)
			return false;
		if (historySize != other.historySize)
			return false;
		if (metaState != other.metaState)
			return false;
		if (pointerCount != other.pointerCount)
			return false;
		if (pointers == null) {
			if (other.pointers != null)
				return false;
		} else if (!pointers.equals(other.pointers))
			return false;
		if (source != other.source)
			return false;
		return true;
	}

	public String pointersToString() {
		String pointersString = "";
		for (int i = 0; i < pointers.size(); i++) {
			pointersString += "pointer[" + i + "] = " + pointers.get(i).toString() + "\n";
		}
		
		return pointersString;
	}

	@Override
	public String toString() {
		return "HMotionEvent [action=" + action + ", actionButton=" + actionButton + ", buttonState=" + buttonState
				+ ", metaState=" + metaState + ", flags=" + flags + ", edgeFlags=" + edgeFlags + ", pointerCount="
				+ pointerCount + ", historySize=" + historySize + ", eventTime=" + eventTime + ", downTime=" + downTime
				+ ", deviceId=" + deviceId + ", source=" + source + ", pointers=" + pointersToString() + "]";
	}
}
