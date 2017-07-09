package com.cn.hisdar.cra.server;

public class ServerInformation {

	private String serverName;
	private String ipAddress;
	private String port;
	private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ServerInformation() {
		
	}
	
	public ServerInformation(String serverName, String ipAddress, String port) {
		this.serverName = serverName;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result
				+ ((serverName == null) ? 0 : serverName.hashCode());
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
		ServerInformation other = (ServerInformation) obj;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (serverName == null) {
			if (other.serverName != null)
				return false;
		} else if (!serverName.equals(other.serverName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServerInformation [serverName=" + serverName + ", ipAddress="
				+ ipAddress + ", port=" + port + "]";
	}
}
