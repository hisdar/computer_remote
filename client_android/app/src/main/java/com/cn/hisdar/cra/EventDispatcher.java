package com.cn.hisdar.cra;

import android.util.Log;

import com.cn.hisdar.cra.activity.CRAActivity;
import com.cn.hisdar.cra.common.Global;
import com.cn.hisdar.cra.server.BytesData;
import com.cn.hisdar.cra.server.HScreenPictureListener;
import com.cn.hisdar.cra.server.HScreenPiture;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class EventDispatcher {

	private static EventDispatcher eventDispatcher = null;

	private ArrayList<HResponseDataListener> responseDataListeners;
	private ArrayList<HServerEventListener> serverEventListeners;
	private ArrayList<HScreenPictureListener> screenPictureListeners;

	private static boolean isPrint = true;
	
	public EventDispatcher() {
		responseDataListeners = new ArrayList<HResponseDataListener>();
		serverEventListeners = new ArrayList<HServerEventListener>();
		screenPictureListeners = new ArrayList<HScreenPictureListener>();
	}
	
	public static EventDispatcher getInstance() {
		
		if (eventDispatcher == null) {
			synchronized (EventDispatcher.class) {
				if (eventDispatcher == null) {
					eventDispatcher = new EventDispatcher();
				}
			}
		}

		return eventDispatcher;
	}

	public void dispatch(HEventData eventData) {
		try {
			//Log.d(CRAActivity.TAG, "xml data is:\n" + eventData.eventData);
			parseMotionEvent(eventData);
		} catch (Exception e) {
			//Log.e(CRAActivity.TAG, "xml parse fail, data is:\n" + eventData.eventData);
			//byte[] byts = eventData.eventData.getBytes();
			//printByteData(byts, true);
		}
	}
		
	private void parseMotionEvent(HEventData eventData) throws ParserConfigurationException, SAXException, IOException {
		// 1. get event type
		DocumentBuilderFactory documentBuilderFactory  = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = dBuilder.parse(new InputSource(new ByteArrayInputStream(eventData.eventData.getBytes())));
		NodeList eventDataNodes = document.getChildNodes();

		if (eventDataNodes.getLength() < 1) {
			Log.e(CRAActivity.TAG, "xml is empty");
			return;
		}
		
		// 2.search node : computerRemote
		Node computerRemoteNode = getNodeFromNodeList(eventDataNodes, Global.XML_NODE_COMPUTER_REMOTE);
		if (computerRemoteNode == null) {
			Log.e(CRAActivity.TAG, "Node:XML_NODE_COMPUTER_REMOTE[" + Global.XML_NODE_COMPUTER_REMOTE + "] not found" );
			Log.i(CRAActivity.TAG, "data:" + eventData.eventData);
			return ;
		}
		
		// 3. search data type node and get value
		Node dataTypeNode = getChildNode(computerRemoteNode, Global.XML_NODE_DATA_TYPE);
		if (dataTypeNode == null) {
			Log.e(CRAActivity.TAG, "XML_NODE_DATA_TYPE[" + Global.XML_NODE_DATA_TYPE + "] not found \n" + eventData.eventData );
			return ;
		}
		
		String dataType = dataTypeNode.getTextContent();
		if (dataType == null) {
			Log.e(CRAActivity.TAG, "get data type fail, data type is null" );
			return ;
		}
		
		if (dataType.equals(Global.DATA_TYPE_RESPONSE_DATA)) {
			// parse response message event node and submit to other function to parse
			Node eventNode = getChildNode(computerRemoteNode, Global.XML_NODE_RESPONSE_DATA);
			HResponseData responseData = parseResponseDataEvent(eventNode);
			notifyHResponseDataEvent(responseData);
		} else if (dataType.equals(Global.DATA_TYPE_SERVER_EVENT)) {
			// parse server event node and submit to other function to parse
			Node eventNode = getChildNode(computerRemoteNode, Global.XML_NODE_SERVER_EVENT);
			HServerEvent serverEvent = parseServerEvent(eventNode);
			if (serverEvent == null) {
				Log.e(CRAActivity.TAG, "paser server event fail, data is:\n" + eventData.eventData );
			} else {
				notifyHServerEvent(serverEvent);
			}
		} else if (dataType.equals(Global.DATA_TYPE_SCREEN_PICTURE)) {
			// parse server event node and submit to other function to parse
			// Log.d(CRAActivity.TAG, "we have screen picture coming");
			Node eventNode = getChildNode(computerRemoteNode, Global.XML_NODE_BYTES_DATA_HEAD);
			BytesData bytesData = parseBytesDataHead(eventData, eventNode);
			if (bytesData == null) {
				Log.e(CRAActivity.TAG, "paser server event fail, data is:\n" + eventData.eventData );
			} else {
				notifyScreenPictureEvent(new HScreenPiture(bytesData));
			}
		} else {
			Log.i(CRAActivity.TAG, "unhandle event:\n" + eventData.eventData);
		}
	}
	
	private HResponseData parseResponseDataEvent(Node responseNode) {
		
		String sendTime = getChildNodeValue(responseNode, Global.XML_NODE_TIME_DATA);
		
		HResponseData responseData = new HResponseData();
		responseData.setSendTime(sendTime);
		responseData.readTime = System.currentTimeMillis();
		
		return responseData;
	}
	
	private HServerEvent parseServerEvent(Node responseNode) {
		
		HServerEvent serverEvent = new HServerEvent();
		serverEvent.event = responseNode.getTextContent().trim();
		if (serverEvent.event == null) {
			return null;
		}
		
		return serverEvent;
	}

	private BytesData parseBytesDataHead(HEventData eventData, Node bytesDataHeadNode) {

		BytesData bytesData = new BytesData();

		String dataSizeString = getChildNodeValue(bytesDataHeadNode, Global.XML_NODE_DATA_SIZE);
		if (dataSizeString == null) {
			return null;
		}

		bytesData.setDataSize(dataSizeString);

		byte[] screenPictureData = new byte[bytesData.getDataSize()];
		try {
			//eventData.bufferedReader.read(screenPictureData, 0, bytesData.getDataSize());
			eventData.serverSocket.getInputStream().read(screenPictureData, 0, bytesData.getDataSize());
		} catch (IOException e) {
			Log.e(CRAActivity.TAG, "read screen piture data fail");
			return null;
		}

		bytesData.setData(screenPictureData);
		return bytesData;
	}

	public void printByteData(byte[] data, boolean isPrint) {

		for (int i = 0; i < data.length / 8 && i < 50; i++) {
			for (int j = 0; j < 8; j++) {
				int index = 8 * i + j;
				System.out.printf("0x%02x ", data[index]);
			}

			System.out.println();
		}
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
	
	private String getChildNodeValue(Node parentNode, String xmlNodeName) {
		
		Node childNode = getChildNode(parentNode, xmlNodeName);
		if (childNode == null) {
			Log.e(CRAActivity.TAG, "get child node faile, child node name:" + xmlNodeName);
			return null;
		}
		
		return childNode.getTextContent();
	}
	
	public void addHResponseDataListener(HResponseDataListener l) {
		for (int i = 0; i < responseDataListeners.size(); i++) {
			if (responseDataListeners.get(i) == l) {
				return;
			}
		}
		
		responseDataListeners.add(l);
	}
	
	public void removeHResponseDataListener(HResponseDataListener l) {
		int size = responseDataListeners.size();
		for (int i = size - 1; i >= 0; i--) {
			if (l == responseDataListeners.get(i)) {
				responseDataListeners.remove(i);
			}
		}
	}
	
	private void notifyHResponseDataEvent(HResponseData responseData) {
		for (int i = 0; i < responseDataListeners.size(); i++) {
			responseDataListeners.get(i).responseDataEvent(responseData);
		}
	}
	
	public void addHServerEventListener(HServerEventListener l) {
		for (int i = 0; i < serverEventListeners.size(); i++) {
			if (l == serverEventListeners.get(i)) {
				return;
			}
		}
		
		serverEventListeners.add(l);
	}
	
	public void removeHServerEventListener(HServerEventListener l) {
		for (int i = serverEventListeners.size() - 1; i >= 0; i--) {
			if (serverEventListeners.get(i) == l) {
				serverEventListeners.remove(i);
			}
		}
	}
	
	private void notifyHServerEvent(HServerEvent serverEvent) {
		for (int i = 0; i < serverEventListeners.size(); i++) {
			serverEventListeners.get(i).serverEvent(serverEvent);
		}
	}


	public void addScreenPictureEventListener(HScreenPictureListener l) {
		for (int i = 0; i < screenPictureListeners.size(); i++) {
			if (screenPictureListeners.get(i) == l) {
				return;
			}
		}

		screenPictureListeners.add(l);
	}

	public void removeScreenPictureEventListener(HScreenPictureListener l) {
		for (int i = 0; i < screenPictureListeners.size(); i++) {
			if (screenPictureListeners.get(i) == l) {
				screenPictureListeners.remove(i);
				return ;
			}
		}
	}

	private void notifyScreenPictureEvent(HScreenPiture screenPiture) {
		for (int i = 0; i < screenPictureListeners.size(); i++) {
			screenPictureListeners.get(i).screenPictureEvent(screenPiture);
		}
	}
}
