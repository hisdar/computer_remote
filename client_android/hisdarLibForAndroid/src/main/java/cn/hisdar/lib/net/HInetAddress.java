package cn.hisdar.lib.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class HInetAddress {

	public static String getInetAddress() {
		String[] ipAddresses = getInetAddresses();
		
		if (ipAddresses == null) {
			return null;
		}
		
		return ipAddresses[0];
	}
	
	public static String[] getInetAddresses() {
		
		ArrayList<String> hostAddressList = new ArrayList<>();
		
		try {
			String hostName = Inet4Address.getLocalHost().getHostName();
			InetAddress[] hostAddresses = Inet4Address.getAllByName(hostName);
			for (int i = 0; i < hostAddresses.length; i++) {
				System.out.println("Hisdar:" + hostAddresses[i].getHostAddress());
				if (isGateWay(hostAddresses[i].getHostAddress())) {
					continue;
				}
				
				if (!isIPV4Address(hostAddresses[i].getHostAddress())) {
					continue;
				}
				
				hostAddressList.add(hostAddresses[i].getHostAddress());
			}
			
			int len = hostAddressList.size();
			String[] hostAddressStrings = new String[len];
			for (int i = 0; i < hostAddressStrings.length; i++) {
				hostAddressStrings[i] = hostAddressList.get(i);
			}
			
			return hostAddressStrings;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static boolean isGateWay(String ipAddress) {
		
		String[] address = ipAddress.split("[.]");
		
		if (address == null) {
			return false;
		}
		
		if (address[address.length - 1].equals("1")) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isIPV4Address(String ipAddress) {
		
		String[] address = ipAddress.split("[.]");
		if (address == null || address.length != 4) {
			return false;
		}
		
		return true;
	}
	
	public void getPhyNetworkInterfaceIp() {
		Enumeration<NetworkInterface> en = null;
		try {
			en = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
	    Enumeration<InetAddress> addresses;
	    while (en.hasMoreElements()) {
	        NetworkInterface networkinterface = en.nextElement();
	        System.out.println(networkinterface.getName());
	        addresses = networkinterface.getInetAddresses();
	        while (addresses.hasMoreElements()) {
	            System.out.println("\t" + addresses.nextElement().getHostAddress() + "");
	        }
	    }
	}
}
