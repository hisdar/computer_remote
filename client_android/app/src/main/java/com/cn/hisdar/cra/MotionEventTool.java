package com.cn.hisdar.cra;

import android.annotation.SuppressLint;
import android.view.MotionEvent;

import com.cn.hisdar.cra.common.Global;

@SuppressLint("NewApi") public class MotionEventTool {

	private static final String POINT_DATA_FORMAT = "\n" 		+
			"			<pointer>\n" 								+
			"				<id>%d</id>\n" 						+
			"				<x>%f</x>\n" 						+
			"				<y>%f</y>\n" 						+
			"				<toolType>%s</toolType>\n" 			+
			"			</pointer>\n";
	
	private static final String MOTION_EVENT_XML_FORMAT = "" +
			"	<motionEvent>\n" +
			"		<action>%s</action>\n" 				+
			"		<actionButton>%s</actionButton>\n" 	+
			"		<buttonState>%s</buttonState>\n" 	+
			"		<metaState>%s</metaState>\n" 		+
			"		<flags>%s</flags>\n" 				+
			"		<edgeFlags>%s</edgeFlags>\n" 		+
			"		<pointerCount>%s</pointerCount>\n" 	+
			"		<historySize>%s</historySize>\n" 	+
			"		<eventTime>%s</eventTime>\n" 		+
			"		<downTime>%s</downTime>\n" 			+
			"		<deviceId>%s</deviceId>\n" 			+
			"		<source>%s</source>\n" 				+
			"		<pointers>%s" 						+
			"		</pointers>\n" 						+
			"	</motionEvent>\n" 						+
			"";
	
	private static final String MOUSE_EVENT_XML_FORMAT = "" +
			"	<mouseEvent>\n" +
			"		<buttonId>%s</buttonId>\n" 			+
			"		<value>%s</value>\n" 				+
			"	</mouseEvent>\n";
	
	private static final String KEY_EVENT_XML_FORMAT = "" +
			"	<keyEvent>\n" +
			"		<buttonId>%s</buttonId>\n" 			+
			"		<value>%s</value>\n" 				+
			"	</keyEvent>\n";
	
	public static String toXmlString(MotionEvent event) {
		
		String pointersData = "";
		for (int i = 0; i < event.getPointerCount(); i++) {
			pointersData += String.format(POINT_DATA_FORMAT,
					i, 
					event.getX(i), 
					event.getY(i), 
					event.getToolType(i));
		}
		
		String eventXmlData = "";
		eventXmlData = String.format(MOTION_EVENT_XML_FORMAT,
				event.getAction()			+ "",
				event.getActionIndex()		+ "",
				event.getButtonState()		+ "",
				event.getMetaState()		+ "",
				event.getFlags() 			+ "",
				event.getEdgeFlags() 		+ "",
				event.getPointerCount() 	+ "",
				event.getHistorySize() 		+ "",
				event.getEventTime() 		+ "",
				event.getDownTime() 		+ "",
				event.getDeviceId() 		+ "",
				event.getSource() 			+ "",
				pointersData);

		String dataType = String.format(Global.DATA_TYPE_FORMAT, Global.XML_NODE_MOTION_EVENT);
		String dataPackage = dataType + eventXmlData;
		
		return toXmlString(dataPackage);
	}
	
	public static String toMouseButtonActionXmlString(int buttonId, int value) {
		
		String dataType = String.format(Global.DATA_TYPE_FORMAT, Global.XML_NODE_MOUSE_BUTTON_EVENT);
		String mouseEventData = String.format(MOUSE_EVENT_XML_FORMAT, buttonId + "", value + "");
		String dataPackage = dataType + mouseEventData;
		
		return toXmlString(dataPackage);
	}
	
	public static String toKeyButtonActionXmlString(int buttonId, int value) {
		
		String dataType = String.format(Global.DATA_TYPE_FORMAT, Global.XML_NODE_KEY_BUTTON_EVENT);
		String mouseEventData = String.format(KEY_EVENT_XML_FORMAT, buttonId + "", value + "");
		String dataPackage = dataType + mouseEventData;
		
		return toXmlString(dataPackage);
	}
	
	public static String toScreenSizeXmlString(int width, int height) {
		String dataType = String.format(Global.DATA_TYPE_FORMAT, Global.DATA_TYPE_SCREEN_SIZE);
		String screenSizeData = String.format(Global.SCREEN_SIZE_XML_FORMAT, width, height);
		
		String dataPackage = dataType + screenSizeData;
		
		return toXmlString(dataPackage);		
	}
	
	public static String toXmlString(String data) {
		String currateTime = Long.valueOf(System.currentTimeMillis()).toString();
		
		String timeData = String.format(Global.TIME_DATA_FORMAT, currateTime);
		String xmlFileData = String.format(Global.CONTROL_DATA_LABEL_2, data, timeData);
		return Global.XML_FILE_HEAD + xmlFileData;
	}
}
