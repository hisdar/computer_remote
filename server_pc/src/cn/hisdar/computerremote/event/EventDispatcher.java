package cn.hisdar.computerremote.event;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

import javax.print.attribute.standard.RequestingUserName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cn.hisdar.computerremote.common.Global;
import cn.hisdar.computerremote.server.CRClient;
import cn.hisdar.computerremote.server.CRServer;
import cn.hisdar.computerremote.server.ScreenSizeEventListener;
import cn.hisdar.lib.adapter.IntegerAdapter;
import cn.hisdar.lib.log.HLog;

public class EventDispatcher {

	private static EventDispatcher motionEventDispatcher = null;
	private static boolean isStop = false;
	
	private static ArrayList<HMotionEventListener> motionEventListeners = null;
	private static ArrayList<HMouseEventListener> mouseEventListeners = null;
	private static ArrayList<HKeyEventListener> keyEventListeners = null;
	private static ArrayList<ScreenSizeEventListener> screenSizeEventListeners = null;
	
	private static Point screenSize = null;
	
	private MotionEventDispatchThread dispatchThread = null;
	private ArrayList<HEventData> motionEventWaitArray = null;
	
	
	private EventDispatcher() {
		motionEventWaitArray = new ArrayList<>();
		dispatchThread = new MotionEventDispatchThread();
		dispatchThread.start();
		
		synchronized (EventDispatcher.class) {
			isStop = false;
		}
	}
	
	public static EventDispatcher getInstance() {
		if (motionEventDispatcher == null) {
			synchronized (EventDispatcher.class) {
				if (motionEventDispatcher == null) {
					motionEventDispatcher = new EventDispatcher();
					
					motionEventListeners = new ArrayList<>();
					mouseEventListeners = new ArrayList<>();
					keyEventListeners = new ArrayList<>();
					screenSizeEventListeners = new ArrayList<>();
				}
			}
		}
		
		return motionEventDispatcher;
	}
	
	public static void distroy() {
		synchronized (EventDispatcher.class) {
			isStop = true;
		}
	}
	
	public void dispatch(HEventData eventData) {
		//HLog.il(motionEventData);
		synchronized (EventDispatcher.class) {
			motionEventWaitArray.add(eventData);
		}

//		synchronized (EventDispatcher.class) {
//			if (!isHandleEvent) {
//				//dispatchThread.interrupt();
//			}
//		}
	}
	
	private class MotionEventDispatchThread extends Thread {
		public void run() {
			while (!isStop) {
				
				HEventData eventData = null;
				if (motionEventWaitArray.size() <= 0) {
//					synchronized (EventDispatcher.class) {
//						isHandleEvent = false;
//					}

					try {
						//sleep(10000);
						sleep(1);
					} catch (InterruptedException e) {}
					
//					synchronized (EventDispatcher.class) {
//						isHandleEvent = true;
//					}
					
					continue;
				}
				
				synchronized (EventDispatcher.class) {
					eventData = new HEventData(motionEventWaitArray.get(0));
					motionEventWaitArray.remove(0);
				}
				
				try {
					if (eventData != null) {
						eventData.timeDataContainer.addTimeData("start parse event", new Date().getTime());
						parseMotionEvent(eventData);
						eventData.timeDataContainer.addTimeData("finish parse event", new Date().getTime());
					}
				} catch (ParserConfigurationException | SAXException | IOException e) {
					HLog.el("Parse motion event data fail, data is:" + eventData.eventData);
					HLog.el(e);
				}
			}
		}
	}
	
	public void parseMotionEvent(HEventData eventData) throws ParserConfigurationException, SAXException, IOException {
		// 1. get event type
		DocumentBuilderFactory documentBuilderFactory  = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = dBuilder.parse(new InputSource(new ByteArrayInputStream(eventData.eventData.getBytes())));
		NodeList eventDataNodes = document.getChildNodes();

		if (eventDataNodes.getLength() < 1) {
			HLog.el("xml is empty");
			return;
		}
		
		// 2.search node : computerRemote
		Node computerRemoteNode = getNodeFromNodeList(eventDataNodes, Global.XML_NODE_COMPUTER_REMOTE);		
		if (computerRemoteNode == null) {
			HLog.el("Node:XML_NODE_COMPUTER_REMOTE[" + Global.XML_NODE_COMPUTER_REMOTE + "] not found" );
			return ;
		}
		
		// 3. search data type node and get value
		Node dataTypeNode = getChildNode(computerRemoteNode, Global.XML_NODE_DATA_TYPE);
		if (dataTypeNode == null) {
			HLog.el("XML_NODE_DATA_TYPE[" + Global.XML_NODE_DATA_TYPE + "] not found" );
			return ;
		}
		
		String dataType = dataTypeNode.getTextContent();
		if (dataType == null) {
			HLog.el("get data type fail, data type is null" );
			return ;
		}
		
		// handle event
		if (dataType.equals(Global.XML_NODE_MOTION_EVENT)) {
			// parse motion event node and submit to other function to parse
			Node motionEventNode = getChildNode(computerRemoteNode, Global.XML_NODE_MOTION_EVENT);
			HMotionEvent motionEvent = parseMotionEvent(motionEventNode);
			notifyHMotionEvent(motionEvent);
			
			// add other event here
		} else if (dataType.equals(Global.XML_NODE_MOUSE_BUTTON_EVENT)) {
			Node mouseEventNode = getChildNode(computerRemoteNode, Global.XML_NODE_MOUSE_BUTTON_EVENT);
			HMouseEvent mouseEvent = parseMouseEvent(mouseEventNode);
			notifyHMouseEvent(mouseEvent);
		} else if (dataType.equals(Global.DATA_TYPE_SCREEN_SIZE)) {
			Node screenSizeNode = getChildNode(computerRemoteNode, Global.DATA_TYPE_SCREEN_SIZE);
			Point screenSize = parseScreenSize(screenSizeNode);
			notifyScreenSizeEvent(screenSize);
		} else if (dataType.equals(Global.XML_NODE_KEY_BUTTON_EVENT)) {
			Node keyEventNode = getChildNode(computerRemoteNode, Global.XML_NODE_KEY_BUTTON_EVENT);
			HKeyEvent mouseEvent = parseKeyEvent(keyEventNode);
			notifyHKeyEvent(mouseEvent);
		} else {			
			HLog.el("unhandled event data, data type:" + dataType );
		}
		
		// response event handled message to phone 
		eventData.timeDataContainer.addTimeData("finish handle event", new Date().getTime());
		responseEventHandledMessage(computerRemoteNode, eventData);
		
	}
	
	private void responseEventHandledMessage(Node computerRemoteNode, HEventData eventData) {
		String timeData = getChildNodeValue(computerRemoteNode, Global.XML_NODE_TIME_DATA);
		String timeDataXml = String.format(Global.TIME_DATA_FORMAT, timeData);
		String responseDataXml = String.format(Global.RESPONSE_DATA_FORMAT, timeDataXml);
		
		String serverTimeDataXml = eventData.timeDataContainer.toXmlString();
		responseDataXml += serverTimeDataXml;
		
		eventData.client.sendResponseData(responseDataXml);
	}
	
	private Point parseScreenSize(Node screenSizeNode) {
		Point screenSize = new Point();
		
		String screenWidthString = getChildNodeValue(screenSizeNode, Global.SCREEN_SIZE_WIDTH);
		String screenHeightString = getChildNodeValue(screenSizeNode, Global.SCREEN_SIZE_HEIGHT);
		
		screenSize.x = IntegerAdapter.parseInt(screenWidthString, -1);
		screenSize.y = IntegerAdapter.parseInt(screenHeightString, -1);
		
		return screenSize;
	}

	private HMouseEvent parseMouseEvent(Node mouseEventNode) {
		HMouseEvent mouseEvent = new HMouseEvent();
		
		String buttonId = getChildNodeValue(mouseEventNode, Global.XML_NODE_BUTTON_ID);
		mouseEvent.setButtonId(buttonId);
		
		String value = getChildNodeValue(mouseEventNode, Global.XML_NODE_BUTTON_VALUE);
		mouseEvent.setValue(value);
		
		return mouseEvent;
	}
	
	private HKeyEvent parseKeyEvent(Node keyEventNode) {
		HKeyEvent mouseEvent = new HKeyEvent();
		
		String buttonId = getChildNodeValue(keyEventNode, Global.XML_NODE_BUTTON_ID);
		mouseEvent.setKeyCode(buttonId);
		
		String value = getChildNodeValue(keyEventNode, Global.XML_NODE_BUTTON_VALUE);
		mouseEvent.setKeyValue(value);
		
		return mouseEvent;
	}

	private HMotionEvent parseMotionEvent(Node motionEventNode) {
		
		HMotionEvent motionEvent = new HMotionEvent();
		
		// get action
		String action = getChildNodeValue(motionEventNode, Global.XML_NODE_ACTION);
		motionEvent.setAction(action);
		
		// get action button
		String actionButton = getChildNodeValue(motionEventNode, Global.XML_NODE_ACTION_BUTTON);
		motionEvent.setActionButton(actionButton);
		
		// get button state
		String buttonState = getChildNodeValue(motionEventNode, Global.XML_NODE_BUTTON_STATE);
		motionEvent.setButtonState(buttonState);
		
		// get meta state
		String metaState = getChildNodeValue(motionEventNode, Global.XML_NODE_META_STATE);
		motionEvent.setMetaState(metaState);
		
		// get flags
		String flags = getChildNodeValue(motionEventNode, Global.XML_NODE_FLAGS);
		motionEvent.setFlags(flags);
		
		// get edge Flags
		String edgeFlags = getChildNodeValue(motionEventNode, Global.XML_NODE_EDGE_FLAGS);
		motionEvent.setEdgeFlags(edgeFlags);
		
		// get pointer Count
		String pointerCount = getChildNodeValue(motionEventNode, Global.XML_NODE_POINTER_COUNT);
		motionEvent.setPpointerCount(pointerCount);
		
		// get history Size
		String historySize = getChildNodeValue(motionEventNode, Global.XML_NODE_HISTORY_SIZE);
		motionEvent.setHistorySize(historySize);
		
		// get event Time
		String eventTime = getChildNodeValue(motionEventNode, Global.XML_NODE_EVENT_TIME);
		motionEvent.setEventTime(eventTime);
		
		// get down Time
		String downTime = getChildNodeValue(motionEventNode, Global.XML_NODE_DOWN_TIME);
		motionEvent.setDownTime(downTime);
		
		// get device Id
		String deviceId = getChildNodeValue(motionEventNode, Global.XML_NODE_DEVICE_ID);
		motionEvent.setDeviceId(deviceId);
		
		// get source
		String source = getChildNodeValue(motionEventNode, Global.XML_NODE_SOURCE);
		motionEvent.setSource(source);
		
		// parse pointers
		Node pointersNode = getChildNode(motionEventNode, Global.XML_NODE_POINTERS);
		parseMotionEventPointers(pointersNode, motionEvent);
		//HLog.dl("finish motion event dispatch");
		return motionEvent;
	}
	
	private void parseMotionEventPointers(Node pointersNode, HMotionEvent motionEvent) {
		//HLog.dl("EventDispatcher.parseMotionEventPointers: node name=" + pointersNode.getNodeName());

		NodeList pointerNodes = pointersNode.getChildNodes();
		for (int i = 0; i < pointerNodes.getLength(); i++) {
			//HLog.dl("EventDispatcher.parseMotionEventPointers: child node name=" + pointerNodes.item(i).getNodeName());
			if (pointerNodes.item(i).getNodeName().equals(Global.XML_NODE_POINTER)) {
				Pointer pointer = parseMotionEventPointer(pointerNodes.item(i));
				motionEvent.addPointer(pointer);
			}
		}
	}
	
	private Pointer parseMotionEventPointer(Node pointerNode) {
		
		//HLog.dl("EventDispatcher.parseMotionEventPointer:pointerNode name=" + pointerNode.getNodeName());
		
		String x = getChildNodeValue(pointerNode, Global.XML_POINTER_X);
		String y = getChildNodeValue(pointerNode, Global.XML_POINTER_Y);
		String id = getChildNodeValue(pointerNode, Global.XML_POINTER_ID);
		String toolType = getChildNodeValue(pointerNode, Global.XML_POINTER_TOOL_TYPE);
		
		Pointer pointer = new Pointer();
		pointer.setId(id);
		pointer.setX(x);
		pointer.setY(y);
		pointer.setToolType(toolType);
		
		return pointer;
	}

	private String getChildNodeValue(Node parentNode, String xmlNodeName) {
		
		Node childNode = getChildNode(parentNode, xmlNodeName);
		if (childNode == null) {
			HLog.el("get child node faile, child node name:" + xmlNodeName);
			return null;
		}
		
		return childNode.getTextContent();
	}

	private Node getNodeFromNodeList(NodeList nodeList, String nodeName) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeName().equals(nodeName)) {
				return nodeList.item(i);
			}
		}
		
		return null;
	}
	
	private Node getChildNode(Node parentNode, String childNodeName) {
		NodeList childNodes = parentNode.getChildNodes();
		
		return getNodeFromNodeList(childNodes, childNodeName);
	}
	
	public void addHMotionEventListener(HMotionEventListener listener) {
		for (int i = 0; i < motionEventListeners.size(); i++) {
			if (motionEventListeners.get(i) == listener) {
				return;
			}
		}
		
		motionEventListeners.add(listener);
	}
	
	public void removeHMotionEventListener(HMotionEventListener listener) {
		for (int i = 0; i < motionEventListeners.size(); i++) {
			if (motionEventListeners.get(i) == listener) {
				motionEventListeners.remove(i);
				return;
			}
		}
	}
	
	public void notifyHMotionEvent(HMotionEvent event) {
		for (int i = 0; i < motionEventListeners.size(); i++) {
			motionEventListeners.get(i).motionEvent(event);
		}
	}
	
	public void addHMouseEventListener(HMouseEventListener listener) {
		for (int i = 0; i < mouseEventListeners.size(); i++) {
			if (mouseEventListeners.get(i) == listener) {
				return;
			}
		}
		
		mouseEventListeners.add(listener);
	}
	
	public void removeHMouseEventListener(HMouseEventListener listener) {
		for (int i = 0; i < mouseEventListeners.size(); i++) {
			if (mouseEventListeners.get(i) == listener) {
				mouseEventListeners.remove(i);
				return;
			}
		}
	}
	
	private void notifyHMouseEvent(HMouseEvent mouseEvent) {
		for (int i = 0; i < mouseEventListeners.size(); i++) {
			mouseEventListeners.get(i).mouseEvent(mouseEvent);
		}
	}
	
	public void addHKeyEventListener(HKeyEventListener listener) {
		for (int i = 0; i < keyEventListeners.size(); i++) {
			if (keyEventListeners.get(i) == listener) {
				return;
			}
		}
		
		keyEventListeners.add(listener);
	}
	
	public void removeHKeyEventListener(HKeyEventListener listener) {
		for (int i = 0; i < keyEventListeners.size(); i++) {
			if (keyEventListeners.get(i) == listener) {
				keyEventListeners.remove(i);
				return;
			}
		}
	}
	
	private void notifyHKeyEvent(HKeyEvent mouseEvent) {
		for (int i = 0; i < mouseEventListeners.size(); i++) {
			keyEventListeners.get(i).keyEvent(mouseEvent);
		}
	}
	
	public void addScreenSizeEventListener(ScreenSizeEventListener listener) {
		for (int i = 0; i < screenSizeEventListeners.size(); i++) {
			if (screenSizeEventListeners.get(i) == listener) {
				return;
			}
		}
		
		screenSizeEventListeners.add(listener);
		listener.screenSizeEvent(screenSize);
	}
	
	public void removeScreenSizeEventListener(ScreenSizeEventListener listener) {
		for (int i = 0; i < screenSizeEventListeners.size(); i++) {
			if (screenSizeEventListeners.get(i) == listener) {
				screenSizeEventListeners.remove(i);
				return;
			}
		}
	}
	
	private void notifyScreenSizeEvent(Point screenSize) {
		EventDispatcher.screenSize = screenSize;
		for (int i = 0; i < screenSizeEventListeners.size(); i++) {
			screenSizeEventListeners.get(i).screenSizeEvent(screenSize);
		}
	}

}
