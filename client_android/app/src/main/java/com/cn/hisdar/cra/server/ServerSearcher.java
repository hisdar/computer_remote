package com.cn.hisdar.cra.server;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.cn.hisdar.cra.activity.CRAActivity;
import com.cn.hisdar.cra.common.Global;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ServerSearcher {
	
	private ArrayList<ServerSearcherEventListener> listeners;
	private int gSserverThreadCount = 0;
	private boolean isStopSearch = false;
	
	public ServerSearcher() {
		listeners = new ArrayList<ServerSearcherEventListener>();
	}
	
	public void stopSearch() {
		isStopSearch = true;
	}
	
	public void startSearch(Context context) {
		Log.i(CRAActivity.TAG, "Start to search server.....");

		isStopSearch = false;
		gSserverThreadCount = 0;
		
		if (!isWifiConnected(context)) {
			notifyServerSearchMessage(new ServerSearcherMessage(ServerSearcherMessage.MESSAGE_WIFI_NOT_CONNECTED));
			Log.e(CRAActivity.TAG, "wifi is not connected");
			return;
		}
		
		int ipAddress = getIPAddress(context);
		int netMask = getNetMask(context);
		int netGate = getNetGate(context);

		int ipHead = ipAddress & netMask;
		int ipCount = ~netMask;
		int maxIP = ipHead + ipCount;
		
//		Log.i(CRAActivity.TAG, "ipAddress=" + getIPAddress(ipAddress));
//		Log.i(CRAActivity.TAG, "netMask=" + getIPAddress(netMask));
//		Log.i(CRAActivity.TAG, "netGate=" + getIPAddress(netGate));
//		Log.i(CRAActivity.TAG, "ipHead=" + getIPAddress(ipHead));
//		Log.i(CRAActivity.TAG, "ipCount=" + ipCount);

		int searchThreadCount = (getCpuCount() - 1) * 16;
		this.gSserverThreadCount = searchThreadCount;
		Log.i(CRAActivity.TAG, "searchThreadCount=" + searchThreadCount);
		
		ArrayList<ArrayList<Integer>> ipListArrayList = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < gSserverThreadCount; i++) {
			ipListArrayList.add(new ArrayList<Integer>());
		}

		int serchIp = 0;
		int searchedCount = 0;
		for (int i = 0; i < ipCount; i++) {
			if (i % 2 == 0) {
				if (ipAddress - (i / 2) <= ipHead) {
					break;
				}
				serchIp = ipAddress - (i / 2);
			} else {
				if (ipAddress + (i / 2) >= maxIP) {
					break;
				}
				serchIp = ipAddress + (i / 2);
			}
			
			searchedCount += 1;
			
			if (serchIp != maxIP && serchIp != netGate && serchIp != ipAddress) {
				Log.i(CRAActivity.TAG, "Hisdar ip=" + getIPAddress(serchIp));
				ipListArrayList.get(i % gSserverThreadCount).add(new Integer(serchIp));
			}
		}

		Log.i(CRAActivity.TAG, "searchedCount=" + searchedCount);
		Log.i(CRAActivity.TAG, "ipCount=" + ipCount);
		if (searchedCount < ipCount) {
			if (serchIp > ipAddress) {
				for (int i = serchIp + 1; i < maxIP; i++) {
					
					serchIp += 1;
					if (serchIp != maxIP && serchIp != netGate && serchIp != ipAddress) {
						Log.i(CRAActivity.TAG, "Hisdar ip=" + getIPAddress(serchIp));
						ipListArrayList.get(searchedCount % gSserverThreadCount).add(new Integer(serchIp));
					}
	
					searchedCount += 1;
				}
			} else {
				for (int i = serchIp - 1; i > ipHead; i--) {
					
					serchIp -= 1;
					if (serchIp != maxIP && serchIp != netGate && serchIp != ipAddress) {
						Log.i(CRAActivity.TAG, "Hisdar ip=" + getIPAddress(serchIp));
						ipListArrayList.get(searchedCount % gSserverThreadCount).add(new Integer(serchIp));
					}
	
					searchedCount += 1;
				}
			}

			for (int i = 0; i < ipListArrayList.size(); i++) {
				Log.i(CRAActivity.TAG, "Serch ip:" + ipListArrayList.get(i));
			}
			
			for (int i = 0; i < ipListArrayList.size(); i++) {
				new ServerSearchThread(ipListArrayList.get(i), context).start();
			}
		}
	}

	private int overTurn(int address) {
		int[] ipArrays = new int[4];
		for (int i = 0; i < ipArrays.length; i++) {
			ipArrays[i] = (address >> (i * 8)) & 0xFF;
		}
		
		int ipCount = 0;
		for (int i = 0; i < ipArrays.length; i++) {
			ipCount |= ipArrays[i] << ((ipArrays.length - i - 1) * 8);
		}
		
		return ipCount;
	}
	
	public boolean isWifiConnected(Context context) {
		ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return mWifi.isConnected();
	}
	
	public int getIPAddress(Context context) {
		
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
		return overTurn(ipAddress);
	}
	
	public int getNetGate(Context context) {
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		int netGate = wifiManager.getDhcpInfo().gateway;
		return overTurn(netGate);
	}
	
	public int getNetMask(Context context) {
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		int netmask = wifiManager.getDhcpInfo().netmask;
		if (netmask != 0) {
			return overTurn(netmask);
		} else {
			return getDefaultNetMask(context, getIPAddress(context));
		}
	}
	
	public int getDefaultNetMask(Context context, int ipAddress) {
		int netEndA = 127 << 24 | 255 << 16 | 255 << 8 | 255;
		int netEndB = 191 << 24 | 255 << 16 | 255 << 8 | 255;
		int netEndC = 223 << 24 | 255 << 16 | 255 << 8 | 255;
		
		if (ipAddress >= 0 && ipAddress <= netEndA) {
			return 255 << 24 | 0 | 0 | 0;
		} else if (ipAddress <= netEndB) {
			return 255 << 24 | 255 << 16 | 0 | 0;
		} else if (ipAddress <= netEndC) {
			return 255 << 24 | 255 << 16 | 255 << 8 | 0;
		} else {
			return 255 << 24 | 255 << 16 | 255 << 8 | 0;
		}
	}

	public int getCpuCount() {
		return 4;
	}
	
	public String getIPAddress(int ipaddress) {
		 return ((ipaddress >> 24) & 0xFF) + "." +       
		        ((ipaddress >> 16) & 0xFF) + "." +       
		        ((ipaddress >> 8)  & 0xFF) + "." +       
		        ((ipaddress >> 0)  & 0xFF) ;
	}
	
	private class ServerSearchThread extends Thread {
		
		private static final int DEFAULT_PORT = 5299;
		
		private ArrayList<Integer> ipAddressList;
		private Context context;
		
		public ServerSearchThread(ArrayList<Integer> ipAddressList, Context context) {
			this.ipAddressList = new ArrayList<Integer>();
			this.ipAddressList.addAll(ipAddressList);
			this.context = context;
		}
		
		public void run() {
			for (int i = 0; i < ipAddressList.size(); i++) {
				Log.i(CRAActivity.TAG, "Search:" + getIPAddress(ipAddressList.get(i)));
				int localIPAddress = getIPAddress(context);
				int netMask = getNetMask(context);
				int id = ipAddressList.get(i) - (netMask & localIPAddress);
				ServerInformation serverInfor = connecteAndGetServerInfor(getIPAddress(ipAddressList.get(i)), id);

				if (isStopSearch) {
					return;
				}
				
				if (serverInfor != null) {
					
					notifyServerSearchEvent(serverInfor);
				}
			}
			
			Log.i(CRAActivity.TAG, "Thread finished:");
			notifyServerSearchFinishedMessage();
		}
		
		private ServerInformation connecteAndGetServerInfor(String ipAddress, int id) {
			Socket socket = null;
			ServerInformation serverInformation = null;

			try {
				socket = new Socket(ipAddress, DEFAULT_PORT);
				serverInformation = getServerInformation(socket);
				serverInformation.setId(id);
				socket.close();
			} catch (IOException | SAXException | ParserConfigurationException e1 ) {
				Log.e(CRAActivity.TAG, e1.getMessage());
			}
			
			return serverInformation;
		}
		
		private ServerInformation getServerInformation(Socket socket) throws ParserConfigurationException, SAXException, IOException {
			ServerInformation serverInformation = new ServerInformation();
			serverInformation.setIpAddress(socket.getInetAddress().getHostAddress());
			serverInformation.setServerName(socket.getInetAddress().getHostName());
			serverInformation.setPort(DEFAULT_PORT + "");
			
			//Log.i(CRAActivity.TAG, "Start read server information");
			StringBuffer serverInforStringBuffer = readServerInformation(socket);
			//Log.i(CRAActivity.TAG, "End read server information:\n" + serverInforStringBuffer);
			
			DocumentBuilderFactory documentBuilderFactory  = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = dBuilder.parse(new InputSource(new ByteArrayInputStream(serverInforStringBuffer.toString().getBytes())));
			NodeList eventDataNodes = document.getChildNodes();
			
			String serverNameString = getNodeValue(eventDataNodes, Global.SERVER_INFO_SERVER_NAME);
			if (serverNameString != null) {
				serverInformation.setServerName(serverNameString);
			}
			
			return serverInformation;
		}
		
		private String getNodeValue(NodeList nodes, String nodeName) {
			
			for (int i = 0; i < nodes.getLength(); i++) {
				String nodeValueString = getNodeValue(nodes.item(i), nodeName);
				if (nodeValueString != null) {
					return nodeValueString;
				}
			}
			
			return null;
		}
		
		private String getNodeValue(Node node, String nodeName) {
			
			//Log.i(CRAActivity.TAG, "--node name:" + nodeName + ", item name:" + node.getNodeName() + ", item value:" + node.getTextContent());
			if (node.getNodeName().equals(nodeName)) {
				return node.getTextContent();
			} else if (node.getChildNodes().getLength() > 0) {
				return getNodeValue(node.getChildNodes(), nodeName);
			} else {
				return null;
			}
		}
		
		private StringBuffer readServerInformation(Socket socket) {
			
			StringBuffer serverStringBuffer = new StringBuffer();
			try {
				InputStream inputStream = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String lineString = reader.readLine();
				while (lineString != null && !lineString.trim().equals(Global.DATA_END_FLAG)) {
					//Log.i(CRAActivity.TAG, "message:" + lineString);
					if (lineString.trim().equals(Global.DATA_BEGIN_FLAG)) {
						// do nothing
					} else {
						serverStringBuffer.append(lineString);
						serverStringBuffer.append("\n");
					}
					
					lineString = reader.readLine();
				}
				
			} catch (IOException e) {
				Log.i(CRAActivity.TAG, e.getMessage());
			}
			
			return serverStringBuffer;
		}
		
	}
	
	public void addServerSearcherListener(ServerSearcherEventListener l) {
		for (int i = 0; i < listeners.size(); i++) {
			if (listeners.get(i) == l) {
				return;
			}
		}
		
		listeners.add(l);
	}
	
	private void notifyServerSearchEvent(ServerInformation serverInformation) {
		Log.e(CRAActivity.TAG, "Found server:" + serverInformation.toString());
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).newServerFoundEvent(serverInformation);
		}
	}
	
	private void notifyServerSearchMessage(ServerSearcherMessage message) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).serverSercherMessageEvent(message);
		}
	}
	
	private synchronized void notifyServerSearchFinishedMessage() {
		if (gSserverThreadCount > 0) {
			gSserverThreadCount -= 1;
		}

		if (gSserverThreadCount == 0) {
			Log.i(CRAActivity.TAG, "Search finished!!!");
			for (int i = 0; i < listeners.size(); i++) {
				ServerSearcherMessage message = new ServerSearcherMessage(ServerSearcherMessage.SEARCH_FINISHED);
				listeners.get(i).serverSercherMessageEvent(message);
			}
		}
	}
	
	public void removeServerSearcherListener(ServerSearcherEventListener l) {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			if (listeners.get(i) == l) {
				listeners.remove(i);
			}
		}
	}
}
